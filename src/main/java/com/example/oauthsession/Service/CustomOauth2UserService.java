package com.example.oauthsession.Service;

import com.example.oauthsession.dto.CustomOAuth2User;
import com.example.oauthsession.dto.GoogleResponse;
import com.example.oauthsession.dto.NaverResponse;
import com.example.oauthsession.dto.OAuth2Response;
import com.example.oauthsession.entity.UserEntity;
import com.example.oauthsession.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOauth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        //super를 통해서 부모클래스에 존재하는 loadUSer 메소드에서 userRequest를 넣어주서 user정보를 가져옴
        OAuth2User oAuth2User = super.loadUser(userRequest);

        //가져온 정보 확인해보기
        System.out.println(oAuth2User.getAttributes());

        //OAuth2User로 구글,네이버 다 넘어오기때문에 구글로그인인지 네이버로그인인지를 확인해야함
        String registraion = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = null;
        //둘을 나누는 이유는 인증규격이 달라서 서로 다른 dto바구니에 담아야함
        if (registraion.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        } else if (registraion.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }
        
        //받아온 정보를 db에 저장,업데이트해주면 됨
        String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();

        UserEntity exist=userRepository.findByUsername(username);

        String role=null;
        if(exist == null) {
            UserEntity user=new UserEntity();
            user.setUsername(username);
            user.setEmail(oAuth2Response.getEmail());
            user.setRole("ROLE_USER");

            userRepository.save(user);
        }else{
                role = exist.getRole();
                exist.setEmail(oAuth2Response.getEmail());
                userRepository.save(exist);
        }

        return new CustomOAuth2User(oAuth2Response,role);
    }
}