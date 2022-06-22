package net.deviceinventory.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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

    public User signIn(OAuth2User oAuth2User) {
        User user = userDtoMapper.fromUserDto(oAuth2User);
        if (userDao.existsByEmail(user.getEmail())) return userDao.findByEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("500"));
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(0, RoleType.ROLE_USER));
        user.setRole(roles);
        user.setActive(true);
        userDao.save(user);
        return user;
    }

    public User signOut(OAuth2User oAuth2User) {
        return null;
    }

    public User leave(OAuth2User oAuth2User) {
        User user = userDtoMapper.fromUserDto(oAuth2User);
        if (!userDao.existsByEmail(user.getEmail())) throw new RuntimeException("User not found");
        user.setActive(false);
        userDao.save(user);
        return user;
    }

}
