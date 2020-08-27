package com.example.demo.core.json;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TestBean implements Cloneable {
    private long uid;
    private String name;

    private static TestBean factory = new TestBean(1,"ywj");

    public static TestBean getTestBean() throws CloneNotSupportedException {
        //这种方式效率高于new
        return (TestBean) factory.clone();
    }
}
