package net.deviceinventory.dao;

import net.deviceinventory.model.Role;
import net.deviceinventory.model.RoleType;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoleDao extends CrudRepository<Role, Long> {

    Optional<Role> findByRoleName(RoleType roleType);

}
