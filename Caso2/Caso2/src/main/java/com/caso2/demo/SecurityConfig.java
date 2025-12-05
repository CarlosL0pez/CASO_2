package com.caso2.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(requests -> requests
                
                // ADMIN -> gestiona usuarios y roles
                .requestMatchers("/usuarios/**", "/roles/**")
                .hasRole("ADMIN")

                // PROFESOR -> reportes
                .requestMatchers("/reportes/**")
                .hasRole("PROFESOR")

                // ESTUDIANTE -> su perfil
                .requestMatchers("/perfil/**")
                .hasRole("ESTUDIANTE")

                // rutas públicas
                .requestMatchers("/", "/index", "/home", "/login",
                                 "/css/**", "/js/**", "/images/**")
                .permitAll()

                // cualquier otra → autenticación
                .anyRequest().authenticated()
        );

        // 🔥 SOLO UN formLogin → SIN BUCLES
        http.formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/index", true)    // redirige al home después del login
                .failureUrl("/login?error=true")
                .permitAll()
        );

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
        );

        http.exceptionHandling(exception ->
                exception.accessDeniedPage("/acceso_denegado")
        );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void configurerGlobal(AuthenticationManagerBuilder build,
                                 @Lazy PasswordEncoder passwordEncoder,
                                 @Lazy UserDetailsService userDetailsService)
            throws Exception {

        build.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }
}
