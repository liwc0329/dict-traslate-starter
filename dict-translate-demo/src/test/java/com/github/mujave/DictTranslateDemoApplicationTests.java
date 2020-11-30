package com.github.mujave;

import com.github.mujave.service.DictCacheService;
import com.github.mujave.service.TestServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DictTranslateDemoApplicationTests {

    @Autowired
    TestServiceImpl testService;

    @Autowired
    DictCacheService dictCacheService;

    @Test
    void test1() {
        System.out.println(testService.getStudent());
    }

    @Test
    void test2() {
        System.out.println(testService.testDictEntity());
    }

    @Test
    void test3() {
        System.out.println(testService.testDictCollection());
    }

    @Test
    void test4(){
        System.out.println(testService.testMap());
    }

}
