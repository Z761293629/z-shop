package com.zhaoyg.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zhaoyg.entity.LoginUser;
import com.zhaoyg.enums.BizCodeEnum;
import com.zhaoyg.enums.SendCodeEnum;
import com.zhaoyg.interceptor.AuthInterceptor;
import com.zhaoyg.model.entity.User;
import com.zhaoyg.mapper.UserMapper;
import com.zhaoyg.request.UserLoginRequest;
import com.zhaoyg.request.UserRegisterRequest;
import com.zhaoyg.service.FileService;
import com.zhaoyg.service.NotifyService;
import com.zhaoyg.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhaoyg.util.CommonUtil;
import com.zhaoyg.util.JwtUtil;
import com.zhaoyg.util.Result;
import com.zhaoyg.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhao
 * @since 2022-08-11
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final FileService fileService;
    private final NotifyService notifyService;


    @Override
    public Result uploadImg(MultipartFile file) {
        String newFilename = randomFilename(file.getOriginalFilename());
        try {
            String bucketName = "z-shop-img";
            fileService.uploadFile(bucketName, newFilename, file.getInputStream());
            return Result.success("http://127.0.0.1:9000/" + bucketName + newFilename);
        } catch (IOException e) {
            log.error("[上传头像错误] ", e);
            return Result.fail(BizCodeEnum.HEAD_UPLOAD_ERROR);
        }
    }

    @Override
    public Result register(UserRegisterRequest userRegisterRequest) {
        // 1.校验验证码
        boolean check = notifyService.checkCode(SendCodeEnum.USER_REGISTER, userRegisterRequest.getCode(), userRegisterRequest.getMail());
        if (!check) {
            return Result.fail(BizCodeEnum.CODE_ERROR);
        }
        // 2. 构建entity
        User user = new User();
        BeanUtils.copyProperties(userRegisterRequest, user);
        user.setCreateTime(LocalDateTime.now());
        String salt = BCrypt.gensalt();
        user.setSecret(salt);
        String pwd = BCrypt.hashpw(userRegisterRequest.getPwd(), salt);
        user.setPwd(pwd);
        // 3.检查唯一
        if (!uniqueMail(userRegisterRequest.getMail())) {
            return Result.fail(BizCodeEnum.ACCOUNT_REPEAT);
        }
        return Result.success(save(user));
    }

    @Override
    public Result login(UserLoginRequest userLoginRequest, HttpServletRequest httpServletRequest) {
        Optional<User> userOptional = getUserByMail(userLoginRequest.getMail());
        if (userOptional.isEmpty()) {
            return Result.fail(BizCodeEnum.ACCOUNT_UNREGISTER);
        }
        User user = userOptional.get();
        boolean check = BCrypt.checkpw(userLoginRequest.getPwd(), user.getPwd());
        if (!check) {
            return Result.fail(BizCodeEnum.ACCOUNT_PWD_ERROR);
        }
        LoginUser loginUser = new LoginUser();
        String ip = CommonUtil.getIpAddr(httpServletRequest);
        loginUser.setIp(ip);
        BeanUtils.copyProperties(user, loginUser);
        return Result.success(JwtUtil.generateJwt(loginUser));
    }

    @Override
    public Result detail() {
        LoginUser loginUser = AuthInterceptor.loginUserThreadLocal.get();
        User user = getById(loginUser.getId());
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return Result.success(userVO);
    }

    private Optional<User> getUserByMail(String mail) {
        return Optional.ofNullable(getOne(Wrappers.<User>lambdaQuery().eq(User::getMail, mail)));
    }

    private boolean uniqueMail(String mail) {
        return count(Wrappers.<User>lambdaQuery().eq(User::getMail, mail)) == 0;
    }

    private String randomFilename(String filename) {
        String folder = DateUtil.format(LocalDateTime.now(), "yyyy/MM/dd");
        return "/user/" + folder + "/" + RandomUtil.randomString(8) + "." + FileUtil.extName(filename);
    }
}
