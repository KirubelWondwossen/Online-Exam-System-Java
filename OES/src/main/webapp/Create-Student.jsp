<!DOCTYPE html>
<html lang="en" dir="ltr">
  <head>
    <meta charset="utf-8" />
    <title>Create Student Account</title>
    <link rel="stylesheet" href="css/User-Login-Register.css" />
    <link rel="stylesheet" href="css/nav.css" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="shortcut icon" type="image/x-icon" href="img/logo2.png" />
  </head>
  <body>
    <nav class="main-nav flex-div">
      <div class="main-nav-left flex-div">
        <i class="fa fa-bars" aria-hidden="true" id="menu-icon"></i>
        <a href="User-Page.jsp?pg=1" class="nav-logo"
          >Online Examination System</a
        >
      </div>
      <div class="main-nav-right flex-div" id="showprofilemenu">
        <p class="user-name"><%=session.getAttribute("username") %></p>
        <span class="role-badge"><%=session.getAttribute("Role") %></span>
        <span class="material-symbols-outlined"> arrow_drop_down </span>
        <span class="material-symbols-outlined"> account_circle </span>
      </div>
    </nav>

    <div class="new">
      <div class="wrapper1">
        <img src="img/20824344_6343825.jpg" alt="Create Student" />
      </div>
      <div class="wrapper2">
        <div class="wrapper">
          <div class="title-text">
            <div class="title login">Create Student Account</div>
          </div>
          <div class="form-container">
            <div class="form-inner">
              <form
                action="<%= request.getContextPath() %>/controller?page=CreateStudent"
                method="post"
                class="login"
              >
                <div class="field">
                  <input
                    type="text"
                    name="username"
                    class="text"
                    placeholder="Student Name"
                    required
                  />
                </div>
                <div class="field">
                  <input
                    type="email"
                    name="email"
                    class="text"
                    placeholder="Email Address"
                    required
                  />
                </div>
                <div class="field">
                  <input
                    type="tel"
                    name="phone_no"
                    class="text"
                    placeholder="Contact Number"
                    required
                    pattern="[0-9]{10}"
                  />
                </div>
                <div class="field">
                  <input
                    type="password"
                    name="password"
                    class="text"
                    placeholder="Temporary Password"
                    required
                  />
                </div>
                <div class="field btn">
                  <div class="btn-layer"></div>
                  <input type="submit" value="Create Student" />
                </div>
                <% if(request.getParameter("msg")!=null) {
                if(request.getParameter("msg").equals("successfully")) { %>
                <div class="signup-link-1">
                  Student account created successfully
                </div>
                <% } if(request.getParameter("msg").equals("unsuccessfully")) {
                %>
                <div class="signup-link-2">
                  Something went wrong. Please try again.
                </div>
                <% } if(request.getParameter("msg").equals("Already")) { %>
                <div class="signup-link-2">Email already registered</div>
                <% } if(request.getParameter("msg").equals("unauthorized")) { %>
                <div class="signup-link-2">Unauthorized access</div>
                <% } } %>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  </body>
</html>
