import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {

    public void onUpdateReceived(Update update) {
        if(update.hasMessage()) {
            if(update.getMessage().hasText()) {
                String msgText = update.getMessage().getText();
                String chatId = update.getMessage().getChatId().toString();
                if(msgText.equals(Commands.CONNECT_WITH_MANAGER)) {
                    if(update.getMessage().getChat().getUserName() == null) {
                        sendMsg(chatId, "В начале заполните \"Имя пользователя\" в настройках телеграма!");
                    } else
                        sendMsgToManager(readFromFile(), update.getMessage().getChat().getUserName(), false);
                } else if(msgText.equals(Commands.MANAGER_CHAT)) {
                    changeChatId(update.getMessage().getChatId());
                } else if(msgText.equals(Commands.SELL_NUMBER)) {
                    if(update.getMessage().getChat().getUserName() == null) {
                        sendMsg(chatId, "В начале заполните \"Имя пользователя\" в настройках телеграма!");
                    } else
                        sendMsgToManager(readFromFile(), update.getMessage().getChat().getUserName(), true);
                } else
                sendMsg(update.getMessage().getChatId().toString(), "Выберите что вы хотите!");
            }
        } else if(update.hasCallbackQuery()) {
            try {
                execute(new SendMessage().setText(update.getCallbackQuery().getData())
                        .setChatId(update.getCallbackQuery().getMessage().getChatId()));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        //sendMsg(update.getMessage().getChatId().toString(), message);
    }

    private String readFromFile() {
        File file = new File("ManagerId");
        BufferedReader br;
        String res = "";
        try {
            br = new BufferedReader(new FileReader(file));
            while ((res = br.readLine()) != null) {
                return res;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    private void changeChatId(Long chatId){
        File file = new File("ManagerId");
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("ManagerId"));
            bw.write(chatId.toString());
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*private InlineKeyboardMarkup initInlineButtons() {
        InlineKeyboardMarkup inlineKeyboardMarkup =new InlineKeyboardMarkup();

        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Тык");
        inlineKeyboardButton.setCallbackData("Button \"Тык\" has been pressed");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<InlineKeyboardButton>();
        keyboardButtonsRow1.add(inlineKeyboardButton);

        List<List<InlineKeyboardButton>> buttons = new ArrayList<List<InlineKeyboardButton>>();
        buttons.add(keyboardButtonsRow1);

        inlineKeyboardMarkup.setKeyboard(buttons);

        return inlineKeyboardMarkup;
    }*/

    private ReplyKeyboardMarkup initReplyButtons() {
        //init keyboard properties
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<KeyboardRow>();

        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(new KeyboardButton(Commands.SELL_NUMBER));
        keyboard.add(keyboardFirstRow);

        KeyboardRow keyboardSecondRow = new KeyboardRow();
        keyboardSecondRow.add(new KeyboardButton(Commands.BUY_NUMBER));
        keyboard.add(keyboardSecondRow);

        KeyboardRow keyBoardThirdRow = new KeyboardRow();
        keyBoardThirdRow.add(new KeyboardButton(Commands.CONNECT_WITH_MANAGER));
        keyboard.add(keyBoardThirdRow);

        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    private synchronized void sendMsgToManager(String chatId, String user, boolean forSales) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(false);
        sendMessage.setChatId(chatId);
        if(!forSales) {
            sendMessage.setText("С вами хочет связаться пользователь @" + user);
        } else {
            sendMessage.setText("С вами хочет связаться пользователь @" + user
                    + " по поводу продажи номера!");
        }
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public synchronized void sendMsg(String chatId, String msgText) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(initReplyButtons());
        Sheet sheet = new Sheet();
        List<List<Object>> msg = new ArrayList<List<Object>>();
        try {
            msg = sheet.getData();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        sendMessage.setText(msgText);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {

        }

        /*for(List row: msg) {
            sendMessage.setText(row.get(0).toString());
            try {
                sendMessage(sendMessage);
            } catch (TelegramApiException e) {

            }
        }*/
    }

    public String getBotUsername() {
        return "nomera_kuplyaprodaja_bot";
    }

    public String getBotToken() {
        return "846951660:AAEomYrbAEiRmfNFJ0hAOMz16Sm1DaygE7g";
    }
}
