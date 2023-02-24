package net.deviceinventory.dto.mappers;

import net.deviceinventory.dto.request.NewDeviceRequest;
import net.deviceinventory.model.Device;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface AdminDtoMapper {

    @Mappings({
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "QRCode", target = "QRCode"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "user", ignore = true)
    })
    Device fromDeviceDto(NewDeviceRequest request);
}
