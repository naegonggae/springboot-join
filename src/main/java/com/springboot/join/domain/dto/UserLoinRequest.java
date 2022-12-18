package com.springboot.join.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserLoinRequest {
    private String userName;
    private String password;
}
