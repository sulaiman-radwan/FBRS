package fbrs.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DatabaseModel {
    private static DatabaseModel instance;
    private final Admin admin = new Admin(0, 0, "الدرش", "059", 0);
    private ObservableList<Market> markets;
    private ObservableList<Seller> sellers;
    private ObservableList<Fisherman> fishermen;
    private List<EntryType> entryTypes;
    private Map<Integer, User> userMap;

    private DatabaseModel() {
    }

    public static DatabaseModel getModel() {
        if (instance == null)
            instance = new DatabaseModel();
        DatabaseManager.getInstance();
        return instance;
    }

    public void fetchData() {
        getAllMarkets();
        getAllFishermen();
        getAllSellers();
        createUserMap();
    }

    public boolean isValidMarketName(String name) {
        for (Market market : getAllMarkets()) {
            if (market.getName().trim().equals(name))
                return false;
        }
        return true;
    }

    public User getUserById(int id) {
        if (id == 0)
            return admin;
        if (userMap == null) {
            createUserMap();
        }
        return userMap.get(id);
    }

    private void createUserMap() {
        userMap = new HashMap<>();

        for (User user : getAllFishermen())
            userMap.put(user.getId(), user);

        for (User user : getAllSellers())
            userMap.put(user.getId(), user);
    }

    public List<Market> getAllMarkets() {
        if (markets == null) {
            markets = DatabaseManager.getInstance().getAllMarkets();
        }
        return markets;
    }

    public List<EntryType> getEntryTypes() {
        if (entryTypes == null) {
            entryTypes = DatabaseManager.getInstance().getEntryTypes();
        }
        return entryTypes;
    }

    public List<EntryType> getEntryTypesSpecialCases() {
        List<EntryType> specialCases = new ArrayList<>();

        for (EntryType entryType : getEntryTypes())
            if (entryType.getCategory() == 2)
                specialCases.add(entryType);

        return specialCases;
    }

    public String getEntryTypeName(int id) {
        for (EntryType entry : getEntryTypes()) {
            if (id == entry.getId())
                return entry.getName();
        }
        return "خطأ";
    }

    public ObservableList<Entry> getAllEntries(Timestamp FromDateCreated, Timestamp ToDateCreated, Timestamp FromDateUpdated, Timestamp ToDateUpdated, int userID) {
        return DatabaseManager.getInstance().getAllEntries(FromDateCreated, ToDateCreated, FromDateUpdated, ToDateUpdated, userID);
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
        return FXCollections.observableArrayList(getAllSellers().stream()
                .filter(seller -> seller.getMarket() == marketId).collect(Collectors.toList()));
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

    public void deactivateUsers(List<User> users) {
        DatabaseManager.getInstance().deactivateUsers(users);
        sellers = null;
        fishermen = null;
        userMap = null;
    }

    public void deactivateUser(User user) {
        DatabaseManager.getInstance().deactivateUser(user);
        sellers = null;
        fishermen = null;
        userMap = null;
    }

    public void reactivateUsers(List<User> users) {
        DatabaseManager.getInstance().reactivateUsers(users);
        sellers = null;
        fishermen = null;
        userMap = null;
    }

    public void reactivateUser(User user) {
        DatabaseManager.getInstance().reactivateUser(user);
        sellers = null;
        fishermen = null;
        userMap = null;
    }

    public void deleteUsers(List<User> users) {
        DatabaseManager.getInstance().deleteUsers(users);
        sellers = null;
        fishermen = null;
        userMap = null;
    }

    public void deleteUser(User user) {
        DatabaseManager.getInstance().deleteUser(user);
        sellers = null;
        fishermen = null;
        userMap = null;
    }

    public void deleteEntry(List<Entry> entries) {
        DatabaseManager.getInstance().deleteEntry(entries);
        sellers = null;
        fishermen = null;
        userMap = null;
    }

    public void deleteEntry(Entry entry) {
        DatabaseManager.getInstance().deleteEntry(entry);
        sellers = null;
        fishermen = null;
        userMap = null;
    }

    public int addUser(int marketId, String name, String phone, int userType) {
        sellers = null;
        fishermen = null;
        userMap = null;
        return DatabaseManager.getInstance().addUser(marketId, name, phone, userType);
    }

    public int addMarket(String market) {
        markets = null;
        return DatabaseManager.getInstance().addMarket(market);
    }

    public int addEntry(int entryType, int giverId, int takerId, int quantity, int price, String comment) {
        sellers = null;
        fishermen = null;
        return DatabaseManager.getInstance().addEntry(entryType, giverId, takerId, quantity, price, comment);
    }

    public void updateDarshKey(int id, int DarshKey) throws SQLException {
        DatabaseManager.getInstance().updateDarshKey(id, DarshKey);
    }

    public int updateDarshKeyByUserType(int id, int userType) {
        return DatabaseManager.getInstance().updateDarshKeyByUserType(id, userType);
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

    public int calculateUserBalance(int id) {
        return DatabaseManager.getInstance().calculateUserBalance(id);
    }
}