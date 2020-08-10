package fbrs.model;

public class Seller extends User {
    private int market;

    public Seller(int id, int darshKey, String name, String phone, int balance, int market) {
        super(id, darshKey, name, phone, balance);
        this.market = market;
    }

    public int getMarket() {
        return market;
    }

    public void setMarket(int market) {
        this.market = market;
    }
}
