package app.security;

import app.dtos.UserDTO;
import app.exceptions.TokenCreationException;
import app.exceptions.TokenValidationException;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class TokenSecurity implements ITokenSecurity {

    @Override
    public String createToken(UserDTO userDTO, String ISSUER, long TOKEN_EXPIRE_TIME, String SECRET_KEY) throws TokenCreationException {
        try {
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(userDTO.getUsername())
                    .issuer(ISSUER)
                    .claim("username", userDTO.getUsername())
                    .claim("roles", userDTO.getRoles().stream().toList())
                    .expirationTime(Date.from(new Date().toInstant().plusSeconds(TOKEN_EXPIRE_TIME)))
                    .build();

            Payload payload = new Payload(claimsSet.toJSONObject());
            JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);
            JWSObject jwsObject = new JWSObject(jwsHeader, payload);
            JWSSigner signer = new MACSigner(SECRET_KEY);
            jwsObject.sign(signer);

            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new TokenCreationException("Failed to create JWT token.", e);
        }
    }

    @Override
    public UserDTO validateToken(String token, String SECRET_KEY) throws TokenValidationException {
        try {
            SignedJWT jwt = SignedJWT.parse(token);

            if (!isTokenVerified(jwt, SECRET_KEY)) {
                throw new TokenValidationException("Invalid token");
            }
            if (hasTokenExpired(jwt)) {
                throw new TokenValidationException("Expired token");
            }

            String username = jwt.getJWTClaimsSet().getStringClaim("username");
            Set<String> roles = new HashSet<>(jwt.getJWTClaimsSet().getStringListClaim("roles"));

            return new UserDTO(username, roles);
        } catch (ParseException e) {
            throw new TokenValidationException("Failed to parse token", e);
        }
    }

    public boolean isTokenVerified(SignedJWT jwt, String SECRET_KEY) throws TokenValidationException {
        try {
            MACVerifier verifier = new MACVerifier(SECRET_KEY);

            return jwt.verify(verifier);
        } catch (JOSEException e) {
            throw new TokenValidationException("Failed to verify token", e);
        }
    }

    public boolean hasTokenExpired(SignedJWT jwt) throws TokenValidationException {
        try {
            return jwt.getJWTClaimsSet().getExpirationTime().before(new Date());
        } catch (ParseException e) {
            throw new TokenValidationException("Failed to extract expiration time from token", e);
        }
    }
}
