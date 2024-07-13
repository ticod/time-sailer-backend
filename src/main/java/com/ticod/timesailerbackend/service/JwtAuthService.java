package com.ticod.timesailerbackend.service;

import com.ticod.timesailerbackend.dto.Tokens;
import com.ticod.timesailerbackend.entity.User;
import com.ticod.timesailerbackend.exception.AppException;
import com.ticod.timesailerbackend.exception.ErrorCode;
import com.ticod.timesailerbackend.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Service
public class JwtAuthService {

    // jwt data
    private final SecretKey key;
    private final Long accessTokenExpireTime;
    private final Long refreshTokenExpireTime;

    // components
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public JwtAuthService(@Value("${jwt.secret-key}") String key,
                          @Value("${jwt.expire-time.access-token}") String accessTokenExpireTime,
                          @Value("${jwt.expire-time.refresh-token}") String refreshTokenExpireTime,
                          UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));
        this.accessTokenExpireTime = Long.parseLong(accessTokenExpireTime);
        this.refreshTokenExpireTime = Long.parseLong(refreshTokenExpireTime);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 회원가입 후 토큰 반환
     * @param user 유저 엔티티
     * @return 엑세스 토큰 및 리프레쉬 토큰
     */
    public Tokens join(User user) {
        // DB 에 유저 존재시 Exception
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new AppException(ErrorCode.USER_EMAIL_DUPLICATED,
                    String.format("가입된 회원 (email: %s)", user.getEmail()));
        }

        // 토큰 생성
        String accessToken = createAccessTokenBy(user.getEmail());
        String refreshToken = createRefreshTokenBy(user.getEmail());

        // 유저 엔티티에 refresh token 저장 및 DB 저장
        user.updateRefreshToken(refreshToken);
        user.encodePasswordBy(passwordEncoder);
        userRepository.save(user);

        // 토큰 반환
        return new Tokens(accessToken, refreshToken);
    }

    /**
     * 로그인 후 예외 처리 및 토큰 반환
     * @param userEmail 유저 이메일
     * @param password 비밀번호
     * @return 엑세스 토큰 및 리프레쉬 토큰
     */
    public Tokens login(String userEmail, String password) {
        // DB 에서 유저 찾기
        User user = userRepository
                .findByEmail(userEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_EMAIL_NOT_FOUND,
                        String.format("가입되지 않은 회원 (email: %s)", userEmail)));

        // 비밀번호 검증
        if (!passwordEncoder.matches(user.getPassword(), password)) {
            throw new AppException(ErrorCode.INVALID_PASSWORD, "잘못된 비밀번호");
        }

        // access token, refresh token 발급
        String accessToken = createAccessTokenBy(userEmail);
        String refreshToken = createRefreshTokenBy(userEmail);

        // refresh token 갱신 및 DB 저장
        user.updateRefreshToken(refreshToken);
        userRepository.save(user);

        // 토큰 반환
        return new Tokens(accessToken, refreshToken);
    }

    /**
     * refresh token 갱신 및 DB 저장
     * @param user 갱신할 대상 User
     * @return 갱신한 User
     */
    public User renewRefreshToken(User user) {
        user.updateRefreshToken(createRefreshTokenBy(user.getEmail()));
        userRepository.save(user);
        return user;
    }

    /**
     * token 에서 user email 획득
     * @param token email 얻을 대상 토큰
     * @return user email
     */
    public String getUserEmailFrom(String token) {
        return JwtProvider.getUserIdFrom(token, key);
    }

    /**
     * token 만료 여부 검사
     * @param token 검사할 대상 token
     * @return 만료 여부 boolean 값
     */
    public boolean isExpired(String token) {
        try {
            return JwtProvider.getClaimsFrom(token, key)
                    .getExpiration()
                    .before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * access token 생성
     * @param email 토큰에 등록할 email
     * @return access token
     */
    public String createAccessTokenBy(String email) {
        return JwtProvider.createToken(email, key, accessTokenExpireTime);
    }

    /**
     * refresh token 생성
     * @param email 토큰에 등록할 email
     * @return refresh token
     */
    public String createRefreshTokenBy(String email) {
        return JwtProvider.createToken(email, key, refreshTokenExpireTime);
    }

    /**
     * jwt 관련 메서드 정의
     */
    private static class JwtProvider {

        /**
         * jwt 생성
         * @param userId 토큰에 들어갈 user id
         * @param key 토큰에 사용할 key
         * @param expireTime 토큰에 들어갈 만료기간
         * @return jwt
         */
        public static String createToken(String userId, SecretKey key, long expireTime) {
            return Jwts.builder()
                    .claim("userId", userId)
                    .subject(userId)
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis() + expireTime))
                    .signWith(key, Jwts.SIG.HS256)
                    .compact();
        }

        /**
         * jwt 에서 Claims 획득
         * @param token 대상 token
         * @param key 사용할 key
         * @return Claims
         */
        public static Claims getClaimsFrom(String token, SecretKey key) {
            return Jwts.parser()
                    .verifyWith(key).build()
                    .parseSignedClaims(token)
                    .getPayload();
        }

        /**
         * jwt 에서 user id 획득
         * @param token 대상 token
         * @param key 사용할 key
         * @return user id
         */
        public static String getUserIdFrom(String token, SecretKey key) {
            return JwtProvider.getClaimsFrom(token, key)
                    .get("userId", String.class);
        }
    }
}
