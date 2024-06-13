package com.bookstore.utils;

import com.bookstore.services.CustomUserDetailServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public UserDetailsService userDetailsService(){
        return new CustomUserDetailServices();

    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService());
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public AccessDeniedHandler customBadRequestException(){
        return (request, response, badRequestException)
                -> response.sendRedirect(request.getContextPath() + "/error/400");
    }

    @Bean
    public AccessDeniedHandler customAccessDeniedHandler(){
        return (request, response, accessDeniedException)
                -> response.sendRedirect(request.getContextPath() + "/error/403");
    }

    @Bean
    public AccessDeniedHandler customNotFoundException(){
        return (request, response, notFoundException)
                -> response.sendRedirect(request.getContextPath() + "/error/404");
    }

    @Bean
    public AccessDeniedHandler customGlobalException(){
        return (request, response, globalException)
                -> response.sendRedirect(request.getContextPath() + "/error/500");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws
            Exception {
        return http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers( "/css/**", "/js/**", "/", "/register",
                                "/error")
                        .permitAll()
                        .requestMatchers( "/books/edit", "/books/delete")
                        .hasAnyAuthority("ADMIN")
                        .requestMatchers("/books", "/books/add")
                        .hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers( "/categories/edit", "/categories/delete")
                        .hasAnyAuthority("ADMIN")
                        .requestMatchers("/categories", "/categories/add")
                        .hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers("/admin", "/admin/manage-roles")
                        .hasAnyAuthority("ADMIN")
                        .anyRequest().authenticated()
                )
                .logout(logout -> logout.logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                )
                .formLogin(formLogin -> formLogin.loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/")
                        .permitAll()
                )
                .rememberMe(rememberMe -> rememberMe.key("uniqueAndSecret")
                        .tokenValiditySeconds(86400)
                        .userDetailsService(userDetailsService())
                )
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.accessDeniedHandler(customBadRequestException()))
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.accessDeniedHandler(customAccessDeniedHandler()))
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.accessDeniedHandler(customNotFoundException()))
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.accessDeniedHandler(customGlobalException()))
                .build();
    }
}
