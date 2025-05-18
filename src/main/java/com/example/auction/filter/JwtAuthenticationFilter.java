package com.example.auction.filter;

import static com.example.auction.common.exception.ErrorCode.EXPIRED_JWT_TOKEN;
import static com.example.auction.common.exception.ErrorCode.INVALID_JWT_SIGNATURE;
import static com.example.auction.common.exception.ErrorCode.INVALID_JWT_TOKEN;
import static com.example.auction.common.exception.ErrorCode.UNKNOWN_JWT_ERROR;
import static com.example.auction.common.exception.ErrorCode.UNSUPPORTED_JWT_TOKEN;

import com.example.auction.filter.dto.FilterErrorDto;
import com.example.auction.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain chain) throws ServletException, IOException {

        String bearerJwt = request.getHeader("Authorization");

        if (bearerJwt == null || !bearerJwt.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String jwt = jwtUtil.substringToken(bearerJwt);

        try {
            // JWT 유효성 검사와 claims 추출
            Claims claims = jwtUtil.extractClaims(jwt);
            if (claims == null) {
                setErrorResponse(request, response, INVALID_JWT_SIGNATURE.getHttpStatus(), INVALID_JWT_SIGNATURE.getMessage());
                return;
            }
            Authentication authentication = jwtUtil.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            chain.doFilter(request, response);
        }catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.", e);
            setErrorResponse(request, response, EXPIRED_JWT_TOKEN.getHttpStatus(), EXPIRED_JWT_TOKEN.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.", e);
            setErrorResponse(request, response, UNSUPPORTED_JWT_TOKEN.getHttpStatus(), UNSUPPORTED_JWT_TOKEN.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.", e);
            setErrorResponse(request, response, INVALID_JWT_TOKEN.getHttpStatus(), INVALID_JWT_TOKEN.getMessage());
        } catch (Exception e) {
            log.error("Internal server error", e);
            setErrorResponse(request, response, UNKNOWN_JWT_ERROR.getHttpStatus(), UNKNOWN_JWT_ERROR.getMessage());
        }

    }
    private void setErrorResponse(HttpServletRequest request, HttpServletResponse response,
        HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");

        FilterErrorDto error = new FilterErrorDto(
            status.value(),
            message,
            LocalDateTime.now(),
            request.getRequestURI()
        );

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        String json = objectMapper.writeValueAsString(error);
        response.getWriter().write(json);
    }

}
