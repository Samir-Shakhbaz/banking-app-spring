package com.sash.banking_app_spring.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Data

public class NotificationSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean loginNotification;
    private boolean checkingAccountNotification;
    private boolean savingsAccountNotification;
    private boolean emailNotification;
    private boolean phoneNotification;

    @OneToOne(mappedBy = "notificationSettings")
    @ToString.Exclude  // Exclude 'user' to avoid circular reference
    private User user;

}

