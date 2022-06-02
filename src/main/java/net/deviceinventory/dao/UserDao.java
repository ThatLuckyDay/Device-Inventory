package net.deviceinventory.dao;

import net.deviceinventory.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserDao extends CrudRepository<User, Long> {
}
