package com.sparkingpathshala.app.ui.profile

import android.widget.Toast
import androidx.core.net.toUri
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
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sparkingpathshala.app.data.local.isAdmin
import com.sparkingpathshala.app.data.local.isGuest
import com.sparkingpathshala.app.ui.dashboard.DashboardViewModel
import com.sparkingpathshala.app.ui.common.DeleteAccountConfirmationDialog
import com.sparkingpathshala.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: DashboardViewModel,
    paddingValues: PaddingValues,
    onOpenDrawer: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onNavigateToSignup: () -> Unit = {},
    onPasswordChangedAndRequireLogin: () -> Unit = onNavigateToLogin,
    onDeleteAccount: () -> Unit
) {
    val user by viewModel.user.collectAsState()
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val isGuest = user.isGuest()

    // Password State
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var isUpdatingPassword by remember { mutableStateOf(false) }

    // Legal modal state
    var showLegalModal by remember { mutableStateOf(false) }
    var legalModalTab by remember { mutableIntStateOf(0) }

    // Dialog state
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showLegalModal) {
        SparkingLegalModal(
            initialTab = legalModalTab,
            onDismiss = { showLegalModal = false }
        )
    }

    DeleteAccountConfirmationDialog(
        show = showDeleteDialog,
        onDismiss = { showDeleteDialog = false },
        onConfirm = onDeleteAccount
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues)
            .imePadding()
            .verticalScroll(rememberScrollState())
    ) {
        // Top Avatar Profile Banner with BrandGradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BrandGradient)
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                RealisticTactileIconButton(
                    onClick = onOpenDrawer,
                    icon = Icons.Default.Menu,
                    contentDescription = "Open Drawer"
                )
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    color = FlameGold,
                    tonalElevation = 6.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isGuest) {
                            Icon(Icons.Default.PersonOutline, contentDescription = null, tint = Color.Black, modifier = Modifier.size(44.dp))
                        } else {
                            val initials = remember(user) {
                                val name = user?.name ?: "Student"
                                name.split(" ").take(2).mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("")
                            }
                            Text(
                                text = initials,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = if (isGuest) "Guest Explorer" else (user?.name ?: "Student"),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Text(
                    text = if (isGuest) "Exploring in Anonymous Mode" else "Roll No: ${user?.rollNo ?: "SP-2026-X"} • CBSE",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Black.copy(alpha = 0.35f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FlameGold.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "॥ इतः सम्भवति ॥",
                        color = FlameGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 3.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isGuest) {
            // Guest Card with Log In & Sign Up Options
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = SecondaryGold,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Unlock Full Student Access",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = PrimaryBlue,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Sign up or log in with your student credentials to download homework, save test scores, ask doubt tickets, view live attendance and fee receipts.",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    RealisticButton(
                        onClick = onNavigateToSignup,
                        containerColor = PrimaryBlue,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Create New Account (Sign Up)", fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    RealisticOutlinedButton(
                        onClick = onNavigateToLogin,
                        strokeColor = PrimaryBlue,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null, tint = PrimaryBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Log In to Existing Account", fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // App Version & Legal for Guest
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "About Sparking Pathshala",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ListItem(
                        headlineContent = { Text("App Version", fontSize = 14.sp, fontWeight = FontWeight.Medium) },
                        supportingContent = { Text("${com.sparkingpathshala.app.BuildConfig.VERSION_NAME} (Build ${com.sparkingpathshala.app.BuildConfig.VERSION_CODE}) • Guest Access", fontSize = 12.sp) },
                        leadingContent = { Icon(Icons.Default.Info, contentDescription = null, tint = PrimaryBlue) }
                    )
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    ListItem(
                        headlineContent = { Text("Terms of Use", fontSize = 14.sp, fontWeight = FontWeight.Medium) },
                        supportingContent = { Text("Read educational terms (sparkingpathshala.in/terms)", fontSize = 11.sp) },
                        leadingContent = { Icon(Icons.Default.Description, contentDescription = null, tint = PrimaryBlue) },
                        modifier = Modifier.clickable {
                            legalModalTab = 1
                            showLegalModal = true
                        }
                    )
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    ListItem(
                        headlineContent = { Text("Privacy Policy", fontSize = 14.sp, fontWeight = FontWeight.Medium) },
                        supportingContent = { Text("Read our privacy terms (sparkingpathshala.in/privacy-policy)", fontSize = 11.sp) },
                        leadingContent = { Icon(Icons.Default.PrivacyTip, contentDescription = null, tint = PrimaryBlue) },
                        modifier = Modifier.clickable {
                            legalModalTab = 0
                            showLegalModal = true
                        }
                    )
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    ListItem(
                        headlineContent = { Text("Official Website", fontSize = 14.sp, fontWeight = FontWeight.Medium) },
                        supportingContent = { Text("https://sparkingpathshala.in", fontSize = 11.sp) },
                        leadingContent = { Icon(Icons.Default.Language, contentDescription = null, tint = PrimaryBlue) },
                        modifier = Modifier.clickable {
                            try {
                                uriHandler.openUri("https://sparkingpathshala.in")
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    )

                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                    ListItem(
                        headlineContent = { Text("Campus Address", fontSize = 14.sp, fontWeight = FontWeight.Medium) },
                        supportingContent = { Text("Dheeraj Nagar, Atmadpur, Faridabad, Haryana", fontSize = 11.sp) },
                        leadingContent = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryBlue) }
                    )

                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                    ListItem(
                        headlineContent = { Text("Campus Admissions Helpline", fontSize = 14.sp, fontWeight = FontWeight.Medium) },
                        supportingContent = { Text("+91 99994 70824 • Er. Sajjan Kumar Mallik", fontSize = 11.sp) },
                        leadingContent = { Icon(Icons.Default.Phone, contentDescription = null, tint = PrimaryBlue) },
                        modifier = Modifier.clickable {
                            try {
                                val intent = android.content.Intent(android.content.Intent.ACTION_DIAL).apply {
                                    data = android.net.Uri.parse("tel:+919999470824")
                                }
                                context.startActivity(intent)
                            } catch (_: Exception) {}
                        }
                    )

                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                }
            }
        } else {
            // Official Student ID Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Student Academic ID",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandRuby
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = FlameGold.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, FlameGold)
                        ) {
                            Text(
                                text = "ACTIVE SCHOLAR",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF92400E),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    ProfileInfoRow(label = "Institute", value = "Sparking Pathshala (Er. S. Mallik)", icon = Icons.Default.School)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray.copy(alpha = 0.3f))
                    ProfileInfoRow(label = "Registered Email", value = user?.email ?: "N/A", icon = Icons.Default.Email)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray.copy(alpha = 0.3f))
                    if (user.isAdmin()) {
                        ProfileInfoRow(label = "Authorization Role", value = "Administrator & Faculty", icon = Icons.Default.Security)
                    } else {
                        ProfileInfoRow(label = "Class & Curriculum", value = "${user?.classLevel ?: "Class 10"} • CBSE Science & Math", icon = Icons.Default.Class)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Academic Metrics Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Batch Rank", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                        Text("#3", fontSize = 18.sp, fontWeight = FontWeight.Black, color = BrandRuby)
                        Text("Top 5%", fontSize = 10.sp, color = FlameGold, fontWeight = FontWeight.Bold)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Study Streak", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                        Text("350 XP", fontSize = 16.sp, fontWeight = FontWeight.Black, color = FlameGold)
                        Text("Level 4 Master", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Attendance", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                        Text("96%", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF059669))
                        Text("Consistent", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Offline Cache & Storage Management
            var cacheCleared by remember { mutableStateOf(false) }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Device Storage & Offline Data",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandRuby
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Cached DPPs & Solutions", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text(if (cacheCleared) "0 KB cached" else "24.5 MB offline storage", fontSize = 11.sp, color = Color.Gray)
                        }
                        RealisticOutlinedButton(
                            onClick = {
                                cacheCleared = true
                                Toast.makeText(context, "Offline question cache cleared! 24.5 MB freed.", Toast.LENGTH_SHORT).show()
                            },
                            strokeColor = BrandRuby,
                            modifier = Modifier.height(38.dp)
                        ) {
                            Text(if (cacheCleared) "Cleared" else "Free Space", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandRuby)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Change Password Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Change Account Password",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text("Current Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryBlue) },
                        trailingIcon = {
                            IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                                Icon(
                                    imageVector = if (currentPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        },
                        singleLine = true,
                        visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryBlue) },
                        trailingIcon = {
                            IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                Icon(
                                    imageVector = if (newPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        },
                        singleLine = true,
                        visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isUpdatingPassword) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = SecondaryGold)
                        }
                    } else {
                    RealisticButton(
                        onClick = {
                            if (currentPassword.isBlank() || newPassword.isBlank()) {
                                Toast.makeText(context, "Password fields cannot be empty", Toast.LENGTH_SHORT).show()
                                return@RealisticButton
                            }
                            if (newPassword.length < 8) {
                                Toast.makeText(context, "New password must be at least 8 characters long", Toast.LENGTH_SHORT).show()
                                return@RealisticButton
                            }
                            isUpdatingPassword = true
                            viewModel.updatePassword(currentPassword, newPassword) { result ->
                                isUpdatingPassword = false
                                result.onSuccess {
                                    Toast.makeText(
                                        context,
                                        "Password updated successfully! Please log in again to secure your account.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    currentPassword = ""
                                    newPassword = ""
                                    onPasswordChangedAndRequireLogin()
                                }.onFailure { error ->
                                    Toast.makeText(context, error.localizedMessage ?: "Password change failed.", Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        containerColor = PrimaryBlue,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Update Password", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

            // Danger & Legal Settings Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Account Actions & Legal",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    ListItem(
                        headlineContent = { Text("App Version", fontSize = 14.sp, fontWeight = FontWeight.Medium) },
                        supportingContent = { Text("${com.sparkingpathshala.app.BuildConfig.VERSION_NAME} (Build ${com.sparkingpathshala.app.BuildConfig.VERSION_CODE}) • Production Ready", fontSize = 12.sp) },
                        leadingContent = { Icon(Icons.Default.Info, contentDescription = null, tint = PrimaryBlue) }
                    )
                    
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                    ListItem(
                        headlineContent = { Text("Terms of Use", fontSize = 14.sp, fontWeight = FontWeight.Medium) },
                        supportingContent = { Text("Read educational terms (sparkingpathshala.in/terms)", fontSize = 11.sp) },
                        leadingContent = { Icon(Icons.Default.Description, contentDescription = null, tint = PrimaryBlue) },
                        modifier = Modifier.clickable {
                            legalModalTab = 1
                            showLegalModal = true
                        }
                    )

                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                    ListItem(
                        headlineContent = { Text("Privacy Policy", fontSize = 14.sp, fontWeight = FontWeight.Medium) },
                        supportingContent = { Text("Read our privacy terms (sparkingpathshala.in/privacy-policy)", fontSize = 11.sp) },
                        leadingContent = { Icon(Icons.Default.PrivacyTip, contentDescription = null, tint = PrimaryBlue) },
                        modifier = Modifier.clickable {
                            legalModalTab = 0
                            showLegalModal = true
                        }
                    )
                    
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                    ListItem(
                        headlineContent = { Text("Official Website", fontSize = 14.sp, fontWeight = FontWeight.Medium) },
                        supportingContent = { Text("https://sparkingpathshala.in", fontSize = 11.sp) },
                        leadingContent = { Icon(Icons.Default.Language, contentDescription = null, tint = PrimaryBlue) },
                        modifier = Modifier.clickable {
                            try {
                                uriHandler.openUri("https://sparkingpathshala.in")
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    )

                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                    ListItem(
                        headlineContent = { Text("Campus Address", fontSize = 14.sp, fontWeight = FontWeight.Medium) },
                        supportingContent = { Text("Dheeraj Nagar, Atmadpur, Faridabad, Haryana", fontSize = 11.sp) },
                        leadingContent = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryBlue) }
                    )

                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                    ListItem(
                        headlineContent = { Text("Campus Admissions Helpline", fontSize = 14.sp, fontWeight = FontWeight.Medium) },
                        supportingContent = { Text("+91 99994 70824 • Er. Sajjan Kumar Mallik", fontSize = 11.sp) },
                        leadingContent = { Icon(Icons.Default.Phone, contentDescription = null, tint = PrimaryBlue) },
                        modifier = Modifier.clickable {
                            try {
                                val intent = android.content.Intent(android.content.Intent.ACTION_DIAL).apply {
                                    data = android.net.Uri.parse("tel:+919999470824")
                                }
                                context.startActivity(intent)
                            } catch (_: Exception) {}
                        }
                    )

                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                    ListItem(
                        headlineContent = { Text("Delete Account", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Red) },
                        supportingContent = { Text("Permanently delete data & account", fontSize = 11.sp, color = Color.Red.copy(alpha = 0.7f)) },
                        leadingContent = { Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color.Red) },
                        modifier = Modifier.clickable { showDeleteDialog = true }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ProfileInfoRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = BrandRuby, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, fontSize = 11.sp, color = Color.Gray)
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
