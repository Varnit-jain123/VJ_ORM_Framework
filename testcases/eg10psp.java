package testcases;

import com.vj.orm.core.*;
import com.vj.orm.exception.DataException;
import DTO_files.*;
import java.util.List;

public class eg10psp {
    public static void main(String[] args) {
        try {
            // 1. Initial Load (Builds Cache)
            DataManager.init();
            
            DataManager dm = DataManager.getDataManager();
            dm.begin();

            System.out.println("\n--- 1. First Fetch (Should be Cache Hit) ---");
            List<Course> list1 = dm.query(Course.class).list();
            for (Course c : list1) System.out.println(c.code + " " + c.title);

            System.out.println("\n--- 2. Updating Course 1 in Cache & DB ---");
            Course c1 = list1.get(0);
            c1.setTitle(c1.getTitle() + " (Updated)");
            dm.update(c1);
            System.out.println("Update Complete.");

            System.out.println("\n--- 3. Second Fetch (Should reflect change from Cache) ---");
            List<Course> list2 = dm.query(Course.class).list();
            for (Course c : list2) System.out.println(c.code + " " + c.title);
            
            System.out.println("\n--- 4. Verification of Cloning ---");
            // Modify a returned object but don't call update()
            Course cTemp = list2.get(0);
            cTemp.setTitle("Accidental Change");
            
            System.out.println("Third Fetch (Should NOT show Accidental Change due to cloning):");
            List<Course> list3 = dm.query(Course.class).list();
            System.out.println("Title in list3: " + list3.get(0).title);

            dm.end();

        } catch (DataException de) {
            System.err.println("ORM Error: " + de.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
