package zerobase.weather.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "날씨 일기 프로젝트 :)",
                version = "2.0",
                description = "날씨 일기를 CRUD 할 수 있는 백엔드 API 입니다."
        )
)
public class SwaggerConfig {
}
