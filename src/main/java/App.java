import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class App {
    private TableFrame tableFrame;
    private EditUserFrame editUserFrame;
    private AddUserFrame addUserFrame;
    private List<User> usersList;
    private GSheetsController gsc;

    public App() {
        gsc = new GSheetsController();
        usersList = new ArrayList<>();
        tableFrame = new TableFrame(this);
        editUserFrame = new EditUserFrame(this);
        addUserFrame = new AddUserFrame(this);
    }

    public TableFrame getTableFrame() {
        return tableFrame;
    }

    public EditUserFrame getEditUserFrame() {
        return editUserFrame;
    }

    public List<User> getUsersList() {
        return usersList;
    }

    public void setUsersList(List<User> usersList) {
        this.usersList = usersList;
    }

    public GSheetsController getGoogleSheetController() {
        return gsc;
    }

    public AddUserFrame getAddUserFrame() {
        return addUserFrame;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                App window = new App();
                window.editUserFrame.setVisible(false);
                window.addUserFrame.setVisible(false);
                window.tableFrame.setVisible(true);
            }
        });
    }
}