package com.sash.banking_app_spring.models;

import jakarta.persistence.*;
import lombok.Data;

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
    private User user;

}

