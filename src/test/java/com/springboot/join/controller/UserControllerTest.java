package com.springboot.join.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.join.domain.dto.UserJoinRequest;
import com.springboot.join.domain.dto.UserLoinRequest;
import com.springboot.join.exceprion.AppException;
import com.springboot.join.exceprion.ErrorCode;
import com.springboot.join.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean // 흉내낼거
    UserService userService;

    @Autowired
    ObjectMapper objectMapper; // 자바 오브젝트를 json으로 만들어

    @Test
    @DisplayName("회원가입 성공")
    void join_Success() throws Exception {
        String userName = "dalnim";
        String password = "1234";

        mockMvc.perform(post("/api/v1/users/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserJoinRequest(userName, password)))) //httpsRequest에 값을 보낼때는 Byte로 보냄
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원가입 실패 - userName 중복")
    void join_Fail() throws Exception {
        String userName = "dalnim";
        String password = "1234";

        when(userService.join(any(), any()))
                .thenThrow(new RuntimeException("해당 userId가 중복됩니다."));

        mockMvc.perform(post("/api/v1/users/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserJoinRequest(userName, password)))) //httpsRequest에 값을 보낼때는 Byte로 보냄
                .andDo(print())
                .andExpect(status().isConflict());
    }

    //테스트 코드 작성
    //Spring Security Test 라이브러리 import.with(csrf()) 사용하기
    //
    //성공 —> Success
    //실패 - id없음 —> Not Found
    //실패 - 잘못된 password 입력 —> Unauthorized
    @Test
    @DisplayName("로그인 성공")
    @WithAnonymousUser // security test gradle 추가하고 작성
    void loin_Success() throws Exception {
        String userName = "dalnim";
        String password = "1234";

        when(userService.login(any(), any()))
                .thenReturn("token");

        mockMvc.perform(post("/api/v1/users/loin")
                        .with(csrf())// security test gradle 추가하고 작성
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoinRequest(userName, password))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("로그인 실패 - userName 없음")
    @WithAnonymousUser // security test gradle 추가하고 작성
    void loin_Fail1() throws Exception {
        String userName = "dalnim";
        String password = "1234";

        when(userService.login(any(), any()))
                .thenThrow(new AppException(ErrorCode.USERNAME_NOT_FOUND, ""));

        mockMvc.perform(post("/api/v1/users/loin")
                        .with(csrf())// security test gradle 추가하고 작성
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoinRequest(userName, password))))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("로그인 실패 - password 없음")
    @WithAnonymousUser // security test gradle 추가하고 작성
    void loin_Fail2() throws Exception {
        String userName = "dalnim";
        String password = "1234";

        when(userService.login(any(), any()))
                .thenThrow(new AppException(ErrorCode.INVALID_PASSWORD, ""));

        mockMvc.perform(post("/api/v1/users/loin")
                        .with(csrf()) // security test gradle 추가하고 작성
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoinRequest(userName, password))))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}