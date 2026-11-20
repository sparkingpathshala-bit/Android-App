package com.sparkingpathshala.app.ui.guests

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sparkingpathshala.app.data.local.GuestTelemetryEntity
import com.sparkingpathshala.app.security.SecurityUtils
import com.sparkingpathshala.app.ui.dashboard.DashboardViewModel
import com.sparkingpathshala.app.ui.theme.PrimaryBlue
import com.sparkingpathshala.app.ui.theme.SecondaryGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminGuestUsersScreen(
    viewModel: DashboardViewModel,
    onNavigateBack: () -> Unit
) {
    val guestList by viewModel.guestList.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var sortByMostActive by remember { mutableStateOf(false) }
    var displayLimit by remember { mutableIntStateOf(15) }

    val filteredGuests = remember(guestList, searchQuery, sortByMostActive) {
        guestList.filter { guest ->
            searchQuery.isBlank() ||
                    guest.guestId.contains(searchQuery, ignoreCase = true) ||
                    guest.deviceName.contains(searchQuery, ignoreCase = true) ||
                    guest.location.contains(searchQuery, ignoreCase = true) ||
                    guest.ipAddress.contains(searchQuery, ignoreCase = true)
        }.let { list ->
            if (sortByMostActive) list.sortedByDescending { it.actionsCount }
            else list.sortedByDescending { it.lastActiveTimestamp }
        }
    }

    val paginatedGuests = filteredGuests.take(displayLimit)
    val hasMore = filteredGuests.size > displayLimit

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Guest User Telemetry", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("${guestList.size} total anonymous sessions", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { sortByMostActive = !sortByMostActive }) {
                        Icon(
                            imageVector = if (sortByMostActive) Icons.AutoMirrored.Filled.TrendingUp else Icons.Default.AccessTime,
                            contentDescription = "Toggle Sort",
                            tint = if (sortByMostActive) SecondaryGold else PrimaryBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // 21-day auto purge notice banner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFFFF3E0)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AutoDelete, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Guest telemetry keeps device logs for 14 days and automatically purges older sessions.",
                        fontSize = 11.sp,
                        color = Color(0xFFE65100),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Search Box
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by device, IP, or location...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            if (paginatedGuests.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No guest sessions found", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(paginatedGuests, key = { it.id }) { guest ->
                        GuestTelemetryCard(guest)
                    }

                    if (hasMore) {
                        item {
                            Button(
                                onClick = { displayLimit += 15 },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                            ) {
                                Text("Load More (${filteredGuests.size - displayLimit} remaining)")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GuestTelemetryCard(guest: GuestTelemetryEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.5.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = guest.deviceName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = SecondaryGold.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${guest.actionsCount} Actions",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryGold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "ID: ${guest.guestId}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Text(
                    text = "IP: ${guest.ipAddress}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Location: ${guest.location}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Last Active: ${SecurityUtils.formatIndianDate(guest.lastActiveTimestamp, "dd MMM, hh:mm a")}",
                    fontSize = 11.sp,
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
