package com.dataease.cloud.test;

import com.dataease.cloud.utils.GlobalConfigurations;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * [描述当前类是什么作用]
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/3 3:09 PM
 **/
@SpringBootTest
public class ApplicationTest {

    @Test
    public void testGetModile(){
        System.out.println(GlobalConfigurations.isReleaseMode());
    }
}
