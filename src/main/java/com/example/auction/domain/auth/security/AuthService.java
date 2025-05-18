package com.example.auction.domain.auth.security;

import com.example.auction.domain.user.repository.UserRepository;
import com.example.auction.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

/*    @Transactional
    public SignupResponse signup(SignupRequest signupRequest) {

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new InvalidRequestException("이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());


        User savedUser = userRepository.save(newUser);

        String bearerToken = jwtUtil.createToken(savedUser.getId(), savedUser.getEmail(),
            savedUser.getUserName(),
            userRole);

        String token =  jwtUtil.substringToken(bearerToken);; // Bearer 토큰 형식으로 반환되서 잘라서 토큰만 전달

        return new SignupResponse( (String) jwtUtil.extractClaims(token).get("userName"));
    }*/

//    public SigninResponse signin(SigninRequest signinRequest) {
//        User user = userRepository.findByEmail(signinRequest.getEmail()).orElseThrow(
//            () -> new InvalidRequestException("가입되지 않은 유저입니다."));
//
//        // 로그인 시 이메일과 비밀번호가 일치하지 않을 경우 401을 반환합니다.
//        if (!passwordEncoder.matches(signinRequest.getPassword(), user.getPassword())) {
//            throw new AuthException("잘못된 비밀번호입니다.");
//        }
//
//        String bearerToken = jwtUtil.createToken(user.getId(), user.getEmail(), user.getUserName(),
//            user.getUserRole());
//        String token =  jwtUtil.substringToken(bearerToken);; // Bearer 토큰 형식으로 반환되서 잘라서 토큰만 전달
//
//        return new SigninResponse(bearerToken, (String) jwtUtil.extractClaims(token).get("userName"));
//    }
}
