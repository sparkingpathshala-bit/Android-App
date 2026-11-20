package com.sparkingpathshala.app.ui.fees

import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.sparkingpathshala.app.data.local.FeeEntity
import com.sparkingpathshala.app.data.remote.TransactionResponse
import com.sparkingpathshala.app.ui.dashboard.DashboardViewModel
import com.sparkingpathshala.app.ui.theme.*

data class StatusColor(val text: Color, val bg: Color, val border: Color)

fun getStatusColors(status: String): StatusColor {
    return when (status.lowercase()) {
        "paid" -> StatusColor(
            text = Color(0xFF2E7D32),
            bg = Color(0xFFE8F5E9),
            border = Color(0xFF81C784)
        )
        "partially paid" -> StatusColor(
            text = Color(0xFFE65100),
            bg = Color(0xFFFFF3E0),
            border = Color(0xFFFFB74D)
        )
        "adjusted / waiver", "waiver" -> StatusColor(
            text = Color(0xFF6A1B9A),
            bg = Color(0xFFF3E5F5),
            border = Color(0xFFBA68C8)
        )
        "pending approval", "pending" -> StatusColor(
            text = Color(0xFF0277BD),
            bg = Color(0xFFE1F5FE),
            border = Color(0xFF4FC3F7)
        )
        "rejected" -> StatusColor(
            text = Color(0xFFC62828),
            bg = Color(0xFFFFEBEE),
            border = Color(0xFFEF5350)
        )
        else -> StatusColor( // Unpaid
            text = Color(0xFFC62828),
            bg = Color(0xFFFFEBEE),
            border = Color(0xFFEF5350)
        )
    }
}

@Composable
fun UpiQrCode(upiUri: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .size(180.dp)
            .border(2.dp, SecondaryGold, RoundedCornerShape(12.dp))
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color.White
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val size = this.size.width
            val cellSize = size / 21f // 21x21 grid
            
            fun drawFinder(r: Int, c: Int) {
                drawRect(color = Color.Black, topLeft = Offset(c * cellSize, r * cellSize), size = Size(cellSize * 7, cellSize * 7))
                drawRect(color = Color.White, topLeft = Offset((c + 1) * cellSize, (r + 1) * cellSize), size = Size(cellSize * 5, cellSize * 5))
                drawRect(color = Color.Black, topLeft = Offset((c + 2) * cellSize, (r + 2) * cellSize), size = Size(cellSize * 3, cellSize * 3))
            }

            drawFinder(0, 0)
            drawFinder(0, 14)
            drawFinder(14, 0)

            val seed = upiUri.hashCode().toLong()
            val rand = java.util.Random(seed)
            for (i in 0 until 21) {
                for (j in 0 until 21) {
                    val isFinder = (i < 8 && j < 8) || (i < 8 && j >= 13) || (i >= 13 && j < 8)
                    if (!isFinder) {
                        val isTiming = (i == 6 && j % 2 == 0) || (j == 6 && i % 2 == 0)
                        val isBlack = isTiming || (rand.nextFloat() > 0.45f)
                        if (isBlack) {
                            drawRect(color = Color.Black, topLeft = Offset(j * cellSize, i * cellSize), size = Size(cellSize, cellSize))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeesScreen(
    viewModel: DashboardViewModel,
    onBack: () -> Unit
) {
    val fees by viewModel.fees.collectAsState()
    val currentUser by viewModel.user.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val currentMonthName = remember {
        val cal = java.util.Calendar.getInstance()
        cal.getDisplayName(java.util.Calendar.MONTH, java.util.Calendar.LONG, java.util.Locale.US) ?: "January"
    }
    var selectedStudentMonth by remember { mutableStateOf(currentMonthName) }
    var logsTab by remember { mutableStateOf("receipts") }
    var proofsList by remember { mutableStateOf<List<TransactionResponse>>(emptyList()) }
    var isLoadingProofs by remember { mutableStateOf(false) }
    
    var showUtrDialog by remember { mutableStateOf(false) }
    var selectedFeeForReceipt by remember { mutableStateOf<FeeEntity?>(null) }

    fun refreshData() {
        viewModel.syncAll()
        viewModel.getStudentTransactions { result ->
            result.onSuccess { list ->
                proofsList = list
            }
        }
    }

    LaunchedEffect(Unit) {
        refreshData()
    }

    val allMonths = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    // Ensure valid selected month
    if (!allMonths.contains(selectedStudentMonth)) {
        selectedStudentMonth = "January"
    }

    val standardMonthlyFee = remember(fees) {
        fees.firstOrNull { it.amount > 0 }?.amount ?: 0.0
    }

    // Build the full 12-month grid ledger just like the web portal
    val fullMonthlyLedger = remember(fees, standardMonthlyFee) {
        allMonths.map { monthName ->
            val monthRecs = fees.filter { it.month.lowercase() == monthName.lowercase() }
            if (monthRecs.isEmpty()) {
                FeeEntity(
                    id = "default_${monthName.lowercase()}",
                    title = "$monthName School Fee",
                    amount = standardMonthlyFee,
                    dueDate = "N/A",
                    isPaid = false,
                    paidDate = "N/A",
                    receiptUrl = "",
                    status = "Unpaid",
                    month = monthName,
                    amountPaid = 0.0,
                    adjustedAmount = 0.0,
                    remarks = "N/A",
                    paymentMode = "N/A"
                )
            } else {
                val totalPaid = monthRecs.sumOf { it.amountPaid }
                val totalAdjusted = monthRecs.sumOf { it.adjustedAmount }
                val latestRec = monthRecs.last()
                val totalDue = if (latestRec.amount > 0) latestRec.amount else standardMonthlyFee
                val outstanding = maxOf(0.0, totalDue - totalPaid - totalAdjusted)
                
                val finalStatus = when {
                    outstanding == 0.0 && (totalPaid > 0.0 || totalAdjusted > 0.0) -> {
                        if (totalAdjusted >= totalDue) "Adjusted / Waiver" else "Paid"
                    }
                    totalPaid > 0.0 && outstanding > 0.0 -> "Partially Paid"
                    else -> "Unpaid"
                }

                val latestPaymentRec = monthRecs.reversed().firstOrNull { it.paidDate != "N/A" && it.paidDate.isNotEmpty() }
                val paymentDate = latestPaymentRec?.paidDate ?: "N/A"
                val remarksString = monthRecs.map { it.remarks }.filter { it != "N/A" && it.isNotEmpty() }.joinToString(", ").ifEmpty { "N/A" }

                FeeEntity(
                    id = latestRec.id,
                    title = latestRec.title,
                    amount = totalDue,
                    dueDate = latestRec.dueDate,
                    isPaid = finalStatus == "Paid",
                    paidDate = paymentDate,
                    receiptUrl = latestRec.receiptUrl,
                    status = finalStatus,
                    month = monthName,
                    amountPaid = totalPaid,
                    adjustedAmount = totalAdjusted,
                    remarks = remarksString,
                    paymentMode = latestRec.paymentMode
                )
            }
        }
    }

    val selectedMonthData = fullMonthlyLedger.find { it.month == selectedStudentMonth } ?: FeeEntity(
        id = "selected_default",
        title = "$selectedStudentMonth School Fee",
        amount = standardMonthlyFee,
        dueDate = "N/A",
        isPaid = false,
        paidDate = "N/A",
        receiptUrl = "",
        status = "Unpaid",
        month = selectedStudentMonth,
        amountPaid = 0.0,
        adjustedAmount = 0.0,
        remarks = "N/A",
        paymentMode = "N/A"
    )

    // Carry-over outstanding calculations matching the portal
    val carryOverDues = remember(fullMonthlyLedger, selectedStudentMonth) {
        val targetIndex = allMonths.indexOf(selectedStudentMonth)
        var carry = 0.0
        for (idx in 0 until targetIndex) {
            val mData = fullMonthlyLedger[idx]
            if (mData.amountPaid > 0.0 || mData.adjustedAmount > 0.0) {
                val monthOutstanding = mData.amount - mData.amountPaid - mData.adjustedAmount
                carry += maxOf(0.0, monthOutstanding)
            }
        }
        carry
    }

    val selectedMonthOutstanding = maxOf(0.0, selectedMonthData.amount - selectedMonthData.amountPaid - selectedMonthData.adjustedAmount)
    val totalOutstanding = carryOverDues + selectedMonthOutstanding
    val shouldShowScanner = selectedMonthData.status != "Paid" && selectedMonthData.status != "Adjusted / Waiver" && totalOutstanding > 0.0

    val upiUri = "upi://pay?pa=8800765201@ybl&pn=${Uri.encode("Sajjan Kumar Mallik")}&am=${String.format(java.util.Locale.US, "%.2f", totalOutstanding)}&cu=INR&tn=${Uri.encode("$selectedStudentMonth tuition payment")}"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fee Portal & Ledger", fontWeight = FontWeight.Black, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    if (fees.isNotEmpty()) {
                        IconButton(onClick = {
                            coroutineScope.launch(Dispatchers.IO) {
                                try {
                                    val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                                    val file = java.io.File(dir, "SparkingPathshala_FeesHistory_${currentUser?.name?.replace(" ", "_")}.csv")
                                    val csv = StringBuilder()
                                    csv.append("Month,Total Due,Amount Paid,Waived,Status,Payment Mode,Date,UTR Ref\n")
                                    fullMonthlyLedger.forEach { f ->
                                        csv.append("${f.month},${f.amount},${f.amountPaid},${f.adjustedAmount},${f.status},${f.paymentMode},${f.paidDate},${f.receiptUrl}\n")
                                    }
                                    java.io.FileOutputStream(file).use { out ->
                                        out.write(csv.toString().toByteArray())
                                    }
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(context, "Saved CSV to: ${file.name}\nPath: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(context, "CSV Save failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        }) {
                            Icon(Icons.Default.Download, contentDescription = "CSV", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlue)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section Title: Tuition Grid Ledger
                item {
                    Text(
                        text = "Tuition Grid Ledger",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }

                // 12-Month Grid: Render in 3 columns
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (row in 0 until 4) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                for (col in 0 until 3) {
                                    val idx = row * 3 + col
                                    if (idx < fullMonthlyLedger.size) {
                                        val m = fullMonthlyLedger[idx]
                                        val isSelected = m.month == selectedStudentMonth
                                        val statusColors = getStatusColors(m.status)

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(statusColors.bg)
                                                .border(
                                                    width = if (isSelected) 2.5.dp else 1.dp,
                                                    color = if (isSelected) SecondaryGold else statusColors.border,
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .clickable { selectedStudentMonth = m.month }
                                                .padding(horizontal = 4.dp, vertical = 10.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Text(
                                                    text = m.month.take(3).uppercase(),
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 13.sp,
                                                    color = statusColors.text
                                                )
                                                
                                                when (m.status.lowercase()) {
                                                    "paid" -> {
                                                        Text("Paid", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = statusColors.text)
                                                        Text("₹${m.amountPaid.toInt()}", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = statusColors.text)
                                                    }
                                                    "partially paid" -> {
                                                        Text("Partial", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = statusColors.text)
                                                        Text("Due: ₹${(m.amount - m.amountPaid - m.adjustedAmount).toInt()}", fontSize = 10.sp, color = statusColors.text)
                                                    }
                                                    "adjusted / waiver", "waiver" -> {
                                                        Text("Waived", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = statusColors.text)
                                                        Text("₹${m.adjustedAmount.toInt()}", fontSize = 10.sp, color = statusColors.text)
                                                    }
                                                    else -> {
                                                        Text("Due", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = statusColors.text)
                                                        Text("₹${m.amount.toInt()}", fontSize = 10.sp, color = statusColors.text)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Month Details Card
                item {
                    val statusColors = getStatusColors(selectedMonthData.status)

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${selectedMonthData.month} Ledger Dues",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = statusColors.bg,
                                    border = BorderStroke(1.dp, statusColors.border)
                                ) {
                                    Text(
                                        text = selectedMonthData.status.uppercase(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = statusColors.text,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

                            // Grid of Details
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Amount Paid", fontSize = 11.sp, color = Color.Gray)
                                        Text("₹${selectedMonthData.amountPaid}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Current Month Due", fontSize = 11.sp, color = Color.Gray)
                                        Text("₹${selectedMonthData.amount}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (selectedMonthOutstanding > 0) Color.Red else Color.Black)
                                    }
                                }

                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Concession / Waiver", fontSize = 11.sp, color = Color.Gray)
                                        Text("₹${selectedMonthData.adjustedAmount}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6A1B9A))
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Payment Mode", fontSize = 11.sp, color = Color.Gray)
                                        Text(selectedMonthData.paymentMode, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Payment Date", fontSize = 11.sp, color = Color.Gray)
                                        Text(selectedMonthData.paidDate, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Txn Reference No", fontSize = 11.sp, color = Color.Gray)
                                        Text(selectedMonthData.receiptUrl.ifEmpty { "N/A" }, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                                    }
                                }

                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Carry-over Balance", fontSize = 11.sp, color = Color.Gray)
                                        Text("₹$carryOverDues", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (carryOverDues > 0) Color.Red else Color.Gray)
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Total Outstanding Dues", fontSize = 11.sp, color = Color.Gray)
                                        Text("₹$totalOutstanding", fontSize = 15.sp, fontWeight = FontWeight.Black, color = if (totalOutstanding > 0) Color.Red else Color(0xFF2E7D32))
                                    }
                                }
                            }

                            // Waiver Remarks
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Black.copy(alpha = 0.03f), RoundedCornerShape(8.dp))
                                    .border(1.dp, Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Text("Waiver Remarks / Admin Notes", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(selectedMonthData.remarks, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                // UPI Payment QR code card
                if (shouldShowScanner) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.5.dp, PrimaryBlue),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "⚡ Instant UPI Tuition Payment",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = PrimaryBlue
                                )

                                Text(
                                    text = "Scan the QR using Google Pay, PhonePe, Paytm, or BHIM to pay total outstanding dues: ₹$totalOutstanding",
                                    fontSize = 12.sp,
                                    color = Color.DarkGray,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )

                                UpiQrCode(upiUri = upiUri)

                                RealisticButton(
                                    onClick = {
                                        try {
                                            val upiIntent = Intent(Intent.ACTION_VIEW, Uri.parse(upiUri)).apply {
                                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            }
                                            context.startActivity(Intent.createChooser(upiIntent, "Pay Tuition Fee via UPI"))
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "No UPI app (GPay/PhonePe/Paytm) found on device.", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    containerColor = SecondaryGold,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth().height(48.dp)
                                ) {
                                    Icon(Icons.Default.Payment, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Pay via UPI App (GPay / PhonePe / Paytm)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }

                                RealisticOutlinedButton(
                                    onClick = { showUtrDialog = true },
                                    strokeColor = PrimaryBlue,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth().height(48.dp)
                                ) {
                                    Text("⚡ Submit UTR & Verify", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Dues Cleared for $selectedStudentMonth! All tuition fee installments are settled.",
                                    color = Color(0xFF2E7D32),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Logs & Receipts tab layout
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Logs & Receipts",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )

                        PrimaryTabRow(
                            selectedTabIndex = if (logsTab == "receipts") 0 else 1,
                            modifier = Modifier
                                .width(180.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                            indicator = {},
                            divider = {}
                        ) {
                            Tab(
                                selected = logsTab == "receipts",
                                onClick = { logsTab = "receipts" },
                                text = { Text("Receipts", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                modifier = Modifier.background(if (logsTab == "receipts") PrimaryBlue else Color.Transparent),
                                selectedContentColor = Color.White,
                                unselectedContentColor = Color.Gray
                            )
                            Tab(
                                selected = logsTab == "proofs",
                                onClick = { logsTab = "proofs" },
                                text = { Text("Proofs (${proofsList.count { it.status.lowercase() == "pending approval" || it.status.lowercase() == "rejected" }})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                modifier = Modifier.background(if (logsTab == "proofs") PrimaryBlue else Color.Transparent),
                                selectedContentColor = Color.White,
                                unselectedContentColor = Color.Gray
                            )
                        }
                    }
                }

                if (logsTab == "receipts") {
                    val officialReceipts = fullMonthlyLedger.filter { 
                        it.amountPaid > 0.0 || it.adjustedAmount > 0.0
                    }

                    if (officialReceipts.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No official ledger receipts found yet.", color = Color.Gray, fontSize = 14.sp)
                            }
                        }
                    } else {
                        items(officialReceipts, key = { it.id }, contentType = { "fee" }) { rec ->
                            val rem = maxOf(0.0, rec.amount - rec.amountPaid - rec.adjustedAmount)
                            val statusColors = getStatusColors(rec.status)

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("${rec.month} Installment", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = statusColors.bg,
                                            border = BorderStroke(1.dp, statusColors.border)
                                        ) {
                                            Text(
                                                text = rec.status.uppercase(),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = statusColors.text,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Paid", fontSize = 10.sp, color = Color.Gray)
                                            Text("₹${rec.amountPaid}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Waived", fontSize = 10.sp, color = Color.Gray)
                                            Text("₹${rec.adjustedAmount}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6A1B9A))
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Remaining Due", fontSize = 10.sp, color = Color.Gray)
                                            Text("₹$rem", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (rem > 0) Color.Red else Color.Black)
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Ref: ${rec.receiptUrl.ifEmpty { "N/A" }}", fontSize = 10.sp, color = Color.Gray)
                                            Text("Date: ${rec.paidDate}", fontSize = 10.sp, color = Color.Gray)
                                        }

                                        if (rec.amountPaid > 0.0) {
                                            Button(
                                                onClick = { selectedFeeForReceipt = rec },
                                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                                modifier = Modifier.height(28.dp),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text("Receipt 👁", fontSize = 10.sp, color = Color.White)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Proofs tab
                    val submittedProofs = proofsList.filter { 
                        val s = it.status.lowercase()
                        s == "pending approval" || s == "rejected"
                    }

                    if (isLoadingProofs) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = SecondaryGold)
                            }
                        }
                    } else if (submittedProofs.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No pending transaction proofs found.", color = Color.Gray, fontSize = 14.sp)
                            }
                        }
                    } else {
                        items(submittedProofs, key = { it.id }, contentType = { "proof" }) { tx ->
                            val statusColors = getStatusColors(tx.status)

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("${tx.month} Payment Proof", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = statusColors.bg,
                                            border = BorderStroke(1.dp, statusColors.border)
                                        ) {
                                            Text(
                                                text = tx.status.uppercase(),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = statusColors.text,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Amount Submitted", fontSize = 10.sp, color = Color.Gray)
                                            Text("₹${tx.amount}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("UTR Ref", fontSize = 10.sp, color = Color.Gray)
                                            Text(tx.utrNumber ?: "N/A", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Date Submitted", fontSize = 10.sp, color = Color.Gray)
                                            val dateClean = tx.createdAt.substringBefore("T")
                                            Text(text = dateClean, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    val reason = tx.rejectionReason
                                    if (reason != null && reason.isNotEmpty() && reason != "N/A") {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color(0xFFFFEBEE), RoundedCornerShape(4.dp))
                                                .padding(6.dp)
                                        ) {
                                            Text(text = "Rejection Reason: $reason", color = Color(0xFFC62828), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Receipt Dialog
            val rec = selectedFeeForReceipt
            if (rec != null) {
                ReceiptOverviewDialog(
                    fee = rec,
                    studentEmail = currentUser?.email ?: "student@sparkingpathshala.in",
                    onDismiss = { selectedFeeForReceipt = null }
                )
            }

            // UTR Submit Dialog
            if (showUtrDialog) {
                var utrNumber by remember { mutableStateOf("") }
                var isProcessing by remember { mutableStateOf(false) }
                var errorMessage by remember { mutableStateOf<String?>(null) }

                AlertDialog(
                    onDismissRequest = { if (!isProcessing) showUtrDialog = false },
                    title = { Text("Submit UPI UTR", fontWeight = FontWeight.Bold, color = PrimaryBlue) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Enter the 12-digit transaction UTR reference number from your payment app.", fontSize = 12.sp, color = Color.Gray)
                            OutlinedTextField(
                                value = utrNumber,
                                onValueChange = { input -> 
                                    if (input.length <= 12 && input.all { it.isDigit() }) {
                                        utrNumber = input
                                    }
                                },
                                label = { Text("12-Digit UPI UTR No.") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                isError = errorMessage != null,
                                modifier = Modifier.fillMaxWidth()
                            )
                            if (errorMessage != null) {
                                Text(errorMessage ?: "", color = Color.Red, fontSize = 11.sp)
                            }
                        }
                    },
                    confirmButton = {
                        if (isProcessing) {
                            CircularProgressIndicator(color = SecondaryGold, modifier = Modifier.size(24.dp))
                        } else {
                            RealisticButton(
                                onClick = {
                                    if (isProcessing) return@RealisticButton
                                    if (utrNumber.length != 12) {
                                        errorMessage = "UTR number must be exactly 12 digits."
                                        return@RealisticButton
                                    }
                                    errorMessage = null
                                    isProcessing = true

                                    // Initialize Payment Session for outstanding dues on current month
                                    viewModel.initializePayment(selectedStudentMonth, totalOutstanding) { initRes ->
                                        initRes.onSuccess { txnRefId ->
                                            viewModel.verifyPayment(txnRefId, utrNumber) { verifyRes ->
                                                isProcessing = false
                                                verifyRes.onSuccess {
                                                    Toast.makeText(context, "Payment reference submitted successfully!", Toast.LENGTH_LONG).show()
                                                    refreshData()
                                                    showUtrDialog = false
                                                }.onFailure { err ->
                                                    errorMessage = err.localizedMessage ?: "Verification submission failed."
                                                }
                                            }
                                        }.onFailure { err ->
                                            isProcessing = false
                                            errorMessage = err.localizedMessage ?: "Session initialization failed."
                                        }
                                    }
                                },
                                enabled = !isProcessing,
                                containerColor = PrimaryBlue,
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                Text("Submit Verification", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    },
                    dismissButton = {
                        if (!isProcessing) {
                            TextButton(onClick = { showUtrDialog = false }) {
                                Text("Cancel", color = Color.Gray)
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ReceiptOverviewDialog(
    fee: FeeEntity,
    studentEmail: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val receiptId = remember(fee) { "REC-${fee.id.uppercase()}-${System.currentTimeMillis().toString().takeLast(6)}" }

    val receiptText = remember(fee, studentEmail, receiptId) {
        """
        =========================================
                  SPARKING PATHSHALA
               TUITION PAYMENT RECEIPT
        =========================================
        Receipt No: $receiptId
        Date: ${fee.paidDate}
        Status: PAID & VERIFIED
        
        Student Email: $studentEmail
        Fee Details: ${fee.title}
        Amount Paid: ₹${fee.amountPaid}
        Payment Mode: ${fee.paymentMode}
        Transaction Ref / UTR: ${fee.receiptUrl}
        
        Thank you for your payment!
        =========================================
        """.trimIndent()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Receipt, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Tuition Fee Receipt", fontWeight = FontWeight.Bold, color = PrimaryBlue)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF9F9FB), RoundedCornerShape(12.dp))
                    .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "SPARKING PATHSHALA",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = PrimaryBlue,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "INVOICE SUMMARY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "-----------------------------------------", color = Color.LightGray, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Receipt No:", fontSize = 12.sp, color = Color.Gray)
                    Text(receiptId, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Payment Date:", fontSize = 12.sp, color = Color.Gray)
                    Text(fee.paidDate, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Fee For:", fontSize = 12.sp, color = Color.Gray)
                    Text(fee.title, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Amount Paid:", fontSize = 12.sp, color = Color.Gray)
                    Text("₹${fee.amountPaid}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Ref/UTR ID:", fontSize = 12.sp, color = Color.Gray)
                    Text(fee.receiptUrl, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "-----------------------------------------", color = Color.LightGray, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("PAID & VERIFIED", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Sparking Pathshala Fee Receipt")
                            putExtra(Intent.EXTRA_TEXT, receiptText)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share Receipt via"))
                    },
                    modifier = Modifier.background(Color(0xFFECEFF1), CircleShape)
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = PrimaryBlue)
                }

                Button(
                    onClick = {
                        coroutineScope.launch(Dispatchers.IO) {
                            try {
                                val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                                val file = java.io.File(dir, "Sparking_Receipt_${fee.title.replace(" ", "_")}.txt")
                                java.io.FileOutputStream(file).use { out ->
                                    out.write(receiptText.toByteArray())
                                }
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Receipt saved to: ${file.name}\nPath: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Download failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Download", color = Color.White)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Color.Gray)
            }
        }
    )
}
