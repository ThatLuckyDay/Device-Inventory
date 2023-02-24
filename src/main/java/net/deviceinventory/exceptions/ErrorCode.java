package net.deviceinventory.exceptions;


public enum ErrorCode {
    FORBIDDEN_SYMBOLS("There are forbidden characters"),
    EMPTY_FIELD("Required field"),
    SERVER_ERROR("undefined", "Server Error"),
    USER_NOT_FOUND("User with email %s not found"),
    ROLE_ALREADY_ADDED("The user = %s is already an administrator"),
    ROLE_ALREADY_DELETE("The user = %s is no longer an administrator"),
    UNDEFINED_ERROR("undefined", "%s"),
    DEVICE_RESERVED("The device is being used by user %s "),
    QR_CODE_EXIST("QR Code already exists by Device with id = %s"),
    QR_CODE_NOT_EXIST("Device with QR Code = %s not exist"),
    DEVICE_NOT_FOUND("Device with id = %s not found");


    private String field;
    private String message;

    ErrorCode(String message) {
        this.message = message;
    }

    ErrorCode(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void formatMessage(String... str) {
        this.message = String.format(this.getMessage(), (Object[]) str);
    }
}

