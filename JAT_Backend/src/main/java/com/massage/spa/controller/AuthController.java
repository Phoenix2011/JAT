package com.massage.spa.controller;

import com.massage.spa.common.result.Result;
import com.massage.spa.security.JwtAuthenticationProvider;
import com.massage.spa.util.JwtTokenUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/login")
    public Result<?> login(@RequestBody LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(authToken);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // 登录成功后生成JWT并返回
        String token = jwtTokenUtil.generateToken(userDetails);
        return Result.success(new TokenResponse(token));
    }

    // 登录请求体
    @Getter
    public static class LoginRequest {
        private String username;
        private String password;
    }

    // 新增 TokenResponse 类
    @Getter
    @Setter
    public static class TokenResponse {
        private String token;
        public TokenResponse(String token) {
            this.token = token;
        }
    }
}