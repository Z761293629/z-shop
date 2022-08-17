package com.zhaoyg.service;

import com.zhaoyg.model.entity.Address;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhaoyg.request.AddressAddRequest;
import com.zhaoyg.util.Result;

/**
 * <p>
 * 电商-公司收发货地址表 服务类
 * </p>
 *
 * @author zhao
 * @since 2022-08-11
 */
public interface AddressService extends IService<Address> {

    void addAddress(AddressAddRequest addressAddRequest);

    Result detail(Long addressId);

    Result delete(Long addressId);

    Result listAllAddressOfUser();

}
