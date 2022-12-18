package com.springboot.join.service;

import com.springboot.join.domain.User;
import com.springboot.join.exceprion.AppException;
import com.springboot.join.exceprion.ErrorCode;
import com.springboot.join.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public String join(String userName, String password) {

        // userName 중복 체크
        userRepository.findByUserName(userName)
                // 있으면 메세지 출력해주기 기능
                .ifPresent(user -> {
                    throw new AppException(ErrorCode.USERNAME_DUPLICATED, userName + "는 이미 있습니다.");
                });

        // 저장
        User user = User.builder()
                .userName(userName)
                .password(encoder.encode(password))
                .build();
        userRepository.save(user);

        return "SUCCESS";
    }

    public String login(String userName, String password) {
        // userName 없음
        User selectedUser = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, userName + "이 없습니다."));

        // password 틀림
        if (!encoder.matches(selectedUser.getPassword(), password)) {
            throw new AppException(ErrorCode.INVALID_PASSWORD, "패스워드를 잘못 입력했습니다.");
        }

        // 앞에서 exception 안났으면 token 발행

        return "token 리턴";
    }
}
