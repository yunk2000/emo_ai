package com.example.dd_ai_end.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Sentiment implements Serializable {
    /**
     * 情感记录表ID（主键）
     */
    private Integer sentimentId;

    /**
     * 情感分类（"愉悦"0, "平静"1, "孤独"2, "焦虑"3）
     */
    private Integer sentimentKind;

    /**
     * 情感得分，四分类最高的得分那个分类，范围就是0-1
     */
    private Double sentimentScore;

}
