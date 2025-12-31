<%@ page import="java.sql.Date" %> <%@ page import="java.util.List" %> <%@ page
import="com.helper.*" %> <%@ page import="com.entity.*" %> <% DatabaseClass DAO
= new DatabaseClass(); String pageAction = request.getParameter("page"); /*
===================== NEW USER (SEND OTP) ===================== */ if
("NewUser".equals(pageAction)) { String username =
request.getParameter("username"); String email = request.getParameter("email");
String phone_no = request.getParameter("phone_no"); String password =
request.getParameter("password"); GEmailSender gEmailSender = new
GEmailSender(); String from = "systemonlineexamination@gmail.com"; String
subject = "OTP Verification"; if (DAO.UserValidate(email)) { OTP otp = new
OTP(); String otpmessage = otp.generateOTP(6); String text = "Your One Time
Password is " + otpmessage + "\n\nVerify your account using this OTP."; boolean
sent = gEmailSender.sendEmail(email, from, subject, text); if (sent) {
response.sendRedirect( "sample.jsp?email=" + email + "&otp=" + otpmessage +
"&username=" + username + "&password=" + password + "&phone=" + phone_no ); }
else { response.sendRedirect("sample.jsp?msg=unsuccessfully"); } } else {
response.sendRedirect("User-Login.jsp?msg=Already"); } } /*
===================== VERIFY OTP & CREATE USER ===================== */ else if
("New1User".equals(pageAction)) { String id =
RandomIdGenerator.generateRandomString(); String username =
request.getParameter("username"); String email = request.getParameter("email");
String phone_no = request.getParameter("phone_no"); String password =
request.getParameter("password"); String otp = request.getParameter("otp");
String votp = request.getParameter("votp"); if (otp != null && otp.equals(votp))
{ User user = new User(); user.setId(id); user.setUsername(username);
user.setEmail(email); user.setPhone_no(phone_no); user.setPassword(password);
user.setCreated_Date(DateFormat.getCurrentDate()); if (DAO.saveUser(user)) {
response.sendRedirect("User-Login.jsp?msg=successfully"); } else {
response.sendRedirect("User-Login.jsp?msg=unsuccessfully"); } } else {
response.sendRedirect("User-Login.jsp?msg=OTPisincorrect"); } } /*
===================== USER LOGIN ===================== */ else if
("LoginUser".equals(pageAction)) { String email = request.getParameter("email");
String password = request.getParameter("password"); User user =
DAO.getUserByEmailAndPassword(email, password); if (user == null) {
request.getRequestDispatcher("User-Login.jsp?msg=unsuccessfully1")
.include(request, response); } else { session.setAttribute("UserStatus", "1");
session.setAttribute("UserId", user.getId()); session.setAttribute("username",
user.getUsername()); session.setAttribute("Role", user.getRole().toString());
switch (user.getRole()) { case ADMIN: case INSTRUCTOR:
response.sendRedirect("User-Page.jsp?pg=1"); break; default:
response.sendRedirect("stud-Page.jsp?spg=1"); } } } /* =====================
LOGOUT ===================== */ else if ("logout".equals(pageAction)) {
session.invalidate(); response.sendRedirect("index.jsp"); } %>
