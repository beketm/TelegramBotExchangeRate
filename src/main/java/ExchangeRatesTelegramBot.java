import io.github.cdimascio.dotenv.Dotenv;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.*;

import static java.util.Arrays.*;
import static java.util.stream.Collectors.toCollection;

public class ExchangeRatesTelegramBot extends TelegramLongPollingBot {

    private static String BOT_TOKEN = "";
    private static String BOT_USERNAME = "";

    private final ExchangeRates exchangeRates = new ExchangeRates();
    private List<String> currencies = asList("USD", "EUR", "RUB");
    private List<String> currencyEmojis = asList("\ue50c", "\ud83c\uddea\ud83c\uddfa", "\ue512");


    public ExchangeRatesTelegramBot() {
        super();

        Dotenv dotenv = null;
        dotenv = Dotenv.configure().load();
        BOT_TOKEN = dotenv.get("BOT_TOKEN");
        BOT_USERNAME = dotenv.get("BOT_USERNAME");

    }

    public void onUpdateReceived(Update update){
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            String introMessageString = "Добрый день! В этом боте вы можете узнать курс валют USD, EUR, RUB к тенге за последние 10 дней";

            if (update.getMessage().getText().equals("/start")) {
                sendIntroMessage(chat_id, introMessageString);
            }

            askCurrency(chat_id);


        }else if (update.hasCallbackQuery()){
            printCurrencyRates(update);
        }

    }

    private void printCurrencyRates(Update update) {
        String call_data = update.getCallbackQuery().getData();
        long message_id = update.getCallbackQuery().getMessage().getMessageId();
        long chat_id = update.getCallbackQuery().getMessage().getChatId();
        String messageString = "";

        Map<String, Map<String, Double>> last10daysRates = exchangeRates.getLast10DaysRates();

        for(Map.Entry<String, Map<String, Double>> entry: last10daysRates.entrySet()){
            messageString += formatDate(entry.getKey()) + ": 1 " + call_data + " = " + String.format("%.2f", entry.getValue().get(call_data)) + " KZT\n";
        }


        SendMessage new_message = new SendMessage();
        new_message.setText(messageString);
        new_message.setChatId(String.valueOf(chat_id));

        try {
            execute(new_message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private static String formatDate(String date){
        String[] array = date.split("-");
        List<String> list = Arrays.asList(array);
        Collections.reverse(list);
        return String.join(".", list);
    }

    private void sendIntroMessage(long chat_id, String introMessageString) {
        SendMessage introMessage = new SendMessage();
        introMessage.setText(introMessageString);
        introMessage.setChatId(String.valueOf(chat_id));

        try {
            execute(introMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void askCurrency(long chat_id) {
        SendMessage currencyMessage = new SendMessage();
        currencyMessage.setText("Выберите валюту");
        currencyMessage.setChatId(String.valueOf(chat_id));

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        for (int i = 0; i < currencies.size(); i++){
            String currencyString = currencyEmojis.get(i) + " " + currencies.get(i);
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(currencyString);
            inlineKeyboardButton.setCallbackData(currencies.get(i));
            rowInline.add(inlineKeyboardButton);
        }

        // Set the keyboard to the markup
        rowsInline.add(rowInline);
        // Add it to the message
        markupInline.setKeyboard(rowsInline);
        currencyMessage.setReplyMarkup(markupInline);

        try {
            execute(currencyMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws TelegramApiException {
        ExchangeRatesTelegramBot bot = new ExchangeRatesTelegramBot();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(bot);

    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }
}
