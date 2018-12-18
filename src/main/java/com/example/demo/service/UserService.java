package com.example.demo.service;

import com.example.demo.domain.bean.User;

public interface UserService {

    User get(int id);

    void insert(String name, int age);

    User getUserById(int name);
}
