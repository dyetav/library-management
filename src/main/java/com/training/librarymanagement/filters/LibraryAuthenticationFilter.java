package com.training.librarymanagement.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.training.librarymanagement.jwt.AuthenticationRequest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class LibraryAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static Logger LOG = LoggerFactory.getLogger(LibraryAuthenticationFilter.class);
    private ObjectMapper objectMapper = new ObjectMapper();

    private AuthenticationManager authenticationManager;

    public LibraryAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        setFilterProcessesUrl("/library-management/signin");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            AuthenticationRequest authenticationRequest = objectMapper.readValue(request.getInputStream(), AuthenticationRequest.class);
            String username = authenticationRequest.getUsername();
            String password = authenticationRequest.getPassword();

            LOG.info("Attempting authentication for user with username {}", username);
            UsernamePasswordAuthenticationToken authenticationRequestObject = new UsernamePasswordAuthenticationToken(
                username,
                password
            );

            return authenticationManager.authenticate(authenticationRequestObject);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        LOG.info("Successfully authenticated: username {}", authResult.getName());
        String key = "secureKey";
        String token = Jwts.builder()
            .setExpiration(Date.from(LocalDateTime.now().plus(5, ChronoUnit.MINUTES).toInstant(ZoneOffset.UTC)))
            .setSubject(authResult.getName())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .signWith(SignatureAlgorithm.HS512, key)
            .compact();

        response.setHeader("Authorization", "Bearer " + token);

    }
}
