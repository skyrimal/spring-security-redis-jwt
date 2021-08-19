package org.learn.security_redis_login.common.jwt;

import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @author Skyrimal
 * @version 1.0
 * @date 2021/8/19 0019 12:33
 */
public class JwtTokenUtil {
    private long expirTime=3600;

    private String secret="123456";

    public void setExpirTime(long expirTime) {
        this.expirTime = expirTime;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String createToken(String username){
        return Jwts.builder()
                //主体
                .setSubject(username)
                //过期时间
                .setExpiration(new Date((new Date()).getTime()+3600*1000))
                //加密
                .signWith(SignatureAlgorithm.HS512,secret).compressWith(CompressionCodecs.GZIP)
                .compact();
    }

    public String parseTokenBodyToUsername(String token){
        return Jwts.parser()
                //如果是中文等涉及编码的字符密钥的话
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    public void deleteToken(String token){

    }
}
