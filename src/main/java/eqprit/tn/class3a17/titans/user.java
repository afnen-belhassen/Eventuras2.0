package eqprit.tn.class3a17.titans;

public class user {
    private int id,phone;
    private String fullname ,password;
    private String email,role,PMR;

    public user(int phone, String fullname, String password, String email, String role, String PMR, int id) {
        this.phone = phone;
        this.fullname = fullname;
        this.password = password;
        this.email = email;
        this.role = role;
        this.PMR = PMR;
        this.id = id;
    }

    public user(int phone, String fullname, String password, String email, String role, String PMR) {
        this.phone = phone;
        this.fullname = fullname;
        this.password = password;
        this.email = email;
        this.role = role;
        this.PMR = PMR;
    }

    public int getId() {
        return id;
    }

    public String getPMR() {
        return PMR;
    }

    public String getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFullname() {
        return fullname;
    }

    public int getPhone() {
        return phone;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPMR(String PMR) {
        this.PMR = PMR;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "user{" +
                "id=" + id +
                ", phone=" + phone +
                ", fullname='" + fullname + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", PMR='" + PMR + '\'' +
                '}';
    }
}
