package testcases;

import com.vj.orm.core.*;
import com.vj.orm.exception.DataException;
import DTO_files.*;
import java.util.List;

public class eg7psp {
    public static void main(String[] args) {
        DataManager dm = DataManager.getDataManager();

        try {
            dm.begin();

            System.out.println("--- Fetching All Courses ---");
            List<Course> courses = dm.query(Course.class).list();
            for (Course c : courses) {
                System.out.println("ID: " + c.code + " | Title: " + c.title);
            }

            System.out.println("\n--- Fetching All Students ---");
            List<Student> students = dm.query(Student.class).list();
            for (Student s : students) {
                System.out.println("Roll: " + s.rollNumber + " | Name: " + s.firstName + " " + s.lastName +
                        " | Aadhar: " + s.aadharCardNumber + " | Course ID: " + s.courseCode + " | DOB: "
                        + s.dateOfBirth);
            }

            dm.end();

        } catch (DataException de) {
            System.err.println("ORM Error: " + de.getMessage());
        }
    }
}
