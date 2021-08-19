package org.learn.security_redis_login.common.sercurity.pojo;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Skyrimal
 * @version 1.0
 * @date 2021/8/19 0019 11:57
 */
public class SysUser extends User {
    public SysUser(String password,String username){
        super(username,password,new ArrayList<>());
    }

    public SysUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }
}
