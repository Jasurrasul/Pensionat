package com.example.pensionat2.controller;

import com.example.pensionat2.dto.RoomDTO;
import com.example.pensionat2.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public String listRooms(Model model) {
        model.addAttribute("rooms", roomService.findAll());
        return "customers/room/list"; // fixad sökväg
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("room", new RoomDTO());
        return "customers/room/form"; // fixad sökväg
    }

    @PostMapping("/save")
    public String saveRoom(@ModelAttribute("room") RoomDTO roomDTO) {
        roomService.save(roomDTO);
        return "redirect:/rooms";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        RoomDTO room = roomService.findById(id);
        model.addAttribute("room", room);
        return "customers/room/form"; // fixad sökväg
    }

    @GetMapping("/delete/{id}")
    public String deleteRoom(@PathVariable Long id) {
        roomService.delete(id);
        return "redirect:/rooms";
    }
}
