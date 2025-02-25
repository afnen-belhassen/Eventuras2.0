package entities;

import java.sql.Timestamp;
import java.util.Date;

public class Event  {
    private int id_event;
    private String title;
    private String description;
    private Date date_event;
    private String location;
    private int user_id;
    /*private List <Integer> userIds= new ArrayList();*/
    private int category_id;
    private String category_name;
    private Timestamp creation_date;//nzid nchouf aleha
    /*public void ajouterInList(int user_id){
        userIds.add(01);
        userIds.add(00);
    }*/

    public Event(int id_event,String title, String description, Date date_event, String location,int user_id, int category_id ,String category_name) {
        this.id_event = id_event;
        this.title = title;
        this.description = description;
        this.date_event = date_event;
        this.location = location;
        this.user_id = 01;
        this.category_id = category_id;
        this.category_name = category_name;
    }
    public Event(int id_event,String title, String description, Date date_event, String location,int user_id, int category_id ) {
        this.id_event = id_event;
        this.title = title;
        this.description = description;
        this.date_event = date_event;
        this.location = location;
        this.user_id = 01;
        this.category_id = category_id;

    }

    public Event(String title, String description, Date date_event, String location,int user_id, int category_id, String category_name) {
        this.title = title;
        this.description = description;
        this.date_event = date_event;
        this.location = location;
        this.user_id = 01;
        this.category_id = category_id;
        this.category_name = category_name;
    }
    /*-------Pour Modification-------*/
    public Event(int id_event,String title){
        this.id_event = id_event;
        this.title = title;


    }

    public int getId_event() {
        return id_event;
    }

    public void setId_event(int id_event) {
        this.id_event = id_event;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate_event() {
        return date_event;
    }

    public void setDate_event(Date date_event) {
        this.date_event = date_event;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id_event=" + id_event +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date_event='" + date_event + '\'' +
                ", location='" + location + '\'' +
                ", user_id=" + user_id +
                ", category=" + category_id +
                ", category_name='" + category_name + '\'' +
                '}';
    }

}
