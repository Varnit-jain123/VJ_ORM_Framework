package DTO_files;

import com.vj.orm.annotation.*;

@Table(name="course")
public class Course {
    @PrimaryKey
    @AutoIncrement
    @Column(name="code")
    public int code;

    @Column(name="title")
    public String title;

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
