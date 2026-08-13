package academicmanagmentsystem.model;

import java.util.Date;

public class Admin extends User {
    private static final long serialVersionUID = 1L;

    private String adminId;
    private int accessLevel;

    public Admin() {
       
    }

    public Admin(int userId, String username, String password, String role, String firstName, String lastName, String email, Date createdDate, String adminId, int accessLevel) {
        super(userId, username, password, role, firstName, lastName, email, createdDate);
        this.adminId = adminId;
        this.accessLevel = accessLevel;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
    }

    @Override
    public String getDashboardInfo() {
        return "Admin Dashboard - " + getFirstName() + " " + getLastName() +
               " | Access Level: " + accessLevel +
               " | System Administrator";
    }

    @Override
    public String toString() {
        return "Admin{" +
                "adminId='" + adminId + '\'' +
                ", accessLevel=" + accessLevel +
                ", " + super.toString() +
                '}';
    }
}
