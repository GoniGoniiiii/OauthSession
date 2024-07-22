package com.example.oauthsession.config;

import com.example.oauthsession.Service.CustomOauth2UserService;
import com.example.oauthsession.oauth2.CustomClientRegistrationRepo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomOauth2UserService customOauth2UserService;
    private final CustomClientRegistrationRepo customClientRegistrationRepo;

    public SecurityConfig(CustomOauth2UserService customOauth2UserService, CustomClientRegistrationRepo customClientRegistrationRepo) {
        this.customOauth2UserService = customOauth2UserService;
        this.customClientRegistrationRepo = customClientRegistrationRepo;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)throws Exception{
        http
                .csrf((csrf)-> csrf.disable());

        http
                .formLogin((login)-> login.disable());

        http
                .httpBasic((basic) -> basic.disable());

        //oauth2변수를 가지고 userEndPoint설정을 지정해야함
        //userEndPoint가 우리가 데이터를 받을수 잇는 userDetailsService를 등록해주는 endPoint라는 뜻
        //
        http
                .oauth2Login((oauth2)-> oauth2
                        .loginPage("/login")
                        .clientRegistrationRepository(customClientRegistrationRepo.clientRegistrationRepository())
                        .userInfoEndpoint((userInfoEndpointConfig) ->
                                userInfoEndpointConfig.userService(customOauth2UserService)));

        http
                .authorizeHttpRequests((auth)->auth
                        .requestMatchers("/","oauth2/**","/login/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
