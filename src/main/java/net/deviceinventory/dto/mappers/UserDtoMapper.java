package net.deviceinventory.dto.mappers;

import net.deviceinventory.dto.request.DeviceRequest;
import net.deviceinventory.dto.response.UserResponse;
import net.deviceinventory.model.Device;
import net.deviceinventory.model.Role;
import net.deviceinventory.model.RoleType;
import net.deviceinventory.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Mapper(componentModel = "spring")
public interface UserDtoMapper {

    default User fromUserDto(OAuth2User oAuth2User) {
        User user = new User();
        user.setFirstName(oAuth2User.getAttribute("given_name"));
        user.setLastName(oAuth2User.getAttribute("family_name"));
        user.setEmail(oAuth2User.getAttribute("email"));
        Set<Role> roles = new HashSet<>();
        oAuth2User
                .getAuthorities()
                .forEach(role -> {
                    if (role.getAuthority().equals("ROLE_USER")) roles.add(new Role(0, RoleType.ROLE_USER));
                    if (role.getAuthority().equals("ROLE_ADMIN")) roles.add(new Role(0, RoleType.ROLE_ADMIN));
                });
        user.setRole(roles);
        return user;
    }

    default UserResponse toUserDto(User user, List<Device> devices) {
        UserResponse userDto = new UserResponse();
        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setRoles(user.getRole());
        userDto.setDevices(devices);
        return userDto;
    }

    @Mappings({
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "QRCode", target = "QRCode"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "user", ignore = true)
    })
    Device fromDeviceDto(DeviceRequest request);
}
