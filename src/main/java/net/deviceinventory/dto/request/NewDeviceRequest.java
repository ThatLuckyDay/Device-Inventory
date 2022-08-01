package net.deviceinventory.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;


@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewDeviceRequest {
    @NotBlank(message = "EMPTY_FIELD")
    String name;
    @NotBlank(message = "EMPTY_FIELD")
    String QRCode;
}
