package com.otesk.bot.doccreators;

import com.otesk.bot.domain.Review;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class ScsFileCreator {

    private String text;
    private final Review review;

    public ScsFileCreator(Review review) {
        this.review = review;
    }

    public void createScsFile() {
        String filename = review.getName() + ".scs";
        String filepath = "E:\\Alex\\Stopgame-review-parser-bot\\src\\main\\resources\\reviews\\" + review.getName() + "\\" + filename;
        createContent();
        File file = new File(filepath);
        try (PrintWriter printWriter = new PrintWriter(file)) {
            printWriter.print(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createContent() {
        text = "";
        addNameNode();
        addGame();
        addRussianIdtf();
        addEnglishIdtf();
        addLogo();
        createText();
        addText();
        addCharacteristics();
        addRating();
        addCriticism();
        addScreenshots();
    }

    private void createText(){
        String scText = "";
        scText = scText + "<html>\n\t<body>\n\t\t<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"/>" +
                "\n<p>" + review.getText() + "</p>\n\t</body>\n<html>";
        String filename = review.getName() + ".html";
        String filepath = "E:\\Alex\\Stopgame-review-parser-bot\\src\\main\\resources\\reviews\\" + review.getName() + "\\" + filename;
        File file = new File(filepath);
        try (PrintWriter printWriter = new PrintWriter(file)) {
            printWriter.print(scText);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addNameNode() {
        text = text + "concept_review -> " + review.getName() + ";;\n\n";
    }

    private void addGame() {
        text = text + review.getName() + " => nrel_review_of_game : " + review.getGame() + ";;\n\n";
    }

    private void addRussianIdtf() {
        text = text + review.getName()
                + " => nrel_main_idtf:\n["
                + review.getMainIdtfRU()
                + "](*<- lang_ru;;*);";
    }

    private void addEnglishIdtf() {
        text = text + "\n["
                + review.getMainIdtfEn()
                + "](*<- lang_en;;*);;\n\n";
    }

    private void addLogo() {
        text = text + review.getName()
                + " <- rrel_key_sc_element: ...\n(*\n\t<- illustration;;"
                + "\n\t<= nrel_sc_text_translation: ...\n\t(*\n\t\t-> \"file://images/"
                + review.getImageName() + "\"(*=>nrel_format : format_jpg;;*);;"
                + "\n\t*);;\n*);;\n\n";
    }

    private void addCharacteristics() {
        text = text + review.getName()
                + " => nrel_characteristics : ...\n(*\n\t<- set;;\n\t=> nrel_system_identifier : [характеристики игры " +
                review.getGame().replaceAll("_", " ") + "](*<- lang_ru;;*);;"
                + "\n\t=> nrel_platform : ...\n\t(*\n\t\t<-set;;\n\t=> nrel_system_identifier : [поддерживаемые платформы игры " +
                review.getGame().replaceAll("_", " ") + "](*<- lang_ru;;*);;" + "\n\t\t-> ";
        for (String platform : review.getCharacteristics().getPlatforms()) text = text + platform + ";";
        text = text + ";\n\t*);;"
                + "\n\t=> nrel_genre : ...\n\t(*\n\t\t<-set;;\n\t=> nrel_system_identifier : [жанры игры " +
                review.getGame().replaceAll("_", " ") + "](*<- lang_ru;;*);;" + "\n\t\t-> ";
        for (String genre : review.getCharacteristics().getGenres()) text = text + genre + ";";
        text = text + ";\n\t*);;"
                + "\n\t=> nrel_developer : ...\n\t(*\n\t\t<-set;;\n\t=> nrel_system_identifier : [разработчики игры " +
                review.getGame().replaceAll("_", " ") + "](*<- lang_ru;;*);;" + "\n\t\t-> ";
        for (String developer : review.getCharacteristics().getDeveloper()) text = text + developer + ";";
        text = text + ";\n\t*);;"
                + "\n\t=> nrel_publishing : ...\n\t(*\n\t\t<-set;;\n\t=> nrel_system_identifier : [издательства игры " +
                review.getGame().replaceAll("_", " ") + "](*<- lang_ru;;*);;" + "\n\t\t-> ";
        for (String publishing : review.getCharacteristics().getPublishing()) text = text + publishing + ";";
        text = text + ";\n\t*);;\n*);;\n\n";
    }

    private void addRating() {
        text = text + review.getName() + " => nrel_game_rating: ...\n(*\n\t<- set;;\n\t=> nrel_system_identifier : [оценка игры " +
                review.getGame().replaceAll("_", " ") + "](*<- lang_ru;;*);;" + "\n"
                + "\t=> nrel_users_rating: ...\n\t(*\n\t\t => nrel_system_identifier: ["
                + review.getGameRating().getRatingFromUsers() + "](*<- lang_ru;;*);;\n\t*);;"
                + "\n\t=> nrel_stopgame_rating: ...\n\t(*\n\t\t => nrel_system_identifier: ["
                + review.getGameRating().getMark().getName() + "](*<- lang_ru;;*);;\n\t*);;\n*);;\n\n";
    }

    private void addText() {
        text = text + review.getName()
                + " <- rrel_key_sc_element: ...\n(*\n\t<- explanation;;\n\t<= nrel_sc_text_translation: ..."
                + "\n\t(*\n\t\t-> \"file://" + review.getName() + ".html\"(*<- lang_ru;;*);;\n\t*);;\n*);;\n\n";
    }

    private void addCriticism() {
        text = text + review.getName() + " => nrel_criticism: ...\n(*\n\t<- set;;\n\t=> nrel_system_identifier : [плюсы и минусы игры " +
                review.getGame().replaceAll("_", " ") + "](*<- lang_ru;;*);;"
                + "\n\t-> rrel_pluses_of_the_game: ...\n\t(*\n\t\t<- set;;\n\t\t=> nrel_system_identifier : [плюсы игры " +
                                review.getGame().replaceAll("_", " ") + "](*<- lang_ru;;*);;";
        for (String plus : review.getPluses()) {
            text = text + "\n\t\t-> ...\n\t\t(*\n\t\t\t=> nrel_system_identifier: [" + plus
                    + "](*<- lang_ru;;*);;\n\t\t*);;";
        }
        text = text + "\n\t*);;\n\t-> rrel_minuses_of_the_game: ...\n\t(*\n\t\t<- set;;\n\t\t=> nrel_system_identifier : [минусы игры " +
                review.getGame().replaceAll("_", " ") + "](*<- lang_ru;;*);;";
        for (String minus : review.getMinuses()) {
            text = text + "\n\t\t-> ...\n\t\t(*\n\t\t\t=> nrel_system_identifier: [" + minus
                    + "](*<- lang_ru;;*);;\n\t\t*);;";
        }
        text = text + "\n\t*);;\n*);;\n\n";
    }

    private void addScreenshots() {
        text = text + review.getName() + " => nrel_screenshots: ...\n(*\n\t<- set;;\n\t=> nrel_system_identifier : [скриншоты игры " +
                review.getGame().replaceAll("_", " ") + "](*<- lang_ru;;*);;";
        for (String screenshot : review.getScreenshotsNames()) {
            text = text + "\n\t-> \"file://images/" + screenshot + "\"(*=>nrel_format:format_jpg;;*);;";
        }
        text = text + "\n*);;";
    }
}
