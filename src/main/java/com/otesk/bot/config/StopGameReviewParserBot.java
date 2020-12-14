package com.otesk.bot.config;

import com.otesk.bot.doccreators.ScsFileCreator;
import com.otesk.bot.domain.Review;
import com.otesk.bot.parsers.StopGameReviewParser;
import com.otesk.bot.utils.ZipArchiver;
import com.vdurmont.emoji.EmojiParser;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StopGameReviewParserBot extends TelegramLongPollingBot {

    private static final Map<String, String> emoji;
    private static final List<String> systemCommand;

    static {
        emoji = new HashMap<>();
        emoji.put("smile", EmojiParser.parseToUnicode(":smiley:"));
        emoji.put("goodluck", EmojiParser.parseToUnicode(":wink:"));
        emoji.put("disappointed", EmojiParser.parseToUnicode(":disappointed:"));
        emoji.put("brain", EmojiParser.parseToUnicode(":brain:"));
        emoji.put("package", EmojiParser.parseToUnicode(":package:"));
        systemCommand = new ArrayList<>();
        systemCommand.add("/start");
        systemCommand.add("/help");
    }

    @Override
    public String getBotToken() {
        return "1483962845:AAH06zGypV-XmTFzGBKBS-oP3QFeMd--e0s";
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (isSystemCommand(update)) {
            SendMessage sendMessage = StopGameReviewParserBot.getSendMessageForSystemCommand(update);
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(update.getMessage().getChatId()));
            if (validateLink(update.getMessage().getText())) {
                StopGameReviewParser stopGameReviewParser = new StopGameReviewParser(update.getMessage().getText());
                Review review = stopGameReviewParser.getReview();
                ScsFileCreator scsFileCreator = new ScsFileCreator(review);
                scsFileCreator.createScsFile();
                message = createMessageAfterSuccessfulParsing(update.getMessage().getChatId(), review.getName());
            } else
                message.setText("Не могу перейти по ссылке" + emoji.get("disappointed") + "\nДавай-ка попробуем ещё раз.");
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if (update.hasCallbackQuery()) {
            String textOfMessage = update.getCallbackQuery().getMessage().getText();
            String reviewName = textOfMessage.substring(textOfMessage.indexOf("\"") + 1, textOfMessage.indexOf("\"?"));
            reviewName = reviewName.replaceAll(" ", "_");
            if (update.getCallbackQuery().getData().equals("archive")) {
                ZipArchiver.archive(reviewName);
                EditMessageText editMessageText = EditMessageText.builder()
                        .chatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()))
                        .messageId(update.getCallbackQuery().getMessage().getMessageId())
                        .text("Архив готов! Можешь пользоваться " + emoji.get("smile"))
                        .build();
                SendDocument sendDocument = new SendDocument();
                sendDocument.setChatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()));
                sendDocument.setDocument(new InputFile(new File("E:\\Alex\\Stopgame-review-parser-bot\\src\\main\\resources\\reviews\\" + reviewName + ".zip"), reviewName + ".zip"));
                try {
                    execute(sendDocument);
                    execute(editMessageText);
                    FileUtils.deleteDirectory(new File("E:\\Alex\\Stopgame-review-parser-bot\\src\\main\\resources\\reviews\\" + reviewName));
                    new File("E:\\Alex\\Stopgame-review-parser-bot\\src\\main\\resources\\reviews\\" + reviewName + ".zip").delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (update.getCallbackQuery().getData().equals("database")) {
                EditMessageText editMessageText = EditMessageText.builder()
                        .chatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()))
                        .messageId(update.getCallbackQuery().getMessage().getMessageId())
                        .text("Обзор на игру сохранён в базе знаний" + emoji.get("brain"))
                        .build();
                try {
                    if (Files.exists(Paths.get("E:\\Alex\\reviews\\" + reviewName))) {
                        FileUtils.deleteDirectory(new File("E:\\Alex\\reviews\\" + reviewName));
                    }
                    Files.move(Paths.get("E:\\Alex\\Stopgame-review-parser-bot\\src\\main\\resources\\reviews\\" + reviewName),
                            Paths.get("E:\\Alex\\reviews\\" + reviewName));
                    execute(editMessageText);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "StopGameReviewParserBot";
    }

    private static boolean isSystemCommand(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            for (String command : systemCommand) {
                if (update.getMessage().getText().equals(command)) return true;
            }
        }
        return false;
    }

    private static SendMessage getSendMessageForSystemCommand(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        if (update.getMessage().getText().equals("/start")) {
            sendMessage.setText("Привет! Я простой telegram bot, который позволит тебе разбирать обзоры на игры" +
                    "\nЧтобы я сделал свою работу, отправь мне ссылку на обзор с сайта stopgame.ru, а дальше я" +
                    " выдам тебе готовый результат " + emoji.get("smile"));
        } else if (update.getMessage().getText().equals("/help")) {
            sendMessage.setText("Мой алгоритм работы:\nОтпавь мне ссылку на обзор с сайта stopgame.ru, я " +
                    "обработаю обзор и дам возможность выбора, что делать дальше с результатом:\n1. Получить архив. " +
                    "Нажми на данную кнопку, и я отправлю тебе архив с результатом парсинга.\n2. Сохранить в базу. " +
                    "Нажми на данную кнопку, и я сохраню распаршенный обзор в базу знаний.\n\nУдачи! " + emoji.get("goodluck"));
        }
        return sendMessage;
    }


    private SendMessage createMessageAfterSuccessfulParsing(long chatId, String reviewName) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Получить архив " + emoji.get("package"));
        inlineKeyboardButton1.setCallbackData("archive");
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton2.setText("Сохранить в базу " + emoji.get("brain"));
        inlineKeyboardButton2.setCallbackData("database");
        row.add(inlineKeyboardButton1);
        row.add(inlineKeyboardButton2);
        inlineKeyboardMarkup.setKeyboard(List.of(row));
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendMessage.setChatId(String.valueOf(chatId));
        reviewName = reviewName.replaceAll("_", " ");
        sendMessage.setText("Готово! Что ты хочешь сделать с обзором на игру под названием \"" + reviewName + "\"?");
        return sendMessage;
    }

    private boolean validateLink(String link) {
        try {
            URL url = new URL(link);
            url.toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
