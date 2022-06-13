package net.deviceinventory.dto.mappers;

import net.deviceinventory.dto.request.RegisterRequest;
import net.deviceinventory.dto.response.UserResponse;
import net.deviceinventory.model.User;
//import net.deviceinventory.security.UserDetailsImpl;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AuthDtoMapper {

//    default UserResponse toUserDto(UserDetailsImpl details) {
//        UserResponse response = new UserResponse();
//        response.setId(details.getId());
//        response.setUsername(details.getUsername());
//        response.setEmail(details.getEmail());
//        List<String> roles = details
//                .getAuthorities()
//                .stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.toList());
//        response.setRoles(roles);
//        return response;
//    }

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(source = "firstName", target = "firstName"),
            @Mapping(source = "lastName", target = "lastName"),
            @Mapping(source = "patronymic", target = "patronymic"),
            @Mapping(source = "email", target = "email"),
            @Mapping(target = "role", ignore = true),
            @Mapping(source = "username", target = "username"),
            @Mapping(source = "password", target = "password"),
            @Mapping(target = "active", ignore = true),
            @Mapping(target = "devices", ignore = true)
    })
    User fromUserDto(RegisterRequest registerRequest);
}
