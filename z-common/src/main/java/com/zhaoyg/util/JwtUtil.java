package com.zhaoyg.util;

import com.zhaoyg.entity.LoginUser;
import io.jsonwebtoken.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Objects;

/**
 * @author zhao
 * @date 2022/8/13
 */
public final class JwtUtil {

    private static final String SUBJECT = "zhao";
    private static final String SECRET = "zhaozhao";
    private static final long EXPIRE = 7 * 24 * 60 * 60 * 1000L;
    private static final String JWT_PREFIX = "z-shop";


    public static String generateJwt(LoginUser loginUser) {
        String jwt = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .claim("id", loginUser.getId())
                .claim("name", loginUser.getName())
                .claim("head_img", loginUser.getHeadImg())
                .claim("mail", loginUser.getMail())
                .claim("ip", loginUser.getIp())
                .setSubject(SUBJECT)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .setIssuedAt(new Date())
                .compact();
        return JWT_PREFIX + jwt;
    }

    public static LoginUser parseJwt(String jwt, HttpServletRequest request) {
        try {
            Jwt result = Jwts.parser().setSigningKey(SECRET).parse(jwt.replace(JWT_PREFIX, ""));
            Claims claims = (Claims) result.getBody();
            //if (!Objects.equals(claims.get("ip"), CommonUtil.getIpAddr(request))) {
            //    return null;
            //}
            LoginUser loginUser = new LoginUser();
            loginUser.setId(Long.parseLong(claims.get("id").toString()));
            loginUser.setMail(claims.get("mail").toString());
            loginUser.setHeadImg(claims.get("head_img").toString());
            loginUser.setName(claims.get("name").toString());
            return loginUser;
        } catch (Exception e) {
            return null;
        }
    }


}
