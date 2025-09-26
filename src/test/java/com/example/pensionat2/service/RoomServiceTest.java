package com.example.pensionat2.service;

import com.example.pensionat2.dto.RoomDTO;
import com.example.pensionat2.model.Room;
import com.example.pensionat2.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;
import com.example.pensionat2.model.RoomType;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoomServiceTest {

    @Mock
    RoomRepository roomRepository;

    @InjectMocks
    RoomService roomService;

    private Room room;
    private RoomDTO roomDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        room = Room.builder()
                .id(1L)
                .roomNumber("101")
                .roomType(RoomType.SINGLE)

                .build();

        roomDTO = RoomDTO.builder()
                .id(1L)
                .roomNumber("101")
                .roomType(RoomType.SINGLE)

                .build();
    }

    @Test
    void findAll_returnsListOfRoomDTOs() {
        when(roomRepository.findAll()).thenReturn(List.of(room));

        List<RoomDTO> result = roomService.findAll();

        assertEquals(1, result.size());
        assertEquals("101", result.get(0).getRoomNumber());
    }

    @Test
    void findById_returnsRoomDTO_whenFound() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        RoomDTO result = roomService.findById(1L);

        assertEquals("101", result.getRoomNumber());
    }

    @Test
    void findById_throwsException_whenNotFound() {
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> roomService.findById(1L));
    }

    @Test
    void save_returnsSavedRoomDTO() {
        when(roomRepository.save(any())).thenReturn(room);

        RoomDTO result = roomService.save(roomDTO);

        assertEquals("101", result.getRoomNumber());
        verify(roomRepository).save(any());
    }

    @Test
    void delete_callsRepositoryDeleteById() {
        doNothing().when(roomRepository).deleteById(1L);

        roomService.delete(1L);

        verify(roomRepository).deleteById(1L);
    }

    @Test
    void findAvailableRooms_returnsRoomDTOList() {
        when(roomRepository.findAvailableRooms(any(), any())).thenReturn(List.of(room));

        List<RoomDTO> availableRooms = roomService.findAvailableRooms(LocalDate.now(), LocalDate.now().plusDays(2));

        assertFalse(availableRooms.isEmpty());
    }

    @Test
    void isRoomAvailable_returnsTrue_whenRoomIsAvailable() {
        when(roomRepository.findAvailableRooms(any(), any(), anyLong())).thenReturn(List.of(room));

        boolean result = roomService.isRoomAvailable(1L, LocalDate.now(), LocalDate.now().plusDays(2), 5L);

        assertTrue(result);
    }

    @Test
    void isRoomAvailable_returnsFalse_whenRoomIsNotAvailable() {
        when(roomRepository.findAvailableRooms(any(), any(), anyLong())).thenReturn(Collections.emptyList());

        boolean result = roomService.isRoomAvailable(1L, LocalDate.now(), LocalDate.now().plusDays(2), 5L);

        assertFalse(result);
    }
}
