package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.HVACRepository
import com.example.data.model.HVACBooking
import com.example.data.model.HVACComplaint
import com.example.data.model.HVACEquipment
import com.example.data.model.HVACInvoice
import com.example.data.model.TechnicianTrackingState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HVACViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HVACRepository

    val bookings: StateFlow<List<HVACBooking>>
    val activeBooking: StateFlow<HVACBooking?>
    val complaints: StateFlow<List<HVACComplaint>>
    val invoices: StateFlow<List<HVACInvoice>>
    val equipment: StateFlow<List<HVACEquipment>>

    private val _trackingState = MutableStateFlow(TechnicianTrackingState())
    val trackingState: StateFlow<TechnicianTrackingState> = _trackingState.asStateFlow()

    private val _isSimulatingMovement = MutableStateFlow(true)
    val isSimulatingMovement: StateFlow<Boolean> = _isSimulatingMovement.asStateFlow()

    // Street milestones for tracking realism
    private val streetWaypoints = listOf(
        Pair(0.05f, "Departing HVAC Regional Depot #4"),
        Pair(0.20f, "Merging onto Expressway 101 North"),
        Pair(0.38f, "Passing Industrial Center Interchange"),
        Pair(0.55f, "Turning onto Oakridge Boulevard"),
        Pair(0.72f, "Entering Maple Crest Residential Sector"),
        Pair(0.88f, "Turning onto Elm Street (2 blocks away)"),
        Pair(1.00f, "Arrived at Customer Driveway (742 Evergreen)")
    )

    init {
        val db = AppDatabase.getInstance(application)
        repository = HVACRepository(db.hvacDao())

        bookings = repository.allBookings.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        activeBooking = repository.activeBooking.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

        complaints = repository.allComplaints.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        invoices = repository.allInvoices.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        equipment = repository.allEquipment.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        // Start live GPS tracking background simulation loop
        startLiveTrackingLoop()
    }

    private fun startLiveTrackingLoop() {
        viewModelScope.launch {
            while (true) {
                delay(2000)
                if (_isSimulatingMovement.value) {
                    _trackingState.update { current ->
                        var nextFraction = current.progressFraction + 0.025f
                        if (nextFraction > 1.0f) {
                            nextFraction = 0.05f // Loop simulation smoothly
                        }

                        val distanceLeft = (5.2f * (1.0f - nextFraction)).coerceAtLeast(0.0f)
                        val eta = (15 * (1.0f - nextFraction)).toInt().coerceAtLeast(1)

                        val currentStreetDesc = streetWaypoints.firstOrNull { it.first >= nextFraction }?.second
                            ?: "Approaching destination"

                        val statusText = if (nextFraction >= 0.98f) {
                            "Arrived on Location"
                        } else if (nextFraction > 0.05f) {
                            "En Route - Live Van Tracking"
                        } else {
                            "Dispatched from Regional Hub"
                        }

                        current.copy(
                            progressFraction = nextFraction,
                            distanceMiles = (Math.round(distanceLeft * 10f) / 10f),
                            etaMinutes = if (nextFraction >= 0.98f) 0 else eta,
                            currentStreet = currentStreetDesc,
                            currentStatus = statusText
                        )
                    }
                }
            }
        }
    }

    fun toggleTrackingSimulation() {
        _isSimulatingMovement.update { !it }
    }

    fun setTrackingProgress(fraction: Float) {
        val clamped = fraction.coerceIn(0f, 1f)
        val distanceLeft = (5.2f * (1.0f - clamped)).coerceAtLeast(0.0f)
        val eta = (15 * (1.0f - clamped)).toInt().coerceAtLeast(0)
        val currentStreetDesc = streetWaypoints.firstOrNull { it.first >= clamped }?.second
            ?: "Approaching destination"

        _trackingState.update {
            it.copy(
                progressFraction = clamped,
                distanceMiles = (Math.round(distanceLeft * 10f) / 10f),
                etaMinutes = eta,
                currentStreet = currentStreetDesc,
                currentStatus = if (clamped >= 0.98f) "Arrived on Location" else "En Route - Live Van Tracking"
            )
        }
    }

    fun resetTechnicianLocation() {
        setTrackingProgress(0.08f)
    }

    // Service Booking
    fun createBooking(
        serviceTitle: String,
        unitType: String,
        scheduledDate: String,
        scheduledTimeSlot: String,
        customerName: String,
        customerPhone: String,
        serviceAddress: String,
        issueDescription: String,
        urgencyLevel: String,
        estimatedCost: Double
    ) {
        viewModelScope.launch {
            repository.createBooking(
                serviceTitle = serviceTitle,
                unitType = unitType,
                scheduledDate = scheduledDate,
                scheduledTimeSlot = scheduledTimeSlot,
                customerName = customerName,
                customerPhone = customerPhone,
                serviceAddress = serviceAddress,
                issueDescription = issueDescription,
                urgencyLevel = urgencyLevel,
                estimatedCost = estimatedCost
            )
            // If urgent/priority, reset tracking to start moving to this new booking
            if (urgencyLevel.contains("Emergency") || urgencyLevel.contains("Priority")) {
                _trackingState.update {
                    it.copy(
                        progressFraction = 0.12f,
                        distanceMiles = 4.8f,
                        etaMinutes = 14,
                        currentStatus = "Emergency Dispatch En Route"
                    )
                }
            }
        }
    }

    fun updateBookingStatus(bookingId: Long, newStatus: String) {
        viewModelScope.launch {
            repository.updateBookingStatus(bookingId, newStatus)
        }
    }

    // Complaints
    fun createComplaint(
        bookingReference: String?,
        category: String,
        severity: String,
        title: String,
        description: String,
        customerName: String,
        contactPhone: String
    ) {
        viewModelScope.launch {
            repository.createComplaint(
                bookingReference = bookingReference,
                category = category,
                severity = severity,
                title = title,
                description = description,
                customerName = customerName,
                contactPhone = contactPhone
            )
        }
    }

    fun resolveComplaint(complaintId: Long, resolutionNotes: String) {
        viewModelScope.launch {
            repository.resolveComplaint(complaintId, resolutionNotes)
        }
    }

    // Invoices
    fun payInvoice(invoiceId: Long, paymentMethod: String) {
        viewModelScope.launch {
            repository.payInvoice(invoiceId, paymentMethod)
        }
    }

    fun generateAutomatedInvoice(
        bookingRef: String,
        customerName: String,
        customerAddress: String,
        serviceType: String,
        laborHours: Double,
        partsTotal: Double,
        partsDescription: String
    ) {
        viewModelScope.launch {
            val diagnosticFee = if (serviceType.contains("Emergency")) 149.0 else 89.0
            val laborRate = 95.0
            val environmentalFee = 18.50

            repository.createCustomInvoice(
                bookingRef = bookingRef,
                customerName = customerName,
                customerAddress = customerAddress,
                diagnosticFee = diagnosticFee,
                laborHours = laborHours,
                laborRate = laborRate,
                partsJson = partsDescription,
                partsTotal = partsTotal,
                environmentalFee = environmentalFee
            )
        }
    }
}
