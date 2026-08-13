package academicmanagmentsystem.fileoperations;

import academicmanagmentsystem.model.*;
import academicmanagmentsystem.operations.*;
import java.io.*;
import java.util.ArrayList;

public class BinaryFileExporter {

    public boolean exportStudentData(String filePath) {
        try {
            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(filePath));

            UserOperations userOps = new UserOperations();
            ArrayList<User> users = userOps.getAllUsers();
            ArrayList<Student> students = new ArrayList<>();

            for (User user : users) {
                if (user instanceof Student) {
                    students.add((Student) user);
                }
            }

            output.writeInt(students.size());

            for (Student student : students) {
                output.writeObject(student);
            }

            output.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean exportCourseData(String filePath) {
        try {
            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(filePath));

            CourseOperations courseOps = new CourseOperations();
            ArrayList<Course> courses = courseOps.getAllCourses();

            output.writeInt(courses.size());

            for (Course course : courses) {
                output.writeObject(course);
            }

            output.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean exportEnrollmentData(String filePath) {
        try {
            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(filePath));

            UserOperations userOps = new UserOperations();
            EnrollmentOperations enrollmentOps = new EnrollmentOperations();
            ArrayList<User> users = userOps.getAllUsers();
            ArrayList<Enrollment> allEnrollments = new ArrayList<>();

            for (User user : users) {
                if (user instanceof Student) {
                    ArrayList<Enrollment> studentEnrollments = enrollmentOps.getStudentCourses(user.getUserId());
                    allEnrollments.addAll(studentEnrollments);
                }
            }

            output.writeInt(allEnrollments.size());

            for (Enrollment enrollment : allEnrollments) {
                output.writeObject(enrollment);
            }

            output.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean exportGradeData(String filePath) {
        try {
            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(filePath));

            UserOperations userOps = new UserOperations();
            GradeOperations gradeOps = new GradeOperations();
            ArrayList<User> users = userOps.getAllUsers();
            ArrayList<Grade> allGrades = new ArrayList<>();

            for (User user : users) {
                if (user instanceof Student) {
                    ArrayList<Grade> studentGrades = gradeOps.getGradesByStudent(user.getUserId());
                    allGrades.addAll(studentGrades);
                }
            }

            output.writeInt(allGrades.size());

            for (Grade grade : allGrades) {
                output.writeObject(grade);
            }

            output.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
