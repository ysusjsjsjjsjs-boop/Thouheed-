package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Hvac
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.HVACViewModel
import com.example.ui.screens.BookingScreen
import com.example.ui.screens.ComplaintsScreen
import com.example.ui.screens.EquipmentScreen
import com.example.ui.screens.InvoicesScreen
import com.example.ui.screens.TrackingScreen
import com.example.ui.theme.HvacBluePrimary
import com.example.ui.theme.HvacThermalOrange
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.StatusGreen

enum class HVACNavigationTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    SERVICES("Services", Icons.Default.Build),
    TRACKING("Live GPS", Icons.Default.Navigation),
    COMPLAINTS("Tickets", Icons.Default.ReportProblem),
    INVOICES("Invoices", Icons.Default.ReceiptLong),
    EQUIPMENT("Systems", Icons.Default.AcUnit)
}

class MainActivity : ComponentActivity() {

    private val viewModel: HVACViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                HVACMainApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HVACMainApp(viewModel: HVACViewModel) {
    var currentTab by remember { mutableStateOf(HVACNavigationTab.SERVICES) }

    val bookings by viewModel.bookings.collectAsStateWithLifecycle()
    val activeBooking by viewModel.activeBooking.collectAsStateWithLifecycle()
    val complaints by viewModel.complaints.collectAsStateWithLifecycle()
    val invoices by viewModel.invoices.collectAsStateWithLifecycle()
    val equipment by viewModel.equipment.collectAsStateWithLifecycle()
    val trackingState by viewModel.trackingState.collectAsStateWithLifecycle()
    val isSimulatingMovement by viewModel.isSimulatingMovement.collectAsStateWithLifecycle()

    val openTicketsCount = complaints.count { it.status != "Resolved" }
    val pendingInvoicesCount = invoices.count { it.status != "Paid" }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(HvacBluePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.AcUnit,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "HVAC Pro Services",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    // Quick indicator showing live tracking status
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(HvacBluePrimary.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(StatusGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "TECH EN ROUTE",
                                color = HvacBluePrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_nav_bar"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                HVACNavigationTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            when (tab) {
                                HVACNavigationTab.COMPLAINTS -> {
                                    if (openTicketsCount > 0) {
                                        BadgedBox(
                                            badge = {
                                                Badge(containerColor = HvacThermalOrange) {
                                                    Text(openTicketsCount.toString())
                                                }
                                            }
                                        ) {
                                            Icon(tab.icon, contentDescription = tab.label)
                                        }
                                    } else {
                                        Icon(tab.icon, contentDescription = tab.label)
                                    }
                                }
                                HVACNavigationTab.INVOICES -> {
                                    if (pendingInvoicesCount > 0) {
                                        BadgedBox(
                                            badge = {
                                                Badge(containerColor = HvacBluePrimary) {
                                                    Text(pendingInvoicesCount.toString())
                                                }
                                            }
                                        ) {
                                            Icon(tab.icon, contentDescription = tab.label)
                                        }
                                    } else {
                                        Icon(tab.icon, contentDescription = tab.label)
                                    }
                                }
                                else -> Icon(tab.icon, contentDescription = tab.label)
                            }
                        },
                        label = {
                            Text(
                                text = tab.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = HvacBluePrimary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentTab, label = "tab_crossfade") { tab ->
                when (tab) {
                    HVACNavigationTab.SERVICES -> {
                        BookingScreen(
                            bookings = bookings,
                            equipment = equipment,
                            onBookService = { title, unit, date, time, name, phone, addr, desc, urgency, estCost ->
                                viewModel.createBooking(title, unit, date, time, name, phone, addr, desc, urgency, estCost)
                            },
                            onNavigateToTracking = { currentTab = HVACNavigationTab.TRACKING },
                            onNavigateToInvoices = { currentTab = HVACNavigationTab.INVOICES }
                        )
                    }

                    HVACNavigationTab.TRACKING -> {
                        TrackingScreen(
                            trackingState = trackingState,
                            isSimulatingMovement = isSimulatingMovement,
                            activeBooking = activeBooking,
                            onToggleSimulation = { viewModel.toggleTrackingSimulation() },
                            onSetProgress = { viewModel.setTrackingProgress(it) },
                            onResetTechnician = { viewModel.resetTechnicianLocation() }
                        )
                    }

                    HVACNavigationTab.COMPLAINTS -> {
                        ComplaintsScreen(
                            complaints = complaints,
                            onCreateComplaint = { ref, cat, sev, title, desc, name, phone ->
                                viewModel.createComplaint(ref, cat, sev, title, desc, name, phone)
                            },
                            onResolveComplaint = { id, notes ->
                                viewModel.resolveComplaint(id, notes)
                            }
                        )
                    }

                    HVACNavigationTab.INVOICES -> {
                        InvoicesScreen(
                            invoices = invoices,
                            onPayInvoice = { id, method ->
                                viewModel.payInvoice(id, method)
                            },
                            onGenerateCustomInvoice = { ref, name, addr, type, hours, parts, desc ->
                                viewModel.generateAutomatedInvoice(ref, name, addr, type, hours, parts, desc)
                            }
                        )
                    }

                    HVACNavigationTab.EQUIPMENT -> {
                        EquipmentScreen(
                            equipmentList = equipment,
                            onBookMaintenance = { serviceTitle ->
                                currentTab = HVACNavigationTab.SERVICES
                            }
                        )
                    }
                }
            }
        }
    }
}

