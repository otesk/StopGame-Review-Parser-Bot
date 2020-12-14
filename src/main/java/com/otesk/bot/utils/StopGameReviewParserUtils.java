package com.otesk.bot.utils;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StopGameReviewParserUtils {
    public static String renderText(String text, Document document, String title) {
        Elements elements = document.getElementsByAttributeValue("class", "platform-badge");
        for (Element element : elements) text = text.replaceAll(element.text(), "");
        elements = document.getElementsByAttributeValue("class", "section-heading");
        for (Element element : elements) text = text.replaceAll(element.text(), "");
        text = text.replaceAll(text.substring(text.indexOf("Плюсы: ")), "");
        elements = document.getElementsByTag("h3");
        StringBuffer renderingText = new StringBuffer(text);
        for (Element element : elements) {
            if (text.contains(element.text())) {
                renderingText.insert(renderingText.indexOf(element.text()), "\n\n");
                renderingText.insert(renderingText.indexOf(element.text()) + element.text().length(), "\n\n");
            }
        }
        renderingText.insert(0, "<b>" + title + "</b>" + "\n\n");
        text = new String(renderingText);
        text = text.replaceAll("\n\n ", "\n\n");
        text = text.replaceAll("\n\n", "<p></p>");
        return text;
    }

    public static List<String> renderSetOfCritics(String text) {
        return Arrays.stream(text.split(";"))
                .map(String::trim)
                .map(plus -> plus.replaceAll("\\.", ""))
                .collect(Collectors.toList());
    }

    public static boolean createReviewDir(String name) {
        File dir = new File("E:\\Alex\\Stopgame-review-parser-bot\\src\\main\\resources\\reviews\\" + name);
        File imageDir = new File("E:\\Alex\\Stopgame-review-parser-bot\\src\\main\\resources\\reviews\\" + name + "\\images");
        if (dir.mkdir()) {
            return imageDir.mkdir();
        }
        return false;
    }
}
