package com.dal.skillswap.controller;

import com.dal.skillswap.entities.Notifications;
import com.dal.skillswap.repository.NotificationsRepository;
import com.dal.skillswap.repository.UserRepository;
import com.dal.skillswap.service.NotificationsService;
import com.dal.skillswap.service.impl.NotificationsServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {NotificationsController.class, NotificationsServiceImpl.class})
@WebMvcTest(NotificationsController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles({"test"})
public class NotificationsControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationsService notificationsService;

    @MockBean
    private NotificationsRepository notificationsRepository;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        openMocks(this);
    }

    @Test
    void getUnreadNotifications_shouldReturnOk() throws Exception {
        List<Notifications> mockNotifications = Collections.singletonList(new Notifications(1L, 1L, 2L, "New message", false, LocalDateTime.now()));
        when(notificationsService.getUnreadNotifications()).thenReturn(mockNotifications);

        String response = mockMvc.perform(get("/api/notifications/unread"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<Notifications> notifications = objectMapper.readValue(response, new TypeReference<List<Notifications>>() {
        });
        assertThat(notifications).isNotEmpty();
    }

    @Test
    void createNotification_shouldReturnOk() throws Exception {
        Notifications notification = new Notifications(1L, 1L, 2L, "New message", false, LocalDateTime.now());
        when(notificationsService.createNotification(any(Notifications.class))).thenReturn(notification);

        String notificationJson = objectMapper.writeValueAsString(notification);
        String createResponse = mockMvc.perform(post("/api/notifications/createNotification")
                        .content(notificationJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(createResponse).isEqualTo("Notification Created Successfully!");
    }

    @Test
    void markNotificationAsRead_shouldReturnOk() throws Exception {
        String putResponse = mockMvc.perform(put("/api/notifications/{userId}/read", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(putResponse.isEmpty());
    }
}
