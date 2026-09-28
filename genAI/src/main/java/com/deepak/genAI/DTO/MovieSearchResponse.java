package com.deepak.genAI.DTO;

public record MovieSearchResponse(
        String title,
        String description,
        double similarity
) {}
