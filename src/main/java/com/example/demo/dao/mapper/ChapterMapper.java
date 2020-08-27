package com.example.demo.dao.mapper;

import com.example.demo.core.project.ChapterBean;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ChapterMapper {

    @Select("SELECT * FROM chapter")
    List<ChapterBean> getAll();
}
