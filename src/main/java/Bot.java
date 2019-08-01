import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Bot extends TelegramLongPollingBot {

    public void onUpdateReceived(Update update) {
        if(update.hasMessage()) {
            if(update.getMessage().hasText()) {
                String msgText = update.getMessage().getText();
                String chatId = update.getMessage().getChatId().toString();
                if(msgText.equals(Commands.CONNECT_MANAGER)) {
                    if(update.getMessage().getChat().getUserName() == null) {
                        sendMsg(chatId, Commands.FILL_NAME);
                    } else {
                        sendMsgToManager(readFromFile(), update.getMessage().getChat().getUserName(), false);
                        sendMsg(chatId, Commands.MANAGER_WILL_COMING_SOON);
                    }
                } else if(msgText.equals(Commands.MANAGER_CHAT)) {
                    changeChatId(update.getMessage().getChatId());
                    sendMsg(chatId, Commands.YOU_ARE_MANAGER);
                } else if(msgText.equals(Commands.SELL_NUMBER)) {
                    if(update.getMessage().getChat().getUserName() == null) {
                        sendMsg(chatId, Commands.FILL_NAME);
                    } else
                        sendMsgToManager(readFromFile(), update.getMessage().getChat().getUserName(), true);
                } else if(msgText.equals(Commands.BUY_NUMBER)) {
                    sendMsg(update.getMessage().getChatId().toString(), Commands.EXAMPLE);
                    System.out.println(Commands.EXAMPLE);
                } else if (msgText.equals("/start")) {
                    sendMsg(chatId, Commands.START_MESSAGE);
                }
                else {
                    if(checkValue(msgText)) {
                        Sheet sheet = new Sheet();
                        Stack<String> res = sheet.getData(parseValue(msgText));
                        if(res.size() == 0) {
                            sendMsg(chatId, Commands.NUMBERS_ARE_OVER);
                            return;
                        } else {
                            while (res.size() != 0) {
                                sendMsg(chatId, res.pop());
                            }
                            sendMsg(chatId, Commands.USE_BUTTON_CONNECT_WITH_MANAGER);
                        }
                    } else {
                        sendMsg(chatId, Commands.CHANGE_NUMBER);
                    }
                }
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

    private String parseValue(String msgText) {
        return msgText.toUpperCase().replace("*", ".") + "..*";
    }

    private boolean checkValue(String msg) {
        return msg.length() == 6;
    }

    private synchronized String readFromFile() {
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
        keyBoardThirdRow.add(new KeyboardButton(Commands.CONNECT_MANAGER));
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
        sendMessage.enableMarkdown(false);
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(initReplyButtons());
        sendMessage.setText(msgText);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {

        }
    }

    public String getBotUsername() {
        return "nomera_kuplyaprodaja_bot";
    }

    public String getBotToken() {
        return "846951660:AAEomYrbAEiRmfNFJ0hAOMz16Sm1DaygE7g";
    }
}
