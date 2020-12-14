package com.otesk.bot.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum Mark {
    RUBBISH("Мусор"), CLAWLER("Проходняк"), COMMENDABLE("Похвально"), AMAZINGLY("Изумительно");

    private final String name;
}
