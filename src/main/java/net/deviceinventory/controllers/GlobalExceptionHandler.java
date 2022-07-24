package net.deviceinventory.controllers;

import net.deviceinventory.exceptions.ErrorCode;
import net.deviceinventory.exceptions.ServerException;
import net.deviceinventory.exceptions.ServerExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ServerExceptionResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        final List<ErrorCode> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> {
                    ErrorCode errorCode = ErrorCode.valueOf(error.getDefaultMessage());
                    errorCode.setField(error.getField());
                    return errorCode;
                })
                .collect(Collectors.toList());
        final List<ErrorCode> typeErrors = ex.getBindingResult().getAllErrors().stream()
                .filter(objectError -> !(objectError instanceof FieldError))
                .map(error -> {
                    ErrorCode errorCode = ErrorCode.valueOf(error.getDefaultMessage());
                    errorCode.setField("request");
                    return errorCode;
                })
                .collect(Collectors.toList());
        return new ServerExceptionResponse(Stream.of(fieldErrors, typeErrors)
                .flatMap(Collection::stream)
                .collect(Collectors.toList())
        );
    }

    @ResponseBody
    @ExceptionHandler(value = ServerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ServerExceptionResponse handleBuscompanyServerException(ServerException ex) {
        final List<ErrorCode> violations = Stream.of(ex.getErrorCode())
                .peek(errorCode -> {
                    errorCode.formatMessage(ex.getParams().toArray(String[]::new));
                })
                .collect(Collectors.toList());
        return new ServerExceptionResponse(violations);
    }

    @ResponseBody
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ServerExceptionResponse handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex) {
        final List<ErrorCode> violations = Stream.of(ErrorCode.UNDEFINED_ERROR)
                .peek(errorCode -> {
                    errorCode.formatMessage(ex.getMessage());
                    errorCode.setField("undefined");
                })
                .collect(Collectors.toList());
        return new ServerExceptionResponse(violations);
    }

    @ResponseBody
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ServerExceptionResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        final List<ErrorCode> violations = Stream.of(ErrorCode.UNDEFINED_ERROR)
                .peek(errorCode -> {
                    errorCode.formatMessage(ex.getMessage());
                    errorCode.setField("undefined");
                })
                .collect(Collectors.toList());
        return new ServerExceptionResponse(violations);
    }

    @ResponseBody
    @ExceptionHandler(value = NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ServerExceptionResponse handleNoHandlerFoundException(NoHandlerFoundException ex) {
        final List<ErrorCode> violations = Stream.of(ErrorCode.UNDEFINED_ERROR)
                .peek(errorCode -> {
                    errorCode.formatMessage(ex.getMessage());
                    errorCode.setField("undefined");
                })
                .collect(Collectors.toList());
        return new ServerExceptionResponse(violations);
    }

    @ResponseBody
    @ExceptionHandler(value = Throwable.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ServerExceptionResponse handleThrowableException(Throwable ex) {
        final List<ErrorCode> violations = Stream.of(ErrorCode.UNDEFINED_ERROR)
                .peek(errorCode -> {
                    errorCode.formatMessage(ex.getMessage());
                    errorCode.setField("undefined");
                })
                .collect(Collectors.toList());
        return new ServerExceptionResponse(violations);
    }

}

