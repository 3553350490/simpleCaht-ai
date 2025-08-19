package com.cuit.pojo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "math")
public class Math {
    String teacherName;
    Integer nums;
    @Override
    public String toString() {
        return "Math{" +
                "teacherName='" + teacherName + '\'' +
                ", nums=" + nums +
                '}';
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public Integer getNums() {
        return nums;
    }

    public void setNums(Integer nums) {
        this.nums = nums;
    }
}
