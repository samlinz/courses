package sec.project.domain;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.*;

@Component
public class SQLiteRepository {

    private Connection connection;

    @PostConstruct
    public void init() throws ClassNotFoundException, SQLException {
        // Initialize in-memory SQLite db.

        // Get connection.
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite::memory");

        createTables();
    }

    private void createTables() throws SQLException {
        Statement createTablesStatement = connection.createStatement();
        // Users table, stores users, passwords, session tokens.
        createTablesStatement.executeUpdate(
                "create table Users(name text primary key, pwd text not null, token text null)");
        // Notes table, stores user created notes.
        createTablesStatement.executeUpdate(
                "create table Notes(user text not null, msg text not null)");
    }

    /**
     * Add user to database.
     */
    public boolean addUser(String name, String password) throws SQLException {
        Statement statement = connection.createStatement();
        final String query = String.format("insert into Users(name, token) values (%s, %s, null)", name, password);
        int i = statement.executeUpdate(query);
        return i == 1;
    }

    /**
     * Add a new note to database.
     */
    public boolean addNote(String user, String message) throws SQLException {
        Statement statement = connection.createStatement();
        final String query = String.format("insert into Notes(user, msg) values (%s, %s)", user, message);
        int i = statement.executeUpdate(query);
        return i == 1;
    }

    /**
     * Get user's information from database.
     */
    private User getUser(String name) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from Users where name = " + name);
        if (!resultSet.next()) return null;

        User user = new User();
        user.name = resultSet.getString("name");
        user.token = resultSet.getString("token");
        user.pwd = resultSet.getString("pwd");

        return user;
    }

    /**
     * Validate password.
     */
    public boolean isPasswordValid(String userName, String password) throws SQLException {
        User user = getUser(userName);
        if (user == null) return false;

        return user.pwd.equals(password);

    }

    /**
     * Create new session for user.
     * Session token is Unix timestamp which is very insecure and predictable.
     */
    public String newSessionForUser(String userName) throws SQLException {
        User user = getUser(userName);
        if (user == null) return null;

        ResultSet resultSet = statement.executeQuery("select * from Users where name = " + name);
    }
}
