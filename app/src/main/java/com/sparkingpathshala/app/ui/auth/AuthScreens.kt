package com.sparkingpathshala.app.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sparkingpathshala.app.R
import com.sparkingpathshala.app.ui.theme.*

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToSignup: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.resetAuthState()
    }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedGradientBackground()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                cornerRadius = 24.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // App Logo with Clay style
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .size(90.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    BrutalistHeader(
                        text = "Sparking Pathshala",
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    
                    Text(
                        text = "Igniting Minds, Empowering Future",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    NeumorphicTextField(
                        value = email,
                        onValueChange = { 
                            email = it 
                            if (uiState is AuthUiState.Error) viewModel.clearError()
                        },
                        placeholderText = "Email or Mobile Number",
                        leadingIcon = Icons.Default.Email,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    NeumorphicTextField(
                        value = password,
                        onValueChange = { 
                            password = it 
                            if (uiState is AuthUiState.Error) viewModel.clearError()
                        },
                        placeholderText = "Password",
                        leadingIcon = Icons.Default.Lock,
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = ElectricCyan
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (email.trim().isNotBlank() && password.trim().isNotBlank()) {
                                    viewModel.login(email.trim(), password.trim())
                                }
                            }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (uiState is AuthUiState.Error) {
                        Text(
                            text = (uiState as AuthUiState.Error).message,
                            color = ErrorRed,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    if (uiState is AuthUiState.Loading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    } else {
                        NeumorphicButton(
                            onClick = { viewModel.login(email.trim(), password.trim()) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "LOG IN",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.clickable { onNavigateToSignup() },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Don't have an account? ", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                        Text(
                            text = "SIGN UP",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            style = TextStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Guest Mode Bypass
                    RealisticOutlinedButton(
                        onClick = { viewModel.loginAsGuest(context) },
                        strokeColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(Icons.Default.Explore, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Explore App as Guest",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(18.dp))

                    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Coded & Designed with ❤️ by ",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Manish",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable {
                                try {
                                    uriHandler.openUri("https://www.instagram.com/buildwithmanish.dev")
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "Version ${com.sparkingpathshala.app.BuildConfig.VERSION_NAME} (Build ${com.sparkingpathshala.app.BuildConfig.VERSION_CODE})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "All rights reserved Sparking Pathshala 2026.",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun SignupScreen(
    viewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onSignupSuccess: () -> Unit,
    onGuestLoginSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var mobile by rememberSaveable { mutableStateOf("") }
    var classLevel by rememberSaveable { mutableStateOf("Class 10") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var consentChecked by rememberSaveable { mutableStateOf(false) }
    var isGuestLoginAttempt by remember { mutableStateOf(false) }
    
    var showLegalModal by remember { mutableStateOf(false) }
    var legalModalTab by remember { mutableIntStateOf(0) }

    var expandedClassMenu by remember { mutableStateOf(false) }
    val classes = remember { (1..12).map { "Class $it" } + listOf("JEE/NEET Prep", "Government Exams") }

    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.resetAuthState()
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.Success -> {
                if (isGuestLoginAttempt) {
                    onGuestLoginSuccess()
                } else {
                    onSignupSuccess()
                }
            }
            is AuthUiState.PendingApproval -> {
                android.widget.Toast.makeText(context, "Registration Successful! Pending Admin Approval.", android.widget.Toast.LENGTH_LONG).show()
            }
            is AuthUiState.Error -> {
                val errorMsg = (uiState as AuthUiState.Error).message
                android.widget.Toast.makeText(context, errorMsg, android.widget.Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    if (showLegalModal) {
        SparkingLegalModal(
            initialTab = legalModalTab,
            onDismiss = { showLegalModal = false }
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedGradientBackground()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                cornerRadius = 24.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .size(80.dp)
                            .align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    BrutalistHeader(
                        text = "Create Account",
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    
                    Text(
                        text = "Join Sparking Pathshala's Digital Portal",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    NeumorphicTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholderText = "Full Name",
                        leadingIcon = Icons.Default.Person,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    NeumorphicTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholderText = "Email Address",
                        leadingIcon = Icons.Default.Email,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    NeumorphicTextField(
                        value = mobile,
                        onValueChange = { mobile = it },
                        placeholderText = "Mobile Number",
                        leadingIcon = Icons.Default.Phone,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Class Selection dropdown in Neumorphic container
                    Box(modifier = Modifier.fillMaxWidth()) {
                        val isDark = LocalThemeIsDark.current
                        val dropdownBg = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC)
                        val dropdownBorder = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
                        val textColor = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .background(dropdownBg, RoundedCornerShape(12.dp))
                                .border(1.5.dp, dropdownBorder, RoundedCornerShape(12.dp))
                                .clickable { expandedClassMenu = true }
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Class,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp).padding(end = 4.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = classLevel,
                                    color = textColor,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        DropdownMenu(
                            expanded = expandedClassMenu,
                            onDismissRequest = { expandedClassMenu = false },
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            classes.forEach { selectedClass ->
                                DropdownMenuItem(
                                    text = { Text(selectedClass, fontWeight = FontWeight.Medium) },
                                    onClick = {
                                        classLevel = selectedClass
                                        expandedClassMenu = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    NeumorphicTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholderText = "Password",
                        leadingIcon = Icons.Default.Lock,
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    if (uiState is AuthUiState.Error) {
                        Text(
                            text = (uiState as AuthUiState.Error).message,
                            color = ErrorRed,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    if (uiState is AuthUiState.PendingApproval) {
                        AlertDialog(
                            onDismissRequest = {
                                viewModel.clearError()
                                onNavigateToLogin()
                            },
                            title = { Text("Registration Submitted", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
                            text = {
                                Text("Your account has been created successfully and is now pending admin approval. You can log in once approved by the administrator.")
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        viewModel.clearError()
                                        onNavigateToLogin()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                ) {
                                    Text("Go to Log In")
                                }
                            }
                        )
                    }

                    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = consentChecked,
                            onCheckedChange = { consentChecked = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary,
                                uncheckedColor = Color.Gray
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(
                                text = "I confirm that I am 18+ or registering with consent of my parent/guardian.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "I agree to the ",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Terms of Use",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable {
                                        legalModalTab = 1
                                        showLegalModal = true
                                    }
                                )
                                Text(
                                    text = " & ",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Privacy Policy",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable {
                                        legalModalTab = 0
                                        showLegalModal = true
                                    }
                                )
                            }
                            Text(
                                text = "Consent to data processing per sparkingpathshala.in",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.clickable {
                                    legalModalTab = 2
                                    showLegalModal = true
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (uiState is AuthUiState.Loading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    } else {
                        NeumorphicButton(
                            onClick = {
                                val cleanName = name.trim()
                                val cleanEmail = email.trim()
                                val cleanMobile = mobile.trim()
                                val cleanPassword = password.trim()
                                val cleanClass = classLevel.trim()
                                if (cleanName.isBlank() || cleanEmail.isBlank() || cleanMobile.isBlank() || cleanPassword.isBlank()) {
                                    android.widget.Toast.makeText(context, "Please fill in all fields", android.widget.Toast.LENGTH_SHORT).show()
                                    return@NeumorphicButton
                                }
                                if (cleanPassword.length < 8) {
                                    android.widget.Toast.makeText(context, "Password must be at least 8 characters long", android.widget.Toast.LENGTH_LONG).show()
                                    return@NeumorphicButton
                                }
                                val hasLetter = cleanPassword.any { it.isLetter() }
                                val hasDigit = cleanPassword.any { it.isDigit() }
                                if (!hasLetter || !hasDigit) {
                                    android.widget.Toast.makeText(context, "Password must contain both letters and numbers", android.widget.Toast.LENGTH_LONG).show()
                                    return@NeumorphicButton
                                }
                                viewModel.signup(cleanName, cleanEmail, cleanClass, cleanMobile, cleanPassword)
                            },
                            enabled = consentChecked,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "SIGN UP",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (consentChecked) Color.White else Color.Gray,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToLogin() },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Already have an account? ", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                        Text(
                            text = "LOG IN",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            style = TextStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Guest Mode Bypass
                    RealisticOutlinedButton(
                        onClick = {
                            isGuestLoginAttempt = true
                            viewModel.loginAsGuest(context)
                        },
                        strokeColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(Icons.Default.Explore, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Explore App as Guest",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Coded & Designed with ❤️ by ",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Manish",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MoltenAmber,
                            style = TextStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline),
                            modifier = Modifier.clickable {
                                try {
                                    uriHandler.openUri("https://www.instagram.com/buildwithmanish.dev")
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "Version ${com.sparkingpathshala.app.BuildConfig.VERSION_NAME} (Build ${com.sparkingpathshala.app.BuildConfig.VERSION_CODE})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "All rights reserved Sparking Pathshala 2026.",
                        fontSize = 10.sp,
                        color = Color.Gray.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}
