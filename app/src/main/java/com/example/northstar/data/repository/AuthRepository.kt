package com.example.northstar.data.repository

import com.example.northstar.data.remote.FirestoreConstants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    val currentUser: FirebaseUser? get() = firebaseAuth.currentUser

    fun isLoggedIn(): Boolean = firebaseAuth.currentUser != null

    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(
        email: String,
        password: String,
        displayName: String
    ): Result<FirebaseUser> {
        var createdUser: FirebaseUser? = null
        return try {
            // Step 1 — create Firebase Auth account
            val result = firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .await()
            val user = result.user!!
            createdUser = user

            // Step 2 — write user document to Firestore
            val userDoc = hashMapOf(
                "uid" to user.uid,
                "displayName" to displayName,
                "email" to email,
                "createdAt" to com.google.firebase.Timestamp.now(),
                "defaultCurrency" to "LKR",
                "activeGoalId" to null
            )
            firestore
                .collection(FirestoreConstants.COLLECTION_USERS)
                .document(user.uid)
                .set(userDoc)
                .await()

            Result.success(user)
        } catch (e: Exception) {
            createdUser?.let { user ->
                runCatching {
                    user.delete().await()
                }
                firebaseAuth.signOut()
            }
            Result.failure(e)
        }
    }

    fun signOut() = firebaseAuth.signOut()
}