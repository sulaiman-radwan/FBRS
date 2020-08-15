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

    public void deactivateUsers(ArrayList<User> users) {
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

    public void reactivateUsers(ArrayList<User> users) {
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

    public void deleteUsers(ArrayList<User> users) {
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

    public ObservableList<Market> getAllMarkets() {
        ObservableList<Market> markets = FXCollections.observableArrayList();

        try {
            String query = "SELECT * FROM markets;";

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

    public ObservableList<Seller> getSellersByMarket(int marketId) {
        ObservableList<Seller> sellers = FXCollections.observableArrayList();

        try {
            String query = "SELECT * FROM users WHERE market_id = ? AND is_active = TRUE and is_deleted = FALSE;";

            PreparedStatement preparedStmt = getConnection().prepareStatement(query);
            preparedStmt.setInt(1, marketId);
            getSeller(sellers, preparedStmt);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sellers;
    }

    private void getSeller(List<Seller> sellers, PreparedStatement preparedStmt) throws SQLException {
        ResultSet resultSet = preparedStmt.executeQuery();

        int id;
        int darshKey;
        String name;
        String phone;
        int balance = 0;
        int marketID;

        while (resultSet.next()) {
            id = resultSet.getInt("user_id");
            darshKey = resultSet.getInt("darsh_key");
            name = resultSet.getString("name");
            phone = resultSet.getString("phone");
            marketID = resultSet.getInt("market_id");
            sellers.add(new Seller(id, darshKey, name, phone, balance, marketID));
        }
    }

    public ObservableList<Seller> getAllSellers() {
        ObservableList<Seller> sellers = FXCollections.observableArrayList();

        try {
            String query = "SELECT * FROM users WHERE user_type = 1 AND is_active = TRUE and is_deleted = FALSE;";

            PreparedStatement preparedStmt = getConnection().prepareStatement(query);
            getSeller(sellers, preparedStmt);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sellers;
    }

    public ObservableList<Fisherman> getAllFishermen() {
        ObservableList<Fisherman> fishermen = FXCollections.observableArrayList();

        try {
            String query = "SELECT * FROM users WHERE user_type > 4 AND is_active = TRUE and is_deleted = FALSE;";

            PreparedStatement preparedStmt = getConnection().prepareStatement(query);
            ResultSet resultSet = preparedStmt.executeQuery();

            int id;
            int darshKey;
            String name;
            String phone;
            int balance = 0;
            int shipType;

            while (resultSet.next()) {
                id = resultSet.getInt("user_id");
                darshKey = resultSet.getInt("darsh_key");
                name = resultSet.getString("name");
                phone = resultSet.getString("phone");
                shipType = resultSet.getInt("user_type");
                fishermen.add(new Fisherman(id, darshKey, name, phone, balance, shipType));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return fishermen;
    }

    public ObservableList<User> getDeletedUsers() {
        ObservableList<User> users = FXCollections.observableArrayList();

        try {
            String query = "SELECT * FROM users WHERE is_active = FALSE and is_deleted = FALSE;";

            PreparedStatement preparedStmt = getConnection().prepareStatement(query);
            ResultSet resultSet = preparedStmt.executeQuery();

            int id;
            int darshKey;
            String name;
            String phone;
            int balance = 0;

            while (resultSet.next()) {
                id = resultSet.getInt("user_id");
                darshKey = resultSet.getInt("darsh_key");
                name = resultSet.getString("name");
                phone = resultSet.getString("phone");
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

    private int generateDarshKey(int userType) {
        //todo: Correcting the error in the event of changing the type of the Fisherman
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

    /* This method will return the balance of a user given its id, if there are no
    * entries for that user, it will return 0 */
    public int getUserBalance(int id){
        Connection connection = getConnection();

        try {

            String query = "select coalesce ((select sum(quantity) from entries e where taker_id = ?),0) - " +
                    "coalesce ((select sum(quantity) from entries e2 where giver_id = ?), 0);";

            PreparedStatement preparedStmt = connection.prepareStatement(query);
            preparedStmt.setInt(1, id);
            preparedStmt.setInt(2, id);

            ResultSet balance = preparedStmt.executeQuery();

            if(balance.next()){
                return balance.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /* This method returns the last date(Timestamp) that a user balance was 0, and returns null
    * if that user has not got a balance 0 yet */
    public Timestamp getLastDeficit(int id){
        Connection connection = getConnection();

        try {

            String query = "with filtered_entries(date_updated, quantity) as (" +
                    "(select date_updated, quantity from entries where taker_id = ?)" +
                    "union " +
                    "(select date_updated, -1 * quantity from entries e2 where giver_id = ?) " +
                    "order by date_updated asc" +
                    "), running_sum (date_updated, running_sum) as(" +
                    "select date_updated , sum(fe.quantity ) over(order by fe.date_updated rows unbounded preceding) as running_sum " +
                    "from filtered_entries fe" +
                    ") " +
                    "select date_updated from running_sum " +
                    "where running_sum = 0 " +
                    "order by date_updated desc " +
                    "limit 1;";

            PreparedStatement preparedStmt = connection.prepareStatement(query);
            preparedStmt.setInt(1, id);
            preparedStmt.setInt(2, id);

            ResultSet lastDeficit = preparedStmt.executeQuery();

            if(lastDeficit.next()){
                return lastDeficit.getTimestamp(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
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
