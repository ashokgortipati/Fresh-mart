package com.example.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FreshGreenPrimary
import com.example.ui.theme.OceanBlueSecondary
import com.example.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onAuthSuccess: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) {
            onAuthSuccess()
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Logo Branding
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(FreshGreenPrimary, OceanBlueSecondary)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.ShoppingBag,
                    contentDescription = "FreshMart Logo",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "FreshMart",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Clothing • Groceries • Seafood",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (state.isLoginMode) "Sign In to Your Account" else "Create a Free Account",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (!state.isLoginMode) {
                        OutlinedTextField(
                            value = state.nameInput,
                            onValueChange = { viewModel.onNameChanged(it) },
                            label = { Text("Full Name") },
                            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_name_input"),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = state.phoneInput,
                            onValueChange = { viewModel.onPhoneChanged(it) },
                            label = { Text("Phone Number") },
                            leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_phone_input"),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    OutlinedTextField(
                        value = state.emailInput,
                        onValueChange = { viewModel.onEmailChanged(it) },
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_email_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = state.passwordInput,
                        onValueChange = { viewModel.onPasswordChanged(it) },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = "Toggle password"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_password_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (state.isLoginMode) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { viewModel.openForgotPassword(true) }) {
                                Text("Forgot Password?", fontSize = 12.sp, color = FreshGreenPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    if (state.errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    if (state.successMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.successMessage!!,
                            color = FreshGreenPrimary,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Primary Submit Button
                    Button(
                        onClick = {
                            if (state.isLoginMode) viewModel.loginWithEmail()
                            else viewModel.signUpWithEmail()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("auth_submit_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = FreshGreenPrimary),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text(
                                text = if (state.isLoginMode) "Sign In" else "Create Account",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Google Sign In CTA
                    OutlinedButton(
                        onClick = { viewModel.loginWithGoogle() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_google_signin"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Google",
                            tint = Color(0xFF4285F4),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Continue with Google",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Toggle mode
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (state.isLoginMode) "Don't have an account?" else "Already have an account?",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        TextButton(onClick = { viewModel.toggleAuthMode() }) {
                            Text(
                                text = if (state.isLoginMode) "Sign Up" else "Sign In",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = FreshGreenPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Forgot Password Dialog
    if (state.showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.openForgotPassword(false) },
            title = { Text("Reset Password", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Enter your email address to receive password reset instructions.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = state.forgotPasswordEmail,
                        onValueChange = { viewModel.setForgotPasswordEmail(it) },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.sendPasswordReset() },
                    colors = ButtonDefaults.buttonColors(containerColor = FreshGreenPrimary)
                ) {
                    Text("Send Instructions")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.openForgotPassword(false) }) { Text("Cancel") }
            }
        )
    }
}
