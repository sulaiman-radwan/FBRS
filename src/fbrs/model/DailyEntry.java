package fbrs.model;

import java.time.LocalDate;

public class DailyEntry {
    private final LocalDate dateCreated;
    private int type;
    private int quantity;

    public DailyEntry(LocalDate dateCreated, int type, int quantity) {
        this.dateCreated = dateCreated;
        this.type = type;
        this.quantity = quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public int getType() {
        return type;
    }

    public int getQuantity() {
        return quantity;
    }
}
