package fbrs.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.sql.Timestamp;

public class StorageEntry {
    BooleanProperty selected;
    private int id;
    private int causedBy;
    private int type;
    private int quantity;
    private Timestamp dateCreated;
    private Timestamp dateUpdated;
    private String comment;

    public StorageEntry(int id, int causedBy, int type, int quantity, Timestamp dateCreated, Timestamp dateUpdated, String comment) {
        this.id = id;
        this.causedBy = causedBy;
        this.type = type;
        this.quantity = quantity;
        this.dateCreated = dateCreated;
        this.dateUpdated = dateUpdated;
        this.comment = comment;
        this.selected = new SimpleBooleanProperty(false);
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCausedBy() {
        return causedBy;
    }

    public void setCausedBy(int causedBy) {
        this.causedBy = causedBy;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Timestamp getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Timestamp dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
