package testcases;

import com.vj.orm.config.*;
import com.vj.orm.core.*;

import java.sql.Connection;
import java.sql.SQLException;

public class eg1psp {
    public static void main(String[] args) {
        String configPath = "conf.json";
        try {
            // Read config
            DBConfig config = ConfigReader.readConfig(configPath);

            // Establish connection
            Connection connection = ConnectionManager.getConnection(config);

            // Extract and print metadata
            MetadataExtractor extractor = new MetadataExtractor(connection);
            extractor.extractAndPrint();

            // Close connection
            ConnectionManager.closeConnection();

        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
        }
    }
}
