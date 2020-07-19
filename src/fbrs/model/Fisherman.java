package fbrs.model;

public class Fisherman extends User {
    private int shipType;

    public Fisherman(int id, String name, String phone, int balance, int shipType) {
        super(id, name, phone, balance);
        this.shipType = shipType;
    }

    public int getShipType() {
        return shipType;
    }

    public void setShipType(int shipType) {
        this.shipType = shipType;
    }
}
