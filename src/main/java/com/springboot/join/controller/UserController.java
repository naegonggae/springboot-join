package com.springboot.join.controller;

import com.springboot.join.domain.dto.UserJoinRequest;
import com.springboot.join.domain.dto.UserLoinRequest;
import com.springboot.join.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody UserJoinRequest dto) {
        userService.join(dto.getUserName(), dto.getPassword());
        return ResponseEntity.ok().body("회원가입이 성공 했습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoinRequest dto) {
        String token = userService.login(dto.getUserName(), dto.getPassword());
        return ResponseEntity.ok().body(token);
    }
}
