package com.example.pensionat2.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingDTO {
    private Long id;
    // För att skapa en bokning
    private Long customerId;
    private Long roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    // För att visa bokningsinformation
    private String customerFirstName;
    private String customerLastName;

    private String roomNumber;
    private String roomType;
      // Antal personer som ska bo i rummet

}
