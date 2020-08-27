package com.example.demo.core.project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class QuestionInfo {
    private int id;
    private int lessonId;

    /**
     * 输入模式题目，使用一种模式需json忽略另一种模式字段
     */
    @JsonIgnore
    private OperatorType type;
    @JsonIgnore
    private List<List<String>> questionItems;  //算数区域
    @JsonIgnore
    private List<List<String>> draftItems;     //草稿区域
    @JsonIgnore
    private List<List<String>> resultItems;    //结果区域
    @JsonIgnore
    private String tips;

    /**
     * 选择题模式
     */
    private String title;
    private List<Integer> selectAnswerList;
    private int rightIndex;


}
