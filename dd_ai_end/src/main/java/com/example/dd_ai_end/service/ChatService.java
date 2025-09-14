package com.example.dd_ai_end.service;

import com.example.dd_ai_end.entity.ChatHistory;
import com.example.dd_ai_end.param.ChatContentParam;
import com.example.dd_ai_end.param.ChatInsertParam;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.List;

public interface ChatService {

    List<ChatContentParam> selectChatInfoByTime (String username, LocalDate timestamp);

    String chatRecordInsert(ChatInsertParam chatInsertParam);
}
