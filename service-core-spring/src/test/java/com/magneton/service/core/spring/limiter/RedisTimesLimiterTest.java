package com.magneton.service.core.spring.limiter;

import com.google.common.util.concurrent.TimeLimiter;
import com.magneton.service.core.limiter.DefaultTimesLimiter;
import com.magneton.service.core.limiter.LimiterRule;
import com.magneton.service.core.limiter.TimesLimiter;
import com.magneton.service.core.limiter.TimesLimiterConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zhangmingshuang
 * @since 2019/6/21
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTimesLimiterTest {

    @Autowired
    private RedisTemplate redisTemplate;

    private TimesLimiterConfig config;
    private String seconds = "seconds";

    @Before
    public void before() {

        Map<String, LimiterRule> rules = new HashMap<>();
        LimiterRule secondsLimiterRule = new LimiterRule();
        //3s
        secondsLimiterRule.setExpireIn(3);
        //3times
        secondsLimiterRule.setTimes(3);
        rules.put(seconds, secondsLimiterRule);

        config = new TimesLimiterConfig();
        config.setRules(rules);

    }


    @Test
    public void test4() {

        Map<String, LimiterRule> rules = new HashMap<>();
        LimiterRule secondsLimiterRule = new LimiterRule();
        //3s
        secondsLimiterRule.setExpireIn(3);
        //3times
        secondsLimiterRule.setTimes(3);
        rules.put(seconds, secondsLimiterRule);

        TimesLimiterConfig config3 = new TimesLimiterConfig();
        config3.setRules(rules);
        config3.setForce(true);

        LimiterRule defaultRule = new LimiterRule();
        defaultRule.setTimes(1);
        defaultRule.setExpireIn(3);
        config3.setDefaultRule(defaultRule);

        TimesLimiter defaultTimesLimiter = new RedisTimesLimiter(redisTemplate);
        defaultTimesLimiter.afterConfigSet(config3);

        int t = 1000;
        CountDownLatch cdl = new CountDownLatch(t);
        AtomicLong error = new AtomicLong();
        for (int i = 0; i < t; i++) {
            new Thread() {
                @Override
                public void run() {
                    boolean r = defaultTimesLimiter.increase("aaaa", "notRule");
                    if (!r) {
                        error.incrementAndGet();
                    }
                    cdl.countDown();
                }
            }.start();
        }
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("失败：" + error);
        Assert.assertEquals(error.intValue(), t - 1);

    }

    @Test
    public void test3() {
        Map<String, LimiterRule> rules = new HashMap<>();
        LimiterRule secondsLimiterRule = new LimiterRule();
        //3s
        secondsLimiterRule.setExpireIn(3);
        //3times
        secondsLimiterRule.setTimes(3);
        rules.put(seconds, secondsLimiterRule);

        TimesLimiterConfig config3 = new TimesLimiterConfig();
        config3.setRules(rules);
        config3.setForce(true);

        LimiterRule defaultRule = new LimiterRule();
        defaultRule.setTimes(1);
        defaultRule.setExpireIn(3);
        config3.setDefaultRule(defaultRule);

        TimesLimiter defaultTimesLimiter = new RedisTimesLimiter(redisTemplate);
        defaultTimesLimiter.afterConfigSet(config3);

        boolean res = defaultTimesLimiter.increase("testKey", "notRule");
        Assert.assertTrue(res);

        res = defaultTimesLimiter.increase("testKey", "notRule");
        Assert.assertFalse(res);
    }

    @Test
    public void test2() {
        TimesLimiter timesLimiter = new RedisTimesLimiter(redisTemplate);
        timesLimiter.afterConfigSet(config);

        int t = 1000;
        CountDownLatch cdl = new CountDownLatch(t);
        AtomicLong error = new AtomicLong();
        for (int i = 0; i < t; i++) {
            new Thread() {
                @Override
                public void run() {
                    boolean r = timesLimiter.increase("test", seconds);
                    if (!r) {
                        error.incrementAndGet();
                    }
                    cdl.countDown();
                }
            }.start();
        }
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("失败：" + error);
        Assert.assertEquals(error.intValue(), t - 3);
    }

    @Test
    public void test() {
        TimesLimiter timesLimiter = new RedisTimesLimiter(redisTemplate);
        timesLimiter.afterConfigSet(config);

        boolean res = timesLimiter.increase("testKey", seconds);
        Assert.assertTrue(res);

        res = timesLimiter.increase("testKey", seconds);
        Assert.assertTrue(res);

        res = timesLimiter.increase("testKey", seconds);
        Assert.assertTrue(res);

        res = timesLimiter.increase("testKey", seconds);
        Assert.assertFalse(res);
        try {
            Thread.sleep(3 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        res = timesLimiter.increase("testKey", seconds);
        Assert.assertTrue(res);
    }

}