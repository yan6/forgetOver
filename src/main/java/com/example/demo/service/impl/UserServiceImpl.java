package com.example.demo.service.impl;

import com.example.demo.dao.mapper.UserMapper;
import com.example.demo.domain.bean.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public User get(int id) {
        return userMapper.get(id);
    }

    @Override
    @Transactional
    public void insert(String name, int age) {
        User user = new User(name, age);
        userMapper.insert(user);
    }

    @Override
    public User getUserById(int name) {
        System.out.println("3333");
        return userMapper.get(name);

    }

}
