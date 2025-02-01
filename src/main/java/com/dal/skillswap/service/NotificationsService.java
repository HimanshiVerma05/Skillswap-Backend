package com.dal.skillswap.service;

import com.dal.skillswap.entities.Notifications;

import java.util.List;

public interface NotificationsService {
    Notifications createNotification(Notifications notification);

    List<Notifications> getUnreadNotifications();
    void markAsRead(Long userId);
}