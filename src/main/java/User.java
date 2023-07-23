public class User {
    private String name;
    private String lastName;
    private final String registerDate;
    private int id;

    public User(String name, String lastName, String registerDate) {
        this.name = name;
        this.lastName = lastName;
        this.registerDate = registerDate;
        this.id = 0;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRegisterDate() {
        return this.registerDate;
    }

    public void setId(int id) { this.id = id; }

    public int getId() { return this.id; }
}