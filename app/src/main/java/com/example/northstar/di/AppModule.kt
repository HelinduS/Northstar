package com.example.northstar.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.northstar.data.local.NorthStarDatabase
import com.example.northstar.data.local.dao.ExpenseDao
import com.example.northstar.data.local.dao.GoalDao
import com.example.northstar.data.local.dao.IncomeDao
import com.example.northstar.data.remote.CurrencyApiService
import com.example.northstar.data.repository.GoalRepository
import com.example.northstar.data.repository.GoalRepositoryImpl
import com.example.northstar.data.repository.ExpenseRepository
import com.example.northstar.data.repository.ExpenseRepositoryImpl
import com.example.northstar.data.repository.IncomeRepository
import com.example.northstar.data.repository.IncomeRepositoryImpl
import com.example.northstar.ui.lock.PinLockManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Inside AppModule.kt
    @Provides
    @Singleton
    fun provideCurrencyApi(): CurrencyApiService {
        return Retrofit.Builder()
            // Updated to the new stable v1 endpoint
            .baseUrl("https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CurrencyApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    // --- Room Database Providers ---

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NorthStarDatabase =
        Room.databaseBuilder(
            context,
            NorthStarDatabase::class.java,
            "northstar_database"
        ).build()

    @Provides
    @Singleton
    fun provideIncomeDao(database: NorthStarDatabase): IncomeDao =
        database.incomeDao()

    @Provides
    @Singleton
    fun provideExpenseDao(database: NorthStarDatabase): ExpenseDao =
        database.expenseDao()

    @Provides
    @Singleton
    fun provideGoalDao(database: NorthStarDatabase): GoalDao =
        database.goalDao()

    // --- Repository Providers ---

    @Provides
    @Singleton
    fun provideIncomeRepository(
        incomeDao: IncomeDao,
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): IncomeRepository = IncomeRepositoryImpl(incomeDao, firestore, firebaseAuth)

    @Provides
    @Singleton
    fun provideGoalRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): GoalRepository = GoalRepositoryImpl(firebaseAuth, firestore)

    @Provides
    @Singleton
    fun provideExpenseRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): ExpenseRepository = ExpenseRepositoryImpl(firebaseAuth, firestore)

    @Provides
    @Singleton
    fun providePinLockManager(application: Application): PinLockManager {
        return PinLockManager(application)
    }
}