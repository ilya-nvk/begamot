# Разработка Андроид-приложения для передержки животных

## Архитектура приложения

Для разработки приложения предлагаю использовать современную архитектуру MVVM (Model-View-ViewModel) с использованием следующих технологий:

1. **UI Layer**: Jetpack Compose для создания современного и гибкого интерфейса
2. **Architecture Components**: ViewModel, LiveData, Room, Navigation
3. **Dependency Injection**: Hilt
4. **Network**: Retrofit, OkHttp
5. **Authentication**: Firebase Authentication
6. **Cloud Storage**: Firebase Storage
7. **Real-time messaging**: Firebase Firestore
8. **Payment Processing**: Stripe SDK

## Структура проекта

```
com.pethosting
├── data
│   ├── api
│   ├── db
│   ├── models
│   └── repositories
├── di
├── domain
│   ├── usecases
│   └── models
├── ui
│   ├── theme
│   ├── common
│   ├── auth
│   ├── profile
│   ├── listings
│   ├── messages
│   ├── payment
│   └── settings
└── utils
```

## Фронтенд (Android приложение)

### build.gradle (project-level)

```gradle
buildscript {
    ext {
        compose_version = '1.5.0'
        kotlin_version = '1.9.0'
        hilt_version = '2.46'
        room_version = '2.5.2'
        retrofit_version = '2.9.0'
        nav_version = '2.7.0'
    }
    
    repositories {
        google()
        mavenCentral()
    }
    
    dependencies {
        classpath 'com.android.tools.build:gradle:8.0.2'
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
        classpath "com.google.dagger:hilt-android-gradle-plugin:$hilt_version"
        classpath 'com.google.gms:google-services:4.3.15'
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}
```

### build.gradle (app-level)

```gradle
plugins {
    id 'com.android.application'
    id 'kotlin-android'
    id 'kotlin-kapt'
    id 'dagger.hilt.android.plugin'
    id 'com.google.gms.google-services'
}

android {
    compileSdk 34
    
    defaultConfig {
        applicationId "com.pethosting"
        minSdk 29
        targetSdk 34
        versionCode 1
        versionName "1.0"
        
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }
    
    buildTypes {
        release {
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = '17'
    }
    
    buildFeatures {
        compose true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion compose_version
    }
}

dependencies {
    // Core
    implementation 'androidx.core:core-ktx:1.10.1'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.9.0'
    
    // Compose
    implementation "androidx.compose.ui:ui:$compose_version"
    implementation "androidx.compose.material3:material3:1.1.1"
    implementation "androidx.compose.ui:ui-tooling-preview:$compose_version"
    implementation 'androidx.activity:activity-compose:1.7.2'
    implementation "androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1"
    implementation "androidx.navigation:navigation-compose:$nav_version"
    
    // Hilt
    implementation "com.google.dagger:hilt-android:$hilt_version"
    kapt "com.google.dagger:hilt-compiler:$hilt_version"
    implementation 'androidx.hilt:hilt-navigation-compose:1.0.0'
    
    // Room
    implementation "androidx.room:room-runtime:$room_version"
    implementation "androidx.room:room-ktx:$room_version"
    kapt "androidx.room:room-compiler:$room_version"
    
    // Retrofit
    implementation "com.squareup.retrofit2:retrofit:$retrofit_version"
    implementation "com.squareup.retrofit2:converter-gson:$retrofit_version"
    implementation "com.squareup.okhttp3:okhttp:4.11.0"
    implementation "com.squareup.okhttp3:logging-interceptor:4.11.0"
    
    // Firebase
    implementation platform('com.google.firebase:firebase-bom:32.2.2')
    implementation 'com.google.firebase:firebase-auth-ktx'
    implementation 'com.google.firebase:firebase-firestore-ktx'
    implementation 'com.google.firebase:firebase-storage-ktx'
    
    // Stripe SDK
    implementation 'com.stripe:stripe-android:20.28.0'
    
    // Image loading
    implementation "io.coil-kt:coil-compose:2.4.0"
    
    // Testing
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
    androidTestImplementation "androidx.compose.ui:ui-test-junit4:$compose_version"
    debugImplementation "androidx.compose.ui:ui-tooling:$compose_version"
}
```

### Модель данных

#### User.kt
```kotlin
data class User(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val profileImageUrl: String? = null,
    val isVerified: Boolean = false,
    val rating: Float = 0f,
    val reviewCount: Int = 0
)
```

#### Pet.kt
```kotlin
data class Pet(
    val id: String = "",
    val ownerId: String = "",
    val name: String = "",
    val type: String = "", // e.g., "Dog", "Cat"
    val breed: String = "",
    val age: Int = 0,
    val description: String = "",
    val imageUrls: List<String> = emptyList()
)
```

#### Listing.kt
```kotlin
data class Listing(
    val id: String = "",
    val ownerId: String = "",
    val title: String = "",
    val description: String = "",
    val petId: String = "",
    val startDate: Long = 0,
    val endDate: Long = 0,
    val price: Double = 0.0,
    val status: ListingStatus = ListingStatus.ACTIVE,
    val createdAt: Long = System.currentTimeMillis()
)

enum class ListingStatus {
    ACTIVE, BOOKED, COMPLETED, CANCELLED
}
```

#### Response.kt
```kotlin
data class Response(
    val id: String = "",
    val listingId: String = "",
    val responderId: String = "",
    val message: String = "",
    val status: ResponseStatus = ResponseStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)

enum class ResponseStatus {
    PENDING, ACCEPTED, REJECTED
}
```

#### Message.kt
```kotlin
data class Message(
    val id: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
```

#### Review.kt
```kotlin
data class Review(
    val id: String = "",
    val listingId: String = "",
    val reviewerId: String = "",
    val receiverId: String = "",
    val rating: Float = 0f,
    val comment: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
```

#### Transaction.kt
```kotlin
data class Transaction(
    val id: String = "",
    val listingId: String = "",
    val payerId: String = "",
    val receiverId: String = "",
    val amount: Double = 0.0,
    val status: TransactionStatus = TransactionStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)

enum class TransactionStatus {
    PENDING, COMPLETED, REFUNDED, FAILED
}
```

### Repository Layer

#### UserRepository.kt
```kotlin
interface UserRepository {
    suspend fun getCurrentUser(): User?
    suspend fun getUserById(userId: String): User?
    suspend fun registerUser(email: String, password: String, user: User): Result<User>
    suspend fun loginUser(email: String, password: String): Result<User>
    suspend fun logoutUser()
    suspend fun updateUser(user: User): Result<User>
    suspend fun uploadUserProfileImage(imageUri: Uri): Result<String>
    suspend fun verifyUser(idImageUri: Uri): Result<Boolean>
}
```

#### PetRepository.kt
```kotlin
interface PetRepository {
    suspend fun getPetsByOwnerId(ownerId: String): List<Pet>
    suspend fun getPetById(petId: String): Pet?
    suspend fun createPet(pet: Pet): Result<Pet>
    suspend fun updatePet(pet: Pet): Result<Pet>
    suspend fun deletePet(petId: String): Result<Boolean>
    suspend fun uploadPetImage(petId: String, imageUri: Uri): Result<String>
}
```

#### ListingRepository.kt
```kotlin
interface ListingRepository {
    suspend fun getAllListings(filters: Map<String, Any>? = null): List<Listing>
    suspend fun getListingsByOwnerId(ownerId: String): List<Listing>
    suspend fun getListingById(listingId: String): Listing?
    suspend fun createListing(listing: Listing): Result<Listing>
    suspend fun updateListing(listing: Listing): Result<Listing>
    suspend fun deleteListing(listingId: String): Result<Boolean>
}
```

#### ResponseRepository.kt
```kotlin
interface ResponseRepository {
    suspend fun getResponsesByListingId(listingId: String): List<Response>
    suspend fun getResponsesByResponderId(responderId: String): List<Response>
    suspend fun createResponse(response: Response): Result<Response>
    suspend fun updateResponseStatus(responseId: String, status: ResponseStatus): Result<Response>
}
```

#### MessageRepository.kt
```kotlin
interface MessageRepository {
    suspend fun getConversationBetweenUsers(userId1: String, userId2: String): List<Message>
    suspend fun getAllConversationsForUser(userId: String): Map<String, List<Message>>
    suspend fun sendMessage(message: Message): Result<Message>
    suspend fun markMessagesAsRead(senderId: String, receiverId: String): Result<Boolean>
}
```

#### ReviewRepository.kt
```kotlin
interface ReviewRepository {
    suspend fun getReviewsByReceiverId(receiverId: String): List<Review>
    suspend fun createReview(review: Review): Result<Review>
}
```

#### PaymentRepository.kt
```kotlin
interface PaymentRepository {
    suspend fun createPaymentIntent(amount: Double, currency: String = "USD"): Result<String>
    suspend fun processPayment(paymentIntentId: String, transaction: Transaction): Result<Transaction>
    suspend fun refundPayment(transactionId: String): Result<Transaction>
}
```

### UI Screens

#### MainActivity.kt
```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PetHostingTheme {
                PetHostingApp()
            }
        }
    }
}

@Composable
fun PetHostingApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val isUserLoggedIn by authViewModel.isUserLoggedIn.collectAsState()
    
    Scaffold { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = if (isUserLoggedIn) "home" else "login",
            modifier = Modifier.padding(paddingValues)
        ) {
            // Authentication
            composable("login") {
                LoginScreen(navController)
            }
            composable("register") {
                RegisterScreen(navController)
            }
            
            // Main navigation
            composable("home") {
                HomeScreen(navController)
            }
            composable("profile") {
                ProfileScreen(navController)
            }
            composable("create-listing") {
                CreateListingScreen(navController)
            }
            composable("listing/{listingId}") { backStackEntry ->
                val listingId = backStackEntry.arguments?.getString("listingId") ?: ""
                ListingDetailScreen(navController, listingId)
            }
            composable("messages") {
                MessagesScreen(navController)
            }
            composable("chat/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                ChatScreen(navController, userId)
            }
            composable("settings") {
                SettingsScreen(navController)
            }
            composable("payment/{listingId}") { backStackEntry ->
                val listingId = backStackEntry.arguments?.getString("listingId") ?: ""
                PaymentScreen(navController, listingId)
            }
        }
    }
}
```

#### LoginScreen.kt
```kotlin
@Composable
fun LoginScreen(navController: NavController) {
    val viewModel: AuthViewModel = hiltViewModel()
    val loginState by viewModel.loginState.collectAsState()
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Pet Hosting",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { viewModel.login(email, password) },
            enabled = email.isNotEmpty() && password.isNotEmpty() && !loginState.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (loginState.isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Login")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(onClick = { navController.navigate("register") }) {
            Text("Don't have an account? Register")
        }
        
        if (loginState.error != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = loginState.error ?: "",
                color = MaterialTheme.colorScheme.error
            )
        }
        
        LaunchedEffect(loginState.isSuccess) {
            if (loginState.isSuccess) {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            }
        }
    }
}
```

#### RegisterScreen.kt
```kotlin
@Composable
fun RegisterScreen(navController: NavController) {
    val viewModel: AuthViewModel = hiltViewModel()
    val registerState by viewModel.registerState.collectAsState()
    
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var profileImage by remember { mutableStateOf<Uri?>(null) }
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        profileImage = uri
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 24.dp)
        )
        
        // Profile image picker
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                .clickable { launcher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (profileImage != null) {
                AsyncImage(
                    model = profileImage,
                    contentDescription = "Profile Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Add Profile Image",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.size(48.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Registration form
        TextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            isError = password != confirmPassword && confirmPassword.isNotEmpty()
        )
        
        if (password != confirmPassword && confirmPassword.isNotEmpty()) {
            Text(
                text = "Passwords do not match",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 16.dp, top = 4.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { 
                if (password == confirmPassword) {
                    val user = User(
                        fullName = fullName,
                        email = email,
                        phone = phone
                    )
                    viewModel.register(email, password, user, profileImage)
                }
            },
            enabled = fullName.isNotEmpty() && email.isNotEmpty() && 
                    phone.isNotEmpty() && password.isNotEmpty() && 
                    password == confirmPassword && !registerState.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (registerState.isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Register")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(onClick = { navController.navigate("login") }) {
            Text("Already have an account? Login")
        }
        
        if (registerState.error != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = registerState.error ?: "",
                color = MaterialTheme.colorScheme.error
            )
        }
        
        LaunchedEffect(registerState.isSuccess) {
            if (registerState.isSuccess) {
                navController.navigate("login") {
                    popUpTo("register") { inclusive = true }
                }
            }
        }
    }
}
```

#### HomeScreen.kt
```kotlin
@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: ListingViewModel = hiltViewModel()
    val listings by viewModel.listings.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadListings()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pet Hosting") },
                actions = {
                    IconButton(onClick = { navController.navigate("profile") }) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                    IconButton(onClick = { navController.navigate("messages") }) {
                        Icon(Icons.Default.Email, contentDescription = "Messages")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("create-listing") }) {
                Icon(Icons.Default.Add, contentDescription = "Create Listing")
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(listings) { listing ->
                    ListingItem(
                        listing = listing,
                        onClick = { navController.navigate("listing/${listing.id}") }
                    )
                }
            }
        }
    }
}

@Composable
fun ListingItem(listing: Listing, onClick: () -> Unit) {
    val viewModel: ListingViewModel = hiltViewModel()
    val pet by viewModel.getPet(listing.petId).collectAsState(initial = null)
    val owner by viewModel.getUser(listing.ownerId).collectAsState(initial = null)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = listing.title,
                    style = MaterialTheme.typography.titleLarge
                )
                
                Text(
                    text = "$${listing.price}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            pet?.let {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Pets,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${it.name} (${it.breed}, ${it.age} years)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = listing.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                owner?.let {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = it.profileImageUrl,
                            contentDescription = "Owner",
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = it.fullName,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                
                val dateFormat = SimpleDateFormat("MMM dd - MMM dd", Locale.getDefault())
                val dateRange = "${dateFormat.format(Date(listing.startDate))} to ${dateFormat.format(Date(listing.endDate))}"
                
                Text(
                    text = dateRange,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
```

#### ListingDetailScreen.kt
```kotlin
@Composable
fun ListingDetailScreen(navController: NavController, listingId: String) {
    val viewModel: ListingDetailViewModel = hiltViewModel()
    val listing by viewModel.listing.collectAsState()
    val pet by viewModel.pet.collectAsState()
    val owner by viewModel.owner.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val responseState by viewModel.responseState.collectAsState()
    
    var responseMessage by remember { mutableStateOf("") }
    
    LaunchedEffect(listingId) {
        viewModel.loadListing(listingId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Listing Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (listing != null && pet != null && owner != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // Pet images carousel
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                ) {
                    if (pet?.imageUrls?.isNotEmpty() == true) {
                        var currentImageIndex by remember { mutableStateOf(0) }
                        
                        AsyncImage(
                            model = pet?.imageUrls?.getOrNull(currentImageIndex),
                            contentDescription = "Pet Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            pet?.imageUrls?.forEachIndexed { index, _ ->
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 4.dp)
                                        .size(8.dp)
                                        .background(
                                            color = if (index == currentImageIndex)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                            shape = CircleShape
                                        )
                                        .clickable { currentImageIndex = index }
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Pets,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Listing header with title and price
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = listing?.title ?: "",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        
                        Text(
                            text = "$${listing?.price}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Date range
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        Text(
                            text = "${dateFormat.format(Date(listing?.startDate ?: 0))} - ${dateFormat.format(Date(listing?.endDate ?: 0))}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Pet information
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Pet Information",
                                style = MaterialTheme.typography.titleMedium
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            pet?.let {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    AsyncImage(
                                        model = it.imageUrls.firstOrNull(),
                                        contentDescription = "Pet Image",
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                    
                                    Spacer(modifier = Modifier.width(16.dp))
                                    
                                    Column {
                                        Text(
                                            text = it.name,
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                        
                                        Text(
                                            text = "${it.type}, ${it.breed}, ${it.age} years old",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = it.description,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Listing description
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = listing?.description ?: "",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Owner information
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Pet Owner",
                                style = MaterialTheme.typography.titleMedium
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            owner?.let {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = it.profileImageUrl,
                                        contentDescription = "Owner Image",
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                    
                                    Spacer(modifier = Modifier.width(16.dp))
                                    
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = it.fullName,
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                        
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.secondary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            
                                            Spacer(modifier = Modifier.width(4.dp))
                                            
                                            Text(
                                                text = "${it.rating} (${it.reviewCount} reviews)",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                    
                                    if (it.id != currentUser?.id) {
                                        IconButton(
                                            onClick = { navController.navigate("chat/${it.id}") }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Chat,
                                                contentDescription = "Chat with owner"
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Response section (only show if not the owner)
                    if (listing?.ownerId != currentUser?.id) {
                        when (responseState) {
                            is ResponseState.Success -> {
                                Text(
                                    text = "Your response has been sent",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            is ResponseState.Pending -> {
                                Text(
                                    text = "Your response is pending",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            is ResponseState.Accepted -> {
                                Column {
                                    Text(
                                        text = "Your response has been accepted!",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    Button(
                                        onClick = { navController.navigate("payment/${listing?.id}") },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(text = "Proceed to Payment")
                                    }
                                }
                            }
                            is ResponseState.NotSent -> {
                                Column {
                                    Text(
                                        text = "Respond to this Listing",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    TextField(
                                        value = responseMessage,
                                        onValueChange = { responseMessage = it },
                                        label = { Text("Your message to the owner") },
                                        modifier = Modifier.fillMaxWidth(),
                                        minLines = 3
                                    )
                                    
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    Button(
                                        onClick = { 
                                            viewModel.respondToListing(
                                                Response(
                                                    listingId = listingId,
                                                    message = responseMessage
                                                )
                                            ) 
                                        },
                                        enabled = responseMessage.isNotEmpty(),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(text = "Send Response")
                                    }
                                }
                            }
                        }
                    } else {
                        // Owner actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedButton(
                                onClick = { navController.navigate("listing/edit/${listing?.id}") },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "Edit Listing")
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Button(
                                onClick = { navController.navigate("listing/responses/${listing?.id}") },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "View Responses")
                            }
                        }
                    }
                }
            }
        }
    }
}
```

#### ProfileScreen.kt
```kotlin
@Composable
fun ProfileScreen(navController: NavController) {
    val viewModel: ProfileViewModel = hiltViewModel()
    val user by viewModel.user.collectAsState()
    val pets by viewModel.pets.collectAsState()
    val listings by viewModel.listings.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadUserProfile()
        viewModel.loadUserPets()
        viewModel.loadUserListings()
        viewModel.loadUserReviews()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // User profile header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    // Cover background
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    )
                    
                    // Profile info overlay
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = user?.profileImageUrl,
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = user?.fullName ?: "",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(16.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(4.dp))
                            
                            Text(
                                text = "${user?.rating} (${user?.reviewCount} reviews)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            
                            if (user?.isVerified == true) {
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified User",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                
                                Spacer(modifier = Modifier.width(4.dp))
                                
                                Text(
                                    text = "Verified",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
                
                // Content with tabs
                var selectedTabIndex by remember { mutableStateOf(0) }
                val tabs = listOf("My Pets", "My Listings", "Reviews")
                
                TabRow(selectedTabIndex = selectedTabIndex) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            text = { Text(title) },
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index }
                        )
                    }
                }
                
                when (selectedTabIndex) {
                    0 -> {
                        // My Pets Tab
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "My Pets",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                
                                Button(
                                    onClick = { navController.navigate("add-pet") }
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add Pet")
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            if (pets.isEmpty()) {
                                EmptyState(
                                    icon = Icons.Default.Pets,
                                    message = "You haven't added any pets yet"
                                )
                            } else {
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(pets) { pet ->
                                        PetCard(
                                            pet = pet,
                                            onClick = { navController.navigate("pet/${pet.id}") }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    1 -> {
                        // My Listings Tab
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "My Listings",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                
                                Button(
                                    onClick = { navController.navigate("create-listing") }
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Create")
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            if (listings.isEmpty()) {
                                EmptyState(
                                    icon = Icons.Default.Description,
                                    message = "You haven't created any listings yet"
                                )
                            } else {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    listings.forEach { listing ->
                                        ListingItem(
                                            listing = listing,
                                            onClick = { navController.navigate("listing/${listing.id}") }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    2 -> {
                        // Reviews Tab
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Reviews",
                                style = MaterialTheme.typography.titleLarge
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            if (reviews.isEmpty()) {
                                EmptyState(
                                    icon = Icons.Default.Star,
                                    message = "You don't have any reviews yet"
                                )
                            } else {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    reviews.forEach { review ->
                                        ReviewItem(review = review)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PetCard(pet: Pet, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                if (pet.imageUrls.isNotEmpty()) {
                    AsyncImage(
                        model = pet.imageUrls.first(),
                        contentDescription = "Pet Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Pets,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = pet.name,
                    style = MaterialTheme.typography.titleMedium
                )
                
                Text(
                    text = "${pet.type}, ${pet.breed}",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Text(
                    text = "${pet.age} years old",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun ReviewItem(review: Review) {
    val viewModel: ProfileViewModel = hiltViewModel()
    val reviewer by viewModel.getUserById(review.reviewerId).collectAsState(initial = null)
    
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                reviewer?.let {
                    AsyncImage(
                        model = it.profileImageUrl,
                        contentDescription = "Reviewer Image",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = it.fullName,
                            style = MaterialTheme.typography.titleSmall
                        )
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            repeat(5) { index ->
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (index < review.rating) 
                                        MaterialTheme.colorScheme.secondary 
                                    else 
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                            Text(
                                text = dateFormat.format(Date(review.createdAt)),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun EmptyState(icon: ImageVector, message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}
```

#### PaymentScreen.kt
```kotlin
@Composable
fun PaymentScreen(navController: NavController, listingId: String) {
    val viewModel: PaymentViewModel = hiltViewModel()
    val listing by viewModel.listing.collectAsState()
    val owner by viewModel.owner.collectAsState()
    val paymentState by viewModel.paymentState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    LaunchedEffect(listingId) {
        viewModel.loadListing(listingId)
    }
    
    var selectedPaymentMethod by remember { mutableStateOf<PaymentMethod?>(null) }
    var isPaymentMethodModalVisible by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (listing != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Payment summary
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Payment Summary",
                            style = MaterialTheme.typography.titleLarge
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Listing",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            Text(
                                text = listing?.title ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Pet Sitter",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            Text(
                                text = owner?.fullName ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Start Date",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            Text(
                                text = dateFormat.format(Date(listing?.startDate ?: 0)),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "End Date",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            Text(
                                text = dateFormat.format(Date(listing?.endDate ?: 0)),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Divider()
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        val serviceFee = (listing?.price ?: 0.0) * 0.1 // 10% service fee
                        val total = (listing?.price ?: 0.0) + serviceFee
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Service Fee",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            Text(
                                text = "$${String.format("%.2f", serviceFee)}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total",
                                style = MaterialTheme.typography.titleMedium
                            )
                            
                            Text(
                                text = "$${String.format("%.2f", total)}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Payment method selection
                Text(
                    text = "Payment Method",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                if (selectedPaymentMethod == null) {
                    OutlinedButton(
                        onClick = { isPaymentMethodModalVisible = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Payment Method")
                    }
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isPaymentMethodModalVisible = true }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = selectedPaymentMethod?.cardBrand ?: "",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                
                                Text(
                                    text = "•••• ${selectedPaymentMethod?.last4 ?: ""}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Change Payment Method"
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Payment terms
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Payment Terms",
                            style = MaterialTheme.typography.titleSmall
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "By proceeding with the payment, you agree that the amount will be held securely until the service is completed. The payment will be released to the pet sitter after you confirm the completion of the service.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Payment button
                Button(
                    onClick = { 
                        selectedPaymentMethod?.let { paymentMethod ->
                            viewModel.processPayment(
                                listingId = listingId,
                                paymentMethodId = paymentMethod.id,
                                amount = (listing?.price ?: 0.0) + ((listing?.price ?: 0.0) * 0.1)
                            )
                        }
                    },
                    enabled = selectedPaymentMethod != null && !isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (paymentState is PaymentState.Loading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Pay Now")
                    }
                }
                
                if (paymentState is PaymentState.Error) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = (paymentState as PaymentState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                // Success state
                if (paymentState is PaymentState.Success) {
                    LaunchedEffect(Unit) {
                        // Navigate to success screen after brief delay
                        delay(1000)
                        navController.navigate("payment-success/${(paymentState as PaymentState.Success).transactionId}") {
                            popUpTo("home")
                        }
                    }
                }
            }
        }
    }
    
    // Payment method modal
    if (isPaymentMethodModalVisible) {
        PaymentMethodModal(
            onDismiss = { isPaymentMethodModalVisible = false },
            onPaymentMethodSelected = { 
                selectedPaymentMethod = it
                isPaymentMethodModalVisible = false
            }
        )
    }
}

@Composable
fun PaymentMethodModal(
    onDismiss: () -> Unit,
    onPaymentMethodSelected: (PaymentMethod) -> Unit
) {
    val viewModel: PaymentViewModel = hiltViewModel()
    val paymentMethods by viewModel.paymentMethods.collectAsState()
    var isAddingNewCard by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        viewModel.loadPaymentMethods()
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Select Payment Method",
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (!isAddingNewCard) {
                    // Show saved payment methods
                    if (paymentMethods.isEmpty()) {
                        Text(
                            text = "No saved payment methods",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            paymentMethods.forEach { paymentMethod ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onPaymentMethodSelected(paymentMethod) }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CreditCard,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    
                                    Spacer(modifier = Modifier.width(16.dp))
                                    
                                    Column {
                                        Text(
                                            text = paymentMethod.cardBrand,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        
                                        Text(
                                            text = "•••• ${paymentMethod.last4}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Add new card button
                    Button(
                        onClick = { isAddingNewCard = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add New Card")
                    }
                } else {
                    // Card input form
                    var cardNumber by remember { mutableStateOf("") }
                    var cardHolder by remember { mutableStateOf("") }
                    var expiryDate by remember { mutableStateOf("") }
                    var cvv by remember { mutableStateOf("") }
                    
                    TextField(
                        value = cardNumber,
                        onValueChange = { 
                            if (it.length <= 16 && it.all { char -> char.isDigit() }) {
                                cardNumber = it
                            }
                        },
                        label = { Text("Card Number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    TextField(
                        value = cardHolder,
                        onValueChange = { cardHolder = it },
                        label = { Text("Card Holder Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextField(
                            value = expiryDate,
                            onValueChange = { 
                                val cleanText = it.replace("/", "")
                                if (cleanText.length <= 4 && cleanText.all { char -> char.isDigit() }) {
                                    if (cleanText.length > 2) {
                                        expiryDate = cleanText.substring(0, 2) + "/" + cleanText.substring(2)
                                    } else {
                                        expiryDate = cleanText
                                    }
                                }
                            },
                            label = { Text("MM/YY") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        TextField(
                            value = cvv,
                            onValueChange = { 
                                if (it.length <= 3 && it.all { char -> char.isDigit() }) {
                                    cvv = it
                                }
                            },
                            label = { Text("CVV") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { isAddingNewCard = false }
                        ) {
                            Text("Cancel")
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Button(
                            onClick = { 
                                // Process and save card
                                val newPaymentMethod = PaymentMethod(
                                    id = "pm_" + UUID.randomUUID().toString(),
                                    cardBrand = "Visa", // Simplified for this example
                                    last4 = cardNumber.takeLast(4)
                                )
                                viewModel.addPaymentMethod(newPaymentMethod)
                                onPaymentMethodSelected(newPaymentMethod)
                            },
                            enabled = cardNumber.length == 16 && 
                                     cardHolder.isNotBlank() && 
                                     expiryDate.length == 5 && 
                                     cvv.length == 3
                        ) {
                            Text("Save Card")
                        }
                    }
                }
            }
        }
    }
}

data class PaymentMethod(
    val id: String,
    val cardBrand: String,
    val last4: String
)

sealed class PaymentState {
    object Idle : PaymentState()
    object Loading : PaymentState()
    data class Success(val transactionId: String) : PaymentState()
    data class Error(val message: String) : PaymentState()
}
```

#### MessagesScreen.kt
```kotlin
@Composable
fun MessagesScreen(navController: NavController) {
    val viewModel: MessagesViewModel = hiltViewModel()
    val conversations by viewModel.conversations.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadConversations()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Messages") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (conversations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "No messages yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(conversations) { conversation ->
                    ConversationItem(
                        conversation = conversation,
                        onClick = { navController.navigate("chat/${conversation.user.id}") }
                    )
                    
                    Divider()
                }
            }
        }
    }
}

@Composable
fun ConversationItem(
    conversation: Conversation,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = conversation.user.profileImageUrl,
            contentDescription = "User Image",
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = conversation.user.fullName,
                    style = MaterialTheme.typography.titleMedium
                )
                
                Text(
                    text = formatTimestamp(conversation.lastMessage.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = conversation.lastMessage.text,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (!conversation.lastMessage.isRead && conversation.lastMessage.receiverId == conversation.currentUserId)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontWeight = if (!conversation.lastMessage.isRead && conversation.lastMessage.receiverId == conversation.currentUserId)
                        FontWeight.Bold
                    else
                        FontWeight.Normal,
                    modifier = Modifier.weight(1f)
                )
                
                if (!conversation.lastMessage.isRead && conversation.lastMessage.receiverId == conversation.currentUserId) {
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                    )
                }
            }
        }
    }
}

data class Conversation(
    val user: User,
    val lastMessage: Message,
    val currentUserId: String
)

fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < 60 * 1000 -> "Just now"
        diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}m ago"
        diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)}h ago"
        diff < 48 * 60 * 60 * 1000 -> "Yesterday"
        else -> {
            val format = SimpleDateFormat("MMM dd", Locale.getDefault())
            format.format(Date(timestamp))
        }
    }
}
```

#### ChatScreen.kt
```kotlin
@Composable
fun ChatScreen(navController: NavController, userId: String) {
    val viewModel: ChatViewModel = hiltViewModel()
    val messages by viewModel.messages.collectAsState()
    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var messageText by remember { mutableStateOf("") }
    
    LaunchedEffect(userId) {
        viewModel.loadUserInfo(userId)
        viewModel.loadMessages(userId)
    }
    
    Scaffold(
        topBar = {
            user?.let {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = it.profileImageUrl,
                                contentDescription = "User Image",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Text(it.fullName)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            } ?: TopAppBar(
                title = { Text("Chat") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Messages
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    reverseLayout = true,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom)
                ) {
                    items(messages.reversed()) { message ->
                        ChatMessage(
                            message = message,
                            isCurrentUser = message.senderId != userId
                        )
                    }
                }
            }
            
            // Message input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Type a message") },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                IconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            viewModel.sendMessage(userId, messageText)
                            messageText = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun ChatMessage(
    message: Message,
    isCurrentUser: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = if (isCurrentUser)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isCurrentUser) 16.dp else 0.dp,
                        bottomEnd = if (isCurrentUser) 0.dp else 16.dp
                    )
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.text,
                color = if (isCurrentUser)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = formatTimestamp(message.timestamp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}
```

## Бэкенд-сервис

Для нашего приложения потребуется надежный backend, который будет обрабатывать следующие аспекты:

1. Аутентификация и управление пользователями
2. Хранение данных (животные, объявления, отзывы)
3. Обработка платежей и транзакций
4. Система сообщений в реальном времени

### Архитектура бэкенда

Предлагается использовать современную микросервисную архитектуру с следующими компонентами:

1. **API Gateway** - центральная точка доступа для мобильного приложения
2. **Auth Service** - управление аутентификацией и авторизацией
3. **User Service** - управление профилями пользователей и верификацией
4. **Listing Service** - управление объявлениями и откликами
5. **Pet Service** - управление данными о питомцах
6. **Messaging Service** - обработка сообщений между пользователями
7. **Payment Service** - обработка платежей и управление транзакциями
8. **Review Service** - управление отзывами и рейтингами

### Технологии для бэкенда

1. **Язык программирования**: Kotlin с Spring Boot
2. **База данных**:
    - PostgreSQL для основных данных
    - Redis для кеширования
    - MongoDB для сообщений
3. **Аутентификация**: JWT с OAuth 2.0
4. **Облачное хранилище**: Amazon S3 для хранения изображений
5. **Обмен сообщениями**: WebSockets с RabbitMQ
6. **Платежный процессор**: Stripe API
7. **Контейнеризация**: Docker и Kubernetes

### Примеры API-интерфейсов

#### Auth API

```
POST /api/auth/register - Регистрация нового пользователя
POST /api/auth/login - Аутентификация пользователя
POST /api/auth/refresh - Обновление токена
POST /api/auth/verify - Отправка документов для верификации
```

#### User API

```
GET /api/users/{id} - Получение информации о пользователе
PUT /api/users/{id} - Обновление профиля пользователя
POST /api/users/{id}/image - Загрузка изображения профиля
GET /api/users/{id}/reviews - Получение отзывов о пользователе
```

#### Pet API

```
GET /api/pets - Получение списка питомцев текущего пользователя
GET /api/pets/{id} - Получение информации о питомце
POST /api/pets - Создание нового питомца
PUT /api/pets/{id} - Обновление информации о питомце
DELETE /api/pets/{id} - Удаление питомца
POST /api/pets/{id}/images - Загрузка изображений питомца
```

#### Listing API

```
GET /api/listings - Получение списка объявлений с фильтрацией
GET /api/listings/{id} - Получение информации об объявлении
POST /api/listings - Создание нового объявления
PUT /api/listings/{id} - Обновление объявления
DELETE /api/listings/{id} - Удаление объявления
GET /api/listings/{id}/responses - Получение откликов на объявление
POST /api/listings/{id}/responses - Создание отклика на объявление
PUT /api/listings/{id}/responses/{responseId} - Обновление статуса отклика
```

#### Messaging API

```
GET /api/messages/conversations - Получение списка диалогов
GET /api/messages/conversations/{userId} - Получение сообщений в диалоге
POST /api/messages/{userId} - Отправка сообщения пользователю
PUT /api/messages/{userId}/read - Отметка сообщений как прочитанных
```

#### Payment API

```
POST /api/payments/intent - Создание платежного намерения
POST /api/payments/process - Обработка платежа
GET /api/payments/transactions - Получение истории транзакций
POST /api/payments/refund/{transactionId} - Возврат средств
```

### База данных

Ниже приведена структура основных таблиц для базы данных PostgreSQL:

```sql
-- Users table
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    profile_image_url VARCHAR(255),
    is_verified BOOLEAN DEFAULT FALSE,
    rating FLOAT DEFAULT 0,
    review_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Pets table
CREATE TABLE pets (
    id UUID PRIMARY KEY,
    owner_id UUID NOT NULL REFERENCES users(id),
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    breed VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Pet images
CREATE TABLE pet_images (
    id UUID PRIMARY KEY,
    pet_id UUID NOT NULL REFERENCES pets(id),
    image_url VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Listings table
CREATE TABLE listings (
    id UUID PRIMARY KEY,
    owner_id UUID NOT NULL REFERENCES users(id),
    pet_id UUID NOT NULL REFERENCES pets(id),
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Responses table
CREATE TABLE responses (
    id UUID PRIMARY KEY,
    listing_id UUID NOT NULL REFERENCES listings(id),
    responder_id UUID NOT NULL REFERENCES users(id),
    message TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Messages table
CREATE TABLE messages (
    id UUID PRIMARY KEY,
    sender_id UUID NOT NULL REFERENCES users(id),
    receiver_id UUID NOT NULL REFERENCES users(id),
    text TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Reviews table
CREATE TABLE reviews (
    id UUID PRIMARY KEY,
    listing_id UUID NOT NULL REFERENCES listings(id),
    reviewer_id UUID NOT NULL REFERENCES users(id),
    receiver_id UUID NOT NULL REFERENCES users(id),
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Transactions table
CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    listing_id UUID NOT NULL REFERENCES listings(id),
    payer_id UUID NOT NULL REFERENCES users(id),
    receiver_id UUID NOT NULL REFERENCES users(id),
    amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    payment_intent_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Payment methods table
CREATE TABLE payment_methods (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    payment_provider_id VARCHAR(255) NOT NULL,
    card_brand VARCHAR(50) NOT NULL,
    last4 VARCHAR(4) NOT NULL,
    exp_month INT NOT NULL,
    exp_year INT NOT NULL,
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Резюме

Предложенное решение представляет собой полноценное Android-приложение для передержки животных, которое соответствует техническому заданию и включает в себя:

1. **Современный UI на основе Jetpack Compose** с Material Design 3
2. **Архитектуру MVVM** для чистого разделения бизнес-логики от интерфейса
3. **Модульную организацию кода** для облегчения поддержки и расширения
4. **Полный набор экранов** для всех требуемых функций
5. **Спецификацию бэкенда** с описанием API и структуры базы данных

Приложение обеспечивает все функциональные возможности, указанные в ТЗ:
- Регистрация и верификация пользователей
- Управление профилями питомцев
- Создание и управление объявлениями
- Отклики на объявления
- Встроенная система сообщений
- Интеграция платежной системы
- Система отзывов и рейтингов

Данное решение готово к дальнейшему развитию и может быть легко масштабировано с добавлением новых функций и улучшений.
