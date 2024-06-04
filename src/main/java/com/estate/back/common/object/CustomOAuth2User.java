package com.estate.back.common.object;

import java.util.Collections;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

//# 회원가입한 사용자 정보를 관리하기 위한 CustomOAuth2User클래스 정의


//# CustomOAuth2User클래스는 인터페이스 OAuth2User를 정의
//! OAuth2User: OAuth모듈에서 제공되는 것 / 사용자 정보를 가져오는 역할을 함
public class CustomOAuth2User implements OAuth2User {
    // 사용자 id저장
    private String id;
    // 사용자 속성을 저장하는 맵을 선언(이 맵은 사용자의 추가정보를 저장할 수 있음.)
    //? 맵의 key가 문자열(String), 값(value)가 Object(객체)이다 / key: 사용자의 정보를 식별하는데 사용되는 문자열 / 값은 해당 정보의 내용을 나타내는 객체
    private Map<String, Object> attributes;
    // 사용자 권한을 저장하는 컬렉션을 GrantedAuthority객체의 컬렉션으로 관리?
    // 이 컬렉션은 GrantedAuthority을 구현한 객체들을 저장
    private Collection<? extends GrantedAuthority> authorities;

    //# 생성자CustomOAuth2User 사용
    // 사용자의 id속성을 받아옴
    public CustomOAuth2User(String id, Map<String, Object> attributes) {
        this.id = id;
        this.attributes = attributes;
        // 사용자 권한은 ROLE_USER으로 지정
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }
    
    //# 메서드 / 속성 반환
    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    //# 메서드 / 권한 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    //# 메서드 / 사용자 이름 반환
    @Override
    public String getName() {
        return this.id;
    }
    
}