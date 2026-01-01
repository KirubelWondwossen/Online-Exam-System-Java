<%@ page import="java.sql.Date" %>
<%@ page import="com.helper.*" %>
<%@ page import="com.entity.*" %>

<%
    DatabaseClass DAO = new DatabaseClass();
    String pageAction = request.getParameter("page");

    if ("NewUser".equals(pageAction)) {

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String phone_no = request.getParameter("phone_no");
        String password = request.getParameter("password");

        GEmailSender gEmailSender = new GEmailSender();
        String from = "systemonlineexamination@gmail.com";
        String subject = "OTP Verification";

        if (DAO.UserValidate(email)) {

            OTP otp = new OTP();
            String otpmessage = otp.generateOTP(6);

            // ✅ JSP-SAFE STRING (ONE LINE)
            String text = "Your One Time Password is " + otpmessage +
                          ". Verify your account using this OTP.";

            boolean sent = gEmailSender.sendEmail(email, from, subject, text);

            if (sent) {
                response.sendRedirect(
                    "sample.jsp?email=" + email +
                    "&otp=" + otpmessage +
                    "&username=" + username +
                    "&password=" + password +
                    "&phone=" + phone_no
                );
            } else {
                response.sendRedirect("sample.jsp?msg=unsuccessfully");
            }

        } else {
            response.sendRedirect("User-Login.jsp?msg=Already");
        }
    }
%>
