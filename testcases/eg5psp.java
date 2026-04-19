package testcases;

import com.vj.orm.core.*;
import com.vj.orm.exception.DataException;
import DTO_files.*;

public class eg5psp {
    public static void main(String[] args) {
        DataManager dm = DataManager.getDataManager();

        try {
            dm.begin();

            Course c = new Course();
            c.title = "Temporary Course";
            int id = dm.save(c);
            System.out.println("Created course with ID: " + id);

            // Prepare the same object for update
            c.code = id;
            c.title = "Advance Java";

            // Perform update
            dm.update(c);

            dm.end();
            System.out.println("Course updated successfully to: " + c.title);

        } catch (DataException de) {
            System.err.println("ORM Error: " + de.getMessage());
        }
    }
}
