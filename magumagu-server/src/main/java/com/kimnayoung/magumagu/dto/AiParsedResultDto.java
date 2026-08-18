package com.kimnayoung.magumagu.dto;

import lombok.Getter;
import java.util.List;

@Getter
public class AiParsedResultDto {
    private String category;
    private String format;
    private List<String> tags;
    private String summary;
    private String extracted_text;
}