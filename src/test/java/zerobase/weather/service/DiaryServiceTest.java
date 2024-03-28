package zerobase.weather.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.Error.ErrorCode;
import zerobase.weather.Error.WeatherException;
import zerobase.weather.domain.DateWeather;
import zerobase.weather.domain.Diary;
import zerobase.weather.repository.DateWeatherRepository;
import zerobase.weather.repository.DiaryRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Transactional
@ExtendWith(MockitoExtension.class)
class DiaryServiceTest {

    @Mock
    private DiaryRepository diaryRepository;

    @Mock
    private DateWeatherRepository dateWeatherRepository;

    @InjectMocks
    private DiaryService diaryService;

    @Test
    @DisplayName("일기 생성 성공")
    void successCreateDiary() {
        //given
        DateWeather dateWeather = new DateWeather();
        dateWeather.setTemperature(100);
        dateWeather.setDate(LocalDate.now());
        dateWeather.setIcon("10n");
        dateWeather.setWeather("Sunny");
        ArrayList<DateWeather> dateWeatherArrayList = new ArrayList<>();
        dateWeatherArrayList.add(dateWeather);

        // DB에 날씨 값이 있는 경우, api 를 호출하지 않고 그 값을 사용
        given(dateWeatherRepository.findAllByDate(any()))
                .willReturn(dateWeatherArrayList);

        ArgumentCaptor<Diary> captor = ArgumentCaptor.forClass(Diary.class);

        //when
        diaryService.createDiary(LocalDate.now(), "Today was happy.");

        //then
        verify(diaryRepository, times(1)).save(captor.capture());
        assertEquals("Today was happy.", captor.getValue().getText());
        assertEquals(100, captor.getValue().getTemperature());
    }

    @Test
    @DisplayName("일기 생성 실패 - 너무 먼 미래")
    void failCreateDiary() {
        WeatherException exception = assertThrows(WeatherException.class,
                () -> diaryService.createDiary(LocalDate.now().plusYears(2000), "Today was happy."));
        assertEquals(exception.getErrorCode(), ErrorCode.INVALID_DATE);
    }

    @Test
    @DisplayName("일기 읽기")
    void successReadDiary() {
        //given
        Diary diary = new Diary();
        List<Diary> diaryList = new ArrayList<>();
        diaryList.add(diary);
        given(diaryRepository.findAllByDate(any()))
                .willReturn(diaryList);
        LocalDate date = LocalDate.now();
        ArgumentCaptor<LocalDate> dateCaptor = ArgumentCaptor.forClass(LocalDate.class);

        //when
        diaryService.readDiary(date);

        //then
        verify(diaryRepository, times(1)).findAllByDate(dateCaptor.capture());
        assertEquals(date, dateCaptor.getValue());
    }

    @Test
    @DisplayName("일기 읽기 - 날짜 범위로 읽기")
    void successReadDiaries() {
        //given
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now();
        ArgumentCaptor<LocalDate> startDateCaptor = ArgumentCaptor.forClass(LocalDate.class);
        ArgumentCaptor<LocalDate> endDateCaptor = ArgumentCaptor.forClass(LocalDate.class);

        //when
        diaryService.readDiaries(startDate, endDate);

        //then
        verify(diaryRepository, times(1)).findAllByDateBetween(startDateCaptor.capture(), endDateCaptor.capture());
        assertEquals(startDate, startDateCaptor.getValue());
        assertEquals(endDate, endDateCaptor.getValue());
    }

    @Test
    @DisplayName("일기 수정 성공")
    void successUpdateDiary() {
        //given
        DateWeather dateWeather = new DateWeather();
        dateWeather.setTemperature(100);
        dateWeather.setDate(LocalDate.now());
        dateWeather.setIcon("10n");
        dateWeather.setWeather("Sunny");
        Diary diary = new Diary();
        diary.setId(1);
        diary.setText("Hello");
        diary.setDateWeather(dateWeather);

        given(diaryRepository.getFirstByDate(any()))
                .willReturn(diary);

        ArgumentCaptor<Diary> captor = ArgumentCaptor.forClass(Diary.class);

        //when
        diaryService.updateDiary(LocalDate.now(), "Hello Again");

        //then
        verify(diaryRepository, times(1)).save(captor.capture());
        assertEquals("Hello Again", captor.getValue().getText());
        assertEquals(100, captor.getValue().getTemperature());
    }

    @Test
    @DisplayName("일기 수정 실패 - 너무 먼 미래")
    void failUpdateDiary() {
        WeatherException exception = assertThrows(WeatherException.class,
                () -> diaryService.updateDiary(LocalDate.now().plusYears(2000), "Today was happy."));
        assertEquals(exception.getErrorCode(), ErrorCode.INVALID_DATE);
    }

    @Test
    @DisplayName("일기 삭제 성공")
    void successDeleteDiary() {
        //given
        LocalDate date = LocalDate.now();
        ArgumentCaptor<LocalDate> captor = ArgumentCaptor.forClass(LocalDate.class);

        //when
        diaryService.deleteDiary(date);

        //then
        verify(diaryRepository, times(1)).deleteAllByDate(captor.capture());
        assertEquals(date, captor.getValue());
    }
}