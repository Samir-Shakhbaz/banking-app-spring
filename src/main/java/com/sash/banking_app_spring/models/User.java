package com.sash.banking_app_spring.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Data
@Table(name = "app_user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String role;
    private boolean passwordChangeRequired;
    @Column(nullable = true) // Optional email field
    private String email;

    @Column(nullable = true) // Optional phone field
    private String phone;
    
    private String resetToken;
    private long resetTokenExpiration;


    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinTable(
            name = "user_accounts",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "account_id")
    )
    private Set<BankingAccount> accounts = new HashSet<>();

    @Getter
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "notification_settings_id", referencedColumnName = "id")
    private NotificationSettings notificationSettings;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.createAuthorityList(List.of("ROLE_" + role.toUpperCase()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username); // excluding 'accounts' to avoid recursion
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof User)) return false;
        User other = (User) obj;
        return Objects.equals(id, other.id) && Objects.equals(username, other.username); // excluding 'accounts'
    }

}
