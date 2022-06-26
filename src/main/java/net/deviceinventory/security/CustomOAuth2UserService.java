package net.deviceinventory.security;

import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        if (!"google".equals(userRequest.getClientRegistration().getRegistrationId())) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("invalid_token", oAuth2User.getAttribute("name"), "")
            );
        }
        return oAuth2User;
    }


}
