package com.springboot.join.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.join.domain.dto.UserJoinRequest;
import com.springboot.join.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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
}