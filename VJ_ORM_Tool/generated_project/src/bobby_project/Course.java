package bobby_project;

import com.vj.orm.annotation.*;

@Table(name="course")
@Cacheable
public class Course {

    @PrimaryKey
    @AutoIncrement
    @Column(name="code")
    public int code;

    @Column(name="title")
    public String title;
}
