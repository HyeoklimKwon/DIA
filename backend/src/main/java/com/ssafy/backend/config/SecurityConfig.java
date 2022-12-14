package com.ssafy.backend.config;

import com.ssafy.backend.jwt.*;
import com.ssafy.backend.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final CorsFilter corsFilter;
    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(
            TokenProvider tokenProvider,
            CorsFilter corsFilter,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAccessDeniedHandler jwtAccessDeniedHandler,
            CustomUserDetailsService customUserDetailsService) {
        this.tokenProvider = tokenProvider;
        this.corsFilter = corsFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD","POST","GET","DELETE","PUT"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // ??????, ?????? ???????????? ???????????? ?????? endpoint??? ??????
    @Bean
    public WebSecurityCustomizer configure(){
        return (web) -> web.ignoring()
                .antMatchers(
                        "/favicon.ico",
                        "/v2/api-docs",
                        "/configuration/ui",
                        "/swagger-resources/**",
                        "/configuration/security",
                        "/swagger-ui.html",
                        "/webjars/**"
                );
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                // ????????? ???????????? ?????? ????????? STATELESS??? ??????
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .headers()
                .frameOptions()
                .sameOrigin()

                .and()
                .authorizeRequests()
                .antMatchers(
                        "/api/v1/login",
                        "/api/v1/signup",
//                        "/api/v1/test",
                        "/api/v1/mailsend",
                        "/api/v1/email/check/*",
                        "/api/v1/check/code/*",
                        "/api/v1/health/*",
                        "/api/v1/health/heart/*",
                        "/ws/**"
                ).permitAll()

                .anyRequest().authenticated()

                .and()
                .apply(new JwtSecurityConfig(tokenProvider, customUserDetailsService))
                .and()
                .build();
    }

    // ??????, ?????? ???????????? ???????????? ?????? endpoint??? ??????
//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        web
//                .ignoring()
//                .antMatchers(
//                        "/favicon.ico",
//                        "/v2/api-docs",
//                        "/configuration/ui",
//                        "/swagger-resources/**",
//                        "/configuration/security",
//                        "/swagger-ui.html",
//                        "/webjars/**"
//                );
//    }

    // http ???????????? ????????? ?????? security
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().disable(); // ???????????? ?????? ??????
//
//        http
//                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
//
//                .exceptionHandling()
//                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
//                .accessDeniedHandler(jwtAccessDeniedHandler)
//
//                // ????????? ???????????? ?????? ????????? STATELESS??? ??????
//                .and()
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//
//                .and()
//                .headers()
//                .frameOptions()
//                .sameOrigin()
//
//                .and()
//                .authorizeRequests()
//                .antMatchers(
//                        "/api/v1/login",
//                        "/api/v1/signup",
////                        "/api/v1/test",
//                        "/api/v1/mailsend",
//                        "/api/v1/email/check/*",
//                        "/api/v1/check/code/*",
//                        "/api/v1/health/*",
//                        "/api/v1/health/heart/*",
//                        "/ws/**",
//                        "/**"
//                ).permitAll()
//
//                .anyRequest().authenticated()
//
//                .and()
//                .apply(new JwtSecurityConfig(tokenProvider, customUserDetailsService));
//    }
}