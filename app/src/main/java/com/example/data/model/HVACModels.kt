package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class HVACBooking(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookingReference: String, // e.g. "HVAC-8491"
    val serviceTitle: String, // e.g. "Central AC Full Diagnostics & Tune-up"
    val unitType: String, // e.g. "Central Split System (3.5 Ton)"
    val scheduledDate: String, // e.g. "Sep 18, 2026"
    val scheduledTimeSlot: String, // e.g. "09:00 AM - 11:00 AM"
    val customerName: String,
    val customerPhone: String,
    val serviceAddress: String,
    val issueDescription: String,
    val urgencyLevel: String, // "Standard", "Priority (Same Day)", "Emergency"
    val estimatedCost: Double,
    val status: String, // "Technician Dispatched", "Scheduled", "In Progress", "Completed", "Cancelled"
    val createdAt: Long = System.currentTimeMillis(),
    val technicianName: String = "Marcus Vance",
    val technicianPhone: String = "+1 (555) 382-4491"
)

@Entity(tableName = "complaints")
data class HVACComplaint(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val ticketId: String, // e.g. "TKT-4109"
    val bookingReference: String?,
    val category: String, // e.g. "No Cold Air / Low Refrigerant", "Compressor Loud Whining Noise", "Condensate Drain Pan Overflow"
    val severity: String, // "Critical (Emergency)", "High", "Medium", "Low"
    val title: String,
    val description: String,
    val customerName: String,
    val contactPhone: String,
    val status: String, // "Under Triage", "Technician Assigned", "Parts Dispatched", "Resolved"
    val resolutionNotes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val responseSlaHours: Int = 2
)

@Entity(tableName = "invoices")
data class HVACInvoice(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val invoiceNumber: String, // e.g. "INV-2026-904"
    val bookingReference: String,
    val customerName: String,
    val customerAddress: String,
    val serviceDate: String,
    val dueDate: String,
    val diagnosticFee: Double,
    val laborHours: Double,
    val laborRatePerHour: Double,
    val partsJson: String, // serializable itemized string or list description
    val partsTotal: Double,
    val environmentalFee: Double, // EPA refrigerant handling fee
    val taxRate: Double = 0.0825, // 8.25%
    val subtotal: Double,
    val taxAmount: Double,
    val totalAmount: Double,
    val status: String, // "Pending", "Paid", "Overdue"
    val paymentMethod: String? = null,
    val paidTimestamp: Long? = null
)

@Entity(tableName = "equipment")
data class HVACEquipment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val unitNickname: String, // e.g. "Upstairs Heat Pump"
    val brand: String, // e.g. "Trane CleanEffects"
    val modelNumber: String, // e.g. "XR16-4TTR6"
    val serialNumber: String, // e.g. "TR-98442-AZ"
    val tonnage: String, // e.g. "3.5 Ton"
    val filterLifePercent: Int, // e.g. 74
    val lastServiceDate: String,
    val nextTuneUpDue: String,
    val refrigerantType: String = "R-410A"
)

// Data class for live technician tracking
data class TechnicianTrackingState(
    val technicianId: Long = 101,
    val name: String = "Marcus Vance",
    val title: String = "Master HVAC Certified Tech (NATE)",
    val phone: String = "+1 (555) 382-4491",
    val vehicleName: String = "Fleet Van #14 (Mobile Repair Unit)",
    val rating: Float = 4.95f,
    val jobsCompleted: Int = 412,
    val progressFraction: Float = 0.42f, // 0.0 to 1.0
    val currentStreet: String = "Approaching 4th Ave & Elm Street",
    val etaMinutes: Int = 11,
    val distanceMiles: Float = 3.2f,
    val currentStatus: String = "En Route - Live GPS",
    val isMoving: Boolean = true
)
