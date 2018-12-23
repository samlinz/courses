package sec.helloinsert;

import java.io.FileReader;
import java.sql.*;
import java.util.Scanner;
import org.h2.tools.RunScript;

public class HelloInsert {

    public static void main(String[] args) throws Exception {
        // Open connection to a database -- do not alter this code
        String databaseAddress = "jdbc:h2:file:./database";
        if (args.length > 0) {
            databaseAddress = args[0];
        }

        Connection connection = DriverManager.getConnection(databaseAddress, "sa", "");

        try {
            // If database has not yet been created, insert content
            RunScript.execute(connection, new FileReader("sql/database-schema.sql"));
            RunScript.execute(connection, new FileReader("sql/database-import.sql"));
        } catch (Throwable t) {
            System.err.println(t.getMessage());
        }

        // Add the code that first reads the agents from the database, then
        // asks for an agent (id and name) and stores the agent to the database.
        // Finally, the program prints the agents in the database again.

        PreparedStatement query = connection.prepareStatement("SELECT id, name FROM Agent");
        PreparedStatement insertQuery = connection.prepareStatement("INSERT INTO Agent(id, name) VALUES(?, ?)");
        try {
            printAgents(query);

            Scanner scanner = new Scanner(System.in, "utf-8");

            System.out.println("");

            System.out.println("New id");
            String newId = scanner.nextLine();
            System.out.println("New name");
            String newName = scanner.nextLine();

            insertQuery.setString(1, newId);
            insertQuery.setString(2, newName);

            insertQuery.executeUpdate();

            printAgents(query);
        } catch (SQLException ex) {
            System.err.println("SQL Error " + ex.getLocalizedMessage());
        } finally {
            query.close();
            insertQuery.close();
            if (!connection.isClosed())
                connection.close();
        }

        connection.close();
    }

    private static void printAgents(PreparedStatement statement) throws SQLException {
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            System.out.println(String.format("%s %s"
                    , resultSet.getString("id")
                    , resultSet.getString("name")
            ));
        }
        resultSet.close();
    }
}
