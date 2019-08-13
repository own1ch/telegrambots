import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
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
            String chatId = update.getMessage().getChatId().toString();
            User user = update.getMessage().getFrom();
            if(update.getMessage().hasText()) {
                String msgText = update.getMessage().getText();
                if(msgText.equals(Commands.CONNECT_MANAGER)) {
                    if(update.getMessage().getChat().getUserName() == null) {
                        sendMsg(user, chatId, Commands.FILL_NAME);
                    } else {
                        sendMsgToManager(readFromFile(), update.getMessage().getChat().getUserName(), false);
                        sendMsg(user, chatId, Commands.MANAGER_WILL_COMING_SOON);
                    }
                } else if(msgText.equals(Commands.MANAGER_CHAT)) {
                    changeChatId(update.getMessage().getChatId());
                    sendMsg(user, chatId, Commands.YOU_ARE_MANAGER);
                } else if(msgText.equals(Commands.SELL_NUMBER)) {
                    if(update.getMessage().getChat().getUserName() == null) {
                        sendMsg(user, chatId, Commands.FILL_NAME);
                    } else {
                        sendMsgToManager(readFromFile(), update.getMessage().getChat().getUserName(), true);
                        sendMsg(user, chatId, Commands.MANAGER_WILL_COMING_SOON);
                        System.out.println(Commands.MANAGER_WILL_COMING_SOON);
                    }
                } else if(msgText.equals(Commands.BUY_NUMBER)) {
                    sendMsg(user, update.getMessage().getChatId().toString(), Commands.EXAMPLE);
                    System.out.println(Commands.EXAMPLE);
                } else if (msgText.equals("/start")) {
                    sendMsg(user, chatId, Commands.START_MESSAGE);
                }
                else {
                    if(checkValue(msgText)) {
                        if(update.getMessage().getFrom().getUserName() != null) {
                            writeLogs(update.getMessage().getFrom().getUserName() + " искал номер " + msgText + "\r\n");
                        }
                        Sheet sheet = new Sheet();
                        Stack<String> res = sheet.getData(parseValue(msgText));
                        if(res.size() == 0) {
                            sendMsg(user, chatId, Commands.NUMBERS_ARE_OVER);
                            return;
                        } else {
                            while (res.size() != 0) {
                                sendMsg(user, chatId, res.pop());
                            }

                            try {
                                execute(sendMsgWithButton(chatId, Commands.USE_BUTTON_CONNECT_WITH_MANAGER));
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        sendMsg(user, chatId, Commands.CHANGE_NUMBER);
                    }
                }
            }
            //addButtonsForUser(chatId);
        } else if(update.hasCallbackQuery()) {
            try {
                String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
                String user = update.getCallbackQuery().getFrom().getUserName();
                execute(sendMsg(chatId));
                execute(sendMsgToManager(readFromFile(), user));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        //sendMsg(update.getMessage().getChatId().toString(), message);
    }

    public static InlineKeyboardMarkup sendInlineKeyBoardMessage() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Связаться с менеджером!");
        inlineKeyboardButton1.setCallbackData("Button \"Тык\" has been pressed");
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<InlineKeyboardButton>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<List<InlineKeyboardButton>>();
        rowList.add(keyboardButtonsRow1);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    private SendMessage sendMsgWithButton(String chatId, String useButtonConnectWithManager) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(useButtonConnectWithManager);
        //sendMessage.enableMarkdown(false);
        sendMessage.setReplyMarkup(sendInlineKeyBoardMessage());
        return sendMessage;
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

    private void writeLogs(String text) {
        File file = new File("log.txt");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        try {
            bw.write(text);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bw.close();
            } catch (IOException e) {

            }
        }
        /*FileWriter fw = null;
        try {
            fw = new FileWriter(file, true);
            fw.write(text);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fw != null) {
                    fw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

    }

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
            sendMessage.setText(Commands.MESSAGE_TO_MANAGER + user);
        } else {
            sendMessage.setText(String.format(Commands.TO_MANAGER_ABOUT_SALES,user));
        }
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private synchronized SendMessage sendMsgToManager(String chatId, String user) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(false);
        sendMessage.setChatId(chatId);
        sendMessage.setText("С вами хочет связаться пользователь @" + user
                + " по поводу покупки номеров!");
        return sendMessage;
    }

    public synchronized void sendMsg(User user, String chatId, String msgText) {
        SendMessage sendMessage = new SendMessage(chatId, msgText);
        sendMessage.enableMarkdown(false);
        sendMessage.setReplyMarkup(initReplyButtons());
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        writeLogs("Пользователю " + user.getUserName() +
                " было отправлено сообщение " + msgText);
    }

    public synchronized SendMessage sendMsg(String chatId) {
        SendMessage sendMessage = new SendMessage(chatId, Commands.MANAGER_WILL_COMING_SOON);
        sendMessage.enableMarkdown(false);
        sendMessage.setReplyMarkup(initReplyButtons());
        return sendMessage;
    }

    public String getBotUsername() {
        return Commands.BOT_USERNAME;
    }

    public String getBotToken() {
        return Commands.BOT_TOKEN;
    }
}
