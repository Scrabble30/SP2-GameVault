package app.controller;

import app.dao.SecurityDAO;
import app.dto.UserDTO;
import app.entity.Role;
import app.entity.User;
import app.exception.PasswordValidationException;
import app.exception.TokenCreationException;
import app.exception.TokenValidationException;
import app.security.TokenSecurity;
import app.security.impl.TokenSecurityImpl;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.javalin.http.*;
import io.javalin.validation.ValidationException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.stream.Collectors;

public class SecurityController {

    private static SecurityController instance;
    private final TokenSecurity tokenSecurity;
    private final SecurityDAO securityDAO;

    private SecurityController(EntityManagerFactory emf) {
        this.tokenSecurity = new TokenSecurityImpl();
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
        } catch (ValidationException e) {
            throw new BadRequestResponse(e.getErrors().toString());
        } catch (EntityNotFoundException e) {
            throw new NotFoundResponse(e.getMessage());
        } catch (PasswordValidationException e) {
            throw new UnauthorizedResponse(e.getMessage());
        } catch (IOException | TokenCreationException e) {
            throw new InternalServerErrorResponse(e.getMessage());
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
        } catch (ValidationException e) {
            throw new BadRequestResponse(e.getErrors().toString());
        } catch (EntityExistsException e) {
            throw new ConflictResponse(e.getMessage());
        } catch (IOException | TokenCreationException e) {
            throw new InternalServerErrorResponse(e.getMessage());
        }
    }

    public UserDTO authenticate(Context ctx) {
        String authorization = ctx.header("Authorization");

        if (authorization == null) {
            throw new UnauthorizedResponse("Missing Authorization header");
        }
        if (!authorization.contains("Bearer")) {
            throw new UnauthorizedResponse("Invalid Authorization header");
        }

        String token = authorization.substring("Bearer ".length());

        try {
            return validateToken(token);
        } catch (TokenValidationException e) {
            throw new UnauthorizedResponse(e.getMessage());
        } catch (IOException e) {
            throw new InternalServerErrorResponse(e.getMessage());
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

        return tokenSecurity.createToken(userDTO, ISSUER, Long.parseLong(TOKEN_EXPIRE_TIME), SECRET_KEY);
    }

    public UserDTO validateToken(String token) throws IOException, TokenValidationException {
        boolean IS_DEPLOYED = (System.getenv("DEPLOYED") != null);
        String SECRET_KEY = IS_DEPLOYED ? System.getenv("SECRET_KEY") : getProperties("config.properties").getProperty("SECRET_KEY");

        return tokenSecurity.validateToken(token, SECRET_KEY);
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
