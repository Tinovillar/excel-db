import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class EditUserFrame extends JFrame {
    private JTextField nameField;
    private JTextField lastNameField;
    private JButton updateButton;
    private User toEdit;

    public EditUserFrame(App app) {
        setTitle("Edit User");
        setSize(800, 400);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 10, 10));

        JLabel nameLabel = new JLabel("Name: ");
        nameField = new JTextField(20);

        JLabel lastNameLabel = new JLabel("Last name: ");
        lastNameField = new JTextField(20);

        updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int rowIndex = toEdit.getId();

                    for(User u : app.getUsersList())
                        if(u.getName().equals(toEdit.getName()) && u.getLastName().equals(toEdit.getLastName()) && u.getRegisterDate().equals(toEdit.getRegisterDate()))
                            toEdit = u;

                    // FALLA ACA, si la lista esta ordenada el row index apunta a cualquier cosa

                    toEdit.setName(nameField.getText());
                    toEdit.setLastName(lastNameField.getText());

                    app.getTableFrame().updateUser(toEdit);

                    app.getGoogleSheetController().updateData(toEdit);

                    toEdit = null;
                    nameField.setText("");
                    lastNameField.setText("");
                    setVisible(false);
                } catch (GeneralSecurityException | IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(lastNameLabel);
        panel.add(lastNameField);
        panel.add(updateButton);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.NORTH);
    }

    public void setUserToEdit(User user) {
        this.toEdit = user;
    }

    public void setLastNameField(String lastName) {
        lastNameField.setText(lastName);
    }

    public void setNameField(String name) {
        nameField.setText(name);
    }
}
