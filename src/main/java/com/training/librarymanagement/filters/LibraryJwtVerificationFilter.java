package com.training.librarymanagement.filters;

import com.training.librarymanagement.jwt.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class LibraryJwtVerificationFilter extends OncePerRequestFilter {

    private static Logger LOG = LoggerFactory.getLogger(LibraryJwtVerificationFilter.class);

    private JwtTokenUtil jwtTokenUtil;

    public LibraryJwtVerificationFilter(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader("Authorization");
        if (requestTokenHeader == null || "".equals(requestTokenHeader) || !requestTokenHeader.startsWith("Bearer ")) {
            LOG.warn("No Bearer token {}", requestTokenHeader);
            filterChain.doFilter(request, response);
            return;
        }

        String token = requestTokenHeader.replace("Bearer ", "");
        String username = jwtTokenUtil.getClaimFromToken(token, Claims::getSubject);
        Claims body = jwtTokenUtil.getBody(token);
        List<Map<String, String>> authorities = (List<Map<String, String>>) body.get("authorities");
        Set<SimpleGrantedAuthority> simpleGrantedAuthorities = authorities.stream()
            .map(a -> new SimpleGrantedAuthority(a.get("authority"))).collect(Collectors.toSet());

        if (jwtTokenUtil.validateToken(token, username)) {
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                username,
                null,
                simpleGrantedAuthorities
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
