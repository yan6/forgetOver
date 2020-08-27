package com.example.demo.core.project;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class QuestionBean {
    private int id;
    private int type;
    private int numberOne;
    private int numberTwo;
    private int result;
    private int lessonId;
    private String tips;
}
