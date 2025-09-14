package com.example.dd_ai_end.mapper;

import com.example.dd_ai_end.entity.Sentiment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface SentimentMapper {

    @Options(useGeneratedKeys = true, keyProperty = "sentimentId")
    int insertSentiment(Sentiment sentiment);
}
