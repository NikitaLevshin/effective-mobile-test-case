package effectivemobile.testcase.auth.config;

import effectivemobile.testcase.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfigurator {

    private final JwtFilter jwtFilter;
    private final UserService userService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(8);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    @Primary
    public AuthenticationManagerBuilder configureAuthenticationManager(
            AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(
                        request -> new CorsConfiguration().applyPermitDefaultValues()
                ))
                .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .sessionManagement(
                        httpSessionManagement -> httpSessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/register").permitAll()
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/auth/token").permitAll()
                        .requestMatchers("/auth/refresh").fullyAuthenticated()
                        .requestMatchers("/task/**").fullyAuthenticated()
                        .requestMatchers("/user/**").fullyAuthenticated()
                        .requestMatchers("/comment/**").fullyAuthenticated()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
