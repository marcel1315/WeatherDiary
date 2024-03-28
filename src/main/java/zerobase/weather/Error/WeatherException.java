package zerobase.weather.Error;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherException extends RuntimeException {
    private ErrorCode errorCode;
    private String errorMessage;

    public WeatherException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
