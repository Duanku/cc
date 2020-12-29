package com.dk.cc.mapper;

import com.dk.cc.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author dk
 * @since 2020-11-28
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
