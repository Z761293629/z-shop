package com.zhaoyg.service;

import com.zhaoyg.UserServiceTest;
import com.zhaoyg.model.entity.Address;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zhao
 * @date 2022/8/11
 */
@Slf4j
class AddressTest extends UserServiceTest {

    @Autowired
    private AddressService addressService;

    @Test
    void testDetail() {
        Address address = addressService.getById(1L);
        log.info(address.toString());
        Assertions.assertNotNull(address);

    }

}
