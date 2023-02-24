package net.deviceinventory.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.deviceinventory.model.Device;
import net.deviceinventory.model.Role;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    long id;
    String firstName;
    String lastName;
    String email;
    Set<Role> roles;
    List<Device> devices;
}
