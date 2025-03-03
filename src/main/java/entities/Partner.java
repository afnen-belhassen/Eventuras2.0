package entities;

import utils.Session;

import java.util.List;

public class Partner {
    private int id;
    private String name;
    private PartnerType type;
    private String contactInfo;
    private String ImagePath;
    private int rating; // New rating field
    private List<Partnership> partnerships;


    // Constructor with rating
    public Partner(int id, String name, PartnerType type, String contactInfo, String ImagePath, int rating) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.contactInfo = contactInfo;
        this.ImagePath = ImagePath;
        this.rating = rating; // Initialize rating
    }


    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public PartnerType getType() { return type; }
    public void setType(PartnerType type) { this.type = type; }

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    public String getImagePath() { return ImagePath; }
    public void setImagePath(String imagePath) { ImagePath = imagePath; }

    public int getRating() { return rating; }  // Getter for rating
    public void setRating(int rating) { this.rating = rating; }  // Setter for rating

    public List<Partnership> getPartnerships() { return partnerships; }
    public void setPartnerships(List<Partnership> partnerships) { this.partnerships = partnerships; }

    @Override
    public String toString() {
        return "Partner{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", contactInfo='" + contactInfo + '\'' +
                ", rating=" + rating +
                '}';
    }
}
