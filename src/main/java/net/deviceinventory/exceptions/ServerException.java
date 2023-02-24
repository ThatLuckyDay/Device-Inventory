package net.deviceinventory.exceptions;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerException extends RuntimeException {
    ErrorCode errorCode;
    List<String> params;

    public ServerException(ErrorCode errorCode, String... params) {
        super();
        this.errorCode = errorCode;
        this.params = Arrays.asList(params);
    }
}

