package bobby_project;

import com.vj.orm.annotation.*;

@Table(name="course_summary")
public class CourseSummary {

    @Column(name="code")
    public int code;

    @Column(name="title")
    public String title;
}
