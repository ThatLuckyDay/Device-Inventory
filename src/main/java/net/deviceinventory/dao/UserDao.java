package net.deviceinventory.dao;

import net.deviceinventory.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserDao extends CrudRepository<User, Long> {

    Optional<User> findByUsername(String username);

}
