package com.example.pensionat2.controller;


import com.example.pensionat2.dto.BookingDTO;
import com.example.pensionat2.dto.CustomerDTO;
import com.example.pensionat2.dto.RoomDTO;
import com.example.pensionat2.model.RoomType;
import com.example.pensionat2.service.BookingService;
import com.example.pensionat2.service.CustomerService;
import com.example.pensionat2.service.RoomService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private RoomService roomService;

    @MockBean
    private BookingService bookingService;

    @Test
    void testShowBookingForm() throws Exception {
        when(customerService.findAll()).thenReturn(
                List.of(CustomerDTO.builder()
                        .id(1L)
                        .firstName("Anna")
                        .lastName("Svensson")
                        .email("anna@example.com")
                        .phone("0701234567")
                        .build())
        );

        mockMvc.perform(get("/bookings/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("/customers/bookings/form"))
                .andExpect(model().attributeExists("booking"))
                .andExpect(model().attributeExists("customers"))
                .andExpect(model().attributeExists("rooms"));
    }

    @Test
    void testFilterAvailableRooms() throws Exception {
        when(customerService.findAll()).thenReturn(
                List.of(CustomerDTO.builder()
                        .id(1L)
                        .firstName("Anna")
                        .lastName("Svensson")
                        .email("anna@example.com")
                        .phone("0701234567")
                        .build())
        );

        RoomDTO room = RoomDTO.builder()
                .id(1L)
                .roomNumber("101")
                .roomType(RoomType.SINGLE)  // Exempel, byt till giltig enum-värde
                .build();

        when(roomService.findAvailableRooms(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(room));

        mockMvc.perform(post("/bookings/new")
                        .param("checkInDate", "2025-06-01")
                        .param("checkOutDate", "2025-06-05"))
                .andExpect(status().isOk())
                .andExpect(view().name("/customers/bookings/form"))
                .andExpect(model().attributeExists("rooms"))
                .andExpect(model().attributeExists("customers"))
                .andExpect(model().attributeExists("booking"))
                .andExpect(model().attribute("rooms", hasSize(1)));
    }

    @Test
    void testSaveBooking_Success() throws Exception {
        when(bookingService.bookRoom(any(BookingDTO.class))).thenReturn(true);

        mockMvc.perform(post("/bookings/save")
                        .param("checkInDate", "2025-06-01")
                        .param("checkOutDate", "2025-06-05"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bookings"));
    }

    @Test
    void testSaveBooking_Failure() throws Exception {
        when(bookingService.bookRoom(any(BookingDTO.class))).thenReturn(false);

        when(customerService.findAll()).thenReturn(
                List.of(CustomerDTO.builder()
                        .id(1L)
                        .firstName("Anna")
                        .lastName("Svensson")
                        .email("anna@example.com")
                        .phone("0701234567")
                        .build())
        );

        when(roomService.findAvailableRooms(any(LocalDate.class), any(LocalDate.class))).thenReturn(List.of());

        mockMvc.perform(post("/bookings/save")
                        .param("checkInDate", "2025-06-01")
                        .param("checkOutDate", "2025-06-05"))
                .andExpect(status().isOk())
                .andExpect(view().name("/customers/bookings/form"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attributeExists("rooms"))
                .andExpect(model().attributeExists("customers"));
    }
}
