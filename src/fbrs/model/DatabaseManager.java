package fbrs.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.postgresql.ds.PGSimpleDataSource;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
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
            return preparedStmt.executeUpdate() > 0;

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
            return preparedStmt.executeUpdate() > 0;

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

            return preparedStmt.executeUpdate() > 0;

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

            return preparedStmt.executeUpdate() > 0;

        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return false;
    }

    public boolean updateEntryQuantity(int entryId, int quantity) {
        try {
            String query = "UPDATE entries SET quantity = ?, date_updated = now() WHERE entry_id = ?";

            PreparedStatement preparedStmt = getConnection().prepareStatement(query);
            preparedStmt.setInt(1, quantity);
            preparedStmt.setInt(2, entryId);

            return preparedStmt.executeUpdate() > 0;

        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return false;
    }

    public boolean resetBroken() {
        try {
            String query = "UPDATE storage_entry SET entry_type = 14 WHERE entry_type = 11;";

            PreparedStatement preparedStmt = getConnection().prepareStatement(query);
            return preparedStmt.executeUpdate() > 0;

        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return false;
    }

    public boolean resetLost() {
        try {
            String query = "UPDATE storage_entry SET entry_type = 14 WHERE entry_type = 4;";

            PreparedStatement preparedStmt = getConnection().prepareStatement(query);
            return preparedStmt.executeUpdate() > 0;

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

            if (user.getBalance() != 0) {
                addEntry(4, user.getId(), 0,
                        user.getBalance(), 0, "حذف مستخدم وتحويل بُكسه لغير معروف المصير");

                addStorageEntry(-1, 4, -1 * user.getBalance(),
                        "حذف مستخدم وتحويل بُكسه لغير معروف المصير");
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void deleteEntries(List<Entry> entries) {
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

    public void deleteStorageEntries(List<StorageEntry> storageEntries) {
        for (StorageEntry storageEntry : storageEntries) {
            deleteStorageEntry(storageEntry);
        }
    }

    public void deleteStorageEntry(StorageEntry storageEntry) {
        try {
            String query = "DELETE FROM storage_entry WHERE storage_id = ?;";

            PreparedStatement preparedStmt = getConnection().prepareStatement(query);
            preparedStmt.setInt(1, storageEntry.getId());
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

    public List<FAQ> getAllFAQ() {
        List<FAQ> faqs = new ArrayList<>();

        try {
            String query = "SELECT question, answer FROM faq";

            PreparedStatement preparedStmt = getConnection().prepareStatement(query);
            ResultSet resultSet = preparedStmt.executeQuery();

            String question;
            String answer;

            while (resultSet.next()) {
                question = resultSet.getString("question");
                answer = resultSet.getString("answer");
                faqs.add(new FAQ(question, answer));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return faqs;
    }

    public int getStorageBalance() {
        int balance = -1;

        try {
            String query = "SELECT SUM(quantity_diff) FROM storage_entry;";

            PreparedStatement preparedStmt = getConnection().prepareStatement(query);
            ResultSet resultSet = preparedStmt.executeQuery();

            while (resultSet.next()) {
                balance = resultSet.getInt(1);
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return balance;
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
            String query = "SELECT type_id, type_name, category, short_desc FROM entry_types ORDER BY type_id;";

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

    public ObservableList<Entry> getAllEntries(Date FromDateCreated, Date ToDateCreated,
                                               Date FromDateUpdated, Date ToDateUpdated, int userID) {
        ObservableList<Entry> entries = FXCollections.observableArrayList();

        try {
            String query;
            if (userID == -1) {
                query = "SELECT entry_id, entry_type, giver_id, taker_id, quantity, unit_price, date_created, date_updated, comment " +
                        "FROM entries " +
                        "WHERE (date_trunc('day', date_created) BETWEEN ? AND ?) OR (date_trunc('day', date_updated) BETWEEN ? AND ?) " +
                        "ORDER BY date_created DESC;";
            } else {
                query = "SELECT entry_id, entry_type, giver_id, taker_id, quantity, unit_price, date_created, date_updated, comment " +
                        "FROM entries " +
                        "WHERE ((date_trunc('day', date_created) BETWEEN ? AND ?) OR ((date_trunc('day', date_updated) BETWEEN ? AND ?))) " +
                        "AND ((giver_id = ?) OR (taker_id = ?)) " +
                        "ORDER BY date_created DESC;";
            }

            PreparedStatement preparedStmt = getConnection().prepareStatement(query);
            preparedStmt.setTimestamp(1, new Timestamp(FromDateCreated.getTime()));
            preparedStmt.setTimestamp(2, new Timestamp(ToDateCreated.getTime()));
            preparedStmt.setTimestamp(3, new Timestamp(FromDateUpdated.getTime()));
            preparedStmt.setTimestamp(4, new Timestamp(ToDateUpdated.getTime()));
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
                price = resultSet.getInt("unit_price");
                comment = resultSet.getString("comment");
                entries.add(new Entry(id, type, giverId, takerId, quantity, price, dateCreated, dateUpdated, comment));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return entries;
    }

    public Entry getEntryByID(int entryID) {
        Entry entry = null;

        try {
            String query = "SELECT entry_id, entry_type, giver_id, taker_id, quantity, unit_price, date_created, date_updated, comment " +
                    "FROM entries WHERE entry_id = ?;";


            PreparedStatement preparedStmt = getConnection().prepareStatement(query);
            preparedStmt.setInt(1, entryID);
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

            if (resultSet.next()) {
                id = resultSet.getInt("entry_id");
                type = resultSet.getInt("entry_type");
                giverId = resultSet.getInt("giver_id");
                takerId = resultSet.getInt("taker_id");
                dateCreated = resultSet.getTimestamp("date_created");
                dateCreated.setNanos(0);
                dateUpdated = resultSet.getTimestamp(("date_updated"));
                dateUpdated.setNanos(0);
                quantity = resultSet.getInt("quantity");
                price = resultSet.getInt("unit_price");
                comment = resultSet.getString("comment");
                entry = new Entry(id, type, giverId, takerId, quantity, price, dateCreated, dateUpdated, comment);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return entry;
    }

    public ObservableList<StorageEntry> getAllStorageEntries(Date FromDateCreated, Date ToDateCreated,
                                                             Date FromDateUpdated, Date ToDateUpdated) {
        ObservableList<StorageEntry> storageEntries = FXCollections.observableArrayList();

        try {
            String query = "SELECT storage_id, caused_by, entry_type, quantity_diff, date_created, date_updated, comment " +
                    "FROM storage_entry " +
                    "WHERE ((date_trunc('day', date_created) BETWEEN ? AND ?) OR (date_trunc('day', date_updated) BETWEEN ? AND ?)) " +
                    "AND entry_type <> 14" +
                    "ORDER BY date_created DESC;";


            PreparedStatement preparedStmt = getConnection().prepareStatement(query);
            preparedStmt.setTimestamp(1, new Timestamp(FromDateCreated.getTime()));
            preparedStmt.setTimestamp(2, new Timestamp(ToDateCreated.getTime()));
            preparedStmt.setTimestamp(3, new Timestamp(FromDateUpdated.getTime()));
            preparedStmt.setTimestamp(4, new Timestamp(ToDateUpdated.getTime()));
            ResultSet resultSet = preparedStmt.executeQuery();

            int id;
            int type;
            int causedBy;
            int quantity;
            Timestamp dateCreated;
            Timestamp dateUpdated;
            String comment;

            while (resultSet.next()) {
                id = resultSet.getInt("storage_id");
                type = resultSet.getInt("entry_type");
                causedBy = resultSet.getInt("caused_by");
                dateCreated = resultSet.getTimestamp("date_created");
                dateCreated.setNanos(0);
                dateUpdated = resultSet.getTimestamp(("date_updated"));
                dateUpdated.setNanos(0);
                quantity = resultSet.getInt("quantity_diff");
                comment = resultSet.getString("comment");
                storageEntries.add(new StorageEntry(id, causedBy, type, quantity, dateCreated, dateUpdated, comment));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return storageEntries;
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
                balance = calculateUserBalanceToDateInc(id, new Date(new java.util.Date().getTime()));
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

            String query = "INSERT INTO entries(entry_type, giver_id, taker_id, quantity, unit_price, date_created, date_updated, comment) " +
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

    public int addStorageEntry(int causedBy, int entryType, int quantityDiff, String comment) {
        int id = -1;
        Connection connection = getConnection();

        try {

            String query = "INSERT INTO storage_entry(caused_by, entry_type, quantity_diff, date_created, date_updated, comment) " +
                    "VALUES (?,?,?,now(), now(),?);";

            PreparedStatement preparedStmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            if (causedBy == -1) {
                preparedStmt.setNull(1, Types.INTEGER);
            } else {
                preparedStmt.setInt(1, causedBy);
            }
            preparedStmt.setInt(2, entryType);
            preparedStmt.setInt(3, quantityDiff);
            preparedStmt.setString(4, comment);

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

    public int calculateBroken() {
        int broken = 0;

        try {
            String query = "SELECT SUM(quantity_diff) FROM storage_entry WHERE entry_type = 11;";
            PreparedStatement preparedStmt = getConnection().prepareStatement(query);

            preparedStmt.executeQuery();
            ResultSet resultSet = preparedStmt.getResultSet();
            if (resultSet.next())
                broken = resultSet.getInt(1);

        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return broken;
    }

    public int calculateLost() {
        int lost = 0;

        try {
            String query = "SELECT SUM(quantity_diff) FROM storage_entry WHERE entry_type = 4;";
            PreparedStatement preparedStmt = getConnection().prepareStatement(query);

            preparedStmt.executeQuery();
            ResultSet resultSet = preparedStmt.getResultSet();
            if (resultSet.next())
                lost = resultSet.getInt(1);

        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return lost;
    }

    public int calculateUserBalanceToDateInc(int id, Date date) {
        int balance = 0;

        try {
            String query = "SELECT COALESCE ((SELECT SUM(quantity) FROM entries e1 WHERE taker_id = ? AND (date_trunc('day', date_created) <= ?)), 0) - " +
                    "COALESCE ((SELECT SUM(quantity) FROM entries e2 WHERE giver_id = ? AND (date_trunc('day', date_created) <= ?)), 0);";
            PreparedStatement preparedStmt = getConnection().prepareStatement(query);
            preparedStmt.setInt(1, id);
            preparedStmt.setTimestamp(2, new Timestamp(date.getTime()));
            preparedStmt.setInt(3, id);
            preparedStmt.setTimestamp(4, new Timestamp(date.getTime()));

            preparedStmt.executeQuery();
            ResultSet resultSet = preparedStmt.getResultSet();
            resultSet.next();
            balance = resultSet.getInt(1);

        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return balance;
    }

    //This method will return 0 if the backup process completed successfully..
    public int backup(String path) throws InterruptedException, IOException {
        ResourceBundle reader = ResourceBundle.getBundle("dbconfig");
        String[] envp = {
                "PGHOST=" + reader.getString("db.serverName"),
                "PGDATABASE=" + reader.getString("db.databaseName"),
                "PGUSER=" + reader.getString("db.username"),
                "PGPASSWORD=" + reader.getString("db.password"),
                "PGPORT=5432",
                "path=C:\\Program Files\\PostgreSQL\\12\\bin"
        };
        String[] cmdArray = {
                "cmd",
                "/c",
                String.format("pg_dump -f \"%s\"", path)
        };
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(cmdArray, envp);
        process.waitFor();
        return process.exitValue();
    }


    //This method will return 0 if the restore process completed successfully..
    public int restore(String path) throws IOException, InterruptedException {
        ResourceBundle reader = ResourceBundle.getBundle("dbconfig");
        String[] envp = {
                "PGHOST=" + reader.getString("db.serverName"),
                "PGDATABASE=" + reader.getString("db.databaseName"),
                "PGUSER=" + reader.getString("db.username"),
                "PGPASSWORD=" + reader.getString("db.password"),
                "PGPORT=5432",
                "path=C:\\Program Files\\PostgreSQL\\12\\bin"
        };
        String[] cmdArray = {
                "cmd",
                "/c",
                String.format("psql -f \"%s\"", path)
        };
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(cmdArray, envp);
        process.waitFor();
        return process.exitValue();
    }

    public void dropDataBaseTables() throws SQLException {
        String query = "DROP SCHEMA public CASCADE; CREATE SCHEMA public;";
        PreparedStatement preparedStmt = getConnection().prepareStatement(query);
        preparedStmt.executeUpdate();
    }

    public void exit() {
        try {
            getConnection().close();
            connection = null;
            System.out.println("Close Connection");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        System.out.println("Exit");
    }
}
