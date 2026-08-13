package academicmanagmentsystem.fileoperations;

import academicmanagmentsystem.model.*;
import academicmanagmentsystem.operations.*;
import java.io.*;
import java.util.Date;

public class TextFileImporter {

    private String lastError = "";

    public String getLastError() {
        return lastError;
    }

    public int importStudentList(String filePath) {
        lastError = "";
        int importedCount = 0;

        try {
            BufferedReader input = new BufferedReader(new FileReader(filePath));
            UserOperations userOps = new UserOperations();

            String line;
            while ((line = input.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split("\\|");

                if (parts.length != 7) {
                    lastError += "Invalid format on line: " + line + "\n";
                    continue;
                }

                String username = parts[0].trim();
                String password = parts[1].trim();
                String firstName = parts[2].trim();
                String lastName = parts[3].trim();
                String email = parts[4].trim();
                String major = parts[5].trim();
                int enrollmentYear;

                try {
                    enrollmentYear = Integer.parseInt(parts[6].trim());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid enrollment year: " + line);
                    continue;
                }

                if (username.isEmpty() || password.isEmpty() || firstName.isEmpty() ||
                    lastName.isEmpty() || email.isEmpty() || major.isEmpty()) {
                    System.out.println("Missing required fields: " + line);
                    continue;
                }

                Student student = new Student();
                student.setUsername(username);
                student.setPassword(password);
                student.setRole("STUDENT");
                student.setFirstName(firstName);
                student.setLastName(lastName);
                student.setEmail(email);
                student.setCreatedDate(new Date());
                student.setStudentId("S" + System.currentTimeMillis());
                student.setMajor(major);
                student.setGpa(0.0);
                student.setEnrollmentYear(enrollmentYear);
                student.setTotalCredits(0);

                if (userOps.addUser(student)) {
                    importedCount++;
                } else {
                    System.out.println("Failed to import: " + username);
                }
            }

            input.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filePath);
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error reading file: " + filePath);
            e.printStackTrace();
        }

        return importedCount;
    }

    public int importGradeSheet(String filePath) {
        int importedCount = 0;

        try {
            BufferedReader input = new BufferedReader(new FileReader(filePath));
            UserOperations userOps = new UserOperations();
            CourseOperations courseOps = new CourseOperations();
            EnrollmentOperations enrollmentOps = new EnrollmentOperations();
            GradeOperations gradeOps = new GradeOperations();

            String line;
            while ((line = input.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split("\\|");

                if (parts.length != 3) {
                    System.out.println("Invalid format: " + line);
                    continue;
                }

                String studentId = parts[0].trim();
                String courseCode = parts[1].trim();
                String gradeLetter = parts[2].trim();

                Student student = null;
                for (User user : userOps.getAllUsers()) {
                    if (user instanceof Student && ((Student) user).getStudentId().equals(studentId)) {
                        student = (Student) user;
                        break;
                    }
                }

                if (student == null) {
                    System.out.println("Student not found: " + studentId);
                    continue;
                }

                Course course = null;
                for (Course c : courseOps.getAllCourses()) {
                    if (c.getCourseCode().equals(courseCode)) {
                        course = c;
                        break;
                    }
                }

                if (course == null) {
                    System.out.println("Course not found: " + courseCode);
                    continue;
                }

                String[] validGrades = {"A", "B+", "B", "C+", "C", "D+", "D", "F"};
                boolean validGrade = false;
                for (String vg : validGrades) {
                    if (vg.equals(gradeLetter)) {
                        validGrade = true;
                        break;
                    }
                }

                if (!validGrade) {
                    System.out.println("Invalid grade: " + gradeLetter);
                    continue;
                }

                Enrollment enrollment = null;
                for (Enrollment e : enrollmentOps.getStudentCourses(student.getUserId())) {
                    if (e.getCourseId() == course.getCourseId()) {
                        enrollment = e;
                        break;
                    }
                }

                if (enrollment == null) {
                    System.out.println("Enrollment not found for student " + studentId + " in course " + courseCode);
                    continue;
                }

                Grade grade = new Grade();
                grade.setEnrollmentId(enrollment.getEnrollmentId());
                grade.setGrade(gradeLetter);
                grade.setNumericGrade(gradeOps.getNumericGrade(gradeLetter));
                grade.setGradeDate(new Date());

                if (gradeOps.addGrade(grade)) {
                    importedCount++;
                } else {
                    System.out.println("Failed to import grade: " + line);
                }
            }

            input.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filePath);
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error reading file: " + filePath);
            e.printStackTrace();
        }

        return importedCount;
    }
}
