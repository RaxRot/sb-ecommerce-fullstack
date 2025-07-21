package com.raxrot.back.security.jwt;

import com.raxrot.back.security.services.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@Slf4j
public class JwtUtils {

    @Value("${spring.app.jwtExpirationInMs}")
    private long jwtExpirationMs;

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    @Value("${spring.app.jwtCookieName}")
    private String jwtCookie;

    //Getting JWT from header
    /*
    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        log.debug("Authentication header: {}",bearerToken);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);//remove bearer prefix
        }
        return null;
    }
     */
    public String getJwtFromCookie(HttpServletRequest request) {
        Cookie cookie= WebUtils.getCookie(request, jwtCookie);
        if (cookie != null) {
            return cookie.getValue();
        }else{
            return null;
        }
    }

    public ResponseCookie getJwtCookie(UserDetailsImpl userPrincipal) {
        String jwt=generateJwtFromUsername(userPrincipal.getUsername());
        ResponseCookie cookie= ResponseCookie.from(jwtCookie,jwt)
                .path("/api")
                .maxAge(24*60*60)
                .httpOnly(false)
                .build();
        return cookie;
    }

    public ResponseCookie getCleanJwtCookie() {
        ResponseCookie cookie= ResponseCookie.from(jwtCookie,null).path("/api").build();
        return cookie;
    }

    //Generating Token from username
    public String generateJwtFromUsername(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date((new Date().getTime()+jwtExpirationMs)))
                .signWith(key())
                .compact();
    }

    //Getting username from JWT Token
    public String getUsernameFromJwt(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build().parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    //Generate Signing key
    public SecretKey key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    //Validate JWT Token
    public boolean validateJwt(String authToken) {
        try {
            log.debug("Token is valid");
            Jwts.parser().verifyWith(key()).build().parseSignedClaims(authToken);
            return true;
        }catch (MalformedJwtException e){
            log.error("Invalid JWT token: {}", e.getMessage());
        }
        catch (ExpiredJwtException e){
            log.error("Expired JWT token: {}", e.getMessage());
        }
        catch (UnsupportedJwtException e){
            log.error("Unsupported JWT token: {}", e.getMessage());
        }
        catch (IllegalArgumentException e){
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
