package zerobase.weather.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import zerobase.weather.Error.ErrorCode;
import zerobase.weather.Error.ErrorResponse;
import zerobase.weather.Error.WeatherException;
import zerobase.weather.WeatherApplication;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(WeatherApplication.class);

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(WeatherException.class)
    public ErrorResponse handleWeatherException(WeatherException e) {
        logger.error(e.getErrorMessage());
        return new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleAllException(Exception e) {
        logger.error(e.toString());
        return new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR.getDescription());
    }
}
