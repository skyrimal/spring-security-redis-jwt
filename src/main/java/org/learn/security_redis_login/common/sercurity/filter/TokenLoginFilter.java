package org.learn.security_redis_login.common.sercurity.filter;

import io.jsonwebtoken.CompressionCodec;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.learn.security_redis_login.common.jwt.JwtTokenUtil;
import org.learn.security_redis_login.common.redis.RedisDao;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * @author Skyrimal
 * @version 1.0
 * @date 2021/8/19 0019 12:22
 */
public class TokenLoginFilter extends UsernamePasswordAuthenticationFilter {

    private RedisDao redisDao;
    private AuthenticationManager authenticationManager;

    public TokenLoginFilter(RedisDao redisDao,AuthenticationManager authenticationManager) {
        this.redisDao = redisDao;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = obtainUsername(request);
        username = (username != null) ? username : "";
        username = username.trim();
        String password = obtainPassword(request);
        password = (password != null) ? password : "";
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password,new ArrayList<>());
        return authenticationManager.authenticate(authRequest);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        JwtTokenUtil tokenUtil = new JwtTokenUtil();
        User principal =(User) authResult.getPrincipal();
        String token = tokenUtil.createToken(principal.getUsername());
        Collection<GrantedAuthority> authorities = principal.getAuthorities();
        redisDao.set(token,authorities);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        super.unsuccessfulAuthentication(request, response, failed);
    }

}
