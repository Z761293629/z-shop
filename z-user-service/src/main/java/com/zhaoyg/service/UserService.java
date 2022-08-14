package com.zhaoyg.service;

import com.zhaoyg.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhaoyg.request.UserLoginRequest;
import com.zhaoyg.request.UserRegisterRequest;
import com.zhaoyg.util.Result;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhao
 * @since 2022-08-11
 */
public interface UserService extends IService<User> {

    Result uploadImg(MultipartFile file);

    Result register(UserRegisterRequest userRegisterRequest);

    Result login(UserLoginRequest userLoginRequest, HttpServletRequest httpServletRequest);

    Result detail();

}
