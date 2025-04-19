package com.begamot.pethosting.di

import com.begamot.pethosting.data.impl.ListingRepositoryImpl
import com.begamot.pethosting.data.impl.MessageRepositoryImpl
import com.begamot.pethosting.data.impl.PaymentRepositoryImpl
import com.begamot.pethosting.data.impl.PetRepositoryImpl
import com.begamot.pethosting.data.impl.ResponseRepositoryImpl
import com.begamot.pethosting.data.impl.ReviewRepositoryImpl
import com.begamot.pethosting.data.impl.UserRepositoryImpl
import com.begamot.pethosting.data.repositories.ListingRepository
import com.begamot.pethosting.data.repositories.MessageRepository
import com.begamot.pethosting.data.repositories.PaymentRepository
import com.begamot.pethosting.data.repositories.PetRepository
import com.begamot.pethosting.data.repositories.ResponseRepository
import com.begamot.pethosting.data.repositories.ReviewRepository
import com.begamot.pethosting.data.repositories.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository
    
    @Binds
    @Singleton
    abstract fun bindPetRepository(
        petRepositoryImpl: PetRepositoryImpl
    ): PetRepository
    
    @Binds
    @Singleton
    abstract fun bindListingRepository(
        listingRepositoryImpl: ListingRepositoryImpl
    ): ListingRepository
    
    @Binds
    @Singleton
    abstract fun bindResponseRepository(
        responseRepositoryImpl: ResponseRepositoryImpl
    ): ResponseRepository
    
    @Binds
    @Singleton
    abstract fun bindMessageRepository(
        messageRepositoryImpl: MessageRepositoryImpl
    ): MessageRepository
    
    @Binds
    @Singleton
    abstract fun bindReviewRepository(
        reviewRepositoryImpl: ReviewRepositoryImpl
    ): ReviewRepository
    
    @Binds
    @Singleton
    abstract fun bindPaymentRepository(
        paymentRepositoryImpl: PaymentRepositoryImpl
    ): PaymentRepository
}
