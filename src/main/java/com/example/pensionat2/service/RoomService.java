package com.example.pensionat2.service;

import com.example.pensionat2.dto.RoomDTO;
import com.example.pensionat2.model.Room;
import com.example.pensionat2.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public List<RoomDTO> findAll() {
        return roomRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public RoomDTO findById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));
        return convertToDTO(room);
    }

    public RoomDTO save(RoomDTO roomDTO) {
        Room room = convertToEntity(roomDTO);
        Room savedRoom = roomRepository.save(room);
        return convertToDTO(savedRoom);
    }

    public void delete(Long id) {
        roomRepository.deleteById(id);
    }

    private RoomDTO convertToDTO(Room room) {
        return RoomDTO.builder()
                .id(room.getId())
                .roomNumber(room.getRoomNumber())
                .roomType(room.getRoomType())
                .build();
    }

    private Room convertToEntity(RoomDTO dto) {
        return Room.builder()
                .id(dto.getId())
                .roomNumber(dto.getRoomNumber())
                .roomType(dto.getRoomType())
                .build();
    }

    public List<RoomDTO> findAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        List<Room> allAvailableRooms = roomRepository.findAvailableRooms(checkIn, checkOut);
        return allAvailableRooms.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public boolean isRoomAvailable(Long roomId, LocalDate checkIn, LocalDate checkOut, Long excludeBookingId) {
        List<Room> availableRooms = roomRepository.findAvailableRooms(checkIn, checkOut, excludeBookingId);
        return availableRooms.stream().anyMatch(room -> room.getId().equals(roomId));
    }
}
