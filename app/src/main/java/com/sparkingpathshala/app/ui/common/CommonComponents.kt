package com.sparkingpathshala.app.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sparkingpathshala.app.ui.theme.PrimaryBlue
import com.sparkingpathshala.app.ui.theme.SecondaryGold
import com.sparkingpathshala.app.ui.theme.RealisticButton
import com.sparkingpathshala.app.ui.theme.RealisticOutlinedButton
import com.sparkingpathshala.app.ui.theme.SparkingIconBox
import com.sparkingpathshala.app.utils.AppConstants
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.DeleteForever

/**
 * Standard confirmation dialog for logging out with modern human craft.
 */
@Composable
fun LogoutConfirmationDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (!show) return
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(22.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        icon = {
            SparkingIconBox(
                icon = Icons.AutoMirrored.Filled.Logout,
                containerColor = MaterialTheme.colorScheme.errorContainer,
                iconTint = MaterialTheme.colorScheme.error,
                boxSize = 48.dp,
                iconSize = 24.dp,
                cornerRadius = 16.dp
            )
        },
        title = {
            Text(
                "Log Out of Account",
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                "Are you sure you want to log out? You will need to sign in again to access your live quizzes, homework, and study material.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                    onConfirm()
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Log Out", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Keep Signed In", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

/**
 * Standard confirmation dialog for account deletion complying with Play Store data safety guidelines.
 */
@Composable
fun DeleteAccountConfirmationDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (!show) return
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(22.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        icon = {
            SparkingIconBox(
                icon = Icons.Default.DeleteForever,
                containerColor = MaterialTheme.colorScheme.errorContainer,
                iconTint = MaterialTheme.colorScheme.error,
                boxSize = 48.dp,
                iconSize = 24.dp,
                cornerRadius = 16.dp
            )
        },
        title = {
            Text(
                "Delete Account Permanently?",
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                "This action cannot be undone. All your student records, fees history, test scores, homework submissions, and doubts will be permanently removed.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                    onConfirm()
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete permanently", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, shape = RoundedCornerShape(12.dp)) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

/**
 * Generic action confirmation dialog (e.g. Delete Item, Remove Student, Discard Changes).
 */
@Composable
fun ConfirmActionDialog(
    show: Boolean,
    title: String,
    message: String,
    confirmLabel: String = "Confirm",
    isDestructive: Boolean = true,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (!show) return
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(22.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        icon = {
            SparkingIconBox(
                icon = Icons.Default.WarningAmber,
                containerColor = if (isDestructive) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                iconTint = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                boxSize = 48.dp,
                iconSize = 24.dp,
                cornerRadius = 16.dp
            )
        },
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp,
                color = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = message,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                    onConfirm()
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(confirmLabel, fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, shape = RoundedCornerShape(12.dp)) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

/**
 * Reusable Class Selector Chips Row (All, Class 1 ... Class 12).
 */
@Composable
fun ClassSelectorChipsRow(
    selectedClass: String,
    onClassSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    options: List<String> = AppConstants.CLASS_OPTIONS_WITH_ALL
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { cls ->
            val isSelected = cls == selectedClass
            FilterChip(
                selected = isSelected,
                onClick = { onClassSelected(cls) },
                label = {
                    Text(
                        text = cls,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryBlue,
                    selectedLabelColor = Color.White,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

/**
 * Reusable Empty State Placeholder with Human-Crafted Visual Warmth.
 */
@Composable
fun EmptyStateView(
    title: String,
    subtitle: String? = null,
    icon: ImageVector = Icons.Default.Inbox,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier.fillMaxWidth().padding(32.dp)
) {
    val finalAction = onAction ?: onActionClick
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f), RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(34.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        if (!subtitle.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        if (!actionLabel.isNullOrBlank() && finalAction != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = finalAction,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(actionLabel, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
        }
    }
}

/**
 * Required form field indicator (*)
 */
@Composable
fun RequiredFieldLabel(
    label: String,
    modifier: Modifier = Modifier,
    textColor: Color = Color.White,
    fontSize: Int = 13
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, fontWeight = FontWeight.Bold, color = textColor, fontSize = fontSize.sp)
        Text(text = " *", fontWeight = FontWeight.Bold, color = Color.Red, fontSize = fontSize.sp)
    }
}

/**
 * Standard centered loading indicator
 */
@Composable
fun CenteredLoadingIndicator(
    modifier: Modifier = Modifier.fillMaxSize(),
    color: Color = SecondaryGold,
    message: String? = null
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = color)
            if (!message.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = message, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

/**
 * Resilient class-matching utility to ensure student materials, homework, and quizzes
 * match seamlessly across standard notations (e.g. "Class 8", "Class 8th", "8th", "8").
 */
fun isClassMatch(targetClass: String?, userClass: String?): Boolean {
    val target = targetClass?.trim() ?: ""
    val user = userClass?.trim() ?: ""
    if (target.isEmpty() || user.isEmpty()) return true
    if (target.equals("All", ignoreCase = true) || target.equals("All Classes", ignoreCase = true)) return true
    if (user.equals("All", ignoreCase = true) || user.equals("All Classes", ignoreCase = true)) return true
    if (target.equals(user, ignoreCase = true)) return true

    // Extract digits if present (e.g. "Class 8" vs "8th")
    val targetDigits = Regex("\\d+").find(target)?.value
    val userDigits = Regex("\\d+").find(user)?.value
    if (targetDigits != null && userDigits != null) {
        return targetDigits == userDigits
    }

    val cleanTarget = target.lowercase().replace("class", "").replace("th", "").replace("st", "").replace("nd", "").replace("rd", "").trim()
    val cleanUser = user.lowercase().replace("class", "").replace("th", "").replace("st", "").replace("nd", "").replace("rd", "").trim()
    return cleanTarget.contains(cleanUser) || cleanUser.contains(cleanTarget)
}

/**
 * Recursively calculates file/directory storage size.
 */
fun getDirectorySize(dir: java.io.File?): Long {
    if (dir == null || !dir.exists()) return 0L
    var size = 0L
    dir.listFiles()?.forEach { file ->
        size += if (file.isDirectory) getDirectorySize(file) else file.length()
    }
    return size
}
