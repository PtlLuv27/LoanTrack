package com.loantrack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/register", "/login", "/css/**", "/js/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/officer/**").hasRole("LOAN_OFFICER")
                .requestMatchers("/borrower/**").hasRole("BORROWER")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("email")
                // Custom Success Handler: Route users based on their role
                .successHandler((request, response, authentication) -> {
                    String redirectUrl = "/dashboard"; // Fallback URL
                    
                    // Loop through the user's roles
                    for (GrantedAuthority authority : authentication.getAuthorities()) {
                        String role = authority.getAuthority();
                        
                        if (role.equals("ROLE_LOAN_OFFICER")) {
                            redirectUrl = "/officer/dashboard";
                            break;
                        } else if (role.equals("ROLE_ADMIN")) {
                            redirectUrl = "/admin/dashboard";
                            break;
                        } else if (role.equals("ROLE_BORROWER")) {
                            redirectUrl = "/dashboard"; // Or wherever the borrower dashboard is
                            break;
                        }
                    }
                    
                    response.sendRedirect(redirectUrl);
                })
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }
}