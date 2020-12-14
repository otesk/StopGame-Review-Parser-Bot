package com.otesk.bot.parsers;

import com.otesk.bot.domain.Characteristics;
import com.otesk.bot.domain.GameRating;
import com.otesk.bot.domain.Mark;
import com.otesk.bot.domain.Review;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.otesk.bot.utils.StopGameReviewParserUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class StopGameReviewParser {
    private String url;
    private Document document;

    public StopGameReviewParser(String url) throws IOException {
        this.url = url;
        this.document = Jsoup.connect(url).get();
    }

    public Review getReview() throws IOException {
        return Review.builder()
                .name(parseNameOfReview())
                .imageName(parseNameOfImage())
                .mainIdtfRU(parseMainIdtfRu())
                .mainIdtfEn(parseMainIdtfEn())
                .characteristics(Characteristics.builder()
                        .platforms(parseCharacteristics(0))
                        .genres(parseCharacteristics(1))
                        .developer(parseCharacteristics(3))
                        .publishing(parseCharacteristics(4))
                        .build()
                )
                .gameRating(GameRating.builder()
                        .ratingFromUsers(parseRatingFromUsers())
                        .mark(parseRatingFromStopGame())
                        .build())
                .text(parseText())
                .pluses(parsePluses())
                .minuses(parseMinuses())
                .screenshotsNames(parseScreenshotNames())
                .game(parseNameOfReview().replaceAll("_review", ""))
                .build();
    }

    private String parseNameOfReview() {
        Elements elements = document.getElementsByAttributeValue("class", "article-title");
        for (Element element : elements) {
            String reviewName = element.text().toLowerCase();
            reviewName = reviewName.replaceAll(":", "");
            reviewName = reviewName.replaceAll(" ", "_");
            return reviewName + "_review";
        }
        return null;
    }

    private String parseNameOfImage() throws IOException {
        Elements elements = document.getElementsByAttributeValue("class", "image-game-logo no-border");
        String fileName = null;
        for (Element element : elements) {
            Element divElement = element.child(0);
            String attr = divElement.attr("style");
            URL url = new URL(attr.substring(attr.indexOf("https://"), attr.indexOf(")")));
            String[] urls = url.toString().split("/");
            fileName = urls[urls.length - 1];
            String filePath = "E:\\Alex\\Stopgame-review-parser-bot\\src\\main\\resources\\reviews\\"
                    + parseNameOfReview() + "\\images\\" + fileName;
            if (StopGameReviewParserUtils.createReviewDir(parseNameOfReview())) {
                downloadImage(url, filePath);
            }
        }
        return fileName;
    }

    private void downloadImage(URL url, String filePath) throws IOException {
        BufferedImage image = ImageIO.read(url);
        File file = new File(filePath);
        ImageIO.write(image, "jpg", file);
    }

    private String parseMainIdtfRu() {
        Elements elements = document.getElementsByAttributeValue("class", "article-header");
        for (Element element : elements) {
            Element h1Element = element.child(0);
            return h1Element.text();
        }
        return null;
    }

    private String parseMainIdtfEn() {
        Elements elements = document.getElementsByAttributeValue("class", "game-details");
        for (Element element : elements) {
            Element h2Element = element.child(0);
            Element aElement = h2Element.child(0);
            return aElement.text() + " review";
        }
        return null;
    }

    private List<String> parseCharacteristics(int index) {
        Elements elements = document.getElementsByAttributeValue("class", "game-specs");
        for (Element element : elements) {
            return Arrays.stream(element.child(index).child(1).text().split(", "))
                    .map(String::toLowerCase)
                    .map(str -> str.replaceAll(" ", "_"))
                    .collect(Collectors.toList());
        }
        return null;
    }

    private String parseRatingFromUsers() {
        Elements elements = document.getElementsByAttributeValue("class", "game-info");
        for (Element element : elements) {
            return element.child(0).child(1).text();
        }
        return null;
    }

    private Mark parseRatingFromStopGame() {
        Elements elements = document.getElementsByAttributeValue("class", "game-stopgame-score");
        for (Element element : elements) {
            String attr = element.child(0).attr("class");
            int choose = Integer.parseInt(attr.substring(attr.indexOf("-") + 1));
            switch (choose) {
                case 1:
                    return Mark.RUBBISH;
                case 2:
                    return Mark.CLAWLER;
                case 3:
                    return Mark.COMMENDABLE;
                case 4:
                    return Mark.AMAZINGLY;
                default:
                    return null;
            }
        }
        return null;
    }

    private String parseText() {
        Elements elements = document.getElementsByAttributeValue("class", "article article-show");
        for (Element element : elements) {
            return StopGameReviewParserUtils.renderText(element.text(), document, parseMainIdtfRu());
        }
        return null;
    }

    private List<String> parsePluses() {
        Elements elements = document.getElementsByAttributeValue("class", "article article-show");
        for (Element element : elements) {
            String text = element.text();
            String plusesString = text.substring(text.indexOf("Плюсы:") + 6, text.indexOf("Минусы:"));
            return StopGameReviewParserUtils.renderSetOfCritics(plusesString);
        }
        return null;
    }

    private List<String> parseMinuses() {
        Elements elements = document.getElementsByAttributeValue("class", "article article-show");
        for (Element element : elements) {
            String text = element.text();
            String minusesString = text.substring(text.indexOf("Минусы:") + 7, text.indexOf("Написать комментарий"));
            return StopGameReviewParserUtils.renderSetOfCritics(minusesString);
        }
        return null;
    }

    private List<String> parseScreenshotNames() throws IOException {
        Elements elements = document.getElementsByAttributeValue("class", "review_image");
        List<String> screenshotPaths = new ArrayList<>();
        for (Element element : elements) {
            Element aElement = element.child(0);
            URL url = new URL(aElement.attr("href"));
            String[] urls = url.toString().split("/");
            String fileName = urls[urls.length - 1];
            String filePath = "E:\\Alex\\Stopgame-review-parser-bot\\src\\main\\resources\\reviews\\" + parseNameOfReview() + "\\images\\" + fileName;
            screenshotPaths.add(fileName);
            downloadImage(url, filePath);
        }
        return screenshotPaths;
    }
}
