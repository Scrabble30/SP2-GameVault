package app.controller;

import app.dao.SecurityDAO;
import app.entity.Role;
import app.entity.User;
import app.exception.APIException;
import app.exception.PasswordValidationException;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nimbusds.jose.JOSEException;
import dk.bugelhartmann.ITokenSecurity;
import dk.bugelhartmann.TokenCreationException;
import dk.bugelhartmann.TokenSecurity;
import dk.bugelhartmann.UserDTO;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.validation.ValidationException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Properties;
import java.util.stream.Collectors;

public class SecurityController {

    private static SecurityController instance;
    private final ITokenSecurity tokenSecurity;
    private final SecurityDAO securityDAO;

    private SecurityController(EntityManagerFactory emf) {
        this.tokenSecurity = new TokenSecurity();
        this.securityDAO = SecurityDAO.getInstance(emf);
    }

    public static SecurityController getInstance(EntityManagerFactory emf) {
        if (instance == null) {
            instance = new SecurityController(emf);
        }

        return instance;
    }

    public void login(Context ctx) {
        try {
            UserDTO userDTO = ctx.bodyValidator(UserDTO.class).get();

            User verifiedUser = securityDAO.getVerifiedUser(userDTO.getUsername(), userDTO.getPassword());
            UserDTO verifiedUserDTO = new UserDTO(
                    verifiedUser.getUsername(),
                    verifiedUser.getRoles().stream()
                            .map(Role::getName)
                            .collect(Collectors.toSet())
            );

            String token = createToken(verifiedUserDTO);

            ObjectNode responseJson = JsonNodeFactory.instance.objectNode();

            responseJson.put("username", verifiedUserDTO.getUsername());
            responseJson.put("token", token);

            ctx.status(HttpStatus.OK);
            ctx.json(responseJson);
        } catch (EntityNotFoundException e) {
            throw new APIException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (ValidationException e) {
            throw new APIException(HttpStatus.BAD_REQUEST, e.getErrors().toString());
        } catch (PasswordValidationException e) {
            throw new APIException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (IOException | TokenCreationException e) {
            throw new APIException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public void register(Context ctx) {
        try {
            UserDTO userDTO = ctx.bodyValidator(UserDTO.class).get();

            User createdUser = securityDAO.create(userDTO.getUsername(), userDTO.getPassword());
            UserDTO createdUserDTO = new UserDTO(
                    createdUser.getUsername(),
                    createdUser.getRoles().stream()
                            .map(Role::getName)
                            .collect(Collectors.toSet())
            );

            String token = createToken(createdUserDTO);

            ObjectNode responseJson = JsonNodeFactory.instance.objectNode();

            responseJson.put("username", createdUserDTO.getUsername());
            responseJson.put("token", token);

            ctx.status(HttpStatus.CREATED);
            ctx.json(responseJson);
        } catch (EntityExistsException e) {
            throw new APIException(HttpStatus.CONFLICT, e.getMessage());
        } catch (ValidationException e) {
            throw new APIException(HttpStatus.BAD_REQUEST, e.getErrors().toString());
        } catch (IOException | TokenCreationException e) {
            throw new APIException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public UserDTO authenticate(Context ctx) {
        String authorization = ctx.header("Authorization");

        if (authorization == null) {
            throw new APIException(HttpStatus.UNAUTHORIZED, "Missing Authorization header");
        }
        if (!authorization.contains("Bearer")) {
            throw new APIException(HttpStatus.UNAUTHORIZED, "Invalid Authorization header");
        }

        String token = authorization.substring("Bearer ".length());

        try {
            return verifyToken(token);
        } catch (APIException e) {
            throw new APIException(e.getStatusCode(), e.getMessage());
        } catch (IOException e) {
            throw new APIException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (Exception e) {
            throw new APIException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private String createToken(UserDTO userDTO) throws IOException, TokenCreationException {
        String ISSUER;
        String TOKEN_EXPIRE_TIME;
        String SECRET_KEY;

        if (System.getenv("DEPLOYED") != null) {
            ISSUER = System.getenv("ISSUER");
            TOKEN_EXPIRE_TIME = System.getenv("TOKEN_EXPIRE_TIME");
            SECRET_KEY = System.getenv("SECRET_KEY");
        } else {
            Properties properties = getProperties("config.properties");

            ISSUER = properties.getProperty("ISSUER");
            TOKEN_EXPIRE_TIME = properties.getProperty("TOKEN_EXPIRE_TIME");
            SECRET_KEY = properties.getProperty("SECRET_KEY");
        }

        return tokenSecurity.createToken(userDTO, ISSUER, TOKEN_EXPIRE_TIME, SECRET_KEY);
    }

    public UserDTO verifyToken(String token) throws IOException {
        boolean IS_DEPLOYED = (System.getenv("DEPLOYED") != null);
        String SECRET = IS_DEPLOYED ? System.getenv("SECRET_KEY") : getProperties("config.properties").getProperty("SECRET_KEY");

        try {
            if (!tokenSecurity.tokenIsValid(token, SECRET)) {
                throw new APIException(HttpStatus.UNAUTHORIZED, "Invalid token");
            }
            if (!tokenSecurity.tokenNotExpired(token)) {
                throw new APIException(HttpStatus.UNAUTHORIZED, "Expired token");
            }

            return tokenSecurity.getUserWithRolesFromToken(token);
        } catch (ParseException | JOSEException e) {
            throw new APIException(HttpStatus.UNAUTHORIZED, "Token could not be verified");
        }
    }

    private Properties getProperties(String resourceName) throws IOException {
        try (InputStream inputStream = SecurityController.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (inputStream == null) {
                throw new IOException(String.format("Property file '%s' not found in the classpath", resourceName));
            }

            Properties properties = new Properties();
            properties.load(inputStream);

            return properties;
        }
    }
}
