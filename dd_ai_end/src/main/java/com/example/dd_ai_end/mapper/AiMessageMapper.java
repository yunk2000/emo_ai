package com.example.dd_ai_end.mapper;

import com.example.dd_ai_end.entity.AiMessage;
import com.example.dd_ai_end.entity.ChatHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface AiMessageMapper {

    AiMessage findAiMessageByAiId(Integer aiMessageId);

    @Options(useGeneratedKeys = true, keyProperty = "aiMessageId")
    int insertAiMessage(AiMessage aiMessage);
}
