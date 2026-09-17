package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.HVACDao
import com.example.data.model.HVACBooking
import com.example.data.model.HVACComplaint
import com.example.data.model.HVACEquipment
import com.example.data.model.HVACInvoice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        HVACBooking::class,
        HVACComplaint::class,
        HVACInvoice::class,
        HVACEquipment::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun hvacDao(): HVACDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hvac_service_db"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed realistic starter data
                        CoroutineScope(Dispatchers.IO).launch {
                            val dao = getInstance(context).hvacDao()
                            seedInitialData(dao)
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedInitialData(dao: HVACDao) {
            // Seed 1 active booking currently en route
            val booking1 = HVACBooking(
                bookingReference = "HVAC-2026-8491",
                serviceTitle = "Central AC Full Diagnostics & Compressor Check",
                unitType = "Central Split 4.0-Ton (Carrier)",
                scheduledDate = "Today",
                scheduledTimeSlot = "10:30 AM - 12:30 PM",
                customerName = "Sarah Jenkins",
                customerPhone = "+1 (555) 728-1934",
                serviceAddress = "742 Evergreen Terrace, Springfield",
                issueDescription = "System blows lukewarm air intermittently and outside fan emits humming vibration.",
                urgencyLevel = "Priority (Same Day)",
                estimatedCost = 285.00,
                status = "Technician Dispatched",
                technicianName = "Marcus Vance",
                technicianPhone = "+1 (555) 382-4491"
            )
            val bookingId1 = dao.insertBooking(booking1)

            val booking2 = HVACBooking(
                bookingReference = "HVAC-2026-8104",
                serviceTitle = "Seasonal Heat Pump Tune-Up & Coil Sanitization",
                unitType = "Ductless Multi-Zone (Mitsubishi)",
                scheduledDate = "Sep 12, 2026",
                scheduledTimeSlot = "02:00 PM - 04:00 PM",
                customerName = "Sarah Jenkins",
                customerPhone = "+1 (555) 728-1934",
                serviceAddress = "742 Evergreen Terrace, Springfield",
                issueDescription = "Periodic bi-annual filter deep clean and subcool pressure verification.",
                urgencyLevel = "Standard",
                estimatedCost = 189.50,
                status = "Completed",
                technicianName = "Elena Gomez",
                technicianPhone = "+1 (555) 492-7718"
            )
            dao.insertBooking(booking2)

            // Seed Complaint
            dao.insertComplaint(
                HVACComplaint(
                    ticketId = "TKT-4412",
                    bookingReference = "HVAC-2026-8491",
                    category = "Cooling Inefficiency / Weak Airflow",
                    severity = "High",
                    title = "Lukewarm Air & High Return Temp",
                    description = "Upstairs temperature reads 79°F despite thermostat target set to 71°F. Condenser fan turning slowly.",
                    customerName = "Sarah Jenkins",
                    contactPhone = "+1 (555) 728-1934",
                    status = "Technician Assigned",
                    resolutionNotes = "Technician Marcus Vance assigned. Van loaded with dual run capacitors and R-410A canister.",
                    responseSlaHours = 1
                )
            )

            // Seed Invoices
            dao.insertInvoice(
                HVACInvoice(
                    invoiceNumber = "INV-2026-882",
                    bookingReference = "HVAC-2026-8491",
                    customerName = "Sarah Jenkins",
                    customerAddress = "742 Evergreen Terrace, Springfield",
                    serviceDate = "Sep 17, 2026",
                    dueDate = "Sep 24, 2026",
                    diagnosticFee = 89.00,
                    laborHours = 1.5,
                    laborRatePerHour = 95.00,
                    partsJson = "Dual Run Capacitor 45/5 uF 440V ($54.00) + Heavy Duty Contactor 30A ($42.00) + Nitrogen Line Test ($30.00)",
                    partsTotal = 126.00,
                    environmentalFee = 18.50,
                    subtotal = 376.00,
                    taxAmount = 31.02,
                    totalAmount = 407.02,
                    status = "Pending"
                )
            )

            dao.insertInvoice(
                HVACInvoice(
                    invoiceNumber = "INV-2026-791",
                    bookingReference = "HVAC-2026-8104",
                    customerName = "Sarah Jenkins",
                    customerAddress = "742 Evergreen Terrace, Springfield",
                    serviceDate = "Sep 12, 2026",
                    dueDate = "Sep 19, 2026",
                    diagnosticFee = 69.00,
                    laborHours = 1.0,
                    laborRatePerHour = 85.00,
                    partsJson = "MERV 13 Antimicrobial Filter Replacement ($28.50) + Coil Cleaner Solvent ($15.00)",
                    partsTotal = 43.50,
                    environmentalFee = 10.00,
                    subtotal = 207.50,
                    taxAmount = 17.12,
                    totalAmount = 224.62,
                    status = "Paid",
                    paymentMethod = "Apple Pay / Visa ****4242",
                    paidTimestamp = System.currentTimeMillis() - 86400000L * 4
                )
            )

            // Seed Registered Equipment
            dao.insertEquipment(
                HVACEquipment(
                    unitNickname = "Main Living Central HVAC",
                    brand = "Carrier Infinity 19VS",
                    modelNumber = "24VNA936A003",
                    serialNumber = "CR-882914-TX",
                    tonnage = "4.0 Ton (19 SEER2)",
                    filterLifePercent = 42,
                    lastServiceDate = "Mar 15, 2026",
                    nextTuneUpDue = "Sep 30, 2026",
                    refrigerantType = "R-410A Puron"
                )
            )

            dao.insertEquipment(
                HVACEquipment(
                    unitNickname = "Master Suite Mini-Split",
                    brand = "Mitsubishi Electric H2i",
                    modelNumber = "MSZ-FH12NA",
                    serialNumber = "ME-441029-JP",
                    tonnage = "1.5 Ton (24 SEER)",
                    filterLifePercent = 85,
                    lastServiceDate = "Sep 12, 2026",
                    nextTuneUpDue = "Mar 12, 2027",
                    refrigerantType = "R-410A"
                )
            )
        }
    }
}
