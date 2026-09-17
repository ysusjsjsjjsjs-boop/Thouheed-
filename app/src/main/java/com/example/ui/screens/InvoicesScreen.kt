package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.data.model.HVACInvoice
import com.example.ui.theme.HvacBluePrimary
import com.example.ui.theme.HvacThermalOrange
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusGreenBg
import com.example.ui.theme.StatusYellow
import com.example.ui.theme.StatusYellowBg

@Composable
fun InvoicesScreen(
    invoices: List<HVACInvoice>,
    onPayInvoice: (Long, String) -> Unit,
    onGenerateCustomInvoice: (String, String, String, String, Double, Double, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedInvoiceForDetail by remember { mutableStateOf<HVACInvoice?>(null) }
    var showCreateInvoiceDialog by remember { mutableStateOf(false) }
    var filterStatus by remember { mutableStateOf("All") }

    val filteredInvoices = remember(filterStatus, invoices) {
        when (filterStatus) {
            "Paid" -> invoices.filter { it.status == "Paid" }
            "Pending" -> invoices.filter { it.status != "Paid" }
            else -> invoices
        }
    }

    val totalInvoiced = invoices.sumOf { it.totalAmount }
    val totalPaid = invoices.filter { it.status == "Paid" }.sumOf { it.totalAmount }
    val totalPending = totalInvoiced - totalPaid

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Billing Summary Metrics Card
            item {
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
                        Text(
                            text = "Automated Invoicing & Balance",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = "$${String.format("%.2f", totalPending)}",
                                    color = if (totalPending > 0) Color(0xFFFBBF24) else Color(0xFF4ADE80),
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = if (totalPending > 0) "Pending Balance Due" else "All Invoices Settled",
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 12.sp
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Total Paid: $${String.format("%.2f", totalPaid)}",
                                    color = Color(0xFF38BDF8),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${invoices.size} total invoices",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // Invoices Header & Filter
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HVAC Service Invoices",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("All", "Pending", "Paid").forEach { status ->
                        FilterChip(
                            selected = filterStatus == status,
                            onClick = { filterStatus = status },
                            label = { Text(status) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = HvacBluePrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Invoice List
            if (filteredInvoices.isEmpty()) {
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
                            Icon(Icons.Default.Receipt, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No invoices in this category", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                items(filteredInvoices) { invoice ->
                    InvoiceSummaryCard(
                        invoice = invoice,
                        onViewDetail = { selectedInvoiceForDetail = invoice },
                        onQuickPay = {
                            onPayInvoice(invoice.id, "Google Pay / Visa ****4242")
                            Toast.makeText(context, "Invoice ${invoice.invoiceNumber} paid successfully!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        // Floating Action Button to generate automated invoice
        FloatingActionButton(
            onClick = { showCreateInvoiceDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_generate_invoice"),
            containerColor = HvacBluePrimary,
            contentColor = Color.White
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Invoice")
                Spacer(modifier = Modifier.width(6.dp))
                Text("New Invoice", fontWeight = FontWeight.Bold)
            }
        }
    }

    // Detailed Invoice Dialog
    selectedInvoiceForDetail?.let { invoice ->
        InvoiceDetailDialog(
            invoice = invoice,
            onDismiss = { selectedInvoiceForDetail = null },
            onPay = {
                onPayInvoice(invoice.id, "Google Pay / Visa ****4242")
                selectedInvoiceForDetail = null
                Toast.makeText(context, "Payment confirmed for ${invoice.invoiceNumber}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Create Invoice Dialog
    if (showCreateInvoiceDialog) {
        CreateInvoiceDialog(
            onDismiss = { showCreateInvoiceDialog = false },
            onConfirm = { bookingRef, custName, custAddr, serviceType, laborHrs, partsCost, partsDesc ->
                onGenerateCustomInvoice(bookingRef, custName, custAddr, serviceType, laborHrs, partsCost, partsDesc)
                showCreateInvoiceDialog = false
                Toast.makeText(context, "Automated invoice generated!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun InvoiceSummaryCard(
    invoice: HVACInvoice,
    onViewDetail: () -> Unit,
    onQuickPay: () -> Unit
) {
    val isPaid = invoice.status == "Paid"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewDetail() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = invoice.invoiceNumber,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isPaid) StatusGreenBg else StatusYellowBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isPaid) "PAID" else "PAYMENT DUE",
                        color = if (isPaid) StatusGreen else StatusYellow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Booking: ${invoice.bookingReference}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Issued: ${invoice.serviceDate} • Due: ${invoice.dueDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "$${String.format("%.2f", invoice.totalAmount)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onViewDetail,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("view_details_${invoice.invoiceNumber}"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Itemized Breakdown", fontSize = 12.sp)
                }

                if (!isPaid) {
                    Button(
                        onClick = onQuickPay,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("pay_invoice_${invoice.invoiceNumber}"),
                        colors = ButtonDefaults.buttonColors(containerColor = StatusGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pay Now", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun InvoiceDetailDialog(
    invoice: HVACInvoice,
    onDismiss: () -> Unit,
    onPay: () -> Unit
) {
    val context = LocalContext.current
    val isPaid = invoice.status == "Paid"

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
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ARCTIC & FLAME HVAC",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = HvacBluePrimary
                        )
                        Text(
                            text = "Licensed & Bonded • TACLA-091248E",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Invoice & Customer Info
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("INVOICE TO:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                                Text(invoice.customerName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(invoice.customerAddress, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(invoice.invoiceNumber, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = HvacBluePrimary)
                                Text("Service Ref: ${invoice.bookingReference}", fontSize = 11.sp)
                                Text("Date: ${invoice.serviceDate}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }

                    // Itemized line items
                    item {
                        Text("ITEMIZED SERVICE BREAKDOWN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }

                    item {
                        LineItemRow("Diagnostic & Safety Inspection", "$${String.format("%.2f", invoice.diagnosticFee)}")
                    }

                    item {
                        val laborTotal = invoice.laborHours * invoice.laborRatePerHour
                        LineItemRow(
                            "Certified Labor (${invoice.laborHours} hrs @ $${String.format("%.0f", invoice.laborRatePerHour)}/hr)",
                            "$${String.format("%.2f", laborTotal)}"
                        )
                    }

                    item {
                        LineItemRow("Parts & Materials (${invoice.partsJson})", "$${String.format("%.2f", invoice.partsTotal)}")
                    }

                    item {
                        LineItemRow("EPA Refrigerant Environmental Fee", "$${String.format("%.2f", invoice.environmentalFee)}")
                    }

                    item { HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp)) }

                    item {
                        LineItemRow("Subtotal", "$${String.format("%.2f", invoice.subtotal)}", isBold = false)
                    }

                    item {
                        LineItemRow("Sales Tax (8.25%)", "$${String.format("%.2f", invoice.taxAmount)}", isBold = false)
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("TOTAL AMOUNT DUE", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("$${String.format("%.2f", invoice.totalAmount)}", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = HvacBluePrimary)
                        }
                    }

                    if (isPaid) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(StatusGreenBg, RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "PAID IN FULL via ${invoice.paymentMethod ?: "Google Pay"}",
                                        color = StatusGreen,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            Toast.makeText(context, "PDF Invoice Exported & Shared!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share PDF", fontSize = 12.sp)
                    }

                    if (!isPaid) {
                        Button(
                            onClick = onPay,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("modal_pay_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = StatusGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Pay Now", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LineItemRow(description: String, price: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = description,
            fontSize = 12.sp,
            color = if (isBold) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = price,
            fontSize = 12.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun CreateInvoiceDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, Double, Double, String) -> Unit
) {
    var bookingRef by remember { mutableStateOf("HVAC-2026-8491") }
    var customerName by remember { mutableStateOf("Sarah Jenkins") }
    var customerAddress by remember { mutableStateOf("742 Evergreen Terrace, Springfield") }
    var serviceType by remember { mutableStateOf("Central AC Diagnostics") }
    var laborHours by remember { mutableStateOf("1.5") }
    var partsCost by remember { mutableStateOf("95.00") }
    var partsDescription by remember { mutableStateOf("Dual Run Capacitor 45/5 uF + Contactor 30A") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Automated Invoice Wizard",
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
                        OutlinedTextField(
                            value = bookingRef,
                            onValueChange = { bookingRef = it },
                            label = { Text("Booking Reference") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { customerName = it },
                            label = { Text("Customer Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = customerAddress,
                            onValueChange = { customerAddress = it },
                            label = { Text("Service Address") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = laborHours,
                                onValueChange = { laborHours = it },
                                label = { Text("Labor Hours") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = partsCost,
                                onValueChange = { partsCost = it },
                                label = { Text("Parts Cost ($)") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = partsDescription,
                            onValueChange = { partsDescription = it },
                            label = { Text("Parts Replaced Summary") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val hours = laborHours.toDoubleOrNull() ?: 1.0
                        val parts = partsCost.toDoubleOrNull() ?: 50.0
                        onConfirm(bookingRef, customerName, customerAddress, serviceType, hours, parts, partsDescription)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("generate_invoice_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HvacBluePrimary)
                ) {
                    Text("Generate Itemized Invoice", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
