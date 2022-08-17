package com.zhaoyg.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zhaoyg.entity.LoginUser;
import com.zhaoyg.enums.AddressStatusEnum;
import com.zhaoyg.enums.BizCodeEnum;
import com.zhaoyg.interceptor.AuthInterceptor;
import com.zhaoyg.model.entity.Address;
import com.zhaoyg.mapper.AddressMapper;
import com.zhaoyg.request.AddressAddRequest;
import com.zhaoyg.service.AddressService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhaoyg.util.Result;
import com.zhaoyg.vo.AddressVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 电商-公司收发货地址表 服务实现类
 * </p>
 *
 * @author zhao
 * @since 2022-08-11
 */
@Slf4j
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addAddress(AddressAddRequest addressAddRequest) {
        LoginUser loginUser = AuthInterceptor.loginUserThreadLocal.get();
        Address address = new Address();
        BeanUtils.copyProperties(addressAddRequest, address);
        Long userId = loginUser.getId();
        address.setUserId(userId);
        address.setCreateTime(LocalDateTime.now());
        if (Objects.equals(address.getDefaultStatus(), AddressStatusEnum.DEFAULT_STATUS.getStatus())) {
            update(Wrappers.<Address>lambdaUpdate()
                    .eq(Address::getUserId, userId)
                    .set(Address::getDefaultStatus, AddressStatusEnum.COMMON_STATUS.getStatus())
            );
        }
        log.info("[新增收货地址],entity=[{}]", JSONUtil.toJsonStr(address));
        save(address);
    }

    @Override
    public Result detail(Long addressId) {
        LoginUser loginUser = AuthInterceptor.loginUserThreadLocal.get();
        Address address = getOne(Wrappers.<Address>lambdaQuery().eq(Address::getId, addressId).eq(Address::getUserId, loginUser.getId()));
        if (Objects.isNull(address)) {
            return Result.fail(BizCodeEnum.ADDRESS_DONT_EXIST);
        }
        AddressVO addressVO = new AddressVO();
        BeanUtils.copyProperties(address, addressVO);
        return Result.success(addressVO);
    }

    @Override
    public Result delete(Long addressId) {
        LoginUser loginUser = AuthInterceptor.loginUserThreadLocal.get();
        boolean result = remove(Wrappers.<Address>lambdaQuery().eq(Address::getUserId, loginUser.getId()).eq(Address::getId, addressId));
        return result ? Result.success(true) : Result.fail(BizCodeEnum.ADDRESS_DELETE_ERROR);
    }

    @Override
    public Result listAllAddressOfUser() {
        LoginUser loginUser = AuthInterceptor.loginUserThreadLocal.get();
        Long userId = loginUser.getId();
        List<Address> addresses = list(
                Wrappers.<Address>lambdaQuery()
                        .eq(Address::getUserId, userId)

        );
        if (CollUtil.isEmpty(addresses)) {
            return Result.fail(BizCodeEnum.ADDRESS_DONT_EXIST);
        }
        List<AddressVO> addressVOList = addresses.stream()
                .map(address -> {
                    AddressVO addressVO = new AddressVO();
                    BeanUtils.copyProperties(address, addressVO);
                    return addressVO;
                })
                .collect(Collectors.toList());
        return Result.success(addressVOList);
    }
}
