package academicmanagmentsystem.fileoperations;

import academicmanagmentsystem.model.*;
import academicmanagmentsystem.operations.*;
import java.io.*;

public class BinaryFileImporter {

    public int importStudentData(String filePath) {
        int importedCount = 0;

        try {
            ObjectInputStream input = new ObjectInputStream(new FileInputStream(filePath));
            UserOperations userOps = new UserOperations();

            int count = input.readInt();

            for (int i = 0; i < count; i++) {
                Student student = (Student) input.readObject();

                if (student.getUsername() == null || student.getUsername().isEmpty()) {
                    System.out.println("Invalid student data: missing username");
                    continue;
                }

                if (userOps.addUser(student)) {
                    importedCount++;
                } else {
                    System.out.println("Failed to import student: " + student.getUsername());
                }
            }

            input.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filePath);
            e.printStackTrace();
        } catch (EOFException e) {
            System.out.println("End of file reached");
        } catch (IOException e) {
            System.out.println("Error reading file: " + filePath);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found error");
            e.printStackTrace();
        }

        return importedCount;
    }

    public int importCourseData(String filePath) {
        int importedCount = 0;

        try {
            ObjectInputStream input = new ObjectInputStream(new FileInputStream(filePath));
            CourseOperations courseOps = new CourseOperations();

            int count = input.readInt();

            for (int i = 0; i < count; i++) {
                Course course = (Course) input.readObject();

                if (course.getCourseCode() == null || course.getCourseCode().isEmpty()) {
                    System.out.println("Invalid course data: missing course code");
                    continue;
                }

                if (courseOps.addCourse(course)) {
                    importedCount++;
                } else {
                    System.out.println("Failed to import course: " + course.getCourseCode());
                }
            }

            input.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filePath);
            e.printStackTrace();
        } catch (EOFException e) {
            System.out.println("End of file reached");
        } catch (IOException e) {
            System.out.println("Error reading file: " + filePath);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found error");
            e.printStackTrace();
        }

        return importedCount;
    }

    public int importEnrollmentData(String filePath) {
        int importedCount = 0;

        try {
            ObjectInputStream input = new ObjectInputStream(new FileInputStream(filePath));
            EnrollmentOperations enrollmentOps = new EnrollmentOperations();

            int count = input.readInt();

            for (int i = 0; i < count; i++) {
                Enrollment enrollment = (Enrollment) input.readObject();

                if (enrollment.getStudentId() == 0 || enrollment.getCourseId() == 0) {
                    System.out.println("Invalid enrollment data: missing student or course ID");
                    continue;
                }

                if (!enrollmentOps.checkDuplicateEnrollment(enrollment.getStudentId(), enrollment.getCourseId())) {
                    if (enrollmentOps.enrollStudent(enrollment.getStudentId(), enrollment.getCourseId())) {
                        importedCount++;
                    } else {
                        System.out.println("Failed to import enrollment");
                    }
                } else {
                    System.out.println("Duplicate enrollment detected, skipping");
                }
            }

            input.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filePath);
            e.printStackTrace();
        } catch (EOFException e) {
            System.out.println("End of file reached");
        } catch (IOException e) {
            System.out.println("Error reading file: " + filePath);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found error");
            e.printStackTrace();
        }

        return importedCount;
    }

    public int importGradeData(String filePath) {
        int importedCount = 0;

        try {
            ObjectInputStream input = new ObjectInputStream(new FileInputStream(filePath));
            GradeOperations gradeOps = new GradeOperations();

            int count = input.readInt();

            for (int i = 0; i < count; i++) {
                Grade grade = (Grade) input.readObject();

                if (grade.getEnrollmentId() == 0) {
                    System.out.println("Invalid grade data: missing enrollment ID");
                    continue;
                }

                String[] validGrades = {"A", "B+", "B", "C+", "C", "D+", "D", "F"};
                boolean validGrade = false;
                for (String vg : validGrades) {
                    if (vg.equals(grade.getGrade())) {
                        validGrade = true;
                        break;
                    }
                }

                if (!validGrade) {
                    System.out.println("Invalid grade value: " + grade.getGrade());
                    continue;
                }

                if (gradeOps.addGrade(grade)) {
                    importedCount++;
                } else {
                    System.out.println("Failed to import grade");
                }
            }

            input.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filePath);
            e.printStackTrace();
        } catch (EOFException e) {
            System.out.println("End of file reached");
        } catch (IOException e) {
            System.out.println("Error reading file: " + filePath);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found error");
            e.printStackTrace();
        }

        return importedCount;
    }
}
