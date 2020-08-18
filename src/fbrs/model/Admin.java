package fbrs.model;

import javafx.beans.property.BooleanProperty;

public class Admin extends User {
    public Admin(int id, int darshKey, String name, String phone, int balance) {
        super(id, darshKey, name, phone, balance);
    }
}
