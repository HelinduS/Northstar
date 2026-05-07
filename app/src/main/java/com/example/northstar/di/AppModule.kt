package com.example.northstar.di

import com.example.northstar.data.repository.ExpenseRepository
import com.example.northstar.data.repository.ExpenseRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideExpenseRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): ExpenseRepository = ExpenseRepositoryImpl(firebaseAuth, firestore)
}