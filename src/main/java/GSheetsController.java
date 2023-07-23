import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.*;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.*;

public class GSheetsController {
    private static Sheets sheetsServices;
    private static final String APP_NAME = "users managment";
    private static final String SPREADSHEET_ID = "1yqG9vg-KsqJNtJ8f805oiFMWiWrz8zKugsUXqY2-fsw";

    private static Credential authorize() throws IOException, GeneralSecurityException {
        InputStream in = GSheetsController.class.getResourceAsStream("/credentials.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JacksonFactory.getDefaultInstance(), new InputStreamReader(in)
        );

        List<String> scopes = List.of(SheetsScopes.SPREADSHEETS);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(),
                clientSecrets, scopes)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
                .setAccessType("offline")
                .build();

        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver())
                .authorize("user");

        return credential;
    }

    private static Sheets getSheetsService() throws IOException, GeneralSecurityException {
        Credential credential = authorize();
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(), credential)
                .setApplicationName(APP_NAME)
                .build();
    }

    public void writeData(User user) throws GeneralSecurityException, IOException {
        sheetsServices = getSheetsService();

        String range = "users";

        ValueRange body = new ValueRange()
                .setValues(List.of(
                        Arrays.asList(user.getName(), user.getLastName(), user.getRegisterDate())
                ));

        AppendValuesResponse appendResult = sheetsServices.spreadsheets().values()
                .append(SPREADSHEET_ID, range, body)
                .setValueInputOption("USER_ENTERED")
                .setInsertDataOption("INSERT_ROWS")
                .setIncludeValuesInResponse(true)
                .execute();
    }

    public List<User> getAllUsers() throws GeneralSecurityException, IOException {
        List<User> list = new ArrayList<>();

        sheetsServices = getSheetsService();

        String range = "users";

        ValueRange response = sheetsServices.spreadsheets().values()
                .get(SPREADSHEET_ID, range)
                .execute();

        List<List<Object>> values = response.getValues();
        if(values != null) {
            for(List<Object> row : values) {
                list.add(new User((String) row.get(0), (String) row.get(1), (String) row.get(2)));
            }
        }

        return list;
    }

    public void updateData(User user) throws GeneralSecurityException, IOException {
        sheetsServices = getSheetsService();

        String range = "users!A" + user.getId();

        ValueRange body = new ValueRange()
                .setValues(List.of(
                        Arrays.asList(user.getName(), user.getLastName(), user.getRegisterDate())
                ));

        UpdateValuesResponse result = sheetsServices.spreadsheets().values()
                .update(SPREADSHEET_ID, range, body)
                .setValueInputOption("RAW")
                .setIncludeValuesInResponse(true)
                .execute();
    }

    public void deleteUser() throws IOException, GeneralSecurityException {
        sheetsServices = getSheetsService();

        DeleteDimensionRequest deleted = new DeleteDimensionRequest()
                .setRange(
                        new DimensionRange()
                                .setSheetId(1704261795)
                                .setDimension("ROWS")
                                .setStartIndex(4)
                                .setEndIndex(5)
                );

        List<Request> requests = new ArrayList<>();
        requests.add(new Request().setDeleteDimension(deleted));

        BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest().setRequests(requests);
        sheetsServices.spreadsheets().batchUpdate(SPREADSHEET_ID, body).execute();
    }
}
