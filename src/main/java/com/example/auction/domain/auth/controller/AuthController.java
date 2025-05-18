package com.example.auction.domain.auth.controller;


import java.security.Principal;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    @GetMapping("/auth")
    public void gi(@AuthenticationPrincipal UserDetails userDetails){
        userDetails.getAuthorities();


    }

//    private final AuthService authService;
//
//    @PostMapping("/auth/signup")
//    public SignupResponse signup(@Valid @RequestBody SignupRequest signupRequest) {
//        return authService.signup(signupRequest);
//    }
//
//    @PostMapping("/auth/signin")
//    public SigninResponse signin(@Valid @RequestBody SigninRequest signinRequest) {
//        return authService.signin(signinRequest);
//    }
}
