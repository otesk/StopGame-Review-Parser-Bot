package com.otesk.bot.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class GameRating {
    private String ratingFromUsers;
    private Mark mark;

    @Override
    public String toString() {
        return "GameRating{" +
                "ratingFromUsers='" + ratingFromUsers + '\'' +
                ", mark=" + mark +
                '}';
    }
}
