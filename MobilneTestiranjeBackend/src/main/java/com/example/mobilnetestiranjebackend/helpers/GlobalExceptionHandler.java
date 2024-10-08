package com.example.mobilnetestiranjebackend.helpers;

import com.example.mobilnetestiranjebackend.exceptions.*;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, String> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return errors;
    }


    @ExceptionHandler(value
            = InvalidDateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse
    handleInvalidDateException(InvalidDateException ex)
    {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(value
            = NonExistingVerificationCodeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse
    handleNonExistingVerificationCodeException(NonExistingVerificationCodeException ex)
    {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(value
            = NonExistingEntityException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse
    handleNonExistingEntityException(NonExistingEntityException ex)
    {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(value
            = CodeExpiredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse
    handleCodeExpiredException(CodeExpiredException ex)
    {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(value
            = UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse
    handleUserAlreadyExistsException(UserAlreadyExistsException ex)
    {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(value
            = InvalidRepeatPasswordException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse
    handleInvalidRepeatPasswordException(InvalidRepeatPasswordException ex)
        {
            return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(value
            = InvalidEnumValueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse
    handleInvalidRoleException(InvalidEnumValueException ex)
    {
        return new ErrorResponse(ex.getMessage());
    }


    @ExceptionHandler(value
            = EmailNotConfirmedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse
    handleEmailNotConfirmedException(EmailNotConfirmedException ex)
    {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(value
            = InvalidAuthenticationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse
    handleEmailNotConfirmedException(InvalidAuthenticationException ex)
    {
        return new ErrorResponse(ex.getMessage());
    }


    @ExceptionHandler(value
            = UserAlreadyConfirmedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse
    handleUserAlreadyConfirmedException(UserAlreadyConfirmedException ex)
    {
        return new ErrorResponse(ex.getMessage());
    }

//    @ExceptionHandler(value
//            = MissingServletRequestPartException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public @ResponseBody ErrorResponse
//    handleMissingServletRequestPartException(MissingServletRequestPartException ex)
//    {
//        return new ErrorResponse("Image must be uploaded");
//    }


    @ExceptionHandler(value
            = EntityAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse
    handleAccommodationAlreadyExistsException(EntityAlreadyExistsException ex)
    {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(value
            = TooManyFilesException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse
    handleTooManyFilesException(TooManyFilesException ex)
    {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(value
            = InvalidFileExtensionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse
    handleInvalidFileExtensionException(InvalidFileExtensionException ex)
    {
        return new ErrorResponse(ex.getMessage());
    }


    @ExceptionHandler(value
            = InvalidAuthorizationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse
    handleInvalidAuthorizationException(InvalidAuthorizationException ex)
    {
        return new ErrorResponse(ex.getMessage());
    }


//    @ExceptionHandler(HttpMessageNotReadableException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
//        return new ErrorResponse("Invalid enum value");
//    }


    @ExceptionHandler(value
            = InvalidInputException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse
    handleInvalidInputException(InvalidInputException ex)
    {
        return new ErrorResponse(ex.getMessage());
    }


    @ExceptionHandler(value
            = ReservationNotEndedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse
    handleReservationNotEndedException(ReservationNotEndedException ex)
    {
        return new ErrorResponse(ex.getMessage());
    }


    @ExceptionHandler(value
            = ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public @ResponseBody ErrorResponse
    handleExpiredJwtException(ExpiredJwtException ex)
    {
        return new ErrorResponse("Session has expired, please login again");
    }



    @ExceptionHandler(value
            = UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public @ResponseBody ErrorResponse
    handleChangedEmailException(UsernameNotFoundException ex)
    {
        return new ErrorResponse("Email was changed, please login again");
    }
}
