package net.deviceinventory.dao;

import net.deviceinventory.model.Device;
import org.springframework.data.repository.CrudRepository;

public interface DeviceDao extends CrudRepository<Device, Long> {
}
