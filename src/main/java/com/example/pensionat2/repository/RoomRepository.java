package com.example.pensionat2.repository;

import com.example.pensionat2.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("""
        SELECT r FROM Room r 
        WHERE r.id NOT IN (
            SELECT b.room.id FROM Booking b 
            WHERE b.checkInDate < :checkOutDate 
              AND b.checkOutDate > :checkInDate 
              AND (:bookingId IS NULL OR b.id != :bookingId)
        )
    """)
    List<Room> findAvailableRooms(
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("bookingId") Long bookingId
    );

    @Query("""
        SELECT r FROM Room r 
        WHERE r.id NOT IN (
            SELECT b.room.id FROM Booking b 
            WHERE b.checkInDate < :checkOutDate 
              AND b.checkOutDate > :checkInDate
        )
    """)
    List<Room> findAvailableRooms(
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );
}

