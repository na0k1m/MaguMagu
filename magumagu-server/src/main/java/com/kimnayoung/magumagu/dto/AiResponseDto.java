package com.kimnayoung.magumagu.dto;

import lombok.Getter;
import java.util.List;

@Getter
public class AiResponseDto {
    private List<Choice> choices;

    @Getter
    public static class Choice {
        private Message message;
    }

    @Getter
    public static class Message {
        private String content; // 프롬프트로 강제한 JSON 텍스트가 들어있음
        private String reasoning_content; // 모델의 추론 과정 (필요시 확인용)
    }
}
