package testcases;

import com.vj.orm.core.*;
import com.vj.orm.exception.DataException;
import DTO_files.*;
import java.sql.Date;

public class eg4psp {
    public static void main(String[] args) {
        DataManager dm = DataManager.getDataManager();

        try {
            dm.begin();
            // Create a Course to ensure we have a valid Foreign Key
            // Course c = new Course();
            // c.setTitle("Python Development");
            // int courseId = dm.save(c);
            // System.out.println("Course added with ID: " + courseId);

            // Create a Student linked to that Course
            Student s = new Student();
            s.rollNumber = 103; // Manual PK as per setup.sql
            s.firstName = "Rahul";
            s.lastName = "Jain";
            s.aadharCardNumber = "998877665533";
            s.courseCode = 2; // Foreign Key Link
            s.gender = "Male";
            s.dateOfBirth = Date.valueOf("1988-05-20");

            int roll = dm.save(s);

            dm.end();
            System.out.println("Student added with Roll Number: " + roll);

        } catch (DataException de) {
            System.err.println("ORM Error: " + de.getMessage());
        }
    }
}
