package entities;

import utils.Session;

public class Partnership {
    private int id;
    private int organizerId;
    private int partnerId;
    private ContractType contractType;
    private String description;
    private boolean isSigned;
    user currentUser = Session.getInstance().getCurrentUser();// True if both parties have signed

    public Partnership( int organizerId, int partnerId, ContractType contractType, String description, boolean isSigned ) {

        this.organizerId = currentUser.getId();
        this.partnerId = partnerId;
        this.contractType = contractType;
        this.description = description;
        this.isSigned = false;
    }

    public Partnership(int id, int organizerId, int partnerId, ContractType contractType, String description, boolean isSigned) {
        this.id = id;
        this.organizerId = currentUser.getId();
        this.partnerId = partnerId;
        this.contractType = contractType;
        this.description = description;
        this.isSigned = isSigned;
    }


    // Getters and Setters
    public int getOrganizerId() {
        return this.organizerId = currentUser.getId();
    }

    public void setOrganizerId(int organizerId) {
        this.organizerId = currentUser.getId();
    }

    public int getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(int partnerId) {
        this.partnerId = partnerId;
    }

    public ContractType getContractType() {
        return contractType;
    }

    public void setContractType(ContractType contractType) {
        this.contractType = contractType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    public boolean isSigned() {
        return isSigned;
    }





    @Override
    public String toString() {
        return "Partnership{" +
                "organizerId=" + organizerId +
                ", partnerId=" + partnerId +
                ", contractType=" + contractType +
                ", description='" + description + '\'' +
                ", isSigned=" + isSigned +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

