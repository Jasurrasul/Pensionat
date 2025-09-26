package com.example.pensionat2.service;

import com.example.pensionat2.dto.BookingDTO;
import com.example.pensionat2.model.Booking;
import com.example.pensionat2.repository.BookingRepository;
import com.example.pensionat2.repository.CustomerRepository;
import com.example.pensionat2.repository.RoomRepository;
import com.example.pensionat2.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final RoomRepository roomRepository;
    private final RoomService roomService;

    private BookingDTO toDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setCustomerId(booking.getCustomer().getId());
        dto.setCustomerFirstName(booking.getCustomer().getFirstName());
        dto.setCustomerLastName(booking.getCustomer().getLastName());
        dto.setRoomId(booking.getRoom().getId());
        dto.setRoomNumber(booking.getRoom().getRoomNumber());
        dto.setRoomType(booking.getRoom().getRoomType().toString());
        dto.setCheckInDate(booking.getCheckInDate());
        dto.setCheckOutDate(booking.getCheckOutDate());
        return dto;
    }

    // Kontrollera både rum och kunds tillgänglighet för bokning
    public boolean canBookRoom(BookingDTO dto) {
        boolean roomAvailable = bookingRepository.findBookingsByRoomIdAndDateRange(
                dto.getRoomId(), dto.getCheckInDate(), dto.getCheckOutDate()).isEmpty();

        boolean customerAvailable = bookingRepository.findBookingsByCustomerIdAndDateRange(
                dto.getCustomerId(), dto.getCheckInDate(), dto.getCheckOutDate()).isEmpty();

        return roomAvailable && customerAvailable;
    }
    public boolean updateBooking(BookingDTO dto) {
        Optional<Booking> optionalBooking = bookingRepository.findById(dto.getId());
        if (optionalBooking.isEmpty()) return false;

        Booking booking = optionalBooking.get();

        // Validera att rummet är tillgängligt för de nya datumen
        boolean isAvailable = roomService.isRoomAvailable(dto.getRoomId(), dto.getCheckInDate(), dto.getCheckOutDate(), dto.getId());
        if (!isAvailable) return false;

        booking.setCheckInDate(dto.getCheckInDate());
        booking.setCheckOutDate(dto.getCheckOutDate());
        booking.setRoom(roomRepository.findById(dto.getRoomId()).orElseThrow());

        bookingRepository.save(booking);
        return true;
    }


    public boolean bookRoom(BookingDTO dto) {
        if (!canBookRoom(dto)) {
            return false; // Rummet eller kunden är upptagen under den valda perioden
        }
        save(dto);
        return true;
    }

    public List<BookingDTO> findAll() {
        return bookingRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public boolean isRoomAvailable(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        List<Booking> existingBookings = bookingRepository.findBookingsByRoomIdAndDateRange(roomId, checkIn, checkOut);
        return existingBookings.isEmpty();
    }

    public BookingDTO findById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        return toDTO(booking);
    }

    public void save(BookingDTO dto) {
        Booking booking = dto.getId() != null
                ? bookingRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Booking not found"))
                : new Booking();

        booking.setCustomer(customerRepository.findById(dto.getCustomerId()).orElseThrow());
        booking.setRoom(roomRepository.findById(dto.getRoomId()).orElseThrow());
        booking.setCheckInDate(dto.getCheckInDate());
        booking.setCheckOutDate(dto.getCheckOutDate());
        bookingRepository.save(booking);
    }
    public boolean deleteById(Long id) {
        if (bookingRepository.existsById(id)) {
            bookingRepository.deleteById(id);
            return true;
        }
        return false;
    }


    public void delete(Long id) {
        bookingRepository.deleteById(id);
    }
}
