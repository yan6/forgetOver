package com.example.demo.dao.mapper;

import com.example.demo.core.project.QuestionBean;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface QuestionMapper {
    @Select("SELECT * FROM question WHERE lesson_id=#{lessonId}")
    @Results({
            @Result(property = "numberOne", column = "number_one"),
            @Result(property = "numberTwo", column = "number_two"),
            @Result(property = "lessonId", column = "lesson_id")
    })
    List<QuestionBean> getQuestionList(@Param("lessonId") int lessonId);

    @Insert("INSERT INTO question(type,number_one,number_two,result,tips,lesson_id)" +
            "VALUES(#{type},#{numberOne},#{numberTwo},#{result},#{tips},#{lessonId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(QuestionBean questionBean);

    @Update("UPDATE question SET tips=#{tips} WHERE id=#{id}")
    int updateTips(@Param(value = "tips") String tips, @Param(value = "id") int id);
}
