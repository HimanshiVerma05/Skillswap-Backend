package com.dal.skillswap.repository;

import com.dal.skillswap.entities.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationsRepository extends JpaRepository<Notifications, Long> {

    List<Notifications> findByReceiverIdAndIsReadOrderByCreatedAtDesc(Long receiverId, boolean isRead);

}