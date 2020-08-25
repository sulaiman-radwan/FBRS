package fbrs.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.sql.Timestamp;
import java.util.Date;

public class Entry {
    BooleanProperty selected;
    private int id;
    private int type;
    private int giverId;
    private int takerId;
    private int quantity;
    private int price;
    private Timestamp dateCreated;
    private Timestamp dateUpdated;
    private String comment;

    public Entry(int id, int type, int giverId, int takerId, int quantity, int price, Timestamp dateCreated, Timestamp dateUpdated, String comment) {
        this.id = id;
        this.type = type;
        this.giverId = giverId;
        this.takerId = takerId;
        this.quantity = quantity;
        this.price = price;
        this.dateCreated = dateCreated;
        this.dateUpdated = dateUpdated;
        this.comment = comment;
        this.selected = new SimpleBooleanProperty(false);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getGiverId() {
        return giverId;
    }

    public void setGiverId(int giverId) {
        this.giverId = giverId;
    }

    public int getTakerId() {
        return takerId;
    }

    public void setTakerId(int takerId) {
        this.takerId = takerId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Timestamp Timestamp) {
        this.dateUpdated = Timestamp;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    @Override
    public String toString() {
        return "Entry{" +
                "selected=" + selected +
                ", id=" + id +
                ", type=" + type +
                ", giverId=" + giverId +
                ", takerId=" + takerId +
                ", quantity=" + quantity +
                ", price=" + price +
                ", dateCreated=" + dateCreated +
                ", dateUpdated=" + dateUpdated +
                ", comment='" + comment + '\'' +
                '}';
    }
}
