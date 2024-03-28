package zerobase.weather.Error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INVALID_DATE("너무 과거 혹은 미래의 날짜입니다."),
    INTERNAL_SERVER_ERROR("서버에 오류가 있습니다."),
    URL_ERROR("URL 이 잘못되었습니다."),
    GET_WEATHER_FAIL("날씨 정보를 가져오는데 실패했습니다."),
    PARSE_WEATHER_ERROR("날씨 정보를 해석하는데 실패했습니다.");

    private final String description;
}
