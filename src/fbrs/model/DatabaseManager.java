package fbrs.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return connection;
    }

    private void connect() {
        ResourceBundle reader;

        try {
            reader = ResourceBundle.getBundle("dbconfig");

            PGSimpleDataSource source = new PGSimpleDataSource();
            source.setServerName(reader.getString("db.serverName"));
            source.setDatabaseName(reader.getString("db.databaseName"));
            source.setUser(reader.getString("db.username"));
            source.setPassword(reader.getString("db.password"));

            connection = source.getConnection();
            System.out.println("Connected to database " + reader.getString("db.databaseName"));

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


    public boolean updateUserName(int id, String name) {
        try {
            String query = "UPDATE users SET name = ? WHERE user_id = ?";

            PreparedStatement preparedStmt = getConnection().prepareStatement(query);
            preparedStmt.setString(1, name);
            preparedStmt.setInt(2, id);
            preparedStmt.executeUpdate();
            return true;

        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return false;
    }

    public void updateDarshKey(int id, int darshKey) throws SQLException {

        String query = "UPDATE users SET darsh_key = ? WHERE user_id = ?";

        PreparedStatement preparedStmt = getConnection().prepareStatement(query);
        preparedStmt.setInt(1, darshKey);
        preparedStmt.setInt(2, id);
        preparedStmt.executeUpdate();

    }

    public int updateDarshKeyByUserType(int id, int userType) {

        String query = "UPDATE users SET darsh_key = ? WHERE user_id = ?";
        try {
            PreparedStatement preparedStmt = getConnection().prepareStatement(query);
            int DarshKey = generateDarshKey(userType);
            preparedStmt.setInt(1, DarshKey);
            preparedStmt.setInt(2, id);
            preparedStmt.executeUpdate();
            return DarshKey;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return -1;
    }

    public boolean updateUserPhone(int id, String phone) {
        try {
            String query = "UPDATE users SET phone = ? WHERE user_id = ?";

            PreparedStatement preparedStmt = getConnection().prepareStatement(query);
            preparedStmt.setString(1, phone);
            preparedStmt.setInt(2, id);
            preparedStmt.executeUpdate();
            return true;

        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return false;
    }

    public boolean updateUserMarket(int id, int marketID) {
        try {
            String query = "UPDATE users SET market_id = ? WHERE user_id = ?";

            PreparedStatement preparedStmt = getConnection().prepareStatement(query);
            preparedStmt.setInt(1, marketID);
            preparedStmt.setInt(2, id);
            preparedStmt.executeUpdate();
            return true;

        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return false;
    }

    public boolean updateFishermanType(int id, int userType) {
        try {
            String query = "UPDATE users SET user_type = ? WHERE user_id = ?";

            PreparedStatement preparedStmt = getConnection().prepareStatement(query);
            preparedStmt.setInt(1, userType);
            preparedStmt.setInt(2, id);
            preparedStmt.executeUpdate();
            return true;

        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return false;
    }

    public void deactivateUsers(List<User> users) {
        for (User user : users) {
            deactivateUser(user);
        }
    }

    public void deactivateUser(User user) {
        try {
            String query = "UPDATE users SET is_active = FALSE WHERE user_id = ?";

            PreparedStatement preparedStmt = getConnection().prepareStatement(query);
            preparedStmt.setInt(1, user.getId());
            preparedStmt.executeUpdate();

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void reactivateUsers(List<User> users) {
        for (User user : users) {
            reactivateUser(user);
        }
    }

    public void reactivateUser(User user) {
        try {
            String query = "UPDATE users SET is_active = TRUE WHERE user_id = ?";

            PreparedStatement preparedStmt = getConnection().prepareStatement(query);
            preparedStmt.setInt(1, user.getId());
            preparedStmt.executeUpdate();

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void deleteUsers(List<User> users) {
        for (User user : users) {
            deleteUser(user);
        }
    }

    public void deleteUser(User user) {
        try {
            String query = "UPDATE users SET is_deleted = TRUE WHERE user_id = ?";

            PreparedStatement preparedStmt = getConnection().prepareStatement(query);
            preparedStmt.setInt(1, user.getId());
            preparedStmt.executeUpdate();

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void deleteEntry(List<Entry> entries) {
        for (Entry entry : entries) {
            deleteEntry(entry);
        }
    }

    public void deleteEntry(Entry entry) {
        try {
            String query = "DELETE FROM entries WHERE entry_id = ?;";

            PreparedStatement preparedStmt = getConnection().prepareStatement(query);
            preparedStmt.setInt(1, entry.getId());
            preparedStmt.executeUpdate();

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public ObservableList<Market> getAllMarkets() {
        ObservableList<Market> markets = FXCollections.observableArrayList();

        try {
            String query = "SELECT market_id, market_name FROM markets ORDER BY market_id;";

            PreparedStatement preparedStmt = getConnection().prepareStatement(query);
            ResultSet resultSet = preparedStmt.executeQuery();

            int marketId;
            String marketName;

            while (resultSet.next()) {
                marketId = resultSet.getInt("market_id");
                marketName = resultSet.getString("market_name");
                markets.add(new Market(marketId, marketName));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return markets;
    }

    public ObservableList<Seller> getAllSellers() {
        ObservableList<Seller> sellers = FXCollections.observableArrayList();

        try {
            String query = "SELECT user_id, darsh_key, name, phone, market_id, " +
                    "COALESCE ((SELECT SUM(quantity) FROM entries e1 WHERE taker_id = users.user_id), 0) - " +
                    "COALESCE ((SELECT SUM(quantity) FROM entries e2 WHERE giver_id = users.user_id), 0) as balance FROM users " +
                    "WHERE user_type = 1 AND is_active = TRUE and is_deleted = FALSE " +
                    "ORDER BY darsh_key;";

            PreparedStatement preparedStmt = getConnection().prepareStatement(query);
            ResultSet resultSet = preparedStmt.executeQuery();

            int id;
            int darshKey;
            String name;
            String phone;
            int balance;
            int marketID;

            while (resultSet.next()) {
                id = resultSet.getInt("user_id");
                darshKey = resultSet.getInt("darsh_key");
                name = resultSet.getString("name");
                phone = resultSet.getString("phone");
                balance = resultSet.getInt("balance");
                marketID = resultSet.getInt("market_id");
                sellers.add(new Seller(id, darshKey, name, phone, balance, marketID));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sellers;
    }

    public ObservableList<Fisherman> getAllFishermen() {
        ObservableList<Fisherman> fishermen = FXCollections.observableArrayList();

        try {
            String query = "SELECT user_id, darsh_key, name, phone, user_type, market_id, " +
                    "COALESCE ((SELECT SUM(quantity) FROM entries e1 WHERE taker_id = users.user_id), 0) - " +
                    "COALESCE ((SELECT SUM(quantity) FROM entries e2 WHERE giver_id = users.user_id), 0) as balance FROM users " +
                    "WHERE user_type > 4 AND is_active = TRUE and is_deleted = FALSE " +
                    "ORDER BY darsh_key;";

            PreparedStatement preparedStmt = getConnection().prepareStatement(query);
            ResultSet resultSet = preparedStmt.executeQuery();

            int id;
            int darshKey;
            String name;
            String phone;
            int balance;
            int shipType;

            while (resultSet.next()) {
                id = resultSet.getInt("user_id");
                darshKey = resultSet.getInt("darsh_key");
                name = resultSet.getString("name");
                phone = resultSet.getString("phone");
                balance = resultSet.getInt("balance");
                shipType = resultSet.getInt("user_type");
                fishermen.add(new Fisherman(id, darshKey, name, phone, balance, shipType));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return fishermen;
    }

    public List<EntryType> getEntryTypes() {
        List<EntryType> entryTypes = new ArrayList<>();

        try {
            String query = "SELECT type_id, type_name, category, short_desc FROM entry_types;";

            PreparedStatement preparedStmt = getConnection().prepareStatement(query);
            ResultSet resultSet = preparedStmt.executeQuery();

            int id;
            String name;
            int category;
            String shortDesc;

            while (resultSet.next()) {
                id = resultSet.getInt("type_id");
                name = resultSet.getString("type_name");
                category = resultSet.getInt("category");
                shortDesc = resultSet.getString("short_desc");
                entryTypes.add(new EntryType(id, name, category, shortDesc));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return entryTypes;
    }

    public ObservableList<Entry> getAllEntries(Timestamp FromDateCreated, Timestamp ToDateCreated,
                                               Timestamp FromDateUpdated, Timestamp ToDateUpdated, int userID) {
        ObservableList<Entry> entries = FXCollections.observableArrayList();

        //Change the creation hour to the last hour of the day to request today's Entries
        ToDateCreated.setHours(24);
        ToDateCreated.setMinutes(59);
        ToDateCreated.setSeconds(59);

        //Change the deletion hour to the last hour of the day to request today's Entries
        ToDateUpdated.setHours(24);
        ToDateUpdated.setMinutes(59);
        ToDateUpdated.setSeconds(59);

        try {
            String query;
            if (userID == -1) {
                query = "SELECT entry_id, entry_type, giver_id, taker_id, quantity, price, date_created, date_updated, comment " +
                        "FROM entries " +
                        "WHERE (date_created BETWEEN ? AND ?) OR (date_updated BETWEEN ? AND ?);";
            } else {
                query = "SELECT entry_id, entry_type, giver_id, taker_id, quantity, price, date_created, date_updated, comment " +
                        "FROM entries " +
                        "WHERE ((date_created BETWEEN ? AND ?) OR (date_updated BETWEEN ? AND ?)) " +
                        "AND ((giver_id = ?) OR (taker_id = ?));";
            }

            PreparedStatement preparedStmt = getConnection().prepareStatement(query);
            preparedStmt.setTimestamp(1, FromDateCreated);
            preparedStmt.setTimestamp(2, ToDateCreated);
            preparedStmt.setTimestamp(3, FromDateUpdated);
            preparedStmt.setTimestamp(4, ToDateUpdated);
            if (userID != -1) {
                preparedStmt.setInt(5, userID);
                preparedStmt.setInt(6, userID);
            }
            ResultSet resultSet = preparedStmt.executeQuery();

            int id;
            int type;
            int giverId;
            int takerId;
            int quantity;
            int price;
            Timestamp dateCreated;
            Timestamp dateUpdated;
            String comment;

            while (resultSet.next()) {
                id = resultSet.getInt("entry_id");
                type = resultSet.getInt("entry_type");
                giverId = resultSet.getInt("giver_id");
                takerId = resultSet.getInt("taker_id");
                dateCreated = resultSet.getTimestamp("date_created");
                dateCreated.setNanos(0);
                dateUpdated = resultSet.getTimestamp(("date_updated"));
                dateUpdated.setNanos(0);
                quantity = resultSet.getInt("quantity");
                price = resultSet.getInt("price");
                comment = resultSet.getString("comment");
                entries.add(new Entry(id, type, giverId, takerId, quantity, price, dateCreated, dateUpdated, comment));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return entries;
    }

    public ObservableList<User> getDeletedUsers() {
        ObservableList<User> users = FXCollections.observableArrayList();

        try {
            String query = "SELECT user_id, darsh_key, name, phone  FROM users WHERE is_active = FALSE and is_deleted = FALSE;";

            PreparedStatement preparedStmt = getConnection().prepareStatement(query);
            ResultSet resultSet = preparedStmt.executeQuery();

            int id;
            int darshKey;
            String name;
            String phone;
            int balance;

            while (resultSet.next()) {
                id = resultSet.getInt("user_id");
                darshKey = resultSet.getInt("darsh_key");
                name = resultSet.getString("name");
                phone = resultSet.getString("phone");
                balance = calculateUserBalance(id);
                users.add(new User(id, darshKey, name, phone, balance) {
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public int addUser(int marketID, String name, String phone, int userType) {
        int id;
        Connection connection = getConnection();

        try {

            String query = "INSERT INTO users(darsh_key, market_id, name, phone, user_type) VALUES  (?,?,?,?,?);";

            PreparedStatement preparedStmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            id = generateDarshKey(userType);
            preparedStmt.setInt(1, id);

            if (marketID > 0)
                preparedStmt.setInt(2, marketID);
            else
                preparedStmt.setNull(2, Types.INTEGER);

            preparedStmt.setString(3, name);

            if (phone.isEmpty())
                preparedStmt.setNull(4, Types.VARCHAR);
            else
                preparedStmt.setString(4, phone);

            preparedStmt.setInt(5, userType);

            preparedStmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }

        return id;
    }

    public int addMarket(String marketName) {
        int id = -1;
        Connection connection = getConnection();

        try {

            String query = "INSERT INTO markets(market_name) VALUES (?);";

            PreparedStatement preparedStmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStmt.setString(1, marketName);

            preparedStmt.executeUpdate();

            ResultSet generatedKeys = preparedStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                id = generatedKeys.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;
    }

    public int addEntry(int entryType, int giverId, int takerId, int quantity, int price, String comment) {
        int id = -1;
        Connection connection = getConnection();

        try {

            String query = "INSERT INTO entries(entry_type, giver_id, taker_id, quantity, price, date_created, date_updated, comment) " +
                    "VALUES (?,?,?,?,?,now(), now(),?);";

            PreparedStatement preparedStmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStmt.setInt(1, entryType);
            preparedStmt.setInt(2, giverId);
            preparedStmt.setInt(3, takerId);
            preparedStmt.setInt(4, quantity);
            preparedStmt.setInt(5, price);
            preparedStmt.setString(6, comment);

            preparedStmt.executeUpdate();

            ResultSet generatedKeys = preparedStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                id = generatedKeys.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;
    }

    private int generateDarshKey(int userType) {
        int key = -1;

        try {
            String query = "SELECT MAX(darsh_key) FROM users WHERE user_type = ?;";
            PreparedStatement preparedStmt = getConnection().prepareStatement(query);
            preparedStmt.setInt(1, userType);

            preparedStmt.executeQuery();
            ResultSet resultSet = preparedStmt.getResultSet();
            resultSet.next();
            int maxDarshKey = resultSet.getInt(1);

            key = maxDarshKey + 1;

        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return key;
    }

    public int calculateUserBalance(int id) {
        int balance = -1;

        try {
            String query = "SELECT COALESCE ((SELECT SUM(quantity) FROM entries e1 WHERE taker_id = ?), 0) - " +
                    "COALESCE ((SELECT SUM(quantity) FROM entries e2 WHERE giver_id = ?), 0);";
            PreparedStatement preparedStmt = getConnection().prepareStatement(query);
            preparedStmt.setInt(1, id);
            preparedStmt.setInt(2, id);

            preparedStmt.executeQuery();
            ResultSet resultSet = preparedStmt.getResultSet();
            resultSet.next();
            balance = resultSet.getInt(1);

        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return balance;
    }

    public void exit() {
        try {
            getConnection().close();
            connection = null;
            System.out.println("Close Connection");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        System.out.print("Exit");
    }
}
