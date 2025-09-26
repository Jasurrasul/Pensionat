package com.example.pensionat2.repository;

import com.example.pensionat2.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b WHERE b.customer.id = :customerId AND " +
            "(:checkIn < b.checkOutDate AND :checkOut > b.checkInDate)")
    List<Booking> findBookingsByCustomerIdAndDateRange(@Param("customerId") Long customerId,
                                                       @Param("checkIn") LocalDate checkIn,
                                                       @Param("checkOut") LocalDate checkOut);

    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId AND " +
            "(:checkIn < b.checkOutDate AND :checkOut > b.checkInDate)")
    List<Booking> findBookingsByRoomIdAndDateRange(@Param("roomId") Long roomId,
                                                   @Param("checkIn") LocalDate checkIn,
                                                   @Param("checkOut") LocalDate checkOut);

    boolean existsByCustomerId(Long customerId);

    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId AND " +
            "(:checkInDate < b.checkOutDate AND :checkOutDate > b.checkInDate)")
    List<Booking> findConflictingBookings(Long roomId, LocalDate checkInDate, LocalDate checkOutDate);
}
