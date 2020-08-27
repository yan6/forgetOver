package com.example.demo.domain.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReplyBean {
    protected Long id;
    protected String sId;//这个字段是 因为 前端 Long 的精度不够 处理不了 超过 2^58 的数字 而加了一个 字符串形式 的 id
    protected int videoId;
    protected String videoTitle;
    protected Long parentReplyId;
    @JsonIgnore
    protected Long parentReplyUid;
    protected Long rootReplyId;
    protected int sequence;
    protected String message;
    protected Date createTime;
    protected Long uid;
    protected User user;
    protected int likeCount;
    protected int reportCount;
    protected boolean liked;
    protected boolean hot;
    protected boolean isUserBlocked;
    protected String userType;
    protected String actionUrl;
    protected String imageUrl;
    protected Integer ugcVideoId;
    protected String parentMessage;
    protected User parentUser;

    public ReplyBean(){}

}

