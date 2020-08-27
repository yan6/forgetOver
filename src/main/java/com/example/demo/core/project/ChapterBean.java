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
public class ChapterBean {
    private int id;
    private String title;
    private List<LessonBean> lessonList;

    public ChapterBean(int id, String title) {
        this.id = id;
        this.title = title;
    }
}
