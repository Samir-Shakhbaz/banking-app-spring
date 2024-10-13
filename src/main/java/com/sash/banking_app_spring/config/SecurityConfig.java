package com.sash.banking_app_spring.config;

import com.sash.banking_app_spring.models.BankingAccount;
import com.sash.banking_app_spring.models.User;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Optional;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity(debug = true)
@AllArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(withDefaults())
                .cors(withDefaults())
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/", "/login", "/accounts/forgot-password").permitAll()
                        .requestMatchers("/accounts/create","/user/create").hasRole("ADMIN")
                        .requestMatchers("/accounts/{id}","/home").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/change-password/reset-password").permitAll()
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/home", true)
                        .successHandler((request, response, authentication) -> {

                            User user = (User) authentication.getPrincipal();
                            System.out.println("User: " + user.getUsername() + " Password Change Required: " + user.isPasswordChangeRequired());

                            if (user.isPasswordChangeRequired()) {
                                response.sendRedirect("/change-password");
                            } else {

                                response.sendRedirect("/accounts/" + user.getId());
                            }
                        })
                        .failureUrl("/login?error=true")
                        .permitAll()
                )


                .logout((logout) -> logout.logoutSuccessUrl("/"));

        return http.build();
    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails admin = User.builder()
//                .username("admin")
//                .password(passwordEncoder().encode("admin"))
//                .roles("ADMIN")
//                .build();
//
//        return new InMemoryUserDetailsManager(admin);
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}

