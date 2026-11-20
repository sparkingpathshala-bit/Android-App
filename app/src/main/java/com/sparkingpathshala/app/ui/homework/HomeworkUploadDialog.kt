package com.sparkingpathshala.app.ui.homework

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sparkingpathshala.app.data.local.HomeworkEntity
import com.sparkingpathshala.app.ui.dashboard.DashboardViewModel
import com.sparkingpathshala.app.ui.theme.PrimaryBlue
import com.sparkingpathshala.app.ui.theme.SecondaryGold
import com.sparkingpathshala.app.utils.AppConstants
import com.sparkingpathshala.app.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Reusable Homework & Assignment Upload / Edit Dialog.
 * Used by both AdminHomeworkScreen and HomeworkScreen to eliminate code duplication.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeworkUploadDialog(
    editingHomework: HomeworkEntity?,
    viewModel: DashboardViewModel,
    onDismiss: () -> Unit,
    onSuccess: (String) -> Unit
) {
    val context = LocalContext.current
    val isEditing = editingHomework != null
    var contentType by remember {
        mutableStateOf(if (editingHomework?.title?.startsWith("[Assignment]", ignoreCase = true) == true) "Assignment" else "Homework")
    }
    var title by remember {
        mutableStateOf(editingHomework?.title?.removePrefix("[Assignment]")?.removePrefix("[Homework]")?.trim() ?: "")
    }
    var selectedClass by remember {
        mutableStateOf(editingHomework?.classLevel ?: AppConstants.DEFAULT_CLASS)
    }
    var selectedSubject by remember {
        mutableStateOf(editingHomework?.subject ?: "Maths")
    }
    var topic by remember { mutableStateOf(editingHomework?.topic ?: "") }
    var batch by remember { mutableStateOf(editingHomework?.batch ?: "") }

    var attachedFileName by remember { mutableStateOf<String?>(null) }
    var attachedFileData by remember { mutableStateOf<String?>(null) }
    var attachedFileType by remember { mutableStateOf("PDF") }
    var isUploading by remember { mutableStateOf(false) }
    val dialogScope = rememberCoroutineScope()

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            dialogScope.launch(Dispatchers.IO) {
                try {
                    val sizeMB = FileUtils.getFileSizeMB(context, it)
                    if (sizeMB > AppConstants.MAX_FILE_SIZE_MB) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "File size (${String.format(java.util.Locale.US, "%.2f", sizeMB)}MB) exceeds ${AppConstants.MAX_FILE_SIZE_MB.toInt()}MB limit.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        val name = FileUtils.getUriFileName(context, it)
                        val fileType = when {
                            name.endsWith(".pdf", ignoreCase = true) -> "PDF"
                            name.endsWith(".docx", ignoreCase = true) || name.endsWith(".doc", ignoreCase = true) -> "DOCX"
                            name.endsWith(".jpg", ignoreCase = true) || name.endsWith(".png", ignoreCase = true) || name.endsWith(".jpeg", ignoreCase = true) -> "IMAGE"
                            else -> "TEXT"
                        }
                        val base64Res = FileUtils.readUriToBase64(context, it)
                        base64Res.onSuccess { pair ->
                            withContext(Dispatchers.Main) {
                                attachedFileName = pair.first
                                attachedFileType = fileType
                                attachedFileData = pair.second
                            }
                        }.onFailure { err ->
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Error reading file: ${err.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Error reading file: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (isEditing) "Edit $contentType" else "New $contentType", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Select Type *", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf("Homework", "Assignment").forEach { type ->
                        FilterChip(
                            selected = contentType == type,
                            onClick = { contentType = type },
                            label = { Text(type, fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                            leadingIcon = {
                                if (contentType == type) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (type == "Assignment") SecondaryGold.copy(alpha = 0.2f) else PrimaryBlue.copy(alpha = 0.2f),
                                selectedLabelColor = if (type == "Assignment") SecondaryGold else PrimaryBlue
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text("Class (Mandatory) *", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                var classExpanded by remember { mutableStateOf(false) }
                Box {
                    OutlinedButton(
                        onClick = { classExpanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(selectedClass)
                    }
                    DropdownMenu(expanded = classExpanded, onDismissRequest = { classExpanded = false }) {
                        AppConstants.CLASS_OPTIONS_WITH_ALL.forEach { cls ->
                            DropdownMenuItem(text = { Text(cls) }, onClick = { selectedClass = cls; classExpanded = false })
                        }
                    }
                }

                Text("Subject (Mandatory) *", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                var subjExpanded by remember { mutableStateOf(false) }
                Box {
                    OutlinedButton(
                        onClick = { subjExpanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(selectedSubject)
                    }
                    DropdownMenu(expanded = subjExpanded, onDismissRequest = { subjExpanded = false }) {
                        AppConstants.ALL_SUBJECTS.forEach { s ->
                            DropdownMenuItem(text = { Text(s) }, onClick = { selectedSubject = s; subjExpanded = false })
                        }
                    }
                }

                OutlinedTextField(
                    value = topic,
                    onValueChange = { topic = it },
                    label = { Text("Topic (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = batch,
                    onValueChange = { batch = it },
                    label = { Text("Batch / Section (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (!isEditing) {
                    Button(
                        onClick = { filePickerLauncher.launch("*/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue.copy(alpha = 0.15f), contentColor = PrimaryBlue),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AttachFile, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (attachedFileName != null) "Attached: $attachedFileName" else "Select File (PDF, DOCX, Image, Text)")
                    }
                    Text("Max upload size: ${AppConstants.MAX_FILE_SIZE_MB.toInt()}MB", fontSize = 10.sp, color = Color.Gray)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank() || selectedClass.isBlank() || selectedSubject.isBlank()) {
                        Toast.makeText(context, "Please fill in Title, Class, and Subject.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val formattedTitle = "[$contentType] ${title.trim()}"
                    isUploading = true
                    if (isEditing) {
                        editingHomework?.let { existing ->
                            viewModel.updateHomework(
                                id = existing.id,
                                title = formattedTitle,
                                description = contentType,
                                subject = selectedSubject,
                                classLevel = selectedClass,
                                topic = topic.ifBlank { null },
                                batch = batch.ifBlank { null },
                                dueDate = "",
                                maxMarks = 100
                            ) { res ->
                                isUploading = false
                                if (res.isSuccess) {
                                    onSuccess("$contentType updated!")
                                } else {
                                    Toast.makeText(context, "Update failed: ${res.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } else {
                        viewModel.uploadHomework(
                            title = formattedTitle,
                            description = contentType,
                            subject = selectedSubject,
                            classLevel = selectedClass,
                            topic = topic.ifBlank { null },
                            batch = batch.ifBlank { null },
                            dueDate = "",
                            maxMarks = 100,
                            fileName = attachedFileName ?: "assignment.txt",
                            fileData = attachedFileData ?: "",
                            fileType = attachedFileType
                        ) { res ->
                            isUploading = false
                            if (res.isSuccess) {
                                onSuccess("$contentType published!")
                            } else {
                                Toast.makeText(context, "Upload failed: ${res.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                enabled = !isUploading,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                if (isUploading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                } else {
                    Text(if (isEditing) "Save Changes" else "Publish", color = Color.White)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isUploading) {
                Text("Cancel")
            }
        }
    )
}
