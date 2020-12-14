package com.otesk.bot.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Setter
@Getter
@Builder
public class Characteristics {
    private List<String> platforms;
    private List<String> genres;
    private List<String> developer;
    private List<String> publishing;

    @Override
    public String toString() {
        return "Characteristics{" +
                "platforms=" + platforms +
                ", genres=" + genres +
                ", developer=" + developer +
                ", publishing=" + publishing +
                '}';
    }
}
