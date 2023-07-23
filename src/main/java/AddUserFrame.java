import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.List;

public class AddUserFrame extends JFrame {
    private JTextField nameField;
    private JTextField lastNameField;
    private JButton addButton;
    private GSheetsController gsc;
    private TableFrame tableFrame;

    public AddUserFrame(App app) {
        gsc = app.getGoogleSheetController();
        tableFrame = app.getTableFrame();

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1, 10, 10));
        setSize(1000, 600);
        setLocationRelativeTo(null);

        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField(20);

        JLabel lastNameLabel = new JLabel("Last name:");
        lastNameField = new JTextField(20);

        addButton = new JButton("Create");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!nameField.getText().isEmpty() && !lastNameField.getText().isEmpty()) {
                    addUser();
                    setVisible(false);
                    tableFrame.setVisible(true);
                }
            }
        });

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(lastNameLabel);
        panel.add(lastNameField);
        panel.add(addButton);

        add(panel, BorderLayout.NORTH);
    }

    public void addUser() {
        User user = new User(nameField.getText(), lastNameField.getText(), LocalDate.now().toString());
        tableFrame.addUser(user);

        try {
            gsc.writeData(user);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }

        nameField.setText("");
        lastNameField.setText("");
    }
}
