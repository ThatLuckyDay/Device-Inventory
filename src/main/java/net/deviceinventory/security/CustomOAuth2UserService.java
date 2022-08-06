package net.deviceinventory.security;

import lombok.NoArgsConstructor;
import net.deviceinventory.dao.UserDao;
import net.deviceinventory.model.Role;
import net.deviceinventory.model.RoleType;
import net.deviceinventory.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@NoArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    @Autowired
    UserDao userDao;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        if (!"google".equals(userRequest.getClientRegistration().getRegistrationId())) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("invalid_token", oAuth2User.getAttribute("name"), "")
            );
        }
        Optional<User> presentUser = userDao.findByEmail(oAuth2User.getAttribute("email"));
        boolean isAdmin = false;
        if (presentUser.isPresent()) isAdmin = presentUser
                .get()
                .getRole()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toList())
                .contains(RoleType.ROLE_ADMIN);
        if (isAdmin) {
            String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
            Set<GrantedAuthority> authorities = new HashSet<>(oAuth2User.getAuthorities());
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            return new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), userNameAttributeName);
        }
        return oAuth2User;
}


}
