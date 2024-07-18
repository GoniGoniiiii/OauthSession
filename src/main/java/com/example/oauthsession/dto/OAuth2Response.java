package com.example.oauthsession.dto;

public interface OAuth2Response {
    
    //제공자 이름(naver,google)
    String getProvider();

    //제공자에서 발급해주는 데이터(번호)
    String getProviderId();

    //사용자 이메일
    String getEmail();

    //사용자 실명(설정한 이름)
    String getName();
}
