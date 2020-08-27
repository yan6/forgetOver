package com.example.demo.core.project;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LessonBean {
    private int id;
    private String title;
    private int chapterId;
    private int locked;
    private List<QuestionInfo> questionList;

    public LessonBean(int id, String title) {
        this.id = id;
        this.title = title;
    }
}
