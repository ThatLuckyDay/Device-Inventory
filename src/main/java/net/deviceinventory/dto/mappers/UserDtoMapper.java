package net.deviceinventory.dto.mappers;

import net.deviceinventory.model.User;
import org.mapstruct.Mapper;
import org.springframework.security.oauth2.core.user.OAuth2User;


@Mapper(componentModel = "spring")
public interface UserDtoMapper {


    default User fromUserDto(OAuth2User oAuth2User) {
        User user = new User();
        user.setFirstName(oAuth2User.getAttribute("given_name"));
        user.setLastName(oAuth2User.getAttribute("family_name"));
        user.setEmail(oAuth2User.getAttribute("email"));
        return user;
    }
}
