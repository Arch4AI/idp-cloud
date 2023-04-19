package com.dataease.cloud.controller;

import com.dataease.cloud.constant.Constants;
import com.dataease.cloud.entity.response.Result;
import com.dataease.cloud.exception.CloudException;
import com.dataease.cloud.utils.CaptchaDto;
import com.dataease.cloud.utils.SliderCaptchaUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * 匿名访问
 * 这个仅仅是测试请勿直接使用
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/8 4:20 PM
 **/
@RestController
@RequestMapping("anonymous")
@Slf4j
public class AnonymousController {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取滑动验证码的信息
     *
     * @param captchaDto
     * @return
     */
    @PostMapping("captcha")
    public Result<CaptchaDto> getCaptchaImg(@RequestBody CaptchaDto captchaDto) {
        //获取验证码
        CaptchaDto dto = SliderCaptchaUtils.getCaptcha(captchaDto);
        //设置redis 五分钟到期
        stringRedisTemplate.opsForValue().set(Constants.SLIDER_CAPTCHA_REDIS_KEY + dto.getTicket(), String.valueOf(dto.getBlockX()), 5, TimeUnit.MINUTES);
        //返回一个对象
        CaptchaDto rt = new CaptchaDto();
        rt.setTicket(dto.getTicket());
        rt.setBlockWidth(dto.getBlockWidth());
        rt.setBlockHeight(dto.getBlockHeight());
        rt.setCanvasWidth(dto.getCanvasWidth());
        rt.setCanvasHeight(dto.getCanvasHeight());
        rt.setBlockSrc(dto.getBlockSrc());
        rt.setCanvasSrc(dto.getCanvasSrc());
        rt.setBlockY(dto.getBlockY());
        return Result.success(rt);
    }

    /**
     * 校验滑动验证码
     *
     * @param request
     * @return
     */
    @PostMapping("captcha/validate")
    public Result validateCaptcha(@RequestBody CaptchaDto request) {
        if (!stringRedisTemplate.hasKey(request.getTicket()))
            CloudException.throwException("Captcha Code not exist");
        String redisValue = stringRedisTemplate.opsForValue().get(Constants.SLIDER_CAPTCHA_REDIS_KEY + request.getTicket());
        if (Math.abs(Integer.valueOf(redisValue) - request.getBlockX()) > Constants.SLIDER_CAPTCHA_ALLOW_DEVIATION)
            CloudException.throwException("Please slide to the specified position.");
        //移除key
        stringRedisTemplate.delete(Constants.SLIDER_CAPTCHA_REDIS_KEY + request.getTicket());
        return Result.success("validate success");
    }


    @GetMapping("{id}/test")
    public Result test1(@PathVariable("id")String id){
        return Result.success(id);
    }

    @PostMapping("{id}/test")
    public Result test2(@PathVariable("id")String id){
        return Result.success(id);
    }

}
