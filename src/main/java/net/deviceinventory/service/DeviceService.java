package net.deviceinventory.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.deviceinventory.dao.DeviceDao;
import net.deviceinventory.dto.mappers.AdminDtoMapper;
import net.deviceinventory.dto.request.NewDeviceRequest;
import net.deviceinventory.exceptions.ErrorCode;
import net.deviceinventory.exceptions.ServerException;
import net.deviceinventory.model.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

@Service
@NoArgsConstructor
@AllArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(level = AccessLevel.PRIVATE)
@Transactional
@Slf4j
public class DeviceService {
    AdminDtoMapper mapper;
    DeviceDao deviceDao;

    public Device addDevice(NewDeviceRequest newDevice) {
        Device device = mapper.fromDeviceDto(newDevice);
        Optional<Device> deviceByQR = deviceDao.findByQRCode(device.getQRCode());
        if (deviceByQR.isPresent()) {
            throw new ServerException(ErrorCode.QR_CODE_EXIST, String.valueOf(deviceByQR.get().getId()));
        }
        return deviceDao.save(device);
    }

    public Device editDevice(NewDeviceRequest newDevice) {
        Device device = mapper.fromDeviceDto(newDevice);
        Optional<Device> deviceByQR = deviceDao.findByQRCode(device.getQRCode());
        if (deviceByQR.isPresent()) {
            if (deviceByQR.get().getId() != device.getId()) {
                throw new ServerException(ErrorCode.QR_CODE_EXIST, String.valueOf(deviceByQR.get().getId()));
            }
        }
        return deviceDao.save(device);
    }

    public Device deleteDevice(Long id) {
        Device device = deviceDao
                .findById(id)
                .orElseThrow(() -> new ServerException(ErrorCode.DEVICE_NOT_FOUND, String.valueOf(id)));
        deviceDao.deleteById(id);
        return device;
    }

    public Device getDevice(Long id) {
        return deviceDao
                .findById(id)
                .orElseThrow(() -> new ServerException(ErrorCode.DEVICE_NOT_FOUND, String.valueOf(id)));
    }

    public byte[] generateQRCodeImage(String text, int width, int height) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = null;
        try {
            bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        try {
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return pngOutputStream.toByteArray();
    }

}
