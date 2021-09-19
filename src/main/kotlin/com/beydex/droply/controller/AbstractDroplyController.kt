package com.beydex.droply.controller

import com.beydex.droply.controller.error.ErrorCodes
import com.beydex.droply.dto.BaseResponse
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.messaging.handler.annotation.MessageExceptionHandler
import org.springframework.messaging.handler.invocation.MethodArgumentResolutionException
import org.springframework.stereotype.Controller
import org.springframework.validation.BindException
import org.springframework.web.bind.MethodArgumentNotValidException
import reactor.core.publisher.Mono
import java.util.stream.Collectors
import javax.validation.ConstraintViolationException

@Controller
abstract class AbstractDroplyController {
    @MessageExceptionHandler(MethodArgumentNotValidException::class)
    open fun methodArgumentNotValidExceptionHandler(e: MethodArgumentNotValidException): Mono<BaseResponse<*>> {
        val message = e.bindingResult.allErrors.stream()
            .map { it.defaultMessage }
            .collect(Collectors.joining(";"))
        return Mono.just(BaseResponse.fail(ErrorCodes.INVALID_VALUE, message))
    }

    @MessageExceptionHandler(ConstraintViolationException::class)
    open fun constraintViolationExceptionHandler(e: ConstraintViolationException): Mono<BaseResponse<*>> {
        val message = e.constraintViolations
            .stream()
            .map { it.message }
            .collect(Collectors.joining(";"))
        return Mono.just(BaseResponse.fail(ErrorCodes.INVALID_VALUE, message))
    }

    @MessageExceptionHandler(BindException::class)
    open fun bindExceptionHandler(e: BindException): BaseResponse<*>? {
        val message = e.bindingResult
            .allErrors
            .stream()
            .map { it.defaultMessage }
            .collect(Collectors.joining(";"))
        return BaseResponse.fail(ErrorCodes.INVALID_VALUE, message)
    }

    @MessageExceptionHandler(HttpMessageNotReadableException::class)
    open fun httpMessageNotReadableExceptionHandler(e: HttpMessageNotReadableException): Mono<BaseResponse<*>> {
        return Mono.just(BaseResponse.fail(ErrorCodes.INVALID_VALUE, ErrorCodes.INVALID_VALUE.description))
    }

    @MessageExceptionHandler(MethodArgumentResolutionException::class)
    open fun methodArgumentResolutionExceptionHandler(e: MethodArgumentResolutionException): Mono<BaseResponse<*>> {
        return Mono.just(BaseResponse.fail(ErrorCodes.INVALID_VALUE, ErrorCodes.INVALID_VALUE.description))
    }

    @MessageExceptionHandler(Exception::class)
    open fun exceptionHandler(e: Exception): Mono<BaseResponse<*>> {
        e.printStackTrace()
        return Mono.just(BaseResponse.fail(ErrorCodes.INTERNAL_ERROR, ""))
    }
}
