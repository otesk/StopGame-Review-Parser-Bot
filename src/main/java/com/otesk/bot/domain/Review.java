package com.otesk.bot.domain;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private String name;
    private String imageName;
    private String mainIdtfRU;
    private String mainIdtfEn;
    private Characteristics characteristics;
    private GameRating gameRating;
    private String text;
    private List<String> pluses;
    private List<String> minuses;
    private List<String> screenshotsNames;
    private String game;

    @Override
    public String toString() {
        return "\n\nReview:\nname = " + name +
                "\nimagePath = " + imageName +
                "\nmainIdtfRu = " + mainIdtfRU +
                "\nmainIdtfEn = " + mainIdtfEn +
                "\n" + characteristics +
                "\n" + gameRating +
                "\n\n TEXT:\n" + text +
                "\nPluses:" + pluses +
                "\nMinuses: " + minuses +
                "\nScreenshots: " + screenshotsNames +
                "\nGame : " + game;

    }
}
