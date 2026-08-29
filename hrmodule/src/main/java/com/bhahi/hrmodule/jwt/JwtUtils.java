package com.bhahi.hrmodule.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Service
public class JwtUtils {

    @Value("${jwt.token.expire}")
    public int jwtTokenExpire;
    private  final SecretKey key;

//===============================================================================================================
    public JwtUtils(@Value("${jwt.password}") String secret){
        byte[] keyBytes=secret.getBytes(StandardCharsets.UTF_8);
        this.key=new SecretKeySpec(keyBytes,"HmacSHA256");
    }

    public int getJwtTokenExpire() {
        return jwtTokenExpire;
    }

//===============================================================================================================

    public String generateToken(UserDetails user, String logId){

        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        String username = user.getUsername();

        String clientId = username.substring(0,4);   // ⭐ extract client id

        return Jwts.builder()
                .subject(username)
                .claim("role", roles)
                .claim("type", "AUTH")
                .claim("database", clientId)   // ✅ correct database routing
                .claim("logId", logId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+jwtTokenExpire))
                .signWith(key)
                .compact();
    }



    //===============================================================================================================

    public String generateGuestToken(String user){

        String clientId = user.substring(0,4);

        return Jwts.builder()
                .subject(user)
                .claim("type", "GUEST")
                .claim("database", clientId) // ✅ correct
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+jwtTokenExpire))
                .signWith(key)
                .compact();
    }

//===============================================================================================================



    public List<String> extractRole(String token){
        return  extractClaims(token, claims -> claims.get("role", List.class));
    }

    public String extractLogId(String token) {
        return extractClaims(token, claims -> claims.get("logId", String.class));
    }
//===============================================================================================================

    public String extractUsername(String token){
        return  extractClaims(token, Claims::getSubject);
    }

//===============================================================================================================


    private <T> T extractClaims(String token, Function<Claims,T> claimsTFunction ){
        return  claimsTFunction.apply(Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload());

    }




    public Claims extractType(String token){
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
//===============================================================================================================

    public boolean isValidToken(String token ){
        return (  !isTokenExpired(token));
    }


//======================================================================================================================

    public boolean isTokenExpired(String token) {
        return extractClaims(token , Claims::getExpiration).before(new Date());
    }

// ====================================================================================================================

}
