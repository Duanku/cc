package com.dk.cc.common.shiro;

import com.dk.cc.common.util.JwtUtils;
import com.dk.cc.common.util.RedisUtil;
import com.dk.cc.entity.User;
import com.dk.cc.service.UserService;
import io.jsonwebtoken.Claims;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserRealm extends AuthorizingRealm {
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    UserService userService;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    //授权管理
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        User user = (User) principalCollection.getPrimaryPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.addRole(user.getRoles());
        return info;
    }

    //权限认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        JwtToken jwtToken = (JwtToken) token;
        //解析token
        Claims claims = jwtUtils.getClaimByToken((String) jwtToken.getPrincipal());
        String userId = claims.get("userId", Long.class).toString();
        User user = userService.getById(Long.valueOf(userId));
        if (user == null) {
            throw new UnknownAccountException("账户不存在!");
        }
        if (user.getStatus() == -1) {
            throw new UnknownAccountException("账户已被锁定");
        }
        //开始认证，只要AccessToken没有过期，或者refreshToken的时间节点和AccessToken一致即可
        if (redisUtil.hasKey(userId)) {
            //判断AccessToken和refreshToken的时间节点是否一致
            long current = (long) redisUtil.get(userId);
            if (current == claims.get("current", Long.class)) {
                return new SimpleAuthenticationInfo(user, jwtToken.getCredentials(), getName());
            } else {
                throw new AuthenticationException("token已经失效，请重新登录！");
            }
        } else {
            throw new AuthenticationException("token过期或者Token错误！！");
        }
    }
}
