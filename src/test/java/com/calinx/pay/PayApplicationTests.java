package com.calinx.pay;

import cn.hutool.core.util.RandomUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PayApplicationTests {

    @Test
    public void contextLoads() {
        Random random = new Random();
        for(int i = 0;i<10;i++){

            int nextInt = random.nextInt(2);
            System.out.println(nextInt);
        }
        System.out.println("################################");
        for(int i = 0;i<10;i++){
            System.out.println(RandomUtil.randomInt(0,2));
        }



    }

}
