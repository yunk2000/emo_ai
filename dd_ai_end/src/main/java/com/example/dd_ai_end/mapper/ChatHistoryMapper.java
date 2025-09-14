package com.example.dd_ai_end.mapper;

import com.example.dd_ai_end.entity.AiMessage;
import com.example.dd_ai_end.entity.ChatHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ChatHistoryMapper {

    List<ChatHistory>  selectByTime(LocalDate timestamp);

    @Options(useGeneratedKeys = true, keyProperty = "messageId")
    int insertChatHistory(ChatHistory chatHistory);
}
