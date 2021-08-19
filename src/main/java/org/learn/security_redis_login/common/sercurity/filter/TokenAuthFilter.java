package org.learn.security_redis_login.common.sercurity.filter;

import org.learn.security_redis_login.common.jwt.JwtTokenUtil;
import org.learn.security_redis_login.common.redis.RedisDao;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

/**
 * @author Skyrimal
 * @version 1.0
 * @date 2021/8/19 0019 12:55
 */
public class TokenAuthFilter extends BasicAuthenticationFilter {

    private RedisDao redisDao;


    public TokenAuthFilter(AuthenticationManager authenticationManager,RedisDao redisDao) {
        super(authenticationManager);
        this.redisDao = redisDao;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        UsernamePasswordAuthenticationToken token = getAuthentication(request);
        if(token==null){
            chain.doFilter(request,response);
        }
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);
        chain.doFilter(request,response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader("token");
        if(token==null) return null;
        JwtTokenUtil tokenUtil = new JwtTokenUtil();
        String username = tokenUtil.parseTokenBodyToUsername(token);
        return new UsernamePasswordAuthenticationToken(username,token, (Collection<? extends GrantedAuthority>) redisDao.get(username));
    }
}
