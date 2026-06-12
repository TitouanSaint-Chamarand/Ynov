package com.example.meetingroom.controller;

import com.example.meetingroom.model.Room;
import com.example.meetingroom.service.RoomService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({RoomController.class, MeetingRoomExceptionHandler.class})
class RoomControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoomService roomService;

    @Test
    void shouldReturnCreated_whenRoomIsValid() throws Exception {
        // Arrange
        when(roomService.create("Salle A", 10))
                .thenReturn(new Room(1L, "Salle A", 10));

        // Act + Assert
        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Salle A\",\"capacity\":10}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/rooms/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Salle A"))
                .andExpect(jsonPath("$.capacity").value(10));

        verify(roomService).create("Salle A", 10);
    }

    @Test
    void shouldReturnBadRequest_whenRoomIsInvalid() throws Exception {
        // Act + Assert
        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"capacity\":10}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(roomService, never()).create(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyInt());
    }
}
