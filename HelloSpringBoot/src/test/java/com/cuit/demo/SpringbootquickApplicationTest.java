package com.cuit.demo;

import com.cuit.pojo.Book;

import com.cuit.pojo.Library;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
//@ComponentScan(basePackages = {"com.cuit.demo", "com.cuit.controller","com.cuit.pojo"})
public class SpringbootquickApplicationTest {
    @Autowired
    Book book;
    @Test
    public void contextLoads() {
        System.out.println(book);
    }

    @Autowired
    Library library;
    @Test
    public void testBooks() {
        System.out.println(library.getBooks());
    }
    @Autowired
    ApplicationContext ioc;
    @Test
    public void contextLoad2(){
        System.out.println(ioc.containsBean("english"));//没加@ImportSource之前false
        System.out.println(ioc.getBean("book"));
    }
}
