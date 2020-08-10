package fbrs.model;

import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseModel {
    private static DatabaseModel instance;

    private ObservableList<Market> markets;
    private ObservableList<Seller> sellers;
    private ObservableList<Fisherman> fishermen;

    private DatabaseModel() {
    }

    public static DatabaseModel getModel() {
        if (instance == null)
            instance = new DatabaseModel();
        DatabaseManager.getInstance();
        return instance;
    }

    public List<Market> getAllMarkets() {
        if (markets == null) {
            markets = DatabaseManager.getInstance().getAllMarkets();
        }
        return markets;
    }

    public Market getMarketByID(int marketID) {
        if (markets == null) {
            markets = DatabaseManager.getInstance().getAllMarkets();
        }
        for (Market market : markets) {
            if (market.getId() == marketID)
                return market;
        }
        return null;
    }

    public ObservableList<Seller> getSellersByMarket(int marketId) {
        //todo:
        /*
        getAllSellers();
        List<Seller> sellersByMarket = new ArrayList<>();
        for (Seller seller : sellers)
            if (seller.getMarket() == marketId)
                sellersByMarket.add(seller);
        return sellersByMarket;
         */
        return DatabaseManager.getInstance().getSellersByMarket(marketId);
    }

    public ObservableList<Seller> getAllSellers() {
        if (sellers == null)
            sellers = DatabaseManager.getInstance().getAllSellers();
        return sellers;
    }

    public ObservableList<Fisherman> getAllFishermen() {
        if (fishermen == null)
            fishermen = DatabaseManager.getInstance().getAllFishermen();
        return fishermen;
    }

    public ObservableList<User> getDeletedUsers() {
        return DatabaseManager.getInstance().getDeletedUsers();
    }

    public void deactivateUsers(ArrayList<User> users) {
        DatabaseManager.getInstance().deactivateUsers(users);
        sellers = null;
        fishermen = null;
    }

    public void deactivateUser(User user) {
        DatabaseManager.getInstance().deactivateUser(user);
        sellers = null;
        fishermen = null;
    }

    public void reactivateUsers(ArrayList<User> users) {
        DatabaseManager.getInstance().reactivateUsers(users);
        sellers = null;
        fishermen = null;
    }

    public void reactivateUser(User user) {
        DatabaseManager.getInstance().reactivateUser(user);
        sellers = null;
        fishermen = null;
    }

    public void deleteUsers(ArrayList<User> users) {
        DatabaseManager.getInstance().deleteUsers(users);
        sellers = null;
        fishermen = null;
    }

    public void deleteUser(User user) {
        DatabaseManager.getInstance().deleteUser(user);
        sellers = null;
        fishermen = null;
    }

    public int addUser(int marketId, String name, String phone, int userType) {
        sellers = null;
        fishermen = null;
        return DatabaseManager.getInstance().addUser(marketId, name, phone, userType);
    }

    public int addMarket(String market) {
        markets = null;
        return DatabaseManager.getInstance().addMarket(market);
    }

    public void updateDarshKey(int id, int DarshKey) throws SQLException {
        DatabaseManager.getInstance().updateDarshKey(id, DarshKey);
    }

    public boolean updateUserName(int id, String name) {
        return DatabaseManager.getInstance().updateUserName(id, name);
    }

    public boolean updateUserPhone(int id, String phone) {
        return DatabaseManager.getInstance().updateUserPhone(id, phone);
    }

    public boolean updateUserMarket(int id, int marketID) {
        return DatabaseManager.getInstance().updateUserMarket(id, marketID);
    }

    public boolean updateFishermanType(int id, int userType) {
        return DatabaseManager.getInstance().updateFishermanType(id, userType);
    }
}
