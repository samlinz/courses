package sec.project.repository;

import org.springframework.stereotype.Component;
import sec.project.domain.Note;
import sec.project.domain.User;

import javax.annotation.PostConstruct;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class SQLiteRepository {

    // DB Connection.
    private Connection connection;

    @PostConstruct
    public void init() throws ClassNotFoundException, SQLException {
        // Initialize in-memory SQLite db.

        // Get connection.
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");

        createTables();
    }

    /**
     * Create database schema.
     */
    private void createTables() throws SQLException {
        Statement createTablesStatement = connection.createStatement();
        // Users table, stores users, passwords, session tokens.
        createTablesStatement.executeUpdate(
                "create table Users(name text primary key, pwd text not null, token text null)");
        // Notes table, stores user created notes.
        createTablesStatement.executeUpdate(
                "create table Notes(user text not null, msg text not null, private integer not null)");
    }

    /**
     * Add user to database.
     */
    public boolean addUser(String name, String password) throws SQLException {
        Statement statement = connection.createStatement();
        final String query = String.format("insert into Users(name, pwd, token) values ('%s', '%s', null)", name, password);
        int i = statement.executeUpdate(query);
        return i == 1;
    }

    /**
     * Add a new note to database.
     */
    public boolean addNote(String user, String message, boolean privateNote) throws SQLException {
        Statement statement = connection.createStatement();
        final String query = String.format("insert into Notes(user, msg, private) values ('%s', '%s', %d)"
                , user, message, privateNote ? 1 : 0);
        int i = statement.executeUpdate(query);
        return i == 1;
    }

    /**
     * Get all notes from database.
     */
    public List<Note> getNotes(String user, String filter) throws SQLException {
        Statement statement = connection.createStatement();
        final String query = String.format(
                "select * from Notes where (private = 0 or (private = 1 and user='%1$s')) and ('%2$s' = 'null' or msg like '%%%2$s%%')"
                , user
                , (filter != null && !filter.isEmpty()) ? filter : "null");
        ResultSet resultSet = statement.executeQuery(query);

        List<Note> result = new ArrayList<>();
        while (resultSet.next()) {
            Note note = new Note();
            note.user = resultSet.getString("user");
            note.message = resultSet.getString("msg");
            result.add(note);
        }

        return result;
    }

    /**
     * Get user's information from database.
     */
    public User getUser(String name) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(
                String.format("select * from Users where name = '%s'", name));
        if (!resultSet.next()) return null;

        User user = new User();
        user.name = resultSet.getString("name");
        user.token = resultSet.getString("token");
        user.pwd = resultSet.getString("pwd");

        return user;
    }

    /**
     * Get user for the session token received in cookie.
     * Returns null if no match.
     */
    public String getUsernameForSession(String session) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(String.format("select * from Users where token='%s'", session));
        if (!resultSet.next()) return null;

        return resultSet.getString("name");
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
    public String newSessionForUser(String username) throws SQLException {
        User user = getUser(username);
        if (user == null) return null;

        final long timestamp = System.currentTimeMillis() / 1000;
        Statement statement = connection.createStatement();
        final int modified = statement.executeUpdate(
                String.format("update Users set token='%d' where name='%s'", timestamp, username));

        return modified == 1 ? String.valueOf(timestamp) : null;
    }

    /**
     * Signs out user from database by nullifying the session token.
     */
    public void removeSessionForUser(String username) throws SQLException {
        User user = getUser(username);
        if (user == null) return;

        final String query = String.format("update Users set token=null where name='%s'", username);
        Statement statement = connection.createStatement();
        statement.execute(query);
    }
}
