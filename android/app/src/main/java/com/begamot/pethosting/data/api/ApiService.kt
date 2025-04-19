package com.begamot.pethosting.data.api

import com.begamot.pethosting.data.models.requestresponse.AuthResponse
import com.begamot.pethosting.data.models.requestresponse.ConversationResponse
import com.begamot.pethosting.data.models.requestresponse.CreateListingRequest
import com.begamot.pethosting.data.models.requestresponse.CreatePaymentIntentRequest
import com.begamot.pethosting.data.models.requestresponse.CreatePetRequest
import com.begamot.pethosting.data.models.requestresponse.CreateResponseRequest
import com.begamot.pethosting.data.models.requestresponse.CreateReviewRequest
import com.begamot.pethosting.data.models.requestresponse.DeleteResponse
import com.begamot.pethosting.data.models.requestresponse.ImageUploadResponse
import com.begamot.pethosting.data.models.requestresponse.ListingDetailResponse
import com.begamot.pethosting.data.models.requestresponse.ListingResponse
import com.begamot.pethosting.data.models.requestresponse.LoginRequest
import com.begamot.pethosting.data.models.requestresponse.MarkReadResponse
import com.begamot.pethosting.data.models.requestresponse.MessageResponse
import com.begamot.pethosting.data.models.requestresponse.PaymentIntentResponse
import com.begamot.pethosting.data.models.requestresponse.PetResponse
import com.begamot.pethosting.data.models.requestresponse.ProcessPaymentRequest
import com.begamot.pethosting.data.models.requestresponse.RegisterRequest
import com.begamot.pethosting.data.models.requestresponse.ResponseResponse
import com.begamot.pethosting.data.models.requestresponse.ReviewResponse
import com.begamot.pethosting.data.models.requestresponse.SendMessageRequest
import com.begamot.pethosting.data.models.requestresponse.TransactionResponse
import com.begamot.pethosting.data.models.requestresponse.UpdateListingRequest
import com.begamot.pethosting.data.models.requestresponse.UpdatePetRequest
import com.begamot.pethosting.data.models.requestresponse.UpdateResponseStatusRequest
import com.begamot.pethosting.data.models.requestresponse.UpdateUserRequest
import com.begamot.pethosting.data.models.requestresponse.UserResponse
import com.begamot.pethosting.data.models.requestresponse.VerificationResponse
import com.begamot.pethosting.data.models.requestresponse.VerifyRequest
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface ApiService {
    // Authentication
    @POST("auth/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<AuthResponse>
    
    @POST("auth/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<AuthResponse>
    
    @POST("auth/verify")
    suspend fun verifyUser(@Body request: VerifyRequest): Response<VerificationResponse>
    
    // User
    @GET("users/{id}")
    suspend fun getUserById(@Path("id") userId: String): Response<UserResponse>
    
    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") userId: String, @Body request: UpdateUserRequest): Response<UserResponse>
    
    @Multipart
    @POST("users/{id}/image")
    suspend fun uploadUserImage(
        @Path("id") userId: String,
        @Part image: MultipartBody.Part
    ): Response<ImageUploadResponse>
    
    // Pets
    @GET("pets")
    suspend fun getUserPets(): Response<List<PetResponse>>
    
    @GET("pets/{id}")
    suspend fun getPetById(@Path("id") petId: String): Response<PetResponse>
    
    @POST("pets")
    suspend fun createPet(@Body request: CreatePetRequest): Response<PetResponse>
    
    @PUT("pets/{id}")
    suspend fun updatePet(@Path("id") petId: String, @Body request: UpdatePetRequest): Response<PetResponse>
    
    @DELETE("pets/{id}")
    suspend fun deletePet(@Path("id") petId: String): Response<DeleteResponse>
    
    @Multipart
    @POST("pets/{id}/images")
    suspend fun uploadPetImage(
        @Path("id") petId: String,
        @Part image: MultipartBody.Part
    ): Response<ImageUploadResponse>
    
    // Listings
    @GET("listings")
    suspend fun getListings(@QueryMap filters: Map<String, String>): Response<List<ListingResponse>>
    
    @GET("listings/{id}")
    suspend fun getListingById(@Path("id") listingId: String): Response<ListingDetailResponse>
    
    @POST("listings")
    suspend fun createListing(@Body request: CreateListingRequest): Response<ListingResponse>
    
    @PUT("listings/{id}")
    suspend fun updateListing(@Path("id") listingId: String, @Body request: UpdateListingRequest): Response<ListingResponse>
    
    @DELETE("listings/{id}")
    suspend fun deleteListing(@Path("id") listingId: String): Response<DeleteResponse>
    
    // Responses to Listings
    @GET("listings/{id}/responses")
    suspend fun getListingResponses(@Path("id") listingId: String): Response<List<ResponseResponse>>
    
    @POST("listings/{id}/responses")
    suspend fun createResponse(@Path("id") listingId: String, @Body request: CreateResponseRequest): Response<ResponseResponse>
    
    @PUT("listings/{id}/responses/{responseId}")
    suspend fun updateResponseStatus(
        @Path("id") listingId: String,
        @Path("responseId") responseId: String,
        @Body request: UpdateResponseStatusRequest
    ): Response<ResponseResponse>
    
    // Messages
    @GET("messages/conversations")
    suspend fun getConversations(): Response<List<ConversationResponse>>
    
    @GET("messages/conversations/{userId}")
    suspend fun getMessages(@Path("userId") userId: String): Response<List<MessageResponse>>
    
    @POST("messages/{userId}")
    suspend fun sendMessage(@Path("userId") userId: String, @Body request: SendMessageRequest): Response<MessageResponse>
    
    @PUT("messages/{userId}/read")
    suspend fun markMessagesAsRead(@Path("userId") userId: String): Response<MarkReadResponse>
    
    // Reviews
    @GET("users/{id}/reviews")
    suspend fun getUserReviews(@Path("id") userId: String): Response<List<ReviewResponse>>
    
    @POST("reviews")
    suspend fun createReview(@Body request: CreateReviewRequest): Response<ReviewResponse>
    
    // Payments
    @POST("payments/intent")
    suspend fun createPaymentIntent(@Body request: CreatePaymentIntentRequest): Response<PaymentIntentResponse>
    
    @POST("payments/process")
    suspend fun processPayment(@Body request: ProcessPaymentRequest): Response<TransactionResponse>
    
    @GET("payments/transactions")
    suspend fun getTransactions(): Response<List<TransactionResponse>>
    
    @POST("payments/refund/{transactionId}")
    suspend fun refundPayment(@Path("transactionId") transactionId: String): Response<TransactionResponse>
}
