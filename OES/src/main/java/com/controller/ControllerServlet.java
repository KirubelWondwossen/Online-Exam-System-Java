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
import com.entity.User;
import com.entity.Role;
import com.entity.Student;

public class ControllerServlet extends HttpServlet {
    
    private DatabaseClass DAO;
    
    @Override
    public void init() throws ServletException {
        DAO = new DatabaseClass();
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
        } else if ("logout".equals(pageAction)) {
            handleLogout(request, response);
        } else {
            response.sendRedirect("index.jsp");
        }
    }
    
    private void handleNewUser(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String phoneNo = request.getParameter("phone_no");
        String password = request.getParameter("password");
        
        GEmailSender emailSender = new GEmailSender();
        String fromEmail = "systemonlineexamination@gmail.com";
        String subject = "OTP Verification";
        
        if (DAO.UserValidate(email)) {
            
            OTP otpGenerator = new OTP();
            String otpValue = otpGenerator.generateOTP(6);
            String messageText = "Your One Time Password is " + otpValue + "\\n\\nVerify your account using this OTP.";
            
            boolean sent = emailSender.sendEmail(email, fromEmail, subject, messageText);
            
            if (sent) {
                String redirectUrl = "sample.jsp?email=" + email + "&otp=" + otpValue + 
                                   "&username=" + username + "&password=" + password + 
                                   "&phone=" + phoneNo;
                response.sendRedirect(redirectUrl);
            } else {
                response.sendRedirect("sample.jsp?msg=unsuccessfully");
            }
        } else {
            response.sendRedirect("User-Login.jsp?msg=Already");
        }
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
}
