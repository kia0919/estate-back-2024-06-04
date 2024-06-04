package com.estate.back.service.implementation;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.estate.back.dto.response.ResponseDto;
import com.estate.back.dto.response.user.GetSignInUserResponseDto;
import com.estate.back.entity.UserEntity;
import com.estate.back.repository.UserRepository;
import com.estate.back.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService {
    // 의존성 주입
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<? super GetSignInUserResponseDto> getSignInUser(String userId) {

        UserEntity userEntity = null;

        try {

            userEntity = userRepository.findByUserId(userId);
            if (userEntity == null) return ResponseDto.authenticationFailed();

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }
        // Get메서드에서
        return GetSignInUserResponseDto.success(userEntity);

    }
    
}
