package com.springboot.join.service;

import com.springboot.join.domain.User;
import com.springboot.join.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public String join(String userName, String password) {

        // userName 중복 체크
        userRepository.findByUserName(userName)
                // 있으면 메세지 출력해주기 기능
                .ifPresent(user -> {
                    throw  new RuntimeException(userName + "는 이미 있습니다.");
                });

        // 저장
        User user = User.builder()
                .userName(userName)
                .password(password)
                .build();
        userRepository.save(user);

        return "SUCCESS";
    }
}
