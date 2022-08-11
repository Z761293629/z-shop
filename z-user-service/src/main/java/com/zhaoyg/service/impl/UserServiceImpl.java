package com.zhaoyg.service.impl;

import com.zhaoyg.model.entity.User;
import com.zhaoyg.mapper.UserMapper;
import com.zhaoyg.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhao
 * @since 2022-08-11
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
