package net.deviceinventory.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.deviceinventory.dao.RoleDao;
import net.deviceinventory.dao.UserDao;
import net.deviceinventory.dto.mappers.UserDtoMapper;
import net.deviceinventory.model.Role;
import net.deviceinventory.model.RoleType;
import net.deviceinventory.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

@Service
@NoArgsConstructor
@AllArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(level = AccessLevel.PRIVATE)
@Transactional
@Slf4j
public class UserService {
    UserDtoMapper userDtoMapper;
    UserDao userDao;
    RoleDao roleDao;

    public User login(OAuth2User oAuth2User) {
        return new User();
    }

    public User registerClient(OAuth2User oAuth2User) {
        User user = userDtoMapper.fromUserDto(oAuth2User);
        if (userDao.existsByEmail(user.getEmail())) throw new RuntimeException("User already present");
        if (userDao.existsByUsername(user.getUsername())) throw new RuntimeException("User already present");
        Set<Role> roles = new HashSet<>();
        Role role = new Role();
        role.setName(RoleType.ROLE_USER);
        roles.add(role);
        user.setRole(roles);
        user.setActive(true);
        userDao.save(user);
        return user;
    }

    public User registerAdmin(OAuth2User oAuth2User) {
        User user = userDtoMapper.fromUserDto(oAuth2User);
        if (userDao.existsByEmail(user.getEmail())) throw new RuntimeException("User already present");
        if (userDao.existsByUsername(user.getUsername())) throw new RuntimeException("User already present");
        Set<Role> roles = new HashSet<>();
        Role role = new Role();
        role.setName(RoleType.ROLE_ADMIN);
        roles.add(role);
        user.setRole(roles);
        user.setActive(true);
        userDao.save(user);
        return user;
    }

    public Set<Role> parseRoles(Set<String> strRoles) {
        Set<Role> roleSet = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleDao.findByName(RoleType.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roleSet.add(userRole);
        } else {
            strRoles.forEach(role -> {
                if ("admin".equals(role)) {
                    Role adminRole = roleDao.findByName(RoleType.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roleSet.add(adminRole);
                } else {
                    Role userRole = roleDao.findByName(RoleType.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roleSet.add(userRole);
                }
            });
        }
        return roleSet;
    }


}
