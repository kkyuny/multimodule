package dev.be.moduleapi.exceptionhandler;

import dev.be.moduleapi.exception.CustomException;
import dev.be.moduleapi.response.CommonResponse;
import dev.be.modulecommon.enums.CodeEnum;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public CommonResponse handleCustomException(CustomException e){
        return CommonResponse.builder()
                .returnCode(e.getReturnCode())
                .returnMsg(e.getReturnMsg())
                .build();
    }

    @ExceptionHandler(Exception.class)
    public CommonResponse handleException(Exception e){
        return CommonResponse.builder()
                .returnCode(CodeEnum.UNKNOWN_ERROR.getCode())
                .returnMsg(CodeEnum.UNKNOWN_ERROR.getMessage())
                .build();
    }
}
