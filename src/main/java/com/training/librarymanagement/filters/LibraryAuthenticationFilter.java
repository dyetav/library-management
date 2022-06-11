package com.training.librarymanagement.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.training.librarymanagement.jwt.AuthenticationRequest;
import com.training.librarymanagement.jwt.JwtTokenUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private JwtTokenUtil jwtTokenUtil;

    public LibraryAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
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
        String token = jwtTokenUtil.generateToken(authResult.getName());
        response.setHeader("Authorization", "Bearer " + token);
    }
}
