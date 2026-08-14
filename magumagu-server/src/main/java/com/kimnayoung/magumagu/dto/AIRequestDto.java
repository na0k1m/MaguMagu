package com.kimnayoung.magumagu.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

public class AIRequestDto {
    @Getter
    @Builder
    public static class Request {
        private String model;
        private List<Message> messages;
        private int max_tokens;
    }

    @Getter
    @Builder
    public static class Message {
        private String role;
        private List<Content> content;
    }

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL) // null인 필드는 JSON에서 아예 빼버림
    public static class Content {
        private String type;
        private String text;
        private ImageUrl image_url;
    }

    @Getter
    @Builder
    public static class ImageUrl {
        private String url;
    }
}
