package com.cuit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

/**
 * 1.创建HelloWorldMainApplication类,并声明这是一个主程序类也是个SpringBoot应用
 */
@SpringBootApplication
/*
* @Target({ElementType.TYPE})
* 限定该注解只能用于类、接口或枚举声明上，不能用于方法或字段。
@Retention(RetentionPolicy.RUNTIME)
* 注解在运行时保留，可通过反射读取，这是Spring实现动态代理和AOP的关键。
@Documented
* 将该注解包含在Javadoc中，方便开发者查阅。
@Inherited
* 允许子类继承父类的注解配置（但实际Spring Boot的自动配置逻辑不依赖继承）。
@SpringBootConfiguration
* 本质是@Configuration的变体，标识该类为配置类
*允许通过@Bean定义Spring容器管理的对象
@EnableAutoConfiguration
* 激活Spring Boot的自动配置机制
*根据spring.factories和条件注解（如@ConditionalOnClass）动态加载配置
*示例：当classpath存在DataSource.class时自动配置数据库连接池
@ComponentScan(
    excludeFilters = {@Filter(
    type = FilterType.CUSTOM,
    classes = {TypeExcludeFilter.class}
), @Filter(
    type = FilterType.CUSTOM,
    classes = {AutoConfigurationExcludeFilter.class}
)}
)

默认扫描当前包及其子包下的@Component、@Service等注解
排除过滤器说明：
TypeExcludeFilter：支持开发者自定义排除规则
AutoConfigurationExcludeFilter：防止自动配置类被重复扫描
* */
//@ComponentScan(basePackages = {"com.cuit.demo", "com.cuit.controller","com.cuit.pojo"})
//@ImportResource(value="classpath:beans.xml")
public class HelloSpringBoot {

    /**
     * 2.编写main方法
     */
    public static void main(String[] args) {
        //3.开始启动主程序类
        SpringApplication.run(HelloSpringBoot.class,args);
    }
}
