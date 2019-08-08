import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {

    static Logger logger;

    private static void initLogger() {
        FileInputStream ins = null;
        try { //полный путь до файла с конфигами
            ins = new FileInputStream("log.config");
            LogManager.getLogManager().readConfiguration(ins);
            logger = Logger.getLogger(Main.class.getName());

            FileHandler fh = new FileHandler("log.txt");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            logger.info("loh");
            logger.info("loh2");
        } catch (Exception ignore){
            ignore.printStackTrace();
        } finally {
            try {
                ins.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ApiContextInitializer.init();
        //initLogger();
        Bot bot = new Bot();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        Commands commands = new Commands();
        try {
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }
}
