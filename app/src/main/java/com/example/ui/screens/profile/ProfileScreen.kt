package com.example.ui.screens.profile

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.ui.theme.*
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.ShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    shopViewModel: ShopViewModel,
    authViewModel: AuthViewModel,
    onNavigateToOrders: () -> Unit,
    onNavigateToTracking: (Order) -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToAuth: () -> Unit
) {
    val userProfile by shopViewModel.userProfile.collectAsState()
    val orders by shopViewModel.liveOrders.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showFaqDialog by remember { mutableStateOf(false) }
    var showSupportChatDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Account & Profile", fontWeight = FontWeight.Bold, fontSize = 18.sp) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .testTag("profile_screen"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // 1. User Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(FreshGreenPrimary, OceanBlueSecondary)
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = userProfile.name.take(2).uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = userProfile.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                                Text(
                                    text = userProfile.email,
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = userProfile.phone,
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 12.sp
                                )
                            }
                            IconButton(onClick = { showEditProfileDialog = true }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Edit Profile", tint = Color.White)
                            }
                        }
                    }
                }
            }

            // 2. Admin Hub Shortcut (if Admin)
            if (userProfile.isAdmin) {
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToAdmin() }
                            .testTag("btn_admin_hub"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = OceanBlueSecondary.copy(alpha = 0.12f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.AdminPanelSettings, contentDescription = null, tint = OceanBlueSecondary)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("FreshMart Admin Hub", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = OceanBlueSecondary)
                                    Text("Inventory, Live Orders & Analytics", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = OceanBlueSecondary)
                        }
                    }
                }
            }

            // 3. Recent Orders Section
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("My Orders (${orders.size})", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(orders.take(3)) { order ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            shopViewModel.selectOrderForTracking(order)
                            onNavigateToTracking(order)
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(order.orderNumber, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("${order.items.size} item(s) • $${String.format("%.2f", order.total)}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Slot: ${order.deliverySlot.dayLabel}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = FreshGreenContainer
                            ) {
                                Text(
                                    text = order.status.displayName,
                                    color = FreshGreenPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // 4. Quick Settings & Options
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Text("Settings & Support", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(8.dp))

                SettingItem(
                    icon = Icons.Outlined.Language,
                    title = "App Language",
                    subtitle = userProfile.preferredLanguage,
                    onClick = { showLanguageDialog = true }
                )
                SettingItem(
                    icon = Icons.Outlined.SupportAgent,
                    title = "Live Customer Support",
                    subtitle = "24/7 Chat & Assistance",
                    onClick = { showSupportChatDialog = true }
                )
                SettingItem(
                    icon = Icons.Outlined.HelpOutline,
                    title = "FAQs & Help Center",
                    subtitle = "Delivery guarantees, returns & AI fitting",
                    onClick = { showFaqDialog = true }
                )
                SettingItem(
                    icon = Icons.Outlined.Logout,
                    title = "Sign Out",
                    subtitle = "Switch account or log out",
                    onClick = {
                        authViewModel.logout()
                        onNavigateToAuth()
                    }
                )

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Dialogs
    if (showEditProfileDialog) {
        var name by remember { mutableStateOf(userProfile.name) }
        var email by remember { mutableStateOf(userProfile.email) }
        var phone by remember { mutableStateOf(userProfile.phone) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email Address") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        shopViewModel.repository.updateUserProfile(name, email, phone)
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FreshGreenPrimary)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showLanguageDialog) {
        val languages = listOf("English", "Español", "हिन्दी (Hindi)", "Français", "Deutsch", "日本語")
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Select Language", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    languages.forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    shopViewModel.setLanguage(lang)
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = userProfile.preferredLanguage.startsWith(lang.take(4)), onClick = {
                                shopViewModel.setLanguage(lang)
                                showLanguageDialog = false
                            })
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(lang, fontSize = 14.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) { Text("Close") }
            }
        )
    }

    if (showFaqDialog) {
        AlertDialog(
            onDismissRequest = { showFaqDialog = false },
            title = { Text("Frequently Asked Questions", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(modifier = Modifier.height(300.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FaqItem("How does Seafood 0°C Delivery work?", "All fresh fish, crabs, and prawns are packed in thermal insulated boxes with ice gel packs straight from the harbor dock, maintaining 0°C throughout transit.")
                    }
                    item {
                        FaqItem("How accurate is the AI Virtual Try-On?", "Our AI Virtual Fitting Engine uses computer vision and Gemini generative models to preserve your exact body shape, height, and skin tone while simulating realistic fabric draping.")
                    }
                    item {
                        FaqItem("What is Same-Day Express Delivery?", "Orders placed before 4:00 PM qualify for same-day delivery slots with live tracking and doorstep notification.")
                    }
                    item {
                        FaqItem("What are the accepted payment methods?", "We accept UPI (Google Pay, PhonePe), Credit/Debit cards (Visa/Mastercard), Net Banking, and Cash on Delivery.")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFaqDialog = false }) { Text("Got it") }
            }
        )
    }

    if (showSupportChatDialog) {
        var chatInput by remember { mutableStateOf("") }
        val messages = remember {
            mutableStateListOf(
                "FreshMart Support" to "Hello Ashok! How can we assist with your groceries, seafood, or AI clothing fit today?"
            )
        }

        AlertDialog(
            onDismissRequest = { showSupportChatDialog = false },
            title = { Text("Live Support Chat", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.height(300.dp)) {
                    LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(messages) { (sender, msg) ->
                            val isMe = sender == "You"
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isMe) FreshGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.widthIn(max = 240.dp)
                                ) {
                                    Text(
                                        text = msg,
                                        color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = chatInput,
                            onValueChange = { chatInput = it },
                            placeholder = { Text("Type message...", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = {
                                if (chatInput.isNotBlank()) {
                                    messages.add("You" to chatInput)
                                    val userText = chatInput
                                    chatInput = ""
                                    messages.add("FreshMart Support" to "Thanks for reaching out regarding '$userText'. Our team has verified your account and will ensure 100% satisfaction!")
                                }
                            }
                        ) {
                            Icon(Icons.Filled.Send, contentDescription = "Send", tint = FreshGreenPrimary)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSupportChatDialog = false }) { Text("Close") }
            }
        )
    }
}

@Composable
fun SettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = FreshGreenPrimary, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun FaqItem(question: String, answer: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(question, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(2.dp))
        Text(answer, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 16.sp)
    }
}
