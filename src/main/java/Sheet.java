import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import javax.swing.text.MaskFormatter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

public class Sheet {
    private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = Sheet.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        //return null;
    }

    /**
     * Prints the names and majors of students in a sample spreadsheet:
     * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
     */
    public Stack<String> getData(String value) {
        // Build a new authorized API client service.
        NetHttpTransport HTTP_TRANSPORT;
        ValueRange response = null;
        Stack<String> result = new Stack<String>();
        final String spreadsheetId = "1WHQd7TRGWohtwGl9EL-vduud4DEC4GORR2jt5Nd4Aio";
        for(int i=0;i<Commands.TABS.size();i++) {
            try {
                HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
                final String range = "'" + Commands.TABS.get(i) + "'!D2:D";
                Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                        .setApplicationName(APPLICATION_NAME)
                        .build();
                response = service.spreadsheets().values()
                        .get(spreadsheetId, range)
                        .execute();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) {
                System.out.println("No data found.");
            } else {
                for (List row : values) {
                    if (row.size() != 0) {
                        String word = row.get(0).toString().toUpperCase();
                        if (word.length() == 8 || word.length() == 9) {
                            String otherFormatWord = createOtherFormat(word);
                            if (word.matches(value) || otherFormatWord.matches(value)) {
                                System.out.printf("%s\n", row.get(0));
                                result.push(word);
                            }
                        } else {
                            if (word.matches(value)) {
                                System.out.printf("%s\n", row.get(0));
                                result.push(word);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    private String createOtherFormat(String word) {
        if(word.length() == 8) {
            return Character.toString(word.charAt(3)) + word.charAt(0) + word.charAt(1) +
                    word.charAt(2) + word.charAt(4) + word.charAt(5) +
                    word.charAt(6) + word.charAt(7);
        } else return Character.toString(word.charAt(3)) + word.charAt(0) + word.charAt(1) +
                word.charAt(2) + word.charAt(4) + word.charAt(5) +
                word.charAt(6) + word.charAt(7) + word.charAt(8);
    }
}
