package net.deviceinventory.dto.mappers;

import net.deviceinventory.dto.response.UserResponse;
import net.deviceinventory.security.UserDetailsImpl;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AuthDtoMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "username", target = "username"),
            @Mapping(source = "email", target = "email"),
            @Mapping(source = "authorities", target = "roles")
    })
    default UserResponse toUserDto(UserDetailsImpl details) {
        UserResponse response = new UserResponse();
        List<String> roles = details
                .getAuthorities()
                .stream()
                .map(Object::toString)
                .collect(Collectors.toList());
        response.setRoles(roles);
        return response;
    }

}
