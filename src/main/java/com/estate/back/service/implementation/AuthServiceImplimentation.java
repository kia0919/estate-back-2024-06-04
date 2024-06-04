package com.estate.back.service.implementation;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.estate.back.common.util.EmailAuthNumberUtil;
import com.estate.back.dto.request.auth.EmailAuthCheckRequestDto;
import com.estate.back.dto.request.auth.EmailAuthRequestDto;
import com.estate.back.dto.request.auth.IdCheckRequestDto;
import com.estate.back.dto.request.auth.SignInRequestDto;
import com.estate.back.dto.request.auth.SignUpRequestDto;
import com.estate.back.dto.response.ResponseDto;
import com.estate.back.dto.response.auth.SignInResponseDto;
import com.estate.back.entity.EmailAuthNumberEntity;
import com.estate.back.entity.UserEntity;
import com.estate.back.provider.JwtProvider;
import com.estate.back.provider.MailProvider;
import com.estate.back.repository.EmailAuthNumberRepository;
import com.estate.back.repository.UserRepository;
import com.estate.back.service.AuthService;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;



// Auth 모듈의 비즈니스 로직 구현체
@Service
@RequiredArgsConstructor
// 의존성 주입
public class AuthServiceImplimentation implements AuthService {

    private final UserRepository userRepository;
    private final EmailAuthNumberRepository emailAuthNumberRepository;

    private final MailProvider mailProvider;
    private final JwtProvider jwtProvider;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    //# 아이디확인 응답처리
    @Override
    public ResponseEntity<ResponseDto> idCheck(IdCheckRequestDto dto) {
        
        try {

            String userId = dto.getUserId();
            boolean existedUser = userRepository.existsByUserId(userId);
            if (existedUser) return ResponseDto.duplicatedId();

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return ResponseDto.success();

    }

    //# 로그인 응답 처리
    @Override
    public ResponseEntity<? super SignInResponseDto> signIn(SignInRequestDto dto) {

        String accessToken = null;
        
        try {

            String userId = dto.getUserId();
            String userPassword = dto.getUserPassword();

            UserEntity userEntity = userRepository.findByUserId(userId);
            if (userEntity == null) return ResponseDto.signInFailed();

            String encodedPasword = userEntity.getUserPassword();
            boolean isMatched = passwordEncoder.matches(userPassword, encodedPasword);
            if (!isMatched) return ResponseDto.signInFailed();

            accessToken = jwtProvider.create(userId);
            if (accessToken == null) return ResponseDto.tokenCreationFailed();

        } catch (Exception exception){
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }
        // 토큰 응답 성공 accessToken으로 반환
        return SignInResponseDto.success(accessToken);

    }

    //# 이메일인증 응답 처리
    @Override
    public ResponseEntity<ResponseDto> emailAuth(EmailAuthRequestDto dto) {    

        try {

            String userEmail = dto.getUserEmail();
            boolean existedEmail = userRepository.existsByUserEmail(userEmail);
            if (existedEmail) return ResponseDto.duplicatedEmail();

            String authNumber = EmailAuthNumberUtil.createNumber();

            EmailAuthNumberEntity emailAuthNumberEntity = new EmailAuthNumberEntity(userEmail, authNumber);
            emailAuthNumberRepository.save(emailAuthNumberEntity);

            mailProvider.mailAuthSend(userEmail, authNumber);

        } catch (MessagingException exception) {
            exception.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }


        return ResponseDto.success();

    }

    //# 이메일 인증 확인 응답 처리
    @Override
    public ResponseEntity<ResponseDto> emailAuthCheck(EmailAuthCheckRequestDto dto) {
        
        try {

            String userEmail = dto.getUserEmail();
            String authNumber = dto.getAuthNumber();

            boolean isMatched = emailAuthNumberRepository.existsByEmailAndAuthNumber(userEmail, authNumber);
            if(!isMatched) return ResponseDto.authenticationFailed();

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return ResponseDto.success();

    }

    //# 회원가입 응답 처리
    @Override
    public ResponseEntity<ResponseDto> signUp(SignUpRequestDto dto) {
    
        try {

            String userId = dto.getUserId();
            String userPassword = dto.getUserPassword();
            String userEmail = dto.getUserEmail();
            String AuthNumber = dto.getAuthNumber();

            //# 데이터베이스의 user 테이블에서 해당하는 userId를 가지고 있는 유저가 있는지 확인하는 역할
            // userRepository.existsById(userId): 입력한 Id가 
            boolean existedUser = userRepository.existsById(userId);
            if (existedUser) return ResponseDto.duplicatedId();

            //# 데이터베이스의 user 테이블에서 해당하는 userEmail를 가지고 있는 유저가 있는지 확인하는 역할
            boolean existedEmail = userRepository.existsByUserEmail(userEmail);
            if (existedEmail) return ResponseDto.duplicatedEmail();

            //# 데이터베이스의 email_auth_number 테이블에서 해당하는 userEmail과 authNumber를 모두 가지고 있는 데이터가 있는지 확인하는 역할
            boolean isMatched = emailAuthNumberRepository.existsByEmailAndAuthNumber(userEmail, AuthNumber);
            if(!isMatched) return ResponseDto.authenticationFailed();

            //# 사용자로부터 입력받은 userPassword를 암호화
            // passwordEncoder.encode(userPassword): userPassword를 암호화하는 역할
            // passwordEncoder: 암호화된 비밀번호를 저장하고, 사용자와 입력한 비밀번호와 비교하여 인증 수행
            String encodeedPassword = passwordEncoder.encode(userPassword);
            dto.setUserPassword(encodeedPassword);
            
            // userEntity객체를 초기화, 데이터베이스에 저장됨
            UserEntity userEntity = new UserEntity(dto);
            userRepository.save(userEntity);

        }catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return ResponseDto.success();

    }
    
}