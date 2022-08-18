package com.zhaoyg.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zhaoyg.model.entity.Banner;
import com.zhaoyg.mapper.BannerMapper;
import com.zhaoyg.service.BannerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhaoyg.util.Result;
import com.zhaoyg.vo.BannerVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhao
 * @since 2022-08-17
 */
@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {

    @Override
    public Result listBanner() {
        List<Banner> list = list(Wrappers.<Banner>lambdaQuery().orderByDesc(Banner::getWeight));
        return Result.success(list.stream().map(this::entityToVO).collect(Collectors.toList()));
    }

    private BannerVO entityToVO(Banner banner) {
        BannerVO bannerVO = new BannerVO();
        BeanUtils.copyProperties(banner, bannerVO);
        return bannerVO;
    }

}
