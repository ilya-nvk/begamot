package com.example.petcourse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class MainActivity : ComponentActivity() {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        client.close()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AppScreen(client)
            }
        }
    }
}

@Serializable
data class PingResponse(val ping: String)

@Serializable
data class Listing(val id: Int, val title: String, val description: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen(client: HttpClient) {
    var ping by remember { mutableStateOf<String?>(null) }
    var listings by remember { mutableStateOf<List<Listing>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Pet Course Client", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {
            scope.launch {
                try {
                    ping = client.get("http://10.0.2.2:8000/ping").body<PingResponse>().ping
                    error = null
                } catch (e: Exception) {
                    error = e.localizedMessage
                }
            }
        }) { Text("Ping Server") }
        ping?.let {
            Text("Response: $it")
        }
        Spacer(Modifier.height(24.dp))
        Button(onClick = {
            scope.launch {
                try {
                    listings = client.get("http://10.0.2.2:8000/listings").body()
                    error = null
                } catch (e: Exception) {
                    error = e.localizedMessage
                }
            }
        }) { Text("Load Listings") }
        Spacer(Modifier.height(8.dp))
        listings.forEach {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(Modifier.padding(8.dp)) {
                    Text(it.title, style = MaterialTheme.typography.titleMedium)
                    Text(it.description, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        error?.let {
            Text("Error: $it", color = MaterialTheme.colorScheme.error)
        }
    }
}
