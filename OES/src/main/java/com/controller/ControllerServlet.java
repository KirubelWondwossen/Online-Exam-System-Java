package com.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.helper.DatabaseClass;
import com.helper.GEmailSender;
import com.helper.OTP;
import com.helper.RandomIdGenerator;
import com.helper.DateFormat;
import com.helper.EmailConfig;
import com.entity.User;
import com.entity.Role;
import com.entity.Student;

public class ControllerServlet extends HttpServlet {
    
    private DatabaseClass DAO;
    
    @Override
    public void init() throws ServletException {
        DAO = new DatabaseClass();
        
        // Test email configuration on startup
        testEmailConfiguration();
    }
    
    private void testEmailConfiguration() {
        try {
            GEmailSender emailSender = new GEmailSender();
            
            if (EmailConfig.isConfigured()) {
                System.out.println("=== Email Configuration Test ===");
                boolean testSuccess = emailSender.testEmailConfiguration(EmailConfig.getFromEmail());
                
                if (testSuccess) {
                    System.out.println("✅ Email configuration test PASSED");
                    System.out.println("Email system is ready for use");
                } else {
                    System.out.println("❌ Email configuration test FAILED");
                    System.out.println("Please check your email settings and restart the server");
                }
                System.out.println("===============================");
            } else {
                System.out.println("⚠️  Email configuration is incomplete");
                System.out.println("Please set EMAIL_USERNAME and EMAIL_PASSWORD environment variables");
                EmailConfig.logConfiguration();
            }
        } catch (Exception e) {
            System.err.println("Error testing email configuration: " + e.getMessage());
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pageAction = request.getParameter("page");
        
        if ("NewUser".equals(pageAction)) {
            handleNewUser(request, response);
        } else if ("New1User".equals(pageAction)) {
            handleOTPVerification(request, response);
        } else if ("LoginUser".equals(pageAction)) {
            handleUserLogin(request, response);
        } else if ("LoginStudent".equals(pageAction)) {
            handleStudentLogin(request, response);
        } else if ("CreateInstructor".equals(pageAction)) {
            handleCreateInstructor(request, response);
        } else if ("CreateStudent".equals(pageAction)) {
            handleCreateStudent(request, response);
        } else if ("logout".equals(pageAction)) {
            handleLogout(request, response);
        } else {
            response.sendRedirect("index.jsp");
        }
    }
    
    private void handleNewUser(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // This method is now deprecated - public signup is disabled
        // Redirect to login page with message
        response.sendRedirect("User-Login.jsp?msg=public_signup_disabled");
    }
    
    private void handleOTPVerification(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String id = RandomIdGenerator.generateRandomString();
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String phoneNo = request.getParameter("phone_no");
        String password = request.getParameter("password");
        String otp = request.getParameter("otp");
        String userOtp = request.getParameter("votp");
        
        if (otp != null && otp.equals(userOtp)) {
            
            User user = new User();
            user.setId(id);
            user.setUsername(username);
            user.setEmail(email);
            user.setPhone_no(phoneNo);
            user.setPassword(password);
            user.setCreated_Date(DateFormat.getCurrentDate());
            
            boolean saved = DAO.saveUser(user);
            
            if (saved) {
                response.sendRedirect("User-Login.jsp?msg=successfully");
            } else {
                response.sendRedirect("User-Login.jsp?msg=unsuccessfully");
            }
        } else {
            response.sendRedirect("User-Login.jsp?msg=OTPisincorrect");
        }
    }
    
    private void handleUserLogin(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        User user = DAO.getUserByEmailAndPassword(email, password);
        
        if (user == null) {
            request.getRequestDispatcher("User-Login.jsp?msg=unsuccessfully1")
                   .include(request, response);
        } else {
            HttpSession session = request.getSession();
            session.setAttribute("UserStatus", "1");
            session.setAttribute("UserId", user.getId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("Role", user.getRole().toString());
            
            if (user.getRole() == Role.ADMIN || user.getRole() == Role.INSTRUCTOR) {
                response.sendRedirect("User-Page.jsp?pg=1");
            } else {
                response.sendRedirect("stud-Page.jsp?spg=1");
            }
        }
    }
    
    private void handleStudentLogin(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        // Use existing DAO method for validation
        boolean isValid = DAO.studLoginValidate(email, password);
        
        if (!isValid) {
            request.getRequestDispatcher("Student-Login.jsp?msg=unsuccessfully1")
                   .include(request, response);
        } else {
            // Get student ID using existing method, then get details
            String studentId = DAO.getstudId(email, password);
            Student student = DAO.getstudDetails(studentId);
            
            HttpSession session = request.getSession();
            session.setAttribute("studStatus", "1");
            session.setAttribute("studId", student.getStudentid());
            session.setAttribute("username", student.getFirstname());
            response.sendRedirect("stud-Page.jsp?spg=1");
        }
    }
    
    private void handleLogout(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        HttpSession session = request.getSession();
        session.invalidate();
        response.sendRedirect("index.jsp");
    }
    
    private void handleCreateInstructor(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        
        // Check if user is logged in and has ADMIN role
        HttpSession session = request.getSession();
        String userRole = (String) session.getAttribute("Role");
        
        if (userRole == null || !"ADMIN".equals(userRole)) {
            response.sendRedirect("Create-Instructor.jsp?msg=unauthorized");
            return;
        }
        
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String phoneNo = request.getParameter("phone_no");
        String password = request.getParameter("password");
        
        // Check if email already exists
        if (!DAO.UserValidate(email)) {
            response.sendRedirect("Create-Instructor.jsp?msg=Already");
            return;
        }
        
        // Create instructor with INSTRUCTOR role
        String id = RandomIdGenerator.generateRandomString();
        User instructor = new User();
        instructor.setId(id);
        instructor.setUsername(username);
        instructor.setEmail(email);
        instructor.setPhone_no(phoneNo);
        instructor.setPassword(password);
        instructor.setCreated_Date(DateFormat.getCurrentDate());
        instructor.setRole(Role.INSTRUCTOR); // Automatically assign INSTRUCTOR role
        
        boolean saved = DAO.saveUser(instructor);
        
        if (saved) {
            // Send welcome email
            sendWelcomeEmail(email, username, password, "INSTRUCTOR");
            response.sendRedirect("Create-Instructor.jsp?msg=successfully");
        } else {
            response.sendRedirect("Create-Instructor.jsp?msg=unsuccessfully");
        }
    }
    
    private void handleCreateStudent(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        
        // Check if user is logged in and has INSTRUCTOR role
        HttpSession session = request.getSession();
        String userRole = (String) session.getAttribute("Role");
        
        if (userRole == null || !"INSTRUCTOR".equals(userRole)) {
            response.sendRedirect("Create-Student.jsp?msg=unauthorized");
            return;
        }
        
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String phoneNo = request.getParameter("phone_no");
        String password = request.getParameter("password");
        
        // Check if email already exists
        if (!DAO.UserValidate(email)) {
            response.sendRedirect("Create-Student.jsp?msg=Already");
            return;
        }
        
        // Create student with STUDENT role
        String id = RandomIdGenerator.generateRandomString();
        User student = new User();
        student.setId(id);
        student.setUsername(username);
        student.setEmail(email);
        student.setPhone_no(phoneNo);
        student.setPassword(password);
        student.setCreated_Date(DateFormat.getCurrentDate());
        student.setRole(Role.STUDENT); // Automatically assign STUDENT role
        
        boolean saved = DAO.saveUser(student);
        
        if (saved) {
            // Send welcome email
            sendWelcomeEmail(email, username, password, "STUDENT");
            response.sendRedirect("Create-Student.jsp?msg=successfully");
        } else {
            response.sendRedirect("Create-Student.jsp?msg=unsuccessfully");
        }
    }
    
    private void sendWelcomeEmail(String email, String username, String password, String role) {
        try {
            GEmailSender emailSender = new GEmailSender();
            String fromEmail = EmailConfig.getFromEmail();
            String subject = "Welcome to Online Examination System";
            
            String messageText = "Dear " + username + ",\n\n" +
                               "Your " + role + " account has been created successfully.\n\n" +
                               "Login Details:\n" +
                               "Email: " + email + "\n" +
                               "Password: " + password + "\n\n" +
                               "Please change your password after first login.\n\n" +
                               "Regards,\n" +
                               "Online Examination System";
            
            boolean sent = emailSender.sendEmail(email, fromEmail, subject, messageText);
            
            if (sent) {
                System.out.println("Welcome email sent to: " + email);
            } else {
                System.err.println("Failed to send welcome email to: " + email);
            }
        } catch (Exception e) {
            System.err.println("Error sending welcome email: " + e.getMessage());
        }
    }
}
