package org.learn.security_redis_login.common.sercurity.service.impl;

import org.learn.security_redis_login.common.sercurity.mapper.UserDetailMapper;
import org.learn.security_redis_login.common.sercurity.pojo.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Skyrimal
 * @version 1.0
 * @date 2021/8/19 0019 11:16
 */
@Primary
@Service("userDetailLoginServiceImpl")
public class UserDetailLoginServiceImpl implements UserDetailsService {
    @Resource
    private UserDetailMapper userDetailMapper;
//    @Autowired
//    private PasswordEncoder passwordEncoder;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userDetailMapper.selectUserByUserName(username);
        if(user==null){
            throw new UsernameNotFoundException("未找到该用户");
        }
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList("admin,user");
        return new User(username,user.getPassword(),grantedAuthorities);
    }
}
