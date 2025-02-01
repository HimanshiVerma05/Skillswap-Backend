package com.dal.skillswap.service.impl;

import com.dal.skillswap.entities.Notifications;
import com.dal.skillswap.entities.User;
import com.dal.skillswap.repository.NotificationsRepository;
import com.dal.skillswap.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class NotificationsServiceImplTests {

    @InjectMocks
    private NotificationsServiceImpl notificationsService;
    @Mock
    private NotificationsRepository notificationsRepository;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateNotification() {
        Notifications notification = new Notifications();
        notification.setCreatedAt(LocalDateTime.now());
        notification.setSenderId(1L);
        notification.setReceiverId(2L);

        when(notificationsRepository.save(notification)).thenReturn(notification);

        Notifications createdNotification = notificationsService.createNotification(notification);

        assertEquals(notification, createdNotification);

        verify(notificationsRepository, times(1)).save(notification);
    }

    @Test
    void testGetUnreadNotifications() {
        User user = new User();
        user.setId(1L);

        List<Notifications> expectedNotifications = new ArrayList<>();
        expectedNotifications.add(new Notifications());
        expectedNotifications.add(new Notifications());

        when(userService.getLoggedInUser()).thenReturn(user);
        when(notificationsRepository.findByReceiverIdAndIsReadOrderByCreatedAtDesc(user.getId(), false))
                .thenReturn(expectedNotifications);

        List<Notifications> unreadNotifications = notificationsService.getUnreadNotifications();

        assertEquals(expectedNotifications, unreadNotifications);

        verify(userService, times(1)).getLoggedInUser();
        verify(notificationsRepository, times(1)).findByReceiverIdAndIsReadOrderByCreatedAtDesc(user.getId(), false);
    }

    @Test
    void testMarkAsRead() {
        Long notificationId = 1L;

        Notifications notification = new Notifications();
        notification.setId(1L);
        notification.setRead(false);

        when(notificationsRepository.findById(notificationId)).thenReturn(Optional.of(notification));

        notificationsService.markAsRead(notificationId);
        assertTrue(notification.isRead());

        verify(notificationsRepository, times(1)).findById(notificationId);
    }
}