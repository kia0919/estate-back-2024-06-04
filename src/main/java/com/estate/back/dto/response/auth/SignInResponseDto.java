package com.estate.back.dto.response.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.estate.back.dto.response.ResponseCode;
import com.estate.back.dto.response.ResponseDto;
import com.estate.back.dto.response.ResponseMessage;

import lombok.Getter;

//# 로그인 Response Body DTO
@Getter
public class SignInResponseDto extends ResponseDto {
    
    private String accessToken;
    private int expires;

    private SignInResponseDto(String accessToken) {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
        this.accessToken = accessToken;
        // expires 생성자를 int타입으로 해서 정수 타이핑 가능
        // JWT 만료시간을 10시간으로 지정한 걸 초단위로 계산(10시간 * 60분 * 60초)
        this.expires = 10 * 60 * 60;
    }

    public static ResponseEntity<SignInResponseDto> success(String accessToken) {
        SignInResponseDto responseBody = new SignInResponseDto(accessToken);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

}
