package com.dal.skillswap.controller;

import com.dal.skillswap.entities.Notifications;
import com.dal.skillswap.repository.NotificationsRepository;
import com.dal.skillswap.repository.UserRepository;
import com.dal.skillswap.service.NotificationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationsController {

    @Autowired
    private NotificationsService notificationsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationsRepository notificationsRepository;


    @GetMapping("/unread")
    public ResponseEntity<List<Notifications>> getUnreadNotifications() {

        List<Notifications> notifications = notificationsService.getUnreadNotifications();
        return ResponseEntity.ok(notifications);
    }

    @PostMapping(value = "/createNotification")
    @ResponseBody
    public ResponseEntity createNotification(@RequestBody Notifications notification) {
        Notifications createdNotification = notificationsService.createNotification(notification);
        return ResponseEntity.ok("Notification Created Successfully!");
    }

    @PutMapping("/{userId}/read")
    public ResponseEntity<?> markNotificationAsRead(@PathVariable Long userId) {
        notificationsService.markAsRead(userId);
        return ResponseEntity.ok().build();
    }
}