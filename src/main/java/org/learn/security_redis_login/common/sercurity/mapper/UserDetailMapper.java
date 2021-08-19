package org.learn.security_redis_login.common.sercurity.mapper;

import org.learn.security_redis_login.common.sercurity.pojo.SysUser;

/**
 * @author Skyrimal
 * @version 1.0
 * @date 2021/8/19 0019 11:38
 */
public interface UserDetailMapper {
    SysUser selectUserByUserName(String username);
}
