package com.estate.back.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.estate.back.dto.request.auth.EmailAuthCheckRequestDto;
import com.estate.back.dto.request.auth.EmailAuthRequestDto;
import com.estate.back.dto.request.auth.IdCheckRequestDto;
import com.estate.back.dto.request.auth.SignInRequestDto;
import com.estate.back.dto.request.auth.SignUpRequestDto;
import com.estate.back.dto.response.ResponseDto;
import com.estate.back.dto.response.auth.SignInResponseDto;
import com.estate.back.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// Auth 모듈 컨트롤러 : /api/v1/auth

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    //# 로그인 요청을 받고 응답 반환
    //? @RequestBody: 로그인 정보가 포함, @Valid: 요청이 유효한지 검사
    //? authService.signIn(requestBody): 사용자 인증 검사/로그인 성공 여부에 따른 응답 생성
    //? return response: 로그인 성공 여부와 관련된 결과를 응답으로 반환
    @PostMapping("/sign-in")
    public ResponseEntity<? super SignInResponseDto> signIn (
        @RequestBody @Valid SignInRequestDto requestBody
    ) {
        ResponseEntity<? super SignInResponseDto> response = authService.signIn(requestBody);
        return response;
    }
    
    //# 아이디 중복확인 요청을 받고 결과에 따른 응답 반환
    //? @RequestBody: 객체로 반환된 IdCheckRequestDto에 매핑, @Valid: 요청이 유효한지 검사
    //? authService의 idCheck메서드를 호출하여 아이디 중복확인을 처리, 그 결과를 ResponseEntity형식으로 받는다
    //? idCheck 메서드는 클라이언트로부터 받은 ID가 데이터베이스에 이미 존재하는지 확인 후 그 결과에 따른 응답 생성
    @PostMapping("/id-check")
    public ResponseEntity<ResponseDto> idCheck (
        @RequestBody @Valid IdCheckRequestDto requestBody
    ) {
        ResponseEntity<ResponseDto> response = authService.idCheck(requestBody);
        //? 결과에 따른 응답을 반환
        return response;
    }

    //# 이메일 인증 요청을 받고 결과에 따른 응답 반환
    //? @RequestBody: 객체로 반환된 EmailAuthRequestDto에 매핑 / @Valid: 요청이 유효한지 검사
    //? authService의 eamilAuth메서드를 호출하여 이메일 인증 처리를 하고, 그 결과를ResponseEntity형식으로 받는다
    //? emailAuth메서드는 클라이언트로부터 받은 이메일 정보를 검증, 인증 메일을 전송 후 결과를 반환
    @PostMapping("/email-auth")
    public ResponseEntity<ResponseDto> emailAuth (
        @RequestBody @Valid EmailAuthRequestDto requestBody
    ){
        ResponseEntity<ResponseDto> response =authService.emailAuth(requestBody);
        //? 이메일 인증 결과 여부에 따른 응답 반환
        return response;
    }

    //# 이메일 인증 확인요청
    @PostMapping("/email-auth-check")
    public ResponseEntity<ResponseDto> emailAuthCheck (
        @RequestBody @Valid EmailAuthCheckRequestDto requestBody
    ) {
        ResponseEntity<ResponseDto> response = authService.emailAuthCheck(requestBody);
        return response;
    }

    //# 회원가입 요청 
    @PostMapping("/sign-up")
    public ResponseEntity<ResponseDto> signUp (
        @RequestBody @Valid SignUpRequestDto requestBody
    ) {
        ResponseEntity<ResponseDto> response = authService.signUp(requestBody);
        return response;
    }
}

