package com.zhaoyg.service.impl;

import com.zhaoyg.model.entity.Address;
import com.zhaoyg.mapper.AddressMapper;
import com.zhaoyg.service.AddressService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 电商-公司收发货地址表 服务实现类
 * </p>
 *
 * @author zhao
 * @since 2022-08-11
 */
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {

}
