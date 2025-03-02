package gui;

public final class UserSession {

    private static UserSession instance;

    private int id;
    private String username;
    private String email;
    private String password;
    private String firstname;
    private String lastname;
    private String birthday;
    private String gender;
    private String picture;
    private String phonenumber;
    private int level;
    private String role;
    private int role_id; // Added role_id field to track the numeric ID

    // Getters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getFirstname() { return firstname; }
    public String getLastname() { return lastname; }
    public String getBirthday() { return birthday; }
    public String getGender() { return gender; }
    public String getPicture() { return picture; }
    public String getPhonenumber() { return phonenumber; }
    public int getLevel() { return level; }
    public String getRole() { return role; }
    public int getRoleId() { return role_id; } // Getter for role_id

    // Setters (added)
    public void setId(int id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setFirstname(String firstname) { this.firstname = firstname; }
    public void setLastname(String lastname) { this.lastname = lastname; }
    public void setBirthday(String birthday) { this.birthday = birthday; }
    public void setGender(String gender) { this.gender = gender; }
    public void setPicture(String picture) { this.picture = picture; }
    public void setPhonenumber(String phonenumber) { this.phonenumber = phonenumber; }
    public void setLevel(int level) { this.level = level; }
    public void setRole(String role) { this.role = role; }
    public void setRoleId(int role_id) { this.role_id = role_id; } // Setter for role_id

    public UserSession(int id, String username, String email, String password, String firstname, String lastname,
                       String birthday, String gender, String picture, String phonenumber, int level, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthday = birthday;
        this.gender = gender;
        this.picture = picture;
        this.phonenumber = phonenumber;
        this.level = level;
        this.role = role;
    }

    // Constructor with role_id
    public UserSession(int id, String username, String email, String password, String firstname, String lastname,
                       String birthday, String gender, String picture, String phonenumber, int level, String role, int role_id) {
        this(id, username, email, password, firstname, lastname, birthday, gender, picture, phonenumber, level, role);
        this.role_id = role_id;
    }

    public static UserSession getInstance(int id, String username, String email, String password, String firstname, String lastname,
                                          String birthday, String gender, String picture, String phonenumber, int level, String role) {
        if(instance == null) {
            instance = new UserSession(id, username, email, password, firstname, lastname, birthday, gender, picture, phonenumber, level, role);
        }
        return instance;
    }

    // New getInstance with role_id
    public static UserSession getInstance(int id, String username, String email, String password, String firstname, String lastname,
                                          String birthday, String gender, String picture, String phonenumber, int level, String role, int role_id) {
        if(instance == null) {
            instance = new UserSession(id, username, email, password, firstname, lastname, birthday, gender, picture, phonenumber, level, role, role_id);
        }
        return instance;
    }

    // Update session data method
    public void updateSessionData(int id, String username, String email, String password, String firstname, String lastname,
                                  String birthday, String gender, String picture, String phonenumber, int level, String role, int role_id) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthday = birthday;
        this.gender = gender;
        this.picture = picture;
        this.phonenumber = phonenumber;
        this.level = level;
        this.role = role;
        this.role_id = role_id;
    }

    public static UserSession getInstance() {
        if (instance == null) {
            throw new IllegalStateException("UserSession has not been initialized.");
        }
        return instance;
    }

    public void cleanUserSession() {
        id = 0;
        username = null;
        email = null;
        password = null;
        firstname = null;
        lastname = null;
        birthday = null;
        gender = null;
        picture = null;
        phonenumber = null;
        level = 0;
        role = null;
        role_id = 0; // Reset role_id as well
    }

    @Override
    public String toString() {
        return "UserSession{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", birthday='" + birthday + '\'' +
                ", gender='" + gender + '\'' +
                ", picture='" + picture + '\'' +
                ", phonenumber='" + phonenumber + '\'' +
                ", level=" + level +
                ", role='" + role + '\'' +
                ", role_id=" + role_id +
                '}';
    }
}