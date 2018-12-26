package com.greentown.lottery.controller;

import com.greentown.common.aop.LogAnnotation;
import com.greentown.lottery.service.OneService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Api(description ="demo接口")
@RestController
@RequestMapping("/api")
public class OneController {

    private final Logger logger = LoggerFactory.getLogger(OneController.class);

    @Resource
    private OneService oneService;

    @LogAnnotation
    @ApiOperation("mysqlTest")
    @GetMapping("/mysqlTest")
    public Integer mysqlTest(){
        return oneService.getOne();
    }


    @Autowired
    private RedisTemplate redisTemplate;

    @LogAnnotation
    @ApiOperation("mysqlTest")
    @GetMapping(value = "/redisTest")
    public String redisTest() {
        try {
            // 缓存有效期2秒
            redisTemplate.opsForValue().set("test-key", "redis测试内容", 2, TimeUnit.SECONDS);
            logger.info("从Redis中读取数据：" + redisTemplate.opsForValue().get("test-key").toString());

            TimeUnit.SECONDS.sleep(3);
            logger.info("等待3秒后尝试读取过期的数据：" + redisTemplate.opsForValue().get("test-key"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "OK";
    }

}
