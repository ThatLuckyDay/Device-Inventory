package net.deviceinventory.security;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    WebClient rest;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User user = super.loadUser(userRequest);
        if (!"google".equals(userRequest.getClientRegistration().getRegistrationId())) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("invalid_token", user.getAttribute("name") +
                            " not in Spring Team", "")
            );
        }

        OAuth2AuthorizedClient client = new OAuth2AuthorizedClient
                (userRequest.getClientRegistration(), user.getName(), userRequest.getAccessToken());
        String url = user.getAttribute("organizations_url");
        if (url == null) throw new OAuth2AuthenticationException(
                new OAuth2Error("invalid_token", user.getAttribute("name") +
                        " not in Spring Team", "")
        );
        List<Map<String, Object>> orgs = rest
                .get().uri(url)
                .attributes(oauth2AuthorizedClient(client))
                .retrieve()
                .bodyToMono(List.class)
                .block();

        if (orgs.stream().anyMatch(org -> "spring-projects".equals(org.get("login")))) {
            return user;
        }
        throw new OAuth2AuthenticationException(
                new OAuth2Error("invalid_token", user.getAttribute("name")
                        + " not in Spring Team", "")
        );
    }


}
