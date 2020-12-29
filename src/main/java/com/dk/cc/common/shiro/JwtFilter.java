package com.dk.cc.common.shiro;

import cn.hutool.json.JSONUtil;
import com.dk.cc.common.lang.Result;
import com.dk.cc.common.util.JwtUtils;
import com.dk.cc.common.util.RedisUtil;
import io.jsonwebtoken.Claims;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends AuthenticatingFilter {
    @Autowired
    JwtUtils jwtUtils;
    //过期时间
    @Value("${dk.jwt.refreshTokenExpireTime}")
    private long refreshTokenExpireTime;

    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String jwt = request.getHeader("Authorization");
        if (StringUtils.isEmpty(jwt)) {
            return null;
        }
        return new JwtToken(jwt);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String jwt = request.getHeader("Authorization");
        if (!StringUtils.isEmpty(jwt)) {
            // 校验jwt
            Claims claim = jwtUtils.getClaimByToken(jwt);
            if (claim == null || jwtUtils.isTokenExpired(claim.getExpiration())) {
                if (refreshToken(servletRequest,servletResponse)) {
                    System.out.println("成功更新token");
                    return true;
                } else {
                    return responseError(servletResponse,"toekn已过期请重新登录");
                }
            }
            return executeLogin(servletRequest, servletResponse);
        }
        return true;
    }
    /**
     * 将非法请求跳转到 /unauthorized/**
     */
    private boolean responseError(ServletResponse response, String message) {
        System.out.println("responseError");
        try {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.setCharacterEncoding("UTF-8");
            //设置编码，否则中文字符在重定向时会变为空字符串
            Result result = Result.login(message);
            String json = JSONUtil.toJsonStr(result);
            httpServletResponse.getWriter().print(json);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {

        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        try {
            Throwable throwable = e.getCause() == null ? e : e.getCause();
            Result result = Result.fail(throwable.getMessage());
            String json = JSONUtil.toJsonStr(result);
            httpServletResponse.getWriter().print(json);
        } catch (IOException ioException) {
        }
        return false;
    }

    //对跨域支持
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {

        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个OPTIONS请求，这里我们给OPTIONS请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(org.springframework.http.HttpStatus.OK.value());
            return false;
        }

        return super.preHandle(request, response);
    }

    /*
     * 这里的getBean是因为使用@Autowired无法把RedisUtil注入进来
     * 这样自动去注入当使用的时候是未NULL，是注入不进去了。通俗的来讲是因为拦截器在spring扫描bean之前加载所以注入不进去。
     *
     * 解决的方法：
     * 可以通过已经初始化之后applicationContext容器中去获取需要的bean.
     * */
    public <T> T getBean(Class<T> clazz, HttpServletRequest request) {
        WebApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
        return applicationContext.getBean(clazz);
    }

    //刷新token
    private boolean refreshToken(ServletRequest request, ServletResponse response) {
        System.out.println("刷新refreshToken");
        HttpServletRequest req = (HttpServletRequest) request;
        RedisUtil redisUtil = getBean(RedisUtil.class, req);

        //获取传递过来的accessToken
        String accessToken = req.getHeader("Authorization");
        //解析token
        Claims claims = jwtUtils.getUserClaims(accessToken);
        //获取token里面的用户名
        String userId = claims.getSubject();
        System.out.println("需要刷新的userId" + userId);
        System.out.println("需要更新的current" + claims.get("current", Long.class));

        //判断refreshToken是否过期了，过期了那么所含的username的键不存在
        System.out.println("判断refreshToken是否过期了：" + redisUtil.hasKey(userId));
        if (redisUtil.hasKey(userId)) {
            //判断refresh的时间节点和传递过来的accessToken的时间节点是否一致，不一致校验失败
            long current = (long) redisUtil.get(userId);
            if (current == claims.get("current", Long.class)) {
                //获取当前时间节点
                long currentTimeMillis = System.currentTimeMillis();
                //生成刷新的token
                String token = jwtUtils.generateToken(Long.parseLong(userId), currentTimeMillis);
                //刷新redis里面的refreshToken,过期时间是30min
                redisUtil.set(userId, currentTimeMillis, refreshTokenExpireTime);
                //再次交给shiro进行认证
                JwtToken jwtToken = new JwtToken(token);
                try {
                    getSubject(request, response).login(jwtToken);
                    // 最后将刷新的AccessToken存放在Response的Header中的Authorization字段返回
                    HttpServletResponse httpServletResponse = (HttpServletResponse) response;

                    httpServletResponse.setHeader("Authorization", token);
                    httpServletResponse.setHeader("Access-Control-Expose-Headers", "Authorization");
                    return true;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return false;
                }
            }
        }
        return false;
    }
}
