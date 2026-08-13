package academicmanagmentsystem.fileoperations;

import academicmanagmentsystem.model.*;
import academicmanagmentsystem.operations.*;
import java.io.*;
import java.util.ArrayList;

public class TextFileExporter {

    public boolean exportTranscript(int studentId, String filePath) {
        try {
            PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(filePath)));

            UserOperations userOps = new UserOperations();
            EnrollmentOperations enrollmentOps = new EnrollmentOperations();
            GradeOperations gradeOps = new GradeOperations();
            CourseOperations courseOps = new CourseOperations();

            User user = userOps.getUserById(studentId);
            if (user == null || !(user instanceof Student)) {
                output.close();
                return false;
            }

            Student student = (Student) user;

            output.println("===== STUDENT TRANSCRIPT =====");
            output.println("Name: " + student.getFirstName() + " " + student.getLastName());
            output.println("Student ID: " + student.getStudentId());
            output.println("Major: " + student.getMajor());
            output.println("GPA: " + student.getGpa());
            output.println();
            output.println("COURSES:");
            output.println("Course Code | Course Name                    | Grade | Credits");
            output.println("----------------------------------------------------------------");

            ArrayList<Enrollment> enrollments = enrollmentOps.getStudentCourses(studentId);
            int totalCredits = 0;

            for (Enrollment enrollment : enrollments) {
                if (enrollment.getStatus().equals("COMPLETED")) {
                    Course course = courseOps.getCourseById(enrollment.getCourseId());
                    ArrayList<Grade> grades = gradeOps.getGradesByStudent(studentId);

                    for (Grade grade : grades) {
                        if (grade.getEnrollmentId() == enrollment.getEnrollmentId()) {
                            output.printf("%-11s | %-30s | %-5s | %d%n",
                                course.getCourseCode(),
                                course.getCourseName(),
                                grade.getGrade(),
                                course.getCreditHours());
                            totalCredits += course.getCreditHours();
                            break;
                        }
                    }
                }
            }

            output.println();
            output.println("Total Credits: " + totalCredits);

            output.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean exportPerformanceReport(int instructorId, String filePath) {
        try {
            PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(filePath)));

            UserOperations userOps = new UserOperations();
            CourseOperations courseOps = new CourseOperations();
            EnrollmentOperations enrollmentOps = new EnrollmentOperations();
            GradeOperations gradeOps = new GradeOperations();

            User user = userOps.getUserById(instructorId);
            if (user == null || !(user instanceof Instructor)) {
                output.close();
                return false;
            }

            Instructor instructor = (Instructor) user;

            output.println("===== INSTRUCTOR PERFORMANCE REPORT =====");
            output.println("Instructor: Dr. " + instructor.getFirstName() + " " + instructor.getLastName());
            output.println("Department: " + instructor.getDepartment());
            output.println("Specialization: " + instructor.getSpecialization());
            output.println();

            ArrayList<Course> courses = courseOps.getCoursesByInstructor(instructorId);

            for (Course course : courses) {
                output.println("Course: " + course.getCourseCode() + " - " + course.getCourseName());
                output.println("Semester: " + course.getSemester() + " " + course.getYear());
                output.println("Credit Hours: " + course.getCreditHours());

                int enrollmentCount = courseOps.getCourseEnrollments(course.getCourseId());
                output.println("Enrolled Students: " + enrollmentCount + "/" + course.getMaxCapacity());

                output.println("Students:");
                output.println("Student Name                    | Grade");
                output.println("-------------------------------------------");

                ArrayList<Enrollment> enrollments = enrollmentOps.getCourseStudents(course.getCourseId());
                for (Enrollment enrollment : enrollments) {
                    if (enrollment.getStatus().equals("ENROLLED") || enrollment.getStatus().equals("COMPLETED")) {
                        User studentUser = userOps.getUserById(enrollment.getStudentId());
                        if (studentUser instanceof Student) {
                            Student student = (Student) studentUser;
                            String gradeLetter = "-";

                            ArrayList<Grade> grades = gradeOps.getGradesByCourse(course.getCourseId());
                            for (Grade grade : grades) {
                                if (grade.getEnrollmentId() == enrollment.getEnrollmentId()) {
                                    gradeLetter = grade.getGrade();
                                    break;
                                }
                            }

                            output.printf("%-30s | %s%n",
                                student.getFirstName() + " " + student.getLastName(),
                                gradeLetter);
                        }
                    }
                }

                output.println();
            }

            output.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean exportAcademicSummary(String filePath) {
        try {
            PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(filePath)));

            UserOperations userOps = new UserOperations();
            CourseOperations courseOps = new CourseOperations();

            output.println("===== ACADEMIC MANAGEMENT SYSTEM SUMMARY =====");
            output.println("Generated: " + new java.util.Date());
            output.println();

            ArrayList<User> users = userOps.getAllUsers();
            int studentCount = 0;
            int instructorCount = 0;
            int adminCount = 0;

            for (User user : users) {
                if (user instanceof Student) studentCount++;
                else if (user instanceof Instructor) instructorCount++;
                else if (user instanceof Admin) adminCount++;
            }

            output.println("SYSTEM STATISTICS:");
            output.println("Total Users: " + users.size());
            output.println("Students: " + studentCount);
            output.println("Instructors: " + instructorCount);
            output.println("Administrators: " + adminCount);
            output.println();

            ArrayList<Course> courses = courseOps.getAllCourses();
            output.println("Total Courses: " + courses.size());
            output.println();

            output.println("COURSE LIST:");
            output.println("Course Code | Course Name                    | Semester      | Instructor");
            output.println("-------------------------------------------------------------------------");

            for (Course course : courses) {
                String instructorName = "-";
                if (course.getInstructorId() != 0) {
                    User instructor = userOps.getUserById(course.getInstructorId());
                    if (instructor != null) {
                        instructorName = instructor.getFirstName() + " " + instructor.getLastName();
                    }
                }

                output.printf("%-11s | %-30s | %-13s | %s%n",
                    course.getCourseCode(),
                    course.getCourseName(),
                    course.getSemester() + " " + course.getYear(),
                    instructorName);
            }

            output.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
