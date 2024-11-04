package com.playdata.orderservice.common.auth;

import com.playdata.orderservice.user.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
// 역할: JWT 토큰을 발급하고, 서명 위조를 검사하는 객체
public class JwtTokenProvider {

    // 서명에 사용할 값(512비트 이상의 랜덤 문자열을 권장)
    // 토큰 위조를 확인할 때 사용하는 서명 값
    // yml 파일이도 property 방식을 써야함.
    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private int expiration;

    @Value("${jwt.secretKeyRt}")
    private String secretKeyRt;

    @Value("${jwt.expirationRt}")
    private int expirationRt;


    // 토큰 생성 메서드
     /*
            {
                "iss": "서비스 이름(발급자)",
                "exp": "2023-12-27(만료일자)",
                "iat": "2023-11-27(발급일자)",
                "email": "로그인한 사람 이메일",
                "role": "Premium"
                ...
                == 서명
            }

            클래임(Claims).
            - `iss` (Issuer): 토큰 발행자를 나타냅니다.
            - `exp` (Expiration Time): 토큰의 만료 시간을 나타냅니다.
            - `sub` (Subject): 토큰의 주제를 나타냅니다.
            - `aud` (Audience): 토큰이 전달되는 대상을 나타냅니다.
            - `nbf` (Not Before): 토큰이 활성화되기 전 시간을 나타냅니다.
            - `iat` (Issued At): 토큰이 발행된 시간을 나타냅니다.
            - `jti` (JWT ID): 토큰을 구별하는 고유한 식별자를 나타냅니다.
     */
    public String createToken(String email, String role) {

        // 페이로드에 들어갈 사용자 정보를 클래임이라 한다.
        // Calims: 페이로드에 들어갈 사용자 정보
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);
        Date date = new Date();  // 만료시간 지정

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(date)
                // 현재 시간 밀리초에 30분을 더한 시간을 만료시간으로 세팅
                .setExpiration(new Date(date.getTime() + expiration * 60 * 1000L))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String createRefreshToken(String email, String role) {

        // 페이로드에 들어갈 사용자 정보를 클래임이라 한다.
        // Calims: 페이로드에 들어갈 사용자 정보
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);
        Date date = new Date();  // 만료시간 지정

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(date)
                // 현재 시간 밀리초에 30분을 더한 시간을 만료시간으로 세팅
                .setExpiration(new Date(date.getTime() + expirationRt * 60 * 1000L))
                .signWith(SignatureAlgorithm.HS256, secretKeyRt)
                .compact();
    }

    /**
     * 클라이언트가 전송한 토큰을 디코딩하여 토큰의 위조 여부를 확인
     * 토큰을 json으로 파싱해서 클레임(토큰 정보)을 리턴
     *
     * @param token - 필터가 전달해 준 토큰
     * @return - 토큰 안에 있는 인증된 유저 정보를 반환
     */
    public TokenUserInfo validateAndGetTokenUserInfo(String token) throws Exception {
        Claims claims = Jwts.parserBuilder()
                // 토큰 발급자의 발급 당시의 서명을 넣어줌. applicaiton.yml 설정이 있는 값
                .setSigningKey(secretKey)
                // 서명 위조 검사: 위조된 경우에는 예외가 발생
                // 만약 위조되지 않았다면 payload를 리턴
                .build()
                .parseClaimsJws(token)
                .getBody();

        log.info("validateAndGetTokenUserInfo() claims: {}", claims);

        return TokenUserInfo.builder()
                .email(claims.getSubject())
                // 클레임이 get 할 수 있는 타입이 정해져 있어서 Role을 못 꺼냅니다.
                // 일단 STring으로 꺼내고, 다시 Role 타입으로 포장해서 집어 넣겠습니다.
                .role(Role.valueOf(claims.get("role", String.class)))
                .build();
    }
}
