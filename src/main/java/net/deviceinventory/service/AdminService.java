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
public class AdminService {
    UserDtoMapper userDtoMapper;
    UserDao userDao;


    public User appointAdmin(User user) {
        User newAdmin = userDao.findByEmail(user.getEmail()).orElseThrow(() -> new RuntimeException("500"));
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(0, RoleType.ROLE_ADMIN));
        user.setRole(roles);
        newAdmin.setRole(roles);
        userDao.save(newAdmin);
        return newAdmin;
    }


}
