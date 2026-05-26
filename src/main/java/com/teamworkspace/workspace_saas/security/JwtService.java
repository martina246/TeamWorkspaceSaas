package com.teamworkspace.workspace_saas.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.teamworkspace.workspace_saas.entity.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    //property injection
    @Value("${jwt.secret}")
    private String secretKey;
    
    public String generateToken(User user) {
        return Jwts.builder()
        .subject(user.getEmail()) //identitet korisnika
        .issuedAt(new Date(System.currentTimeMillis())) //vreme kada je token napravljen
        .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 25)) //vreme isteka tokena
        .signWith(getSignKey()) //digitalni potpis tokena sa secret key-em
        .compact(); //pretvrara sve u pravi jwt string
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey); //pretvaramo strig u bytove
        return Keys.hmacShaKeyFor(keyBytes); //bytes u security key
    }

    public String extractUsername(String token) {
        return Jwts.parser()//procita, proveri, izvuce podatke iz jwt-a
        .verifyWith(getSignKey())//parser dobija secret key
        .build() //pravimo parser
        .parseSignedClaims(token) //parser cita token (claims-podavi unutar jwt-a)
        .getPayload()
        .getSubject();
    }

    public boolean isTokenValid(String token, User user) {
        String username = extractUsername(token);
        return username.equals(user.getEmail()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return Jwts.parser()
        .verifyWith(getSignKey())
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getExpiration();
    }
}
