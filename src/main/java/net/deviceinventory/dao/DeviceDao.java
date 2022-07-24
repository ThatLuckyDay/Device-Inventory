package net.deviceinventory.dao;

import net.deviceinventory.model.Device;
import net.deviceinventory.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceDao extends CrudRepository<Device, Long> {

    boolean existsByQRCode(String QRCode);

    Optional<Device> findByQRCode(String QRCode);

    List<Device> findByUser(User user);

}
