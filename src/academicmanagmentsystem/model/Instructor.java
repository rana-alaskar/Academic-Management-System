package academicmanagmentsystem.model;

import java.util.Date;

public class Instructor extends User {
    private static final long serialVersionUID = 1L;

    private String instructorId;
    private String department;
    private String officeLocation;
    private String phoneExtension;
    private String specialization;

    public Instructor() {
        
    }

    public Instructor(int userId, String username, String password, String role, String firstName, String lastName, String email, Date createdDate, String instructorId, String department, String officeLocation, String phoneExtension, String specialization) {
        super(userId, username, password, role, firstName, lastName, email, createdDate);
        this.instructorId = instructorId;
        this.department = department;
        this.officeLocation = officeLocation;
        this.phoneExtension = phoneExtension;
        this.specialization = specialization;
    }

    public String getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getOfficeLocation() {
        return officeLocation;
    }

    public void setOfficeLocation(String officeLocation) {
        this.officeLocation = officeLocation;
    }

    public String getPhoneExtension() {
        return phoneExtension;
    }

    public void setPhoneExtension(String phoneExtension) {
        this.phoneExtension = phoneExtension;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    @Override
    public String getDashboardInfo() {
        return "Instructor Dashboard - Dr. " + getFirstName() + " " + getLastName() +
               " | Department: " + department + " | Office: " + officeLocation +
               " | Specialization: " + specialization;
    }

    @Override
    public String toString() {
        return "Instructor{" +
                "instructorId='" + instructorId + '\'' +
                ", department='" + department + '\'' +
                ", officeLocation='" + officeLocation + '\'' +
                ", phoneExtension='" + phoneExtension + '\'' +
                ", specialization='" + specialization + '\'' +
                ", " + super.toString() +
                '}';
    }
}
