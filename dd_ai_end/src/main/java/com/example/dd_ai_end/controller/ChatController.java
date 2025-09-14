package com.example.dd_ai_end.controller;

import com.example.dd_ai_end.entity.ChatHistory;
import com.example.dd_ai_end.entity.User;
import com.example.dd_ai_end.param.ChatContentParam;
import com.example.dd_ai_end.param.ChatInsertParam;
import com.example.dd_ai_end.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatController {
    private final ChatService chatService;

    /**
     * 按日期查找该天内所有的信息
     */
    @GetMapping("selectChatInfoByTime")
    public List<ChatContentParam> selectChatInfoByTime (String username, LocalDate timestamp){
        return chatService.selectChatInfoByTime(username,timestamp);
    }

    /**
     * 插入信息
     */
    @PostMapping("chatRecordInsert")
    public ResponseEntity<?> chatRecordInsert(@RequestBody ChatInsertParam chatInsertParam) {
        try {
            String result = chatService.chatRecordInsert(chatInsertParam);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "result", result
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "数据插入失败", "detail", e.getMessage()));
        }
//        return chatService.chatRecordInsert(chatInsertParam);
    }

    /**
     *
     */
}
