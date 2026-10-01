package com.foodrescue.repository;

import com.foodrescue.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientUser_IdOrderByCreatedAtDesc(Long recipientUserId);
    long countByRecipientUser_IdAndReadFalse(Long recipientUserId);
    List<Notification> findByRecipientUser_IdAndReadFalse(Long recipientUserId);
}
