package project.api.rest.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import project.api.rest.exceptions.ErrorResponse;

import java.io.IOException;


@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());


        String message = getExceptionMessage(authException);

        ErrorResponse errorResponse = new ErrorResponse(
                null,
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                message,
                null
        );

        new ObjectMapper().writeValue(response.getOutputStream(), errorResponse);
    }

    private String getExceptionMessage(AuthenticationException authException) {
        if (authException instanceof InvalidBearerTokenException){
            return  "The token has expired please sign in again";
        }

        if (authException instanceof BadCredentialsException){
            return  "Username or password incorrect";
        }

        return "Your session has expired. Please sign in again.";
    }
}
