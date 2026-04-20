package bobby_project;

import com.vj.orm.annotation.*;

@Table(name="student")
@Cacheable
public class Student {

    @PrimaryKey
    @AutoIncrement
    @Column(name="roll_number")
    public int rollNumber;

    @Column(name="first_name")
    public String firstName;

    @Column(name="last_name")
    public String lastName;

    @Column(name="aadhar_card_number")
    public String aadharCardNumber;

    @Column(name="course_code")
    public int courseCode;

    @Column(name="gender")
    public String gender;

    @Column(name="date_of_birth")
    public java.sql.Date dateOfBirth;
}
