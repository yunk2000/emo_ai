package com.example.dd_ai_end.service;

import com.example.dd_ai_end.common.EmotionServiceClient;
import com.example.dd_ai_end.common.EmotionServiceResult;
import com.example.dd_ai_end.common.SentimentClient;
import com.example.dd_ai_end.common.SentimentResult;
import com.example.dd_ai_end.entity.AiMessage;
import com.example.dd_ai_end.entity.ChatHistory;
import com.example.dd_ai_end.entity.Sentiment;
import com.example.dd_ai_end.entity.User;
import com.example.dd_ai_end.mapper.AiMessageMapper;
import com.example.dd_ai_end.mapper.ChatHistoryMapper;
import com.example.dd_ai_end.mapper.SentimentMapper;
import com.example.dd_ai_end.mapper.UserMapper;
import com.example.dd_ai_end.param.ChatContentParam;
import com.example.dd_ai_end.param.ChatInsertParam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{
    private final ChatHistoryMapper chatHistoryMapper;
    private final UserMapper userMapper;
    private final SentimentMapper sentimentMapper;
    private final AiMessageMapper aiMessageMapper;

    @Override
    public List<ChatContentParam> selectChatInfoByTime (String username, LocalDate timestamp){
        List<ChatHistory> chatHistories = chatHistoryMapper.selectByTime(timestamp);
//        System.out.println(chatHistories);
        List<ChatContentParam> result = new ArrayList<>();

        for (ChatHistory chat : chatHistories) {
            // 根据 userId 查询 username
            String foundUsername = userMapper.findUsernameById(chat.getUserId());
//            System.out.println(foundUsername);

            if (!Objects.equals(foundUsername, username)){
                continue;
            }

            // 根据aiId外键查询对应ai信息
            AiMessage aiMessage = aiMessageMapper.findAiMessageByAiId(chat.getAiMessageId());
//            System.out.println(aiMessage);

            // 构造返回对象（假设 ChatContentParam 包含 username 和聊天内容）
            ChatContentParam param = new ChatContentParam();
            param.setUsername(foundUsername);
            param.setContent(chat.getContent());
            param.setTimestamp(chat.getTimestamp());
            param.setHss(chat.getHss());
            param.setContentAi(aiMessage.getContentAi());
            param.setUseTime(aiMessage.getUseTime());

            result.add(param);
        }

        return result;
    }

    public String chatRecordInsert(ChatInsertParam chatInsertParam){
        // 1. 获取用户ID
        User user = userMapper.selectByUsername(chatInsertParam.getUsername());
        Integer userId = user.getUserId();
        if (userId == null) throw new RuntimeException("用户不存在");

        // 先调用情绪分类模型，得到该语句情绪，并进行存储
        String UserQuest = chatInsertParam.getContent();
        SentimentClient sentimentClient = new SentimentClient();
        SentimentResult result = sentimentClient.predictSentiment(UserQuest);
        int lab = result.getLabel();
        System.out.println("标签：" + result.getLabel());
        System.out.println("置信度：" + result.getConfidence());

        Sentiment sentiment = new Sentiment();
        sentiment.setSentimentKind(lab);  // 示例情绪类型
        sentiment.setSentimentScore(result.getConfidence());   // 示例情绪得分
        sentimentMapper.insertSentiment(sentiment);

        // 然后调用文心一言大语言模型，存储起来
        String label_string = "";
        if(lab == 0){
            label_string = "愉悦";
        }
        else if(lab == 1){
            label_string = "平静";
        }
        else if(lab == 2){
            label_string = "孤独";
        }else {
            label_string = "焦虑";
        }
        String C_Quest = "文本情感是"+ label_string + ", 请根据文本情感辅助回答问题: " + UserQuest;
        System.out.println("标签：" + C_Quest);

        EmotionServiceClient client = new EmotionServiceClient();
        EmotionServiceResult response = client.getEmotionResponse(C_Quest);
        System.out.println(response.getContentAi());
        System.out.println(response.getTimestampAi());
        System.out.println(response.getUseTime());

        AiMessage aiMessage = new AiMessage();
        aiMessage.setContentAi(response.getContentAi());  // 示例回复内容
        aiMessage.setTimestampAi(response.getTimestampAi());
        aiMessage.setUseTime(response.getUseTime());                   // 示例响应耗时（毫秒）
        aiMessageMapper.insertAiMessage(aiMessage);

        // 然后把上述内容一并存入数据库
        ChatHistory chat = new ChatHistory();
        chat.setUserId(userId);
        chat.setContent(chatInsertParam.getContent());
        chat.setTimestamp(chatInsertParam.getTimestamp());
        chat.setHss(chatInsertParam.getHss());
        chat.setAiMessageId(aiMessage.getAiMessageId());   // 关联AI消息ID
        chat.setSentimentId(sentiment.getSentimentId());    // 关联情绪ID
        chatHistoryMapper.insertChatHistory(chat);

        return response.getContentAi();
    }

}
