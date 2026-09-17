package com.example.data

import com.example.data.dao.HVACDao
import com.example.data.model.HVACBooking
import com.example.data.model.HVACComplaint
import com.example.data.model.HVACEquipment
import com.example.data.model.HVACInvoice
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class HVACRepository(private val dao: HVACDao) {

    val allBookings: Flow<List<HVACBooking>> = dao.getAllBookings()
    val activeBooking: Flow<HVACBooking?> = dao.getActiveTrackingBooking()
    val allComplaints: Flow<List<HVACComplaint>> = dao.getAllComplaints()
    val allInvoices: Flow<List<HVACInvoice>> = dao.getAllInvoices()
    val allEquipment: Flow<List<HVACEquipment>> = dao.getAllEquipment()

    suspend fun createBooking(
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
    ): Long {
        val randSuffix = (1000..9999).random()
        val bookingRef = "HVAC-2026-$randSuffix"

        val booking = HVACBooking(
            bookingReference = bookingRef,
            serviceTitle = serviceTitle,
            unitType = unitType,
            scheduledDate = scheduledDate,
            scheduledTimeSlot = scheduledTimeSlot,
            customerName = customerName,
            customerPhone = customerPhone,
            serviceAddress = serviceAddress,
            issueDescription = issueDescription,
            urgencyLevel = urgencyLevel,
            estimatedCost = estimatedCost,
            status = if (urgencyLevel.contains("Emergency") || urgencyLevel.contains("Priority")) {
                "Technician Dispatched"
            } else {
                "Scheduled"
            },
            technicianName = "Marcus Vance",
            technicianPhone = "+1 (555) 382-4491"
        )
        val bookingId = dao.insertBooking(booking)

        // Automatically generate an automated initial invoice for this service
        val invNum = "INV-2026-${(100..999).random()}"
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        val todayStr = sdf.format(Date())
        val dueStr = sdf.format(Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000))

        val diagnosticFee = if (urgencyLevel.contains("Emergency")) 149.0 else 89.0
        val laborHours = 1.5
        val laborRate = 95.0
        val partsTotal = (estimatedCost - diagnosticFee - (laborHours * laborRate)).coerceAtLeast(45.0)
        val environmentalFee = 18.50
        val subtotal = diagnosticFee + (laborHours * laborRate) + partsTotal + environmentalFee
        val tax = subtotal * 0.0825
        val total = subtotal + tax

        val invoice = HVACInvoice(
            invoiceNumber = invNum,
            bookingReference = bookingRef,
            customerName = customerName,
            customerAddress = serviceAddress,
            serviceDate = todayStr,
            dueDate = dueStr,
            diagnosticFee = diagnosticFee,
            laborHours = laborHours,
            laborRatePerHour = laborRate,
            partsJson = "Standard Inspection Seal Kit ($25.00) + Airflow Sensor Check ($20.00)",
            partsTotal = partsTotal,
            environmentalFee = environmentalFee,
            subtotal = subtotal,
            taxAmount = tax,
            totalAmount = total,
            status = "Pending"
        )
        dao.insertInvoice(invoice)

        return bookingId
    }

    suspend fun updateBookingStatus(id: Long, status: String) {
        dao.updateBookingStatus(id, status)
    }

    suspend fun createComplaint(
        bookingReference: String?,
        category: String,
        severity: String,
        title: String,
        description: String,
        customerName: String,
        contactPhone: String
    ): Long {
        val randTicket = "TKT-${Random.nextInt(1000, 9999)}"
        val slaHours = when (severity) {
            "Critical (Emergency)" -> 1
            "High" -> 3
            "Medium" -> 12
            else -> 24
        }
        val complaint = HVACComplaint(
            ticketId = randTicket,
            bookingReference = bookingReference,
            category = category,
            severity = severity,
            title = title,
            description = description,
            customerName = customerName,
            contactPhone = contactPhone,
            status = "Technician Assigned",
            resolutionNotes = "Support supervisor acknowledged. Assigned to regional dispatch unit.",
            responseSlaHours = slaHours
        )
        return dao.insertComplaint(complaint)
    }

    suspend fun resolveComplaint(id: Long, notes: String) {
        dao.resolveComplaint(id, "Resolved", notes)
    }

    suspend fun payInvoice(id: Long, paymentMethod: String) {
        dao.markInvoicePaid(id, paymentMethod)
    }

    suspend fun createCustomInvoice(
        bookingRef: String,
        customerName: String,
        customerAddress: String,
        diagnosticFee: Double,
        laborHours: Double,
        laborRate: Double,
        partsJson: String,
        partsTotal: Double,
        environmentalFee: Double
    ): Long {
        val invNum = "INV-2026-${(100..999).random()}"
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        val todayStr = sdf.format(Date())
        val dueStr = sdf.format(Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000))

        val subtotal = diagnosticFee + (laborHours * laborRate) + partsTotal + environmentalFee
        val tax = subtotal * 0.0825
        val total = subtotal + tax

        val invoice = HVACInvoice(
            invoiceNumber = invNum,
            bookingReference = bookingRef,
            customerName = customerName,
            customerAddress = customerAddress,
            serviceDate = todayStr,
            dueDate = dueStr,
            diagnosticFee = diagnosticFee,
            laborHours = laborHours,
            laborRatePerHour = laborRate,
            partsJson = partsJson,
            partsTotal = partsTotal,
            environmentalFee = environmentalFee,
            subtotal = subtotal,
            taxAmount = tax,
            totalAmount = total,
            status = "Pending"
        )
        return dao.insertInvoice(invoice)
    }

    suspend fun updateEquipmentFilter(id: Long, newLife: Int) {
        // can be used to reset filter life after service
    }
}
