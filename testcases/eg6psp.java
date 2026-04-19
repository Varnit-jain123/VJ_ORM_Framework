package testcases;

import com.vj.orm.core.*;
import com.vj.orm.exception.DataException;
import DTO_files.*;

public class eg6psp {
    public static void main(String[] args) {
        DataManager dm = DataManager.getDataManager();

        try {
            dm.begin();

            Course c = new Course();
            c.title = "Course to Delete";
            int id = dm.save(c);
            System.out.println("Created course with ID: " + id);

            // Delete course by ID
            dm.delete(Course.class, id);

            dm.end();
            System.out.println("Course " + id + " deleted successfully.");

        } catch (DataException de) {
            System.err.println("ORM Error: " + de.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
