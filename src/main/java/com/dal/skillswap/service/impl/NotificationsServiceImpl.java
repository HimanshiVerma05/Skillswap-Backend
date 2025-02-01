package com.dal.skillswap.service.impl;

import com.dal.skillswap.entities.Notifications;
import com.dal.skillswap.entities.User;
import com.dal.skillswap.repository.NotificationsRepository;
import com.dal.skillswap.service.NotificationsService;
import com.dal.skillswap.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationsServiceImpl implements NotificationsService {

    @Autowired
    private NotificationsRepository notificationsRepository;

    @Autowired
    private UserService userService;

    /**
     * Create a new notification
     * @param notification
     * @return
     */
    @Override
    public Notifications createNotification(Notifications notification) {
        notification.setCreatedAt(LocalDateTime.now());
        notificationsRepository.save(notification);
        return notification;
    }

    /**
     * Get all unread notifications
     * @return
     */
    @Override
    public List<Notifications> getUnreadNotifications() {
        User user = userService.getLoggedInUser();

        return notificationsRepository.findByReceiverIdAndIsReadOrderByCreatedAtDesc(user.getId(), false);
    }

    /**
     * Mark a notification as read
     * @param notificationId
     */
    @Override
    public void markAsRead(Long notificationId) {
        Optional<Notifications> notification = notificationsRepository.findById(notificationId);
        if (notification.isEmpty()) {
            throw new IllegalArgumentException("No notifications found for notificationId " + notificationId);
        }

        notification.get().setRead(true);
        notificationsRepository.save(notification.get());
    }
}
