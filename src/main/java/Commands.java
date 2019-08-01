import java.io.*;
import java.util.ArrayList;

public class Commands {
    public static String CONNECT_MANAGER = "Хочу связаться с менеджером!";
    public static String BUY_NUMBER = "Хочу купить номер!";
    public static String SELL_NUMBER = "Хочу продать номер!";
    public static String MANAGER_CHAT = "Сделай мой чат менеджерским!";
    public static String EXAMPLE = "Введите номер для поиска по примеру: " +
            "001мем(если вам не важно какой знак, замените его знаком \"*\", пример: **1м*м). " +
            "Если вы найдёте интересующий вас номер, нажмите кнопку \"Связаться с менеджером\". ";
    public static String FILL_NAME = "В начале заполните \"Имя пользователя\" в настройках телеграма!";
    public static String START_MESSAGE = "Приветствую!";
    public static String YOU_ARE_MANAGER = "Вы стали менеджером канала!";
    public static String NUMBERS_ARE_OVER = "Таких номеров нет в базе!";
    public static String USE_BUTTON_CONNECT_WITH_MANAGER = "После выбора номера нажмите кнопку \"Связаться с менеджером\" " +
            "и менеджер свяжется с вами!";
    public static String CHANGE_NUMBER = "Вы неправильно ввели маску номера!";
    public static String MANAGER_WILL_COMING_SOON = "Менеджер скоро с вами свяжется!";
    public static ArrayList<String> TABS = new ArrayList<String>();

    public Commands() {

        FileInputStream fstream;
        try {
            fstream = new FileInputStream("Commands.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream, "Cp1251"));

            String strLine;
            while ((strLine = br.readLine()) != null){
                String text = strLine.substring(strLine.indexOf("\"") + 1, strLine.length() - 1);
                if (strLine.contains("CONNECT_MANAGER")) {
                    CONNECT_MANAGER = text;
                } else if (strLine.contains("BUY_NUMBER")) {
                    BUY_NUMBER = text;
                } else if (strLine.contains("SELL_NUMBER")) {
                    SELL_NUMBER = text;
                } else if (strLine.contains("MANAGER_CHAT")) {
                    MANAGER_CHAT = text;
                } else if (strLine.contains("EXAMPLE")) {
                    EXAMPLE = text;
                } else if (strLine.contains("FILL_NAME")) {
                    FILL_NAME = text;
                } else if (strLine.contains("START_MESSAGE")) {
                    START_MESSAGE = text;
                } else if (strLine.contains("YOU_ARE_MANAGER")) {
                    YOU_ARE_MANAGER = text;
                } else if (strLine.contains("NUMBERS_ARE_OVER")) {
                    NUMBERS_ARE_OVER = text;
                } else if(strLine.contains("USE_BUTTON_CONNECT_WITH_MANAGER")) {
                    USE_BUTTON_CONNECT_WITH_MANAGER = text;
                } else if(strLine.contains("CHANGE_NUMBER")) {
                    CHANGE_NUMBER = text;
                } else if(strLine.contains("month")) {
                    TABS.add(text);
                }
            }
            fstream.close();
        } catch (FileNotFoundException e) {
            System.out.println("Вы уверены что в папке есть файл Commands.txt?");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
