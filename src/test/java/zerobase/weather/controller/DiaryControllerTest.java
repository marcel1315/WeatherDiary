package zerobase.weather.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import zerobase.weather.Error.ErrorCode;
import zerobase.weather.Error.WeatherException;
import zerobase.weather.domain.Diary;
import zerobase.weather.service.DiaryService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DiaryController.class)
class DiaryControllerTest {
    @MockBean
    private DiaryService diaryService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("일기 작성 성공")
    void successCreateDiary() throws Exception {
        mockMvc.perform(post("/create/diary?date=2022-02-02")
                .content("hello weather diary"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("일기 작성 실패 - 너무 먼 미래")
    void failCreateDiary() throws Exception {
        //given
        doThrow(new WeatherException(ErrorCode.INVALID_DATE))
                .when(diaryService)
                .createDiary(any(), anyString());

        //then
        mockMvc.perform(post("/create/diary?date=4000-01-01")
                .contentType(MediaType.TEXT_PLAIN)
                .content("this is diary for distant future"))
                .andExpect(jsonPath("$.errorCode").value("INVALID_DATE"));
    }

    @Test
    @DisplayName("일기 수정 성공")
    void successUpdateDiary() throws Exception {
        //given
        mockMvc.perform(post("/create/diary?date=2022-02-02")
                .contentType(MediaType.TEXT_PLAIN)
                .content("hello weather diary"))
                .andExpect(status().isOk());
        // when
        // then
        mockMvc.perform(put("/update/diary?date=2022-02-02")
                .contentType(MediaType.TEXT_PLAIN)
                .content("this is updated diary"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("일기 수정 실패 - 너무 먼 미래")
    void failUpdateDiary() throws Exception {
        //given
        doThrow(new WeatherException(ErrorCode.INVALID_DATE))
                .when(diaryService)
                .updateDiary(any(), anyString());

        //when
        //then
        mockMvc.perform(put("/update/diary?date=4000-01-01")
                .contentType(MediaType.TEXT_PLAIN)
                .content("this is diary for distant future"))
                .andExpect(jsonPath("$.errorCode").value("INVALID_DATE"));
    }

    @Test
    @DisplayName("일기 읽기 성공")
    void successReadDiary() throws Exception {
        //given
        List<Diary> diaries = new ArrayList<>();
        diaries.add(
                new Diary()
        );
        given(diaryService.readDiary(any()))
                .willReturn(diaries);

        // when
        // then
        mockMvc.perform(get("/read/diary?date=2022-02-02"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("일기 제거 성공")
    void successDeleteDiary() throws Exception {
        mockMvc.perform(delete("/delete/diary?date=2022-02-02"))
                .andExpect(status().isOk());
    }
}