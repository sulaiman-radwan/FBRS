package fbrs.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public abstract class User {
    public final static int ADMIN_TYPE = 0;
    public final static int SELLER_TYPE = 1;
    public final static int FISHERMAN_LAUNCH_TYPE = 5;
    public final static int FISHERMAN_HASAKA_TYPE = 6;

    int id;
    int darshKey;
    String name;
    String phone;
    int balance;
    BooleanProperty selected;

    public User(int id, int darshKey, String name, String phone, int balance) {
        this.id = id;
        this.darshKey = darshKey;
        this.name = name.replaceAll("أ", "ا");
        this.phone = phone;
        this.balance = balance;
        this.selected = new SimpleBooleanProperty(false);
    }

    public static int getUserTypeID(String type) {
        switch (type) {
            case "صياد لنش":
                return FISHERMAN_LAUNCH_TYPE;
            case "صياد حسكة":
                return FISHERMAN_HASAKA_TYPE;
            case "تاجر":
                return SELLER_TYPE;
        }
        return -1;
    }

    public int getDarshKey() {
        return darshKey;
    }

    public void setDarshKey(int darshKey) {
        this.darshKey = darshKey;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
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
        return darshKey + " - " + name;
    }
}
