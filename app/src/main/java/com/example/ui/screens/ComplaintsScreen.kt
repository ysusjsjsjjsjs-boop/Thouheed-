package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContactSupport
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.WaterDamage
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.HVACComplaint
import com.example.ui.theme.HvacBluePrimary
import com.example.ui.theme.HvacThermalOrange
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusGreenBg
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusRedBg
import com.example.ui.theme.StatusYellow
import com.example.ui.theme.StatusYellowBg

@Composable
fun ComplaintsScreen(
    complaints: List<HVACComplaint>,
    onCreateComplaint: (String?, String, String, String, String, String, String) -> Unit,
    onResolveComplaint: (Long, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedStatusFilter by remember { mutableStateOf("All") }
    var resolvingComplaint by remember { mutableStateOf<HVACComplaint?>(null) }

    val filteredComplaints = remember(selectedStatusFilter, complaints) {
        when (selectedStatusFilter) {
            "Open" -> complaints.filter { it.status != "Resolved" }
            "Resolved" -> complaints.filter { it.status == "Resolved" }
            else -> complaints
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Emergency 24/7 Dispatch Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF7F1D1D), Color(0xFF991B1B))
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "24/7 HVAC Emergency Triage",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Refrigerant leak, electrical burning smell, or total system failure.",
                                    color = Color(0xFFFECACA),
                                    fontSize = 12.sp
                                )
                            }

                            Button(
                                onClick = {
                                    Toast.makeText(context, "Dialing Emergency Dispatch: 1-800-HVAC-911", Toast.LENGTH_LONG).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Call, contentDescription = null, tint = Color(0xFF991B1B), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Call 911-HVAC", color = Color(0xFF991B1B), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Ticket Filter Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Complaints & Service Tickets",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Guaranteed SLA response times tracked in real time",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Filter Chips
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("All", "Open", "Resolved").forEach { status ->
                        FilterChip(
                            selected = selectedStatusFilter == status,
                            onClick = { selectedStatusFilter = status },
                            label = { Text(status) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = HvacBluePrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Complaints List
            if (filteredComplaints.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.AssignmentTurnedIn,
                                contentDescription = null,
                                tint = StatusGreen,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No complaints found",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "All systems operating normally under optimal cooling parameters.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(filteredComplaints) { complaint ->
                    ComplaintCard(
                        complaint = complaint,
                        onResolve = { resolvingComplaint = complaint }
                    )
                }
            }
        }

        // FAB to register a new complaint
        FloatingActionButton(
            onClick = { showCreateDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_create_complaint"),
            containerColor = HvacThermalOrange,
            contentColor = Color.White
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = "Report Issue")
                Spacer(modifier = Modifier.width(6.dp))
                Text("File Ticket", fontWeight = FontWeight.Bold)
            }
        }
    }

    // New Complaint Dialog
    if (showCreateDialog) {
        CreateComplaintDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { bookingRef, cat, sev, title, desc, name, phone ->
                onCreateComplaint(bookingRef, cat, sev, title, desc, name, phone)
                showCreateDialog = false
            }
        )
    }

    // Resolve Complaint Dialog
    resolvingComplaint?.let { ticket ->
        ResolveTicketDialog(
            ticket = ticket,
            onDismiss = { resolvingComplaint = null },
            onConfirm = { notes ->
                onResolveComplaint(ticket.id, notes)
                resolvingComplaint = null
            }
        )
    }
}

@Composable
fun ComplaintCard(
    complaint: HVACComplaint,
    onResolve: () -> Unit
) {
    val isCritical = complaint.severity.contains("Critical") || complaint.severity == "High"
    val isResolved = complaint.status == "Resolved"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Ticket ID & Severity badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = complaint.ticketId,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp
                    )
                    complaint.bookingReference?.let { ref ->
                        Text(
                            text = " • $ref",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isResolved) StatusGreenBg
                            else if (isCritical) StatusRedBg
                            else StatusYellowBg
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isResolved) "RESOLVED" else complaint.severity.uppercase(),
                        color = if (isResolved) StatusGreen else if (isCritical) StatusRed else StatusYellow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = complaint.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Category: ${complaint.category}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = complaint.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )

            // SLA & Status Bar
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.HourglassTop,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Status: ${complaint.status}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = "SLA: ${complaint.responseSlaHours}h Response",
                    fontSize = 12.sp,
                    color = HvacThermalOrange,
                    fontWeight = FontWeight.Bold
                )
            }

            // Resolution Notes (if any)
            complaint.resolutionNotes?.let { notes ->
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(StatusGreenBg.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Technician Note: $notes",
                        color = Color(0xFF14532D),
                        fontSize = 12.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }

            if (!isResolved) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onResolve,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("resolve_ticket_button_${complaint.ticketId}"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Update / Resolve Ticket", color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CreateComplaintDialog(
    onDismiss: () -> Unit,
    onConfirm: (String?, String, String, String, String, String, String) -> Unit
) {
    val categories = listOf(
        "No Cooling / Lukewarm Air",
        "Strange Grinding / Squealing Noise",
        "Water Overflow / Drain Pan Clog",
        "Electrical / Breaker Tripping",
        "Thermostat Sensor Malfunction",
        "Technician Tardiness / Quality Concern"
    )

    var selectedCategory by remember { mutableStateOf(categories.first()) }
    var selectedSeverity by remember { mutableStateOf("High") }
    var title by remember { mutableStateOf("AC not cooling properly") }
    var description by remember { mutableStateOf("Unit runs continuously but indoor temperature remains above 78°F.") }
    var customerName by remember { mutableStateOf("Sarah Jenkins") }
    var contactPhone by remember { mutableStateOf("+1 (555) 728-1934") }
    var bookingRef by remember { mutableStateOf("HVAC-2026-8491") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "File Service Complaint / Ticket",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text("Issue Category", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            items(categories) { cat ->
                                FilterChip(
                                    selected = selectedCategory == cat,
                                    onClick = { selectedCategory = cat },
                                    label = { Text(cat, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = HvacBluePrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    item {
                        Text("Severity & SLA Tier", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            listOf("Critical (Emergency)", "High", "Medium", "Low").forEach { sev ->
                                FilterChip(
                                    selected = selectedSeverity == sev,
                                    onClick = { selectedSeverity = sev },
                                    label = { Text(sev, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = if (sev.contains("Critical")) StatusRed else HvacThermalOrange,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Issue Title / Headline") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Detailed Description of Issue") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = bookingRef,
                            onValueChange = { bookingRef = it },
                            label = { Text("Related Booking Reference (Optional)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onConfirm(
                            bookingRef.ifBlank { null },
                            selectedCategory,
                            selectedSeverity,
                            title,
                            description,
                            customerName,
                            contactPhone
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_complaint_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HvacThermalOrange)
                ) {
                    Text("Submit Ticket & Dispatch Alert", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ResolveTicketDialog(
    ticket: HVACComplaint,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var resolutionNotes by remember {
        mutableStateOf("Replaced dual run capacitor (45/5 uF). Refrigerant pressures verified within manufacturer subcool specs (10°F). Tested cooling delta-T at 18°F.")
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Resolve Ticket ${ticket.ticketId}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = ticket.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = resolutionNotes,
                    onValueChange = { resolutionNotes = it },
                    label = { Text("Technician Resolution Log") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = { onConfirm(resolutionNotes) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = StatusGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Mark Resolved", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
