package app.security;

import app.dtos.UserDTO;
import app.exceptions.TokenCreationException;
import app.exceptions.TokenValidationException;

public interface ITokenSecurity {

    String createToken(UserDTO userDTO, String ISSUER, long TOKEN_EXPIRE_TIME, String SECRET_KEY) throws TokenCreationException;

    UserDTO validateToken(String token, String SECRET_KEY) throws TokenValidationException;
}
