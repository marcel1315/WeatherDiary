package zerobase.weather.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.Error.ErrorCode;
import zerobase.weather.Error.WeatherException;
import zerobase.weather.WeatherApplication;
import zerobase.weather.domain.DateWeather;
import zerobase.weather.domain.Diary;
import zerobase.weather.repository.DateWeatherRepository;
import zerobase.weather.repository.DiaryRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional(readOnly = true)
@Service
public class DiaryService {

    @Value("${openweathermap.key}")
    private String apiKey;

    private final DiaryRepository diaryRepository;
    private final DateWeatherRepository dateWeatherRepository;

    private static final Logger logger = LoggerFactory.getLogger(WeatherApplication.class);

    public DiaryService(DiaryRepository diaryRepository,
                        DateWeatherRepository dateWeatherRepository) {
        this.diaryRepository = diaryRepository;
        this.dateWeatherRepository = dateWeatherRepository;
    }

    @Transactional(readOnly = false)
    @Scheduled(cron = "0 0 1 * * *")
    public void saveWeatherDate() {
        logger.info("오늘도 날씨 데이터를 잘 가져옴");
        dateWeatherRepository.save(getWeatherFromApi(LocalDate.now()));
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    public void createDiary(LocalDate date, String text) {
        if (date.isAfter(LocalDate.ofYearDay(3900, 1))) {
            throw new WeatherException(ErrorCode.INVALID_DATE);
        }

        logger.info("start to create diary");
        DateWeather dateWeather = getDateWeather(date);

        Diary nowDiary = new Diary();
        nowDiary.setDateWeather(dateWeather);
        nowDiary.setText(text);
        diaryRepository.save(nowDiary);
        logger.info("end to create diary");
    }

    public List<Diary> readDiary(LocalDate date) {
        logger.info("read diary");
        return diaryRepository.findAllByDate(date);
    }

    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
        logger.info("read diaries");
        return diaryRepository.findAllByDateBetween(startDate, endDate);
    }

    @Transactional(readOnly = false)
    public void updateDiary(LocalDate date, String text) {
        if (date.isAfter(LocalDate.ofYearDay(3900, 1))) {
            throw new WeatherException(ErrorCode.INVALID_DATE);
        }

        logger.info("start to update diary");
        Diary nowDiary = diaryRepository.getFirstByDate(date);
        nowDiary.setText(text);
        diaryRepository.save(nowDiary);
        logger.info("end to update diary");
    }

    @Transactional(readOnly = false)
    public void deleteDiary(LocalDate date) {
        logger.info("start to delete diary");
        diaryRepository.deleteAllByDate(date);
        logger.info("end to delete diary");
    }

    private DateWeather getWeatherFromApi(LocalDate date) {
        // 날씨 데이터 가져오기. (API 또는 DB 에서 가져오기)
        String weatherData = getWeatherString();

        Map<String, Object> parsedWeather = parseWeather(weatherData);
        DateWeather dateWeather = new DateWeather();
        dateWeather.setDate(date);
        dateWeather.setWeather(parsedWeather.get("main").toString());
        dateWeather.setIcon(parsedWeather.get("icon").toString());
        dateWeather.setTemperature((Double) parsedWeather.get("temp"));
        return dateWeather;
    }

    private DateWeather getDateWeather(LocalDate date) {
        List<DateWeather> dateWeatherListFromDB = dateWeatherRepository.findAllByDate(date);
        if (dateWeatherListFromDB.isEmpty()) {
            // 새로 api 에서 날씨 정보를 가져와야한다
            // 현재 날씨를 가져오도록 함
            return getWeatherFromApi(date);
        } else {
            return dateWeatherListFromDB.get(0);
        }
    }

    private String getWeatherString() {
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=" + apiKey;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

            return response.toString();
        } catch (MalformedURLException e) {
            throw new WeatherException(ErrorCode.URL_ERROR);
        } catch (IOException e) {
            throw new WeatherException(ErrorCode.GET_WEATHER_FAIL);
        }
    }

    private Map<String, Object> parseWeather(String jsonString) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;

        try {
            jsonObject = (JSONObject) jsonParser.parse(jsonString);
        } catch (ParseException e) {
            throw new WeatherException(ErrorCode.PARSE_WEATHER_ERROR);
        }
        Map<String, Object> resultMap = new HashMap<>();

        JSONObject mainData = (JSONObject) jsonObject.get("main");
        resultMap.put("temp", mainData.get("temp"));
        JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
        JSONObject weatherData = (JSONObject) weatherArray.get(0);
        resultMap.put("main", weatherData.get("main"));
        resultMap.put("icon", weatherData.get("icon"));
        return resultMap;
    }
}
