package DTO_files;

import com.vj.orm.annotation.*;

@Table(name="student")
public class Student {
    @PrimaryKey
    @Column(name="roll_number")
    public int rollNumber;

    @Column(name="first_name")
    public String firstName;

    @Column(name="last_name")
    public String lastName;

    @Column(name="aadhar_card_number")
    public String aadharCardNumber;

    @ForeignKey(parent="course", column="code")
    @Column(name="course_code")
    public int courseCode;

    @Column(name="gender")
    public String gender;

    @Column(name="date_of_birth")
    public java.sql.Date dateOfBirth;

    public int getRollnumber() {
        return this.rollNumber;
    }

    public void setRollnumber(int rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getFirstname() {
        return this.firstName;
    }

    public void setFirstname(String firstName) {
        this.firstName = firstName;
    }

    public String getLastname() {
        return this.lastName;
    }

    public void setLastname(String lastName) {
        this.lastName = lastName;
    }

    public String getAadharcardnumber() {
        return this.aadharCardNumber;
    }

    public void setAadharcardnumber(String aadharCardNumber) {
        this.aadharCardNumber = aadharCardNumber;
    }

    public int getCoursecode() {
        return this.courseCode;
    }

    public void setCoursecode(int courseCode) {
        this.courseCode = courseCode;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public java.sql.Date getDateofbirth() {
        return this.dateOfBirth;
    }

    public void setDateofbirth(java.sql.Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

}
