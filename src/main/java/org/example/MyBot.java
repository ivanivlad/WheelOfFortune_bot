package org.example;

import org.example.wonder_field.Game;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.HashMap;
import java.util.Map;

public class MyBot extends TelegramLongPollingBot {

        final String BOT_NAME;
        final String BOT_TOKEN;

        public TelegramBotsApi botsApi;

        private Map<Long, Game> games;

        public MyBot(String BOT_NAME, String BOT_TOKEN) {
            this.BOT_NAME = BOT_NAME;
            this.BOT_TOKEN = BOT_TOKEN;
            games = new HashMap<Long, Game>();

            try {
                TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
                botsApi.registerBot(this);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onUpdateReceived(Update update) {
            if (update.hasMessage() && update.getMessage().hasText()) {
                String action = update.getMessage().getText();
                long chatID = update.getMessage().getChatId();
                switch (action){
                    case "/help": sendMessage("Я игра Поле чудес. Нажми /play", chatID);
                        break;
                    case "/play": {
                        sendMessage("https://play-lh.googleusercontent.com/4KvVvdJxsYr2f-wAS4BHnh6kb7VA5oUvZHMprRXvWR_mrrnU97eqcJWnQqeNypJgIi8", chatID);
                    }
                        Game currentGame = new Game(this, chatID);

                        currentGame.initMock();
                        currentGame.start();

                        games.put(chatID, currentGame);

                        break;
                    default: {
                        currentGame = games.get(chatID);
                        if (currentGame == null) {
                            sendMessage("Я игра Поле чудес. Нажми /play", chatID);
                        } else {
                            currentGame.listen(action);
                        }
                    }
                }
            }
        }

        @Override
        public String getBotUsername() {
            return BOT_NAME; //"WheelOfFortune_2024";
        }

        @Override
        public String getBotToken() {
            return BOT_TOKEN; //"6868571378:AAHMUlKdjT1cxVde-LvqXwCTbewpjpz0k4o";
        }

        public void sendMessage(String msg, long chatId){

            SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
            message.setChatId(chatId);
            message.setText(msg);

            try {
                execute(message); // Call method to send the message
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

        }
}
