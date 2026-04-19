package testcases;

import com.vj.orm.config.*;
import com.vj.orm.core.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.io.IOException;

public class eg2psp {
    public static void main(String[] args) {
        String configPath = "conf.json";
        try {
            // Read config
            DBConfig config = ConfigReader.readConfig(configPath);

            // Establish connection
            Connection connection = ConnectionManager.getConnection(config);

            // Generate entities
            EntityGenerator generator = new EntityGenerator(connection);
            generator.generateEntities();

            // Close connection
            ConnectionManager.closeConnection();

            System.out.println("Phase 2 completion: Entity classes generated successfully.");

        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
        }
    }
}
