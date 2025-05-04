package project.api.rest.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import project.api.rest.handler.CustomAuthenticationEntryPoint;
import project.api.rest.service.UserServiceImpl;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomAuthenticationEntryPoint customAuthenticationEntryPoint) throws Exception {
        return http


                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> {

                    //Public Endpoints
                    auth.requestMatchers(HttpMethod.POST, "/api/v1/auth/sign-up").permitAll();
                    auth.requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll();

                    //SWAGGER Endpoint
                    auth.requestMatchers(HttpMethod.GET, "/swagger-ui-custom.html").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/swagger-ui/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api-docs/**").permitAll();

                    //Private Endpoints
                    auth.requestMatchers(HttpMethod.POST, "/api/v1/users").hasAuthority("CREATE");
                    auth.requestMatchers(HttpMethod.GET, "/api/v1/users").hasAuthority("READ");
                    auth.requestMatchers(HttpMethod.GET, "/api/v1/users/{id}").hasAuthority("READ");
                    auth.requestMatchers(HttpMethod.PUT, "/api/v1/users/{id}").hasAuthority("UPDATE");
                    auth.requestMatchers(HttpMethod.DELETE, "/api/v1/users/{id}").hasAuthority("DELETE");

                    //Gemini AI Endpoint
                    auth.requestMatchers(HttpMethod.GET, "/api/v1/gemini/nickname/{name}").authenticated();


                    auth.anyRequest().denyAll();

                })

                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .oauth2ResourceServer(oauth2 -> oauth2.authenticationEntryPoint(customAuthenticationEntryPoint))

                .httpBasic(basic ->
                        basic.authenticationEntryPoint(customAuthenticationEntryPoint))

                .build();

    }



    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserServiceImpl userServiceImpl) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userServiceImpl);
        return daoAuthenticationProvider;
    }

    //Because I don't want to add any prefix
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }


}
