package com.dk.cc.service.impl;

import com.dk.cc.entity.User;
import com.dk.cc.mapper.UserMapper;
import com.dk.cc.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dk
 * @since 2020-11-28
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
