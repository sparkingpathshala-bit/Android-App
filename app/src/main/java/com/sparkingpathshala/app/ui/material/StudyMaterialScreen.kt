package com.sparkingpathshala.app.ui.material

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.sparkingpathshala.app.data.local.StudyMaterialEntity
import com.sparkingpathshala.app.data.local.isAdmin
import com.sparkingpathshala.app.data.local.isGuest
import com.sparkingpathshala.app.ui.dashboard.DashboardViewModel
import com.sparkingpathshala.app.ui.theme.PrimaryBlue
import com.sparkingpathshala.app.ui.theme.SecondaryGold
import com.sparkingpathshala.app.ui.theme.BrandGradient
import com.sparkingpathshala.app.ui.theme.RealisticButton
import com.sparkingpathshala.app.ui.theme.RealisticOutlinedButton
import com.sparkingpathshala.app.ui.theme.RealisticTactileIconButton
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyMaterialScreen(
    viewModel: DashboardViewModel,
    paddingValues: PaddingValues,
    onOpenDrawer: () -> Unit = {},
    onNavigateToUpload: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onNavigateToSignup: () -> Unit = {},
    isPremiumOnly: Boolean = false
) {
    val materials by viewModel.studyMaterials.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.syncStudyMaterials()
    }
    
    val user by viewModel.user.collectAsState()
    val studentClass = user?.classLevel ?: "Class 10"
    val isAdmin = user.isAdmin()
    val isGuest = user.isGuest()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(if (isPremiumOnly) "premium-material" else "All") }
    var selectedClass by remember(studentClass, isAdmin, isGuest) { mutableStateOf(if (isAdmin || isGuest) "All" else studentClass) }
    var selectedSubject by remember { mutableStateOf("All") }
    var selectedSortOption by remember { mutableStateOf("Latest First") }
    var filterDownloadedOnly by remember { mutableStateOf(false) }
    var showGuestGateDialog by remember { mutableStateOf(false) }
    
    // Pagination State
    var currentPage by remember { mutableIntStateOf(1) }
    val pageSize = 10

    // Reset pagination to page 1 whenever any filter or search query changes
    LaunchedEffect(searchQuery, selectedCategory, selectedClass, selectedSubject, selectedSortOption, filterDownloadedOnly) {
        currentPage = 1
    }

    val subjects = remember(materials) {
        listOf("All") + materials.map { it.subject }.distinct().sorted()
    }
    
    val filteredAndSortedMaterials = remember(materials, searchQuery, selectedCategory, selectedClass, selectedSubject, selectedSortOption, isPremiumOnly, studentClass, isAdmin, isGuest, filterDownloadedOnly) {
        val filtered = materials.filter { material ->
            val matchesSearch = searchQuery.isEmpty() || 
                material.title.contains(searchQuery, ignoreCase = true) ||
                material.description.contains(searchQuery, ignoreCase = true) ||
                material.subject.contains(searchQuery, ignoreCase = true)
            
            val matchesCategory = if (isPremiumOnly) {
                material.category.equals("premium-material", ignoreCase = true)
            } else {
                !material.category.equals("premium-material", ignoreCase = true) && (
                    selectedCategory == "All" || material.category.equals(selectedCategory, ignoreCase = true)
                )
            }
                
            val matchesClass = if (isAdmin || isGuest) {
                selectedClass == "All" || material.classLevel.equals(selectedClass, ignoreCase = true)
            } else {
                material.classLevel.equals(studentClass, ignoreCase = true) || material.classLevel.equals("All", ignoreCase = true)
            }
                
            val matchesSubject = selectedSubject == "All" ||
                material.subject.equals(selectedSubject, ignoreCase = true)
                
            val matchesDownloaded = !filterDownloadedOnly || material.isDownloaded

            matchesSearch && matchesCategory && matchesClass && matchesSubject && matchesDownloaded
        }

        when (selectedSortOption) {
            "Title (A-Z)" -> filtered.sortedBy { it.title.lowercase() }
            "Title (Z-A)" -> filtered.sortedByDescending { it.title.lowercase() }
            "Subject" -> filtered.sortedBy { it.subject.lowercase() }
            "Oldest First" -> filtered.sortedBy { it.id }
            else -> filtered.sortedByDescending { it.id }
        }
    }

    val paginatedMaterials = remember(filteredAndSortedMaterials, currentPage) {
        filteredAndSortedMaterials.take(currentPage * pageSize)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            // Banner Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BrandGradient)
                    .padding(vertical = 10.dp, horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RealisticTactileIconButton(
                            icon = Icons.Default.Menu,
                            contentDescription = "Menu",
                            onClick = onOpenDrawer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isPremiumOnly) "Premium Study Hub" else "Digital Library",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    if (isAdmin) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SecondaryGold
                        ) {
                            Text(
                                text = "ADMIN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search and Filter Bar Section
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                placeholder = { Text("Search title, subject, notes...", fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedPlaceholderColor = Color.Gray,
                    unfocusedPlaceholderColor = Color.Gray,
                    focusedBorderColor = SecondaryGold,
                    unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                )
            )
            
            if (!isPremiumOnly) {
                Spacer(modifier = Modifier.height(12.dp))
                
                // Category Selector Chips Row
                val categories = listOf("All", "Notes", "Guides", "Question Bank", "Test Series")
                val categoryIds = listOf("All", "notes", "guides", "question-bank", "test-series")
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Downloaded Toggle Chip
                    FilterChip(
                        selected = filterDownloadedOnly,
                        onClick = { filterDownloadedOnly = !filterDownloadedOnly },
                        label = { Text("Downloaded", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                        leadingIcon = {
                            Icon(
                                imageVector = if (filterDownloadedOnly) Icons.Default.CheckCircle else Icons.Default.CloudDownload,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (filterDownloadedOnly) Color.White else PrimaryBlue
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF2E7D32),
                            selectedLabelColor = Color.White,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        border = null
                    )

                    categories.forEachIndexed { index, name ->
                        val catId = categoryIds[index]
                        val isSelected = selectedCategory == catId
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = catId },
                            label = { Text(name, fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryBlue,
                                selectedLabelColor = Color.White,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = null
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Dropdown selectors for Subject and Class
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Class Selector (Locked for regular enrolled Students, Dropdown for Admins and Guests)
                if (!isAdmin && !isGuest) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = "Locked", modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = studentClass,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                } else {
                    var classExpanded by remember { mutableStateOf(false) }
                    val classes = listOf("All Classes") + (1..12).map { "Class $it" }
                    val currentClassLabel = if (selectedClass == "All") "All Classes" else selectedClass
                    
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedButton(
                            onClick = { classExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        ) {
                            Text(currentClassLabel, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 12.sp)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                        
                        DropdownMenu(
                            expanded = classExpanded,
                            onDismissRequest = { classExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.45f)
                        ) {
                            classes.forEach { classLabel ->
                                DropdownMenuItem(
                                    text = { Text(classLabel, fontSize = 14.sp) },
                                    onClick = {
                                        selectedClass = if (classLabel == "All Classes") "All" else classLabel
                                        classExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                
                // Subject Dropdown
                var subjectExpanded by remember { mutableStateOf(false) }
                val currentSubjectLabel = if (selectedSubject == "All") "All Subjects" else selectedSubject
                
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { subjectExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    ) {
                        Text(currentSubjectLabel, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 12.sp)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                    
                    DropdownMenu(
                        expanded = subjectExpanded,
                        onDismissRequest = { subjectExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.45f)
                    ) {
                        subjects.forEach { sub ->
                            val displayLabel = if (sub == "All") "All Subjects" else sub
                            DropdownMenuItem(
                                text = { Text(displayLabel, fontSize = 14.sp) },
                                onClick = {
                                    selectedSubject = sub
                                    subjectExpanded = false
                                }
                            )
                        }
                    }
                }
                
                // Sort Dropdown Selector
                var sortExpanded by remember { mutableStateOf(false) }
                val sortOptions = listOf("Latest First", "Title (A-Z)", "Subject")

                Box(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { sortExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    ) {
                        Text("Sort: $selectedSortOption", maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 12.sp)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                    }

                    DropdownMenu(
                        expanded = sortExpanded,
                        onDismissRequest = { sortExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.45f)
                    ) {
                        sortOptions.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt, fontSize = 14.sp) },
                                onClick = {
                                    selectedSortOption = opt
                                    sortExpanded = false
                                }
                            )
                        }
                    }
                }
            }

        Spacer(modifier = Modifier.height(8.dp))

        if (filteredAndSortedMaterials.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (filterDownloadedOnly) "No downloaded materials found.\nTap Download on any study material to access it offline." else "No materials available with this filter.",
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 20.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(paginatedMaterials, key = { it.id }, contentType = { "study_material" }) { material ->
                    MaterialCardItem(
                        material = material,
                        isGuest = isGuest,
                        onDownload = {
                            if (isGuest) {
                                showGuestGateDialog = true
                            } else {
                                viewModel.downloadMaterial(context, material.id) { success ->
                                    if (success) {
                                        Toast.makeText(context, "Download Complete!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Download Failed. Check network connection.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        },
                        onView = {
                            openPdfFile(context, material.localPath)
                        },
                        onDelete = {
                            viewModel.deleteDownloadedMaterial(material.id)
                            Toast.makeText(context, "File deleted locally.", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                if (paginatedMaterials.size < filteredAndSortedMaterials.size) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            OutlinedButton(
                                onClick = { currentPage = currentPage + 1 },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue)
                            ) {
                                Text("Load More (${filteredAndSortedMaterials.size - paginatedMaterials.size} remaining)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Guest Mode Download Gate Dialog
        if (showGuestGateDialog) {
            AlertDialog(
                onDismissRequest = { showGuestGateDialog = false },
                icon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(36.dp))
                },
                title = {
                    Text("Student Account Required", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                },
                text = {
                    Text(
                        "Downloading offline study materials is reserved for registered students. Please Sign Up or Log In to save files on your device.",
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                },
                confirmButton = {
                    RealisticButton(
                        text = "Sign Up",
                        onClick = {
                            showGuestGateDialog = false
                            onNavigateToSignup()
                        },
                        gradientColors = listOf(PrimaryBlue, Color(0xFF1565C0)),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                },
                dismissButton = {
                    RealisticOutlinedButton(
                        text = "Log In",
                        onClick = {
                            showGuestGateDialog = false
                            onNavigateToLogin()
                        },
                        borderColor = PrimaryBlue,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            )
        }
        }

        if (user.isAdmin()) {
            FloatingActionButton(
                onClick = onNavigateToUpload,
                containerColor = SecondaryGold,
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .padding(bottom = paddingValues.calculateBottomPadding())
            ) {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = "Upload Study Material"
                )
            }
        }
    }
}

@Composable
fun MaterialCardItem(
    material: StudyMaterialEntity,
    isGuest: Boolean = false,
    onDownload: () -> Unit,
    onView: () -> Unit,
    onDelete: () -> Unit
) {
    var isDownloading by remember { mutableStateOf(false) }
    val isActuallyDownloaded = !isGuest && material.isDownloaded

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Subject tag & premium badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFE3F2FD),
                            modifier = Modifier.wrapContentSize()
                        ) {
                            Text(
                                text = material.subject.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
 
                        if (material.category.equals("premium-material", ignoreCase = true)) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFFFF3E0),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SecondaryGold),
                                modifier = Modifier.wrapContentSize()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = SecondaryGold,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "PREMIUM",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SecondaryGold
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = material.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // Icon
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = null,
                    tint = SecondaryGold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = material.description,
                fontSize = 13.sp,
                color = Color.Gray,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Offline Status Indicator
                Text(
                    text = if (isActuallyDownloaded) "Available Offline" else if (isGuest) "Login to Download" else "Needs Download",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isActuallyDownloaded) Color(0xFF2E7D32) else Color.Gray
                )
                
                Row {
                    if (isActuallyDownloaded) {
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete File", tint = Color.Red)
                        }
                        Button(
                            onClick = onView,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open", fontSize = 12.sp)
                        }
                    } else {
                        if (isDownloading) {
                            CircularProgressIndicator(modifier = Modifier.size(28.dp), color = SecondaryGold)
                        } else {
                            Button(
                                onClick = {
                                    onDownload()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isGuest) "Download" else "Download", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

fun openPdfFile(context: Context, filePath: String) {
    val file = File(filePath)
    if (!file.exists()) {
        Toast.makeText(context, "Note file does not exist. Please re-download.", Toast.LENGTH_SHORT).show()
        return
    }
    
    val uri = FileProvider.getUriForFile(
        context,
        "com.sparkingpathshala.app.fileprovider",
        file
    )

    val mimeType = when (file.extension.lowercase()) {
        "png" -> "image/png"
        "jpg", "jpeg" -> "image/jpeg"
        "pdf" -> "application/pdf"
        "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        "doc" -> "application/msword"
        "txt" -> "text/plain"
        else -> "application/pdf"
    }
    
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, mimeType)
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    
    try {
        val chooser = Intent.createChooser(intent, "Open file with")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "No application found to open this file. Please install a compatible viewer (e.g., PDF Reader or Gallery).",
            Toast.LENGTH_LONG
        ).show()
    }
}
