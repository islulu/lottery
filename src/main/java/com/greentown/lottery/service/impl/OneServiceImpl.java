package com.greentown.lottery.service.impl;

import com.greentown.lottery.mapper.OneMapper;
import com.greentown.lottery.service.OneService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class OneServiceImpl implements OneService {
    @Resource
    private OneMapper oneMapper;

    @Override
    public Integer getOne() {
        Integer one = oneMapper.getOne();
        return one;
    }
}
