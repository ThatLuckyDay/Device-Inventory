package net.deviceinventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class DeviceInventoryApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(DeviceInventoryApplication.class, args);
            System.out.println("Hello device");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
