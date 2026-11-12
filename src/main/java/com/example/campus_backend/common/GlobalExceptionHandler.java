package com.example.campus_backend.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> handleBadRequest(IllegalArgumentException ex) {
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<Void> handleUploadTooBig(MaxUploadSizeExceededException ex) {
        return Result.error("图片不能超过 5MB");
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleOther(Exception ex) {
        ex.printStackTrace();
        return Result.error("服务器繁忙，请稍后再试");
    }
}
