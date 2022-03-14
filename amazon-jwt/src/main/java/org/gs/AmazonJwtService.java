package org.gs;

import io.smallrye.jwt.build.Jwt;

import javax.inject.Singleton;

@Singleton
public class AmazonJwtService {

    public String generateJwt() {
        return Jwt.issuer("amazon-jwt")
                .subject("amazon-cart")
                .groups("writer").expiresAt(System.currentTimeMillis() + 3600).sign();
    }
}
