package com.example.demo.dao.mapper;

import com.example.demo.domain.bean.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE id = #{ id }")
    User get(int id);

    @Select("SELECT * FROM users")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "age", column = "age")
    })
    List<User> getAll();

    @Insert("INSERT INTO user(name,age) VALUES(#{name}, #{age})")
    void insert(User user);
}
