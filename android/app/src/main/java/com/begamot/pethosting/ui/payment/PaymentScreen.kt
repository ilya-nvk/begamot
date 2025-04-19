package com.begamot.pethosting.ui.payment

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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

                        HorizontalDivider()
                        
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
                                imageVector = Icons.Default.ShoppingCart, // credit card
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
                    enabled = selectedPaymentMethod != null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (paymentState is PaymentViewModel.PaymentState.Loading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Pay Now")
                    }
                }
                
                if (paymentState is PaymentViewModel.PaymentState.Error) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = (paymentState as PaymentViewModel.PaymentState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                // Success state
                if (paymentState is PaymentViewModel.PaymentState.Success) {
                    LaunchedEffect(Unit) {
                        // Navigate to success screen after brief delay
                        delay(1000)
                        navController.navigate("payment-success/${(paymentState as PaymentViewModel.PaymentState.Success).transactionId}") {
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
                                        imageVector = Icons.Default.ShoppingCart, // credit card
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
