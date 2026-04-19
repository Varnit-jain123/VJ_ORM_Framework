package testcases;

import com.vj.orm.core.*;
import com.vj.orm.exception.DataException;
import DTO_files.*;
import java.util.List;

public class eg8psp {
    public static void main(String[] args) {
        DataManager dm = DataManager.getDataManager();

        try {
            dm.begin();

            System.out.println("--- 1. Courses with code > 1 ---");
            List<Course> courses = dm.query(Course.class)
                    .where("code").gt(1)
                    .list();
            for (Course c : courses)
                System.out.println(c.code + ": " + c.title);

            System.out.println("\n--- 2. Students with Roll between 101 and 105 ---");
            List<Student> students = dm.query(Student.class)
                    .where("rollNumber").between(101, 105)
                    .list();
            for (Student s : students)
                System.out.println(s.rollNumber + ": " + s.firstName);

            System.out.println("\n--- 3. Complex Query: Roll > 200 OR FirstName = 'Bobby' ---");
            List<Student> students2 = dm.query(Student.class)
                    .where("rollNumber").gt(200)
                    .or("firstName").eq("Bobby")
                    .list();
            for (Student s : students2)
                System.out.println(s.rollNumber + ": " + s.firstName);

            dm.end();

        } catch (DataException de) {
            System.err.println("ORM Error: " + de.getMessage());
        }
    }
}
