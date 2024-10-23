package app.security;

import app.dto.UserDTO;
import app.exception.TokenCreationException;
import app.exception.TokenValidationException;

public interface TokenSecurity {

    String createToken(UserDTO userDTO, String ISSUER, long TOKEN_EXPIRE_TIME, String SECRET_KEY) throws TokenCreationException;

    UserDTO validateToken(String token, String SECRET_KEY) throws TokenValidationException;
}
