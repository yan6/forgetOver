package com.example.demo.dao.mapper;

import com.example.demo.core.project.LessonBean;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface LessonMapper {

    @Select("SELECT * FROM lesson WHERE chapter_id=#{chapterId}")
    @Results({
            @Result(property = "chapterId", column = "chapter_id")
    })
    List<LessonBean> getLessonList(@Param("chapterId") int chapterId);
}
