import java.awt.event.*;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Comparator;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TableFrame extends JFrame{
    private List<User> users;
    private GSheetsController gsc;
    private JTable usersTable;
    private DefaultTableModel tableModel;

    public TableFrame (App app) {
        gsc = app.getGoogleSheetController();
        users = app.getUsersList();

        setTitle("Gum App");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // USERS TABLE

        tableModel = new DefaultTableModel() {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableModel.setColumnIdentifiers(new Object[]{"Id", "Name", "Last name", "Register", "Edit"});
        usersTable = new JTable(tableModel);

        usersTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int columnIndex = usersTable.columnAtPoint(e.getPoint());
                int rowIndex = usersTable.rowAtPoint(e.getPoint());

                if(columnIndex == 4 && e.getClickCount() == 2) {
                    User u = new User(tableModel.getValueAt(rowIndex, 1).toString(), tableModel.getValueAt(rowIndex, 2).toString(), tableModel.getValueAt(rowIndex, 3).toString());
                    u.setId(rowIndex);
                    System.out.println(u.getName() + u.getLastName() + u.getId());
                    app.getEditUserFrame().setUserToEdit(u);
                    app.getEditUserFrame().setNameField(u.getName());
                    app.getEditUserFrame().setLastNameField(u.getLastName());
                    app.getEditUserFrame().setVisible(true);
                }
            }
        });

        usersTable.getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int columnIndex = usersTable.columnAtPoint(e.getPoint());
                String columnName = usersTable.getColumnName(columnIndex);

                switch (columnName) {
                    case "Last name" -> {
                        if (e.getClickCount() == 1) {
                            filterByLastNameAsc();
                        } else {
                            filterByLastNameDes();
                        }
                    }
                    case "Name" -> {
                        if (e.getClickCount() == 1) {
                            filterByNameAsc();
                        } else {
                            filterByNameDes();
                        }
                    }
                }
            }
        });

        usersTable.setRowHeight(25);
        usersTable.getTableHeader().setReorderingAllowed(false);
        usersTable.setFont(new Font("Arial", Font.PLAIN, 20));
        JScrollPane scrollPane = new JScrollPane(usersTable);

        // END USERS TABLE

        // Search Panel

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new GridLayout(0, 1, 10, 10));

        JLabel searchLabel = new JLabel("Search...");
        JTextField searchField = new JTextField(20);

        JButton addUserButton = new JButton("New user");
        addUserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                app.getAddUserFrame().setVisible(true);
                setVisible(false);
            }
        });

        searchField.setFont(new Font("Arial", Font.PLAIN, 20));
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 20));

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(addUserButton);

        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                for(int i = usersTable.getRowCount() - 1; i >= 0 ; i--) {
                    tableModel.removeRow(i);
                }

                for(User u : users) {
                    if(u.getName().toLowerCase().contains(searchField.getText().toLowerCase()) || u.getLastName().toLowerCase().contains(searchField.getText().toLowerCase()))
                        tableModel.addRow(new Object[]{u.getId(), u.getName(), u.getLastName(), u.getRegisterDate()});
                }
            }
        });

        // End Search Panel

        setLayout(new BorderLayout());
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        loadData();
    }

    public void addUser(User user) {
        user.setId(users.size() + 1);
        users.add(user);
        tableModel.addRow(new Object[]{user.getId(), user.getName(), user.getLastName(), user.getRegisterDate()});
    }

    public void updateUser(User user) {
        boolean updated = false;
        System.out.println(user.getName() + user.getLastName() + user.getId());
        for(int i = 0; i < tableModel.getRowCount() && !updated; i++) {
            System.out.println(Integer.parseInt(tableModel.getValueAt(i,0).toString()));
            if(Integer.parseInt(tableModel.getValueAt(i,0).toString()) == user.getId()) {
                tableModel.setValueAt(user.getName(), i, 1);
                tableModel.setValueAt(user.getLastName(), i, 2);
                updated = true;
            }
        }
    }

    public void reloadData(User user) {
        boolean updated = false;
        for(int i = 0; i < tableModel.getRowCount() && !updated; i++) {
            if(Integer.parseInt(tableModel.getValueAt(i,0).toString()) == user.getId()) {
                tableModel.setValueAt(user.getName(), i, 1);
                tableModel.setValueAt(user.getLastName(), i, 2);
                updated = true;
            }
        }
    }

    public void loadData() {
        int i = 1;
        try {
            List<User> allUsers = gsc.getAllUsers();
            for(User u : allUsers) {
                u.setId(i);
                users.add(u);
                tableModel.addRow(new Object[]{u.getId(), u.getName(), u.getLastName(), u.getRegisterDate()});
                i++;
            }
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void filterByNameAsc() {
        users.sort(new Comparator<User>() {
            public int compare(User o1, User o2) {
                return Integer.compare(o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase()), 0);
            }
        });

        for(int i = usersTable.getRowCount() - 1; i >= 0 ; i--) {
            tableModel.removeRow(i);
        }

        for(User u : users) {
            tableModel.addRow(new Object[]{u.getId(), u.getName(), u.getLastName(), u.getRegisterDate()});
        }
    }

    public void filterByNameDes() {
        users.sort(new Comparator<User>() {
            public int compare(User o1, User o2) {
                return Integer.compare(o2.getName().toLowerCase().compareTo(o1.getName().toLowerCase()), 0);
            }
        });

        for(int i = usersTable.getRowCount() - 1; i >= 0 ; i--) {
            tableModel.removeRow(i);
        }

        for(User u : users) {
            tableModel.addRow(new Object[]{u.getId(), u.getName(), u.getLastName(), u.getRegisterDate()});
        }
    }

    public void filterByLastNameAsc() {
        users.sort(new Comparator<User>() {
            public int compare(User o1, User o2) {
                return Integer.compare(o1.getLastName().toLowerCase().compareTo(o2.getLastName().toLowerCase()), 0);
            }
        });

        for(int i = usersTable.getRowCount() - 1; i >= 0 ; i--) {
            tableModel.removeRow(i);
        }

        for(User u : users) {
            tableModel.addRow(new Object[]{u.getId(), u.getName(), u.getLastName(), u.getRegisterDate()});
        }
    }

    public void filterByLastNameDes() {
        users.sort(new Comparator<User>() {
            public int compare(User o1, User o2) {
                return Integer.compare(o2.getLastName().toLowerCase().compareTo(o1.getLastName().toLowerCase()), 0);
            }
        });

        for(int i = usersTable.getRowCount() - 1; i >= 0 ; i--) {
            tableModel.removeRow(i);
        }

        for(User u : users) {
            tableModel.addRow(new Object[]{u.getId(), u.getName(), u.getLastName(), u.getRegisterDate()});
        }
    }
}