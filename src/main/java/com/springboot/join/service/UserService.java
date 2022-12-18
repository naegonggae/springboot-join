package com.springboot.join.service;

import com.springboot.join.domain.User;
import com.springboot.join.exceprion.AppException;
import com.springboot.join.exceprion.ErrorCode;
import com.springboot.join.repository.UserRepository;
import com.springboot.join.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    @Value("${jwt.token.secret}") // application.yml에 껍데기 정보있음 , 실제는 환경변수에 있음
    private String key;
    private Long expireTimeMs = 1000 * 60 * 60l; // 1시간(=1초 * 60 * 60)

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
        log.info("selectedPw:{}, Pw:{}", selectedUser.getPassword(), password);
        if (!encoder.matches(password, selectedUser.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD, "패스워드를 잘못 입력했습니다.");
        }

        String token = JwtTokenUtil.createToken(selectedUser.getUserName(), key, expireTimeMs);
        // 앞에서 exception 안났으면 token 발행

        return token;
    }
}
