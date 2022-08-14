package com.luv2code.springmvc;

import com.luv2code.springmvc.models.*;
import com.luv2code.springmvc.repository.HistoryGradeDao;
import com.luv2code.springmvc.repository.MathGradeDao;
import com.luv2code.springmvc.repository.ScienceGradeDao;
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource("/application-test.properties")
@SpringBootTest
public class StudentAndGradeServiceTest {

    @Autowired
    private StudentAndGradeService studentService;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private MathGradeDao mathGradeDao;

    @Autowired
    private ScienceGradeDao scienceGradeDao;

    @Autowired
    private HistoryGradeDao historyGradeDao;

    @Autowired
    private JdbcTemplate jdbc;

    @Value("${sql.script.create.student}")
    private String sqlAddStudent;

    @Value("${sql.script.create.math.grade}")
    private String sqlAddMathGrade;

    @Value("${sql.script.create.science.grade}")
    private String sqlAddScienceGrade;

    @Value("${sql.script.create.history.grade}")
    private String sqlAddHistoryGrade;

    @Value("${sql.script.delete.student}")
    private String sqlDeleteStudent;

    @Value("${sql.script.delete.math.grade}")
    private String sqlDeleteMathGrade;

    @Value("${sql.script.delete.science.grade}")
    private String sqlDeleteScienceGrade;

    @Value("${sql.script.delete.history.grade}")
    private String sqlDeleteHistoryGrade;

    @BeforeEach
    public void setupDatabase(){
        jdbc.execute(sqlAddStudent);
        jdbc.execute(sqlAddMathGrade);
        jdbc.execute(sqlAddScienceGrade);
        jdbc.execute(sqlAddHistoryGrade);
    }

    @Test
    public void createStudentService(){

        studentService.createStudent("Erhan","Karaarslan","erhannkaraarslan@gmail.com");

        CollegeStudent student=studentDao.findByEmailAddress("erhannkaraarslan@gmail.com");

        assertEquals("erhannkaraarslan@gmail.com",student.getEmailAddress(),"find by email");

    }

    @Test
    public void isStudentNullCheck(){
        assertTrue(studentService.checkIfStudentIsNull(1));
        assertFalse(studentService.checkIfStudentIsNull(0));
    }

    @Test
    public void deleteStudentService(){

        Optional<CollegeStudent> deletedCollegeStudent=studentDao.findById(1);

        Optional<MathGrade> deletedMathGrade=mathGradeDao.findById(1);
        Optional<ScienceGrade> deletedScienceGrade=scienceGradeDao.findById(1);
        Optional<HistoryGrade> deletedHistoryGrade=historyGradeDao.findById(1);

        assertTrue(deletedCollegeStudent.isPresent(),"ReturnTrue");
        assertTrue(deletedMathGrade.isPresent(),"ReturnTrue");
        assertTrue(deletedScienceGrade.isPresent(),"ReturnTrue");
        assertTrue(deletedHistoryGrade.isPresent(),"ReturnTrue");

        studentService.deleteStudent(1);


        deletedCollegeStudent=studentDao.findById(1);
        deletedMathGrade=mathGradeDao.findById(1);
        deletedScienceGrade=scienceGradeDao.findById(1);
        deletedHistoryGrade=historyGradeDao.findById(1);

        assertFalse(deletedCollegeStudent.isPresent(),"Return False");
        assertFalse(deletedMathGrade.isPresent(),"Return False");
        assertFalse(deletedScienceGrade.isPresent(),"Return False");
        assertFalse(deletedHistoryGrade.isPresent(),"Return False");


    }

    @Sql("/insertData.sql")
    @Test
    public void getGradeBookService(){
        Iterable<CollegeStudent> iterableCollegeStudents=studentService.getGradebook();

        List<CollegeStudent> collegeStudents=new ArrayList<>();

        for(CollegeStudent collegeStudent : iterableCollegeStudents){
            collegeStudents.add(collegeStudent);
        }

        assertEquals(5,collegeStudents.size());


    }

    @Test
    public void createGradeService(){

        //create the grade
        assertTrue(studentService.createGrade(80.50,1,"math"));
        assertTrue(studentService.createGrade(90.50,1,"science"));
        assertTrue(studentService.createGrade(75.50,1,"history"));
        //get all grades with students
        Iterable<MathGrade> mathGrades=mathGradeDao.findGradeByStudentId(1);
        Iterable<ScienceGrade> scienceGrades=scienceGradeDao.findGradeByStudentId(1);
        Iterable<HistoryGrade> historyGrades=historyGradeDao.findGradeByStudentId(1);

        //verify there is grades
        assertTrue(((Collection<MathGrade>) mathGrades).size()==2,"Student has match grades");
        assertTrue(((Collection<ScienceGrade>) scienceGrades).size()==2,"Student has science grades");
        assertTrue(((Collection<HistoryGrade>) historyGrades).size()==2,"Student has history grades");

    }

    @Test
    public void createGradeServiceReturnFalse(){
        assertFalse(studentService.createGrade(105,1,"math"));
        assertFalse(studentService.createGrade(-5,1,"math"));
        assertFalse(studentService.createGrade(80.5,2,"math"));
        assertFalse(studentService.createGrade(80.5,1,"literature"));
    }

    @Test
    public void deleteGradeService(){
        assertEquals(1,studentService.deleteGrade(1,"math"),"Returns student id after delete");
        assertEquals(1,studentService.deleteGrade(1,"science"),"Returns student id after delete");
        assertEquals(1,studentService.deleteGrade(1,"history"),"Returns student id after delete");
    }

    @Test
    public void deleteGradeServiceReturnStudentIdOfZero(){
        assertEquals(0,studentService.deleteGrade(0,"science"));
        assertEquals(0,studentService.deleteGrade(0,"literature"),"No student should have a literature class");
    }

    @Test
    public void studentInformation(){
        GradebookCollegeStudent gradebookCollegeStudent=studentService.studentInformation(1);
        assertNotNull(gradebookCollegeStudent);
        assertEquals(1,gradebookCollegeStudent.getId());
        assertEquals("Merve",gradebookCollegeStudent.getFirstname());
        assertEquals("Karaarslan",gradebookCollegeStudent.getLastname());
        assertEquals("merveekaraarslan@gmail.com",gradebookCollegeStudent.getEmailAddress());
        assertTrue(gradebookCollegeStudent.getStudentGrades().getMathGradeResults().size()==1);
        assertTrue(gradebookCollegeStudent.getStudentGrades().getScienceGradeResults().size()==1);
        assertTrue(gradebookCollegeStudent.getStudentGrades().getHistoryGradeResults().size()==1);

    }
    @Test
    public void StudentInformationServiceReturnNull(){

        GradebookCollegeStudent gradebookCollegeStudent=studentService.studentInformation(0);
        assertNull(gradebookCollegeStudent);
    }

    @AfterEach
    public void setupAfterTransaction(){
        jdbc.execute(sqlDeleteStudent);
        jdbc.execute(sqlDeleteMathGrade);
        jdbc.execute(sqlDeleteScienceGrade);
        jdbc.execute(sqlDeleteHistoryGrade);
    }
}
