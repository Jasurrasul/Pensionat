package com.example.pensionat2.controller;

import com.example.pensionat2.dto.BookingDTO;
import com.example.pensionat2.dto.CustomerDTO;
import com.example.pensionat2.dto.RoomDTO;
import com.example.pensionat2.service.BookingService;
import com.example.pensionat2.service.CustomerService;
import com.example.pensionat2.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private BookingService bookingService;

    @GetMapping("/new")
    public String showBookingForm(Model model) {
        model.addAttribute("booking", new BookingDTO());
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("rooms", List.of()); // tom lista för att inte dölja formulärdel
        return "/customers/bookings/form";
    }

    @PostMapping("/new")
    public String filterAvailableRooms(@ModelAttribute("booking") BookingDTO bookingDTO, Model model) {
        List<CustomerDTO> customers = customerService.findAll();
        model.addAttribute("customers", customers);

        // Nu utan antal gäster — skickar null i parameter
        List<RoomDTO> availableRooms = roomService.findAvailableRooms(
                bookingDTO.getCheckInDate(),
                bookingDTO.getCheckOutDate()

        );

        if (availableRooms.isEmpty()) {
            model.addAttribute("error", "Inga lediga rum för valda datum.");
        }
        model.addAttribute("rooms", availableRooms);
        model.addAttribute("booking", bookingDTO);
        return "/customers/bookings/form";
    }

    @PostMapping("/save")
    public String saveBooking(@ModelAttribute("booking") BookingDTO bookingDTO, Model model) {
        boolean success;

        if (bookingDTO.getId() != null) {
            // Uppdatera befintlig bokning
            success = bookingService.updateBooking(bookingDTO);
        } else {
            // Ny bokning
            success = bookingService.bookRoom(bookingDTO);
        }

        if (!success) {
            model.addAttribute("error", "Det gick inte att spara bokningen. Rummet kan vara upptaget.");
            model.addAttribute("customers", customerService.findAll());
            model.addAttribute("rooms", roomService.findAvailableRooms(bookingDTO.getCheckInDate(), bookingDTO.getCheckOutDate()));
            model.addAttribute("booking", bookingDTO);
            return "/customers/bookings/form";
        }

        return "redirect:/bookings";
    }

    @GetMapping
    public String listBookings(Model model) {
        List<BookingDTO> bookings = bookingService.findAll();
        model.addAttribute("bookings", bookings);
        return "/customers/bookings/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteBooking(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean deleted = bookingService.deleteById(id);
        if (deleted) {
            redirectAttributes.addFlashAttribute("message", "Bokningen togs bort.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Kunde inte ta bort bokningen.");
        }
        return "redirect:/bookings";
    }

    @GetMapping("/change/{id}")
    public String showChangeForm(@PathVariable Long id, Model model) {
        BookingDTO bookingDTO = bookingService.findById(id);
        if (bookingDTO == null) {
            model.addAttribute("error", "Bokningen finns inte.");
            return "redirect:/bookings";
        }
        model.addAttribute("booking", bookingDTO);
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("rooms", roomService.findAll());

        return "/customers/bookings/change";  // Samma vy som bokningsformuläret
    }
}
