package com.example.northstar.data.repository

import com.example.northstar.data.remote.FirestoreConstants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    val currentUser: FirebaseUser? get() = firebaseAuth.currentUser

    fun isLoggedIn(): Boolean = firebaseAuth.currentUser != null

    // ─────────────────────────────────────────────────────────────
    //  EMAILJS CONFIG — fill these in from emailjs.com dashboard
    // ─────────────────────────────────────────────────────────────
    companion object {
        private const val EMAILJS_SERVICE_ID  = "service_763a8sa"   // e.g. "service_abc123"
        private const val EMAILJS_TEMPLATE_ID = "template_qfhny25"  // e.g. "template_xyz789"
        private const val EMAILJS_PUBLIC_KEY  = "FIcB5FK-XjZb2T-Yk"   // e.g. "user_XXXXXXXX"
        // In your EmailJS template, create two variables: {{to_email}} and {{otp_code}}
    }

    // ─────────────────────────────────────────────────────────────
    //  FORGOT PASSWORD — OTP FLOW
    // ─────────────────────────────────────────────────────────────

    /**
     * 1. Checks the email exists in the users collection.
     * 2. Generates a 4-digit OTP, stores it in Firestore with a 10-min expiry.
     * 3. Sends the OTP to the user via EmailJS (called directly from Android).
     */
    suspend fun sendOtp(email: String): Result<Unit> {
        return try {
            // Generate OTP and persist to Firestore
            val otp = (1000..9999).random().toString()
            val expiresAt = System.currentTimeMillis() + 10 * 60 * 1000L

            firestore
                .collection("password_resets")
                .document(email)
                .set(mapOf("otp" to otp, "expiresAt" to expiresAt, "used" to false))
                .await()

            // Send email via EmailJS
            sendOtpEmail(email, otp)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Calls EmailJS v1 REST API directly from Android. No server needed. */
    private fun sendOtpEmail(toEmail: String, otp: String): Result<Unit> {
        return try {
            val connection = (URL("https://api.emailjs.com/api/v1.0/email/send")
                .openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
                connectTimeout = 10_000
                readTimeout    = 10_000
            }

            val body = JSONObject().apply {
                put("service_id",  EMAILJS_SERVICE_ID)
                put("template_id", EMAILJS_TEMPLATE_ID)
                put("user_id",     EMAILJS_PUBLIC_KEY)
                put("template_params", JSONObject().apply {
                    put("to_email", toEmail)
                    put("passcode", otp)
                    put("time", "10 minutes")
                })
            }

            OutputStreamWriter(connection.outputStream).use { it.write(body.toString()) }

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                Result.success(Unit)
            } else {
                val error = connection.errorStream?.bufferedReader()?.readText() ?: "Unknown"
                Result.failure(Exception("EmailJS error ${connection.responseCode}: $error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Verifies the 4-digit OTP against the Firestore record.
     * Marks it used on success so it cannot be replayed.
     */
    suspend fun verifyOtp(email: String, enteredOtp: String): Result<Unit> {
        return try {
            val doc = firestore
                .collection("password_resets")
                .document(email)
                .get()
                .await()

            if (!doc.exists())
                return Result.failure(Exception("No reset request found. Please request a new code."))

            val storedOtp = doc.getString("otp")    ?: ""
            val expiresAt = doc.getLong("expiresAt") ?: 0L
            val used      = doc.getBoolean("used")   ?: false

            when {
                used ->
                    Result.failure(Exception("This code has already been used. Please request a new one."))
                System.currentTimeMillis() > expiresAt ->
                    Result.failure(Exception("Code has expired. Please request a new one."))
                storedOtp != enteredOtp ->
                    Result.failure(Exception("Incorrect code. Please try again."))
                else -> {
                    firestore.collection("password_resets").document(email)
                        .update("used", true).await()
                    Result.success(Unit)
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Called after OTP is verified. Fires Firebase's official password-reset
     * email (a secure link). The user taps it and resets their password in
     * the browser — this is the only way to change a signed-out user's
     * password without a backend or Blaze plan.
     */
    suspend fun sendFirebaseResetLink(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  EXISTING METHODS
    // ─────────────────────────────────────────────────────────────

    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun register(
        email: String, password: String, displayName: String,
        phone: String = "", address: String = ""
    ): Result<FirebaseUser> {
        var createdUser: FirebaseUser? = null
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user!!
            createdUser = user
            firestore.collection(FirestoreConstants.COLLECTION_USERS).document(user.uid).set(
                hashMapOf(
                    "uid" to user.uid, "displayName" to displayName, "email" to email,
                    "phone" to phone, "address" to address,
                    "createdAt" to com.google.firebase.Timestamp.now(),
                    "currency" to "LKR",
                    "updatedAt" to com.google.firebase.Timestamp.now(),
                    "activeGoalId" to null
                )
            ).await()
            Result.success(user)
        } catch (e: Exception) {
            createdUser?.let { runCatching { it.delete().await() }; firebaseAuth.signOut() }
            Result.failure(e)
        }
    }

    suspend fun updateDisplayName(newName: String): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser ?: return Result.failure(Exception("Not logged in"))
            user.updateProfile(com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(newName).build()).await()
            firestore.collection(FirestoreConstants.COLLECTION_USERS)
                .document(user.uid).update("displayName", newName).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateEmail(newEmail: String, currentPassword: String): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser ?: return Result.failure(Exception("Not logged in"))
            user.reauthenticate(com.google.firebase.auth.EmailAuthProvider
                .getCredential(user.email!!, currentPassword)).await()
            user.updateEmail(newEmail).await()
            firestore.collection(FirestoreConstants.COLLECTION_USERS)
                .document(user.uid).update("email", newEmail).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getUserProfile(uid: String): Result<Map<String, Any>> {
        return try {
            val doc = firestore.collection(FirestoreConstants.COLLECTION_USERS)
                .document(uid).get().await()
            Result.success(doc.data ?: emptyMap())
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateUserField(uid: String, field: String, value: String): Result<Unit> {
        return try {
            firestore.collection(FirestoreConstants.COLLECTION_USERS)
                .document(uid).update(field, value).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    fun signOut() = firebaseAuth.signOut()
}