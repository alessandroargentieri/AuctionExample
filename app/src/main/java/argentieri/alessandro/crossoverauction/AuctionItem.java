package argentieri.alessandro.crossoverauction;

import java.sql.Date;

/**
 * Created by pccasa on 12/08/2016.
 */
public class AuctionItem {

    private int id;
    private String name;
    private String description;
    private String cathegory;
    private String uri;
    private String expiration_date;
    private int offer;
    private int won;
    private String offering_user;
    private String seller;

    //empty constructor
    public AuctionItem() {
    }
    //constructor 2
    public AuctionItem(int id, String name, String description, String cathegory, String uri, String expiration_date, int offer, int won, String offering_user, String seller){

        this.id = id;
        this.name = name;
        this.description = description;
        this.cathegory = cathegory;
        this.uri = uri;
        this.expiration_date = expiration_date;
        this.offer = offer;
        this.won = won;
        this.offering_user = offering_user;
        this.seller = seller;

    }

    /*-----------id--------------*/
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /*-----------name--------------*/
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*-----------description--------------*/
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /*-----------cathegory--------------*/
    public String getCathegory() {
        return cathegory;
    }

    public void setCathegory(String cathegory) {
        this.cathegory = cathegory;
    }

    /*--------------uri-----------------*/
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    /*-----------expiration_date--------------*/
    public String getExpirationDate() {
        return expiration_date;
    }

    public void setExpirationDate(String expiration_date) {
        this.expiration_date = expiration_date;
    }

    /*-----------offer--------------*/
    public int getOffer() {
        return offer;
    }

    public void setOffer(int offer) {
        this.offer = offer;
    }
    /*-----------won--------------*/
    public int getWon() {
        return won;
    }

    public void setWon(int won) {
        this.won = won;
    }

    /*-----------offering_user--------------*/
    public String getOfferingUser() {
        return offering_user;
    }

    public void setOfferingUser(String offering_user) {
        this.offering_user = offering_user;
    }

    /*-----------seller--------------*/
    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

}