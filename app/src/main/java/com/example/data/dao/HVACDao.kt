package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.HVACBooking
import com.example.data.model.HVACComplaint
import com.example.data.model.HVACEquipment
import com.example.data.model.HVACInvoice
import kotlinx.coroutines.flow.Flow

@Dao
interface HVACDao {

    // Bookings
    @Query("SELECT * FROM bookings ORDER BY id DESC")
    fun getAllBookings(): Flow<List<HVACBooking>>

    @Query("SELECT * FROM bookings WHERE id = :id LIMIT 1")
    suspend fun getBookingById(id: Long): HVACBooking?

    @Query("SELECT * FROM bookings WHERE status = 'Technician Dispatched' OR status = 'In Progress' LIMIT 1")
    fun getActiveTrackingBooking(): Flow<HVACBooking?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: HVACBooking): Long

    @Update
    suspend fun updateBooking(booking: HVACBooking)

    @Query("UPDATE bookings SET status = :newStatus WHERE id = :id")
    suspend fun updateBookingStatus(id: Long, newStatus: String)

    // Complaints
    @Query("SELECT * FROM complaints ORDER BY createdAt DESC")
    fun getAllComplaints(): Flow<List<HVACComplaint>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComplaint(complaint: HVACComplaint): Long

    @Update
    suspend fun updateComplaint(complaint: HVACComplaint)

    @Query("UPDATE complaints SET status = :newStatus, resolutionNotes = :notes WHERE id = :id")
    suspend fun resolveComplaint(id: Long, newStatus: String, notes: String?)

    // Invoices
    @Query("SELECT * FROM invoices ORDER BY id DESC")
    fun getAllInvoices(): Flow<List<HVACInvoice>>

    @Query("SELECT * FROM invoices WHERE id = :id LIMIT 1")
    suspend fun getInvoiceById(id: Long): HVACInvoice?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: HVACInvoice): Long

    @Update
    suspend fun updateInvoice(invoice: HVACInvoice)

    @Query("UPDATE invoices SET status = 'Paid', paymentMethod = :method, paidTimestamp = :timestamp WHERE id = :id")
    suspend fun markInvoicePaid(id: Long, method: String, timestamp: Long = System.currentTimeMillis())

    // Equipment
    @Query("SELECT * FROM equipment ORDER BY id ASC")
    fun getAllEquipment(): Flow<List<HVACEquipment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipment(equipment: HVACEquipment): Long

    @Update
    suspend fun updateEquipment(equipment: HVACEquipment)
}
