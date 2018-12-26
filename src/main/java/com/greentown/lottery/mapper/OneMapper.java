package com.greentown.lottery.mapper;

import com.greentown.common.mapper.IBaseMapper;
import com.greentown.lottery.dto.OneDO;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface OneMapper extends IBaseMapper<OneDO> {

    Integer getOne();

}