package com.estate.back.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.estate.back.filter.JwtAuthenticationFilter;
import com.estate.back.handler.OAuth2SuccessHandler;
import com.estate.back.service.implementation.OAuth2UserSerivceImplementation;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

// Spring Web Security 설정
// - Basic 인증 미사용
// - CSRF 정책 미사용
// - Session 생성 정책 미사용
// - CORS 정책 (모든 출처 - 모든 메서드 - 모든 패턴 허용)
// - JwtAuthenticationFilter 추가 (UsernamePasswordAuthenticationFilter 이전에 추가)
@Configurable
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2UserSerivceImplementation oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    
    @Bean
    protected SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
            .httpBasic(HttpBasicConfigurer::disable)
            .csrf(CsrfConfigurer::disable)
            .sessionManagement(sessionManagement -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .cors(cors -> cors
                .configurationSource(corsConfigurationSource())
            )
            // HTTP요청에 대한 권한을 부여하는 방법 정의
            // authorizeHttpRequests: HTTP요청에 대한 권한을 부여하는 메서드
            .authorizeHttpRequests(request -> request
                // 해당 경로에 대한 요청은 모두 허용한다. / 인증되지 않은 사용자여도 해당 경로로 접근하는 요청은 모두 허용된다
                .requestMatchers("/", "/api/v1/auth/**", "/oauth2/callback/*").permitAll()
                // "/api/v1/board/" 여기에 해당하는 것만, hasRole(USER): USER권한
                .requestMatchers("/api/v1/board/").hasRole("USER")
                .requestMatchers("/api/v1/board/*/comment").hasRole("ADMIN")
                // .anyRequest().authenticated(): 위에서 정한 경로외의 요청은 모두 인증이 필요함.
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(endpoint -> endpoint.baseUri("/api/v1/auth/oauth2"))
                .redirectionEndpoint(endpoint -> endpoint.baseUri("/oauth2/callback/*"))
                .userInfoEndpoint(endpoint -> endpoint.userService(oAuth2UserService))
                .successHandler(oAuth2SuccessHandler)
            )
            .exceptionHandling(exception->exception
                .authenticationEntryPoint(new AuthorizationFailEntryPoint())
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();

    }

    // Cors 정책 설정
    @Bean
    protected CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;

    }

}

class AuthorizationFailEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write("{ \"code\": \"AF\", \"message\": \"Authorization Failed\" }");

    }

    

}