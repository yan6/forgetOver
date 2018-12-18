package com.example.demo.domain.bean;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class User {
    private int id;
    private String name;
    private int age;

    public User(){}

    public User(String name,int age){
        this.name=name;
        this.age=age;
    }
}
