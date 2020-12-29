package com.dk.cc.common.config;

import com.dk.cc.common.shiro.JwtToken;
import com.dk.cc.common.util.RedisUtil;
import io.jsonwebtoken.Claims;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 解决跨域问题
 */
//@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .maxAge(3600)
                .allowedHeaders("*");
    }
}
/*
 * 这里的getBean是因为使用@Autowired无法把RedisUtil注入进来
 * 这样自动去注入当使用的时候是未NULL，是注入不进去了。通俗的来讲是因为拦截器在spring扫描bean之前加载所以注入不进去。
 *
 * 解决的方法：
 * 可以通过已经初始化之后applicationContext容器中去获取需要的bean.
 * */
//    public <T> T getBean(Class<T> clazz, HttpServletRequest request) {
//        WebApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
//        return applicationContext.getBean(clazz);
//    }
//
//    //刷新token
//    private boolean refreshToken(ServletRequest request, ServletResponse response) {
//        System.out.println("刷新refreshToken");
//        HttpServletRequest req = (HttpServletRequest) request;
//        RedisUtil redisUtil = getBean(RedisUtil.class, req);
//
//        //获取传递过来的accessToken
//        String accessToken = req.getHeader("Authorization");
//        //解析token
//        Claims claims = jwtUtils.getUserClaims(accessToken);
//        //获取token里面的用户名
//        String userId = claims.getSubject();
//        System.out.println("需要刷新的userId" + userId);
//        System.out.println("需要更新的current" + claims.get("current", Long.class));
//
//        //判断refreshToken是否过期了，过期了那么所含的username的键不存在
//        System.out.println("判断refreshToken是否过期了：" + redisUtil.hasKey(userId));
//        if (redisUtil.hasKey(userId)) {
//            //判断refresh的时间节点和传递过来的accessToken的时间节点是否一致，不一致校验失败
//            long current = (long) redisUtil.get(userId);
//            if (current == claims.get("current", Long.class)) {
//                //获取当前时间节点
//                long currentTimeMillis = System.currentTimeMillis();
//                //生成刷新的token
//                String token = jwtUtils.generateToken(Long.parseLong(userId), currentTimeMillis);
//                //刷新redis里面的refreshToken,过期时间是30min
//                redisUtil.set(userId, currentTimeMillis, refreshTokenExpireTime);
//                //再次交给shiro进行认证
//                JwtToken jwtToken = new JwtToken(token);
//                try {
//                    getSubject(request, response).login(jwtToken);
//                    // 最后将刷新的AccessToken存放在Response的Header中的Authorization字段返回
//                    HttpServletResponse httpServletResponse = (HttpServletResponse) response;
//
//                    httpServletResponse.setHeader("Authorization", token);
//                    httpServletResponse.setHeader("Access-Control-Expose-Headers", "Authorization");
//                    return true;
//                } catch (Exception e) {
//                    System.out.println(e.getMessage());
//                    return false;
//                }
//            }
//        }
//        return false;
//    }