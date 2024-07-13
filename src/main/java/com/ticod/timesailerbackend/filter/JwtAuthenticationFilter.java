package com.ticod.timesailerbackend.filter;

import com.ticod.timesailerbackend.entity.User;
import com.ticod.timesailerbackend.service.JwtAuthService;
import com.ticod.timesailerbackend.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtAuthService jwtAuthService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Header 꺼내기
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("Authorization: {}", authorization);

        // AccessToken 없는 경우 Block
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            log.info("authorization 없음");
            filterChain.doFilter(request, response);
            return;
        }

        // AccessToken 꺼내기
        String accessToken = authorization.split(" ")[1].trim();

        // AccessToken 만료시
        if (jwtAuthService.isExpired(accessToken)) {
            log.info("access token 만료");
            
            // RefreshToken Header 꺼내기
            String refreshTokenHeader = request.getHeader("RefreshToken");

            // RefreshToken 없는 경우 Block
            if (refreshTokenHeader == null) {
                log.info("refresh token 없음");
                filterChain.doFilter(request, response);
                return;
            }

            // RefreshToken 꺼내기
            String refreshToken = refreshTokenHeader.split(" ")[1].trim();

            // RefreshToken 에서 email 꺼내기
            String refreshTokenUserEmail = jwtAuthService.getUserEmailFrom(refreshToken);

            // DB 에서 RefreshToken 꺼내기
            User user = userService.getUserByEmail(refreshTokenUserEmail);

            // RefreshToken 동일하지 않은 경우 Block
            if (user == null || !user.getRefreshToken().equals(refreshToken)) {
                log.info("user == null 또는 유효하지 않은 refresh token");
                filterChain.doFilter(request, response);
                return;
            }

            // RefreshToken 유효 기간 체크 및 유효기간 경과시 갱신
            if (jwtAuthService.isExpired(refreshToken)) {
                String newRefreshToken = jwtAuthService.renewRefreshToken(user).getRefreshToken();
                log.info("refresh token 만료, 갱신 : {}", newRefreshToken);
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.addHeader("refreshToken", newRefreshToken);
                filterChain.doFilter(request, response);
                return;
            }

            // access token 갱신
            String newAccessToken = jwtAuthService.createAccessTokenBy(user.getEmail());
            response.addHeader(HttpHeaders.AUTHORIZATION, newAccessToken);

            // Token userId (email) 추출
            String userId = jwtAuthService.getUserEmailFrom(newAccessToken);
            log.info("userId: {}", userId);
            log.info("access token: {}", newAccessToken);

            // 권한 부여 및 setDetails
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userId, null);
            authenticationToken.setDetails(new WebAuthenticationDetailsSource()
                    .buildDetails(request)
            );
            filterChain.doFilter(request, response);
            return;
        }

        // Token userId (email) 추출
        String userId = jwtAuthService.getUserEmailFrom(accessToken);
        log.info("userId: {}", userId);

        // 권한 부여 및 setDetails
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userId,
                        null,
                        List.of(new SimpleGrantedAuthority("USER")));
        authenticationToken.setDetails(new WebAuthenticationDetailsSource()
                .buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}
