package com.example.demo.service;

import com.example.demo.core.project.ChapterListInfo;
import com.example.demo.core.project.QuestionBean;
import com.example.demo.core.project.QuestionInfo;
import com.example.demo.domain.bean.User;

public interface UserService {

    User get(int id);

    void insert(String name, int age);

    User getUserById(int name);

    ChapterListInfo getChapterListInfo();

    void insertQuestionBean(QuestionBean questionBean);

    QuestionInfo getQuestionBean(int numberOne, int numberTwo);

    String test1(int numberOne, int numberTwo);

    ChapterListInfo getChapterListInfoOther();
}
