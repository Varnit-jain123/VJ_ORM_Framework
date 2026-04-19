package testcases;

import com.vj.orm.core.*;
import com.vj.orm.exception.DataException;
import DTO_files.*;
import java.util.List;

public class eg9psp {
    public static void main(String[] args) {
        DataManager dm = DataManager.getDataManager();
        
        try {
            dm.begin();

            System.out.println("--- 1. Fetching from View (course_summary) ---");
            List<CourseSummary> list = dm.query(CourseSummary.class).list();
            for (CourseSummary cs : list) {
                System.out.println(cs.getCode() + " " + cs.getTitle());
            }

            System.out.println("\n--- 2. Attempting to SAVE to View (Should Fail) ---");
            try {
                CourseSummary csNew = new CourseSummary();
                csNew.code = 99;
                csNew.title = "Fail Course";
                dm.save(csNew);
            } catch (DataException de) {
                System.out.println("Caught Expected Error: " + de.getMessage());
            }

            dm.end();

        } catch (DataException de) {
            System.err.println("ORM Error: " + de.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
