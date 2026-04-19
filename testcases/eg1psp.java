package testcases;

import com.vj.orm.config.*;
import com.vj.orm.core.*;

import java.sql.Connection;
import java.sql.SQLException;

public class eg1psp {
    public static void main(String[] args) {
        String configPath = "conf.json";
        try {
            // 1. Read config
            DBConfig config = ConfigReader.readConfig(configPath);

            // 2. Establish connection
            Connection connection = ConnectionManager.getConnection(config);

            // 3. Extract and print metadata
            MetadataExtractor extractor = new MetadataExtractor(connection);
            extractor.extractAndPrint();

            // 4. Close connection
            ConnectionManager.closeConnection();

        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
        }
    }
}
