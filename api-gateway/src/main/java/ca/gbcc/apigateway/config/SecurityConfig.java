package ca.gbcc.apigateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final String[] noAuthResourceUris = {
            "/swagger-ui",
            "/swagger-ui/*",
            "/v3/api-docs/**",
            "/swagger-resource/**",
            "/api-docs/**",
            "/aggregate/**"
    };


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        log.info("Initializing Security Filter Chain...");


        //not a good practice to disable csrf, only for learning for this course
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable) //Disable CSRF protection (not good practice)
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll())     //All request temporarily permitted
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers(noAuthResourceUris)
//                        .permitAll()
//                        .anyRequest().authenticated()) //All request require authentication
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults()))
                .build();

    }

}
