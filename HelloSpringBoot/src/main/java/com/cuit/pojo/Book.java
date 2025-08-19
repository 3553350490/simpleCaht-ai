package com.cuit.pojo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import jakarta.validation.constraints.Email; // Spring Boot 3.x
import org.springframework.validation.annotation.Validated;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 将配置文件里面的属性映射到Book的每一个属性里面去
 *我们使用@ConfigurationProperties来实现,而prefix的值就是指定
 * 了与配置文件当中的哪一个(在这里就是值Book)进行映射，但是这个注解要想生效就必须
 * 要使得Book组件为容器里面的组件，那么我们需要加上@Component
 */
@ConfigurationProperties(prefix = "book")
    /*
    * 我们将@ConfigurationProperties(prefix = "book")删除掉，
    * Book类上面的@Component保留，然后使用@Value
    * (为Spring底层的一个注解，可以使用${key},
    * 字面量,#{spel})来给Book的属性注入值。
    * */
@Component
//@Validated // 让 @Value 注入时就能校验
@PropertySource(value="classpath:my.properties")
public class Book {
    @Value("${book.name}")//配置文件的Key
    //@Email(message = "book.name 必须是邮箱格式")//JSR-303 校验
    String name;
    //@Email(message = "book.name 必须是邮箱格式")
   //@Value("2020/11/9")//字面量
    Date time;
    //@Value("#{12*3.4}")//SPEL表达式
            /*#{...}：Spring Expression Language（Spring 表达式语言）。
            这里 12*3.4 会被计算成 40.8，然后赋值给 price。
            SpEL 支持：
            数学运算（+ - * / %）
            调用 Bean 方法（#{myBean.method()}）
            条件运算（#{age > 18 ? 'adult' : 'child'}）
            访问集合（#{myList[0]}）
            * */
    Double price;
//    Book{name='李四', time=Mon Nov 09 00:00:00 CST 2020, price=40.8, map=null, list=null, math=null}
    //不支持复杂类型(List是支持的
    //@Value("${book.map}")会报错
    Map<String,String> map;
    @Value("book.list")
    List<String> list;
    Math math;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public Math getMath() {
        return math;
    }

    public void setMath(Math math) {
        this.math = math;
    }

    @Override
    public String toString() {
        return "Book{" +
                "name='" + name + '\'' +
                ", time=" + time +
                ", price=" + price +
                ", map=" + map +
                ", list=" + list +
                ", math=" + math +
                '}';
    }
}
