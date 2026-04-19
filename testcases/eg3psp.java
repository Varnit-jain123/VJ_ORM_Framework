package testcases;

import com.vj.orm.core.*;
import com.vj.orm.exception.DataException;
import DTO_files.*;

public class eg3psp {
    public static void main(String[] args) {
        DataManager dm = DataManager.getDataManager();

        try {
            // 1. Start transaction
            dm.begin();

            // 2. Create and save a Course
            Course c = new Course();
            c.setTitle("java");

            int code = dm.save(c);

            // 3. Commit and close
            dm.end();

            System.out.println("Course added with code as: " + code);

        } catch (DataException de) {
            System.err.println("Database Error: " + de.getMessage());
        }
    }
}
