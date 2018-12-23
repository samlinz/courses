package sec.hellodatabase;

import java.io.FileReader;
import java.sql.*;

import org.h2.tools.RunScript;

public class HelloDatabase {

    public static void main(String[] args) throws Exception {
        // Open connection to a database -- do not alter this code
        String databaseAddress = "jdbc:h2:file:./database";
        if (args.length > 0) {
            databaseAddress = args[0];
        }

        Connection connection = DriverManager.getConnection(databaseAddress, "sa", "");

        ResultSet resultSet = null;
        try {
            try {
                // If database has not yet been created, insert content
                RunScript.execute(connection, new FileReader("sql/database-schema.sql"));
                RunScript.execute(connection, new FileReader("sql/database-import.sql"));
            } catch (Throwable t) {
                System.err.println(t.getMessage());
            }

            // Add the code that reads the Agents from the database
            // and prints them here
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT id, name FROM Agent");
            while (resultSet.next()) {
                System.out.println(
                        String.format("%s %s"
                                , resultSet.getString("id")
                                , resultSet.getString("name")
                        ));
            }
        } finally {
            if (resultSet != null)
                resultSet.close();
            if (connection != null && !connection.isClosed())
                connection.close();
        }
    }
}
