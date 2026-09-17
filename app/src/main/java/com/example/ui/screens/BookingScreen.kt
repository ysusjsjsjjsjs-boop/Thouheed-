package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.HVACBooking
import com.example.data.model.HVACEquipment
import com.example.ui.theme.HvacBluePrimary
import com.example.ui.theme.HvacBluePrimaryDark
import com.example.ui.theme.HvacThermalOrange
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusGreenBg
import com.example.ui.theme.StatusYellow
import com.example.ui.theme.StatusYellowBg

data class HVACServiceOption(
    val title: String,
    val category: String,
    val icon: ImageVector,
    val basePrice: Double,
    val durationDesc: String,
    val description: String
)

val serviceCatalog = listOf(
    HVACServiceOption(
        title = "Central AC Diagnostic & Repair",
        category = "Cooling",
        icon = Icons.Default.AcUnit,
        basePrice = 89.00,
        durationDesc = "60 - 90 min",
        description = "Full manifold pressure check, electrical capacitor and contactor tests, compressor amp draw."
    ),
    HVACServiceOption(
        title = "Heat Pump Seasonal Tune-Up",
        category = "Hybrid",
        icon = Icons.Default.Whatshot,
        basePrice = 79.00,
        durationDesc = "45 - 60 min",
        description = "Reversing valve test, outdoor coil chemical wash, defrost control verification, and airflow test."
    ),
    HVACServiceOption(
        title = "Emergency Refrigerant Leak & Recharge",
        category = "Urgent",
        icon = Icons.Default.Build,
        basePrice = 149.00,
        durationDesc = "1 - 2 hrs",
        description = "Electronic halogen sniff test, nitrogen leak localization, brazing seal, and virgin R-410A charge."
    ),
    HVACServiceOption(
        title = "Air Duct Sanitization & Balancing",
        category = "Air Quality",
        icon = Icons.Default.Air,
        basePrice = 119.00,
        durationDesc = "90 - 120 min",
        description = "HEPA negative pressure extraction, anti-microbial fogging, and CFM register airflow balancing."
    ),
    HVACServiceOption(
        title = "Smart Thermostat Installation",
        category = "Controls",
        icon = Icons.Default.DeviceThermostat,
        basePrice = 69.00,
        durationDesc = "30 - 45 min",
        description = "C-wire power adapter retrofit, smart Wi-Fi integration (Ecobee/Nest/Honeywell), and multi-stage testing."
    )
)

@Composable
fun BookingScreen(
    bookings: List<HVACBooking>,
    equipment: List<HVACEquipment>,
    onBookService: (String, String, String, String, String, String, String, String, String, Double) -> Unit,
    onNavigateToTracking: () -> Unit,
    onNavigateToInvoices: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showBookingDialog by remember { mutableStateOf(false) }
    var selectedServiceForBooking by remember { mutableStateOf(serviceCatalog.first()) }
    var selectedFilterCategory by remember { mutableStateOf("All") }

    val filteredServices = remember(selectedFilterCategory) {
        if (selectedFilterCategory == "All") serviceCatalog
        else serviceCatalog.filter { it.category == selectedFilterCategory }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Climate Status & Active Maintenance Banner
            item {
                EquipmentStatusCard(equipment = equipment)
            }

            // Quick Action Dispatch Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "HVAC Services Catalog",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Certified technicians ready for on-demand dispatch",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Service Category Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val categories = listOf("All", "Cooling", "Hybrid", "Urgent", "Air Quality", "Controls")
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedFilterCategory == cat,
                            onClick = { selectedFilterCategory = cat },
                            label = { Text(cat) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = HvacBluePrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Service Catalog Items
            items(filteredServices) { service ->
                ServiceCatalogCard(
                    service = service,
                    onBookClick = {
                        selectedServiceForBooking = service
                        showBookingDialog = true
                    }
                )
            }

            // Bookings Section Header
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Service Bookings (${bookings.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Active or Past Bookings
            if (bookings.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Build,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No active HVAC appointments",
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Tap any service above or the '+' button to schedule certified service.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(bookings) { booking ->
                    BookingItemCard(
                        booking = booking,
                        onTrackTechnician = onNavigateToTracking,
                        onViewInvoice = onNavigateToInvoices
                    )
                }
            }
        }

        // Floating Action Button to book service
        FloatingActionButton(
            onClick = { showBookingDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_book_service"),
            containerColor = HvacBluePrimary,
            contentColor = Color.White
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = "Book Service")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Book Service", fontWeight = FontWeight.Bold)
            }
        }
    }

    // Booking Creation Dialog
    if (showBookingDialog) {
        BookingFormDialog(
            initialService = selectedServiceForBooking,
            onDismiss = { showBookingDialog = false },
            onConfirm = { serviceTitle, unitType, date, timeSlot, name, phone, address, issue, urgency, estCost ->
                onBookService(serviceTitle, unitType, date, timeSlot, name, phone, address, issue, urgency, estCost)
                showBookingDialog = false
            }
        )
    }
}

@Composable
fun EquipmentStatusCard(equipment: List<HVACEquipment>) {
    val primaryUnit = equipment.firstOrNull() ?: return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                    )
                )
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFF38BDF8).copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AcUnit,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = primaryUnit.unitNickname,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "${primaryUnit.brand} • ${primaryUnit.tonnage}",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }
                }

                // Filter life pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (primaryUnit.filterLifePercent < 50) Color(0xFFEF4444).copy(alpha = 0.25f)
                            else Color(0xFF22C55E).copy(alpha = 0.25f)
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "Filter: ${primaryUnit.filterLifePercent}%",
                        color = if (primaryUnit.filterLifePercent < 50) Color(0xFFFCA5A5) else Color(0xFF86EFAC),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Refrigerant", color = Color(0xFF64748B), fontSize = 11.sp)
                    Text(primaryUnit.refrigerantType, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
                Column {
                    Text("Last Serviced", color = Color(0xFF64748B), fontSize = 11.sp)
                    Text(primaryUnit.lastServiceDate, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
                Column {
                    Text("Next Tune-Up", color = Color(0xFF64748B), fontSize = 11.sp)
                    Text(primaryUnit.nextTuneUpDue, color = Color(0xFF38BDF8), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ServiceCatalogCard(
    service: HVACServiceOption,
    onBookClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onBookClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            service.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = service.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "⏱ Est. ${service.durationDesc}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = "$${String.format("%.0f", service.basePrice)}",
                    style = MaterialTheme.typography.titleLarge,
                    color = HvacBluePrimary,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = service.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onBookClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("book_button_${service.category.lowercase()}"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Schedule This Service",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun BookingItemCard(
    booking: HVACBooking,
    onTrackTechnician: () -> Unit,
    onViewInvoice: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Status and Reference Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = booking.bookingReference,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 13.sp
                )

                val isDispatched = booking.status == "Technician Dispatched"
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDispatched) StatusGreenBg else StatusYellowBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = booking.status.uppercase(),
                        color = if (isDispatched) StatusGreen else StatusYellow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = booking.serviceTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Unit: ${booking.unitType}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${booking.scheduledDate} • ${booking.scheduledTimeSlot}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = booking.serviceAddress,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (booking.status == "Technician Dispatched" || booking.status == "In Progress") {
                    Button(
                        onClick = onTrackTechnician,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("track_tech_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = HvacBluePrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Track Tech", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = onViewInvoice,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("view_invoice_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Invoices", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun BookingFormDialog(
    initialService: HVACServiceOption,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String, String, String, String, String, Double) -> Unit
) {
    var selectedService by remember { mutableStateOf(initialService.title) }
    var unitType by remember { mutableStateOf("Central Split System (3.5 Ton)") }
    var scheduledDate by remember { mutableStateOf("Tomorrow (Sep 19, 2026)") }
    var scheduledTimeSlot by remember { mutableStateOf("09:00 AM - 11:00 AM") }
    var customerName by remember { mutableStateOf("Sarah Jenkins") }
    var customerPhone by remember { mutableStateOf("+1 (555) 728-1934") }
    var serviceAddress by remember { mutableStateOf("742 Evergreen Terrace, Springfield") }
    var issueDescription by remember { mutableStateOf("AC struggling to maintain 72°F during afternoon heat.") }
    var urgencyLevel by remember { mutableStateOf("Priority (Same Day)") }

    val baseCost = when (urgencyLevel) {
        "Emergency" -> 220.0
        "Priority (Same Day)" -> 149.0
        else -> 89.0
    }

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
                        text = "Schedule HVAC Service",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = selectedService,
                            onValueChange = { selectedService = it },
                            label = { Text("Service Title") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = unitType,
                            onValueChange = { unitType = it },
                            label = { Text("HVAC Equipment Type") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = scheduledDate,
                                onValueChange = { scheduledDate = it },
                                label = { Text("Date") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = scheduledTimeSlot,
                                onValueChange = { scheduledTimeSlot = it },
                                label = { Text("Time Slot") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Text(
                            text = "Urgency & Dispatch Tier",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Standard", "Priority (Same Day)", "Emergency").forEach { tier ->
                                FilterChip(
                                    selected = urgencyLevel == tier,
                                    onClick = { urgencyLevel = tier },
                                    label = { Text(tier, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = if (tier.contains("Emergency")) HvacThermalOrange else HvacBluePrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = serviceAddress,
                            onValueChange = { serviceAddress = it },
                            label = { Text("Service Address") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = issueDescription,
                            onValueChange = { issueDescription = it },
                            label = { Text("Issue / Symptoms Observed") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Estimated Diagnostic & Dispatch", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                    Text("Parts & labor confirmed on-site", fontSize = 10.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                                }
                                Text(
                                    text = "$${String.format("%.2f", baseCost)}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onConfirm(
                            selectedService,
                            unitType,
                            scheduledDate,
                            scheduledTimeSlot,
                            customerName,
                            customerPhone,
                            serviceAddress,
                            issueDescription,
                            urgencyLevel,
                            baseCost
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("confirm_booking_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HvacBluePrimary)
                ) {
                    Text("Confirm Booking & Dispatch", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}
