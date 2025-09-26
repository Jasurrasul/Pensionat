package com.example.pensionat2.service;

import com.example.pensionat2.dto.BookingDTO;
import com.example.pensionat2.model.Booking;
import com.example.pensionat2.model.Customer;
import com.example.pensionat2.model.Room;
import com.example.pensionat2.repository.BookingRepository;
import com.example.pensionat2.repository.CustomerRepository;
import com.example.pensionat2.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDate;
import java.util.*;
import com.example.pensionat2.model.RoomType;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    @Mock
    BookingRepository bookingRepository;

    @Mock
    CustomerRepository customerRepository;

    @Mock
    RoomRepository roomRepository;

    @Mock
    RoomService roomService;

    @InjectMocks
    BookingService bookingService;

    private Booking booking;
    private BookingDTO bookingDTO;
    private Customer customer;
    private Room room;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("Anna");
        customer.setLastName("Andersson");

        room = new Room();
        room.setId(1L);
        room.setRoomNumber("101");
        room.setRoomType(RoomType.SINGLE);


        booking = new Booking();
        booking.setId(1L);
        booking.setCustomer(customer);
        booking.setRoom(room);
        booking.setCheckInDate(LocalDate.of(2025, 6, 1));
        booking.setCheckOutDate(LocalDate.of(2025, 6, 5));

        bookingDTO = new BookingDTO();
        bookingDTO.setId(1L);
        bookingDTO.setCustomerId(1L);
        bookingDTO.setCustomerFirstName("Anna");
        bookingDTO.setCustomerLastName("Andersson");
        bookingDTO.setRoomId(1L);
        bookingDTO.setRoomNumber("101");
        bookingDTO.setRoomType("SINGLE");
        bookingDTO.setCheckInDate(LocalDate.of(2025, 6, 1));
        bookingDTO.setCheckOutDate(LocalDate.of(2025, 6, 5));
    }

    @Test
    void canBookRoom_returnsTrue_whenRoomAndCustomerAvailable() {
        when(bookingRepository.findBookingsByRoomIdAndDateRange(anyLong(), any(), any())).thenReturn(Collections.emptyList());
        when(bookingRepository.findBookingsByCustomerIdAndDateRange(anyLong(), any(), any())).thenReturn(Collections.emptyList());

        boolean result = bookingService.canBookRoom(bookingDTO);

        assertTrue(result);
        verify(bookingRepository).findBookingsByRoomIdAndDateRange(1L, bookingDTO.getCheckInDate(), bookingDTO.getCheckOutDate());
        verify(bookingRepository).findBookingsByCustomerIdAndDateRange(1L, bookingDTO.getCheckInDate(), bookingDTO.getCheckOutDate());
    }

    @Test
    void canBookRoom_returnsFalse_whenRoomNotAvailable() {
        when(bookingRepository.findBookingsByRoomIdAndDateRange(anyLong(), any(), any())).thenReturn(List.of(booking));
        when(bookingRepository.findBookingsByCustomerIdAndDateRange(anyLong(), any(), any())).thenReturn(Collections.emptyList());

        boolean result = bookingService.canBookRoom(bookingDTO);

        assertFalse(result);
    }

    @Test
    void updateBooking_successful_whenRoomAvailable() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(roomService.isRoomAvailable(1L, bookingDTO.getCheckInDate(), bookingDTO.getCheckOutDate(), 1L)).thenReturn(true);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(bookingRepository.save(any())).thenReturn(booking);

        boolean result = bookingService.updateBooking(bookingDTO);

        assertTrue(result);
        verify(bookingRepository).save(booking);
    }

    @Test
    void updateBooking_fails_whenBookingNotFound() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        boolean result = bookingService.updateBooking(bookingDTO);

        assertFalse(result);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void updateBooking_fails_whenRoomNotAvailable() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(roomService.isRoomAvailable(1L, bookingDTO.getCheckInDate(), bookingDTO.getCheckOutDate(), 1L)).thenReturn(false);

        boolean result = bookingService.updateBooking(bookingDTO);

        assertFalse(result);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void bookRoom_successful_whenCanBook() {
        BookingDTO newBooking = new BookingDTO();
        newBooking.setCustomerId(1L);
        newBooking.setRoomId(1L);
        newBooking.setCheckInDate(LocalDate.of(2025, 6, 10));
        newBooking.setCheckOutDate(LocalDate.of(2025, 6, 15));

        when(bookingRepository.findBookingsByRoomIdAndDateRange(anyLong(), any(), any())).thenReturn(Collections.emptyList());
        when(bookingRepository.findBookingsByCustomerIdAndDateRange(anyLong(), any(), any())).thenReturn(Collections.emptyList());
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(bookingRepository.save(any())).thenReturn(new Booking());

        boolean result = bookingService.bookRoom(newBooking);

        assertTrue(result);
        verify(bookingRepository).save(any());
    }

    @Test
    void bookRoom_fails_whenCannotBook() {
        when(bookingRepository.findBookingsByRoomIdAndDateRange(anyLong(), any(), any())).thenReturn(List.of(booking));
        when(bookingRepository.findBookingsByCustomerIdAndDateRange(anyLong(), any(), any())).thenReturn(Collections.emptyList());

        boolean result = bookingService.bookRoom(bookingDTO);

        assertFalse(result);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void findById_returnsDTO_whenFound() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        BookingDTO result = bookingService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Anna", result.getCustomerFirstName());
    }

    @Test
    void findById_throwsException_whenNotFound() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bookingService.findById(1L));
    }

    @Test
    void deleteById_returnsTrue_whenExists() {
        when(bookingRepository.existsById(1L)).thenReturn(true);
        doNothing().when(bookingRepository).deleteById(1L);

        boolean result = bookingService.deleteById(1L);

        assertTrue(result);
        verify(bookingRepository).deleteById(1L);
    }

    @Test
    void deleteById_returnsFalse_whenNotExists() {
        when(bookingRepository.existsById(1L)).thenReturn(false);

        boolean result = bookingService.deleteById(1L);

        assertFalse(result);
        verify(bookingRepository, never()).deleteById(anyLong());
    }
}
