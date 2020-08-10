package fbrs.model;

public class Fisherman extends User {
    private int shipType;

    public Fisherman(int id, int darshKey, String name, String phone, int balance, int shipType) {
        super(id, darshKey, name, phone, balance);
        this.shipType = shipType;
    }

    public int getShipType() {
        return shipType;
    }

    public void setShipType(int shipType) {
        this.shipType = shipType;
    }
}
