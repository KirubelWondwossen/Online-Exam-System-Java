<%@page import="com.helper.*"%>
<%@page import="com.entity.*"%>
<%@page import="javax.servlet.http.Part"%>
<%@page import="java.util.List"%>
<%
DatabaseClass DAO = new DatabaseClass();
String examId = request.getParameter("examId");
%>
<%if(session.getAttribute("UserStatus")!=null){
	if(session.getAttribute("UserStatus").equals("1")){ 
		String id = (String) session.getAttribute("UserId");
		User user=DAO.getUserDetails(id);
%>
<!-- --------------------------main body ---------------------------------------------->

<div class="main-body">
	<div class="main-containt">
		<div class="small-containers">
			<div class="dash-board container-padding">
				<div class="flex-div-center">
					<span>View Exam Attempts</span>
				</div>
				<a href="">Today is
					<span id="day">day</span>,
					<span id="daynum">00</span>
					<span id="month">month</span>
					<span id="year">0000</span>
					<span class="display-time"></span>
				</a>
			</div>
			<hr>
		</div>

		<!-- Exam Selection -->
		<div class="small-containers batchlist" id="batchlist">
			<div class="student-containt container-padding">
				<div><i class="fa fa-list" aria-hidden="true"></i>
					<span>Select Exam</span>
				</div>
				<div>
					<select name="exam-dropdown" class="batch-dropdown" id="exam" onchange="location = this.value;">
						<option value="View-Exam-Attempts.jsp">--- Select Exam ---</option>
						<% 
						List<Exam> Listexam = DAO.getAllexam(id);
						if(Listexam != null) {
							for(Exam exam : Listexam) {
								String selected = (examId != null && examId.equals(exam.getExamid())) ? "selected" : "";
						%>
							<option value="View-Exam-Attempts.jsp?examId=<%=exam.getExamid()%>" <%=selected%>>
								<%=exam.getExamtitle()%>
							</option>
						<%	}
						} %>
					</select>
				</div>
			</div>
			<hr>
		</div>

		<!-- Attempts Table -->
		<% if(examId != null && !examId.trim().isEmpty()) {
			List<ExamAttempt> attempts = DAO.getSubmittedAttemptsByExam(examId);
		%>
			<div class="small-containers">
				<div class="student-containt container-padding">
					<div><i class="fa fa-list" aria-hidden="true"></i>
						<span>Exam Attempts</span>
					</div>
				</div>
				<hr>
				<div class="student-containt-list container-padding">
					<% if(attempts != null && !attempts.isEmpty()) { %>
						<table>
							<tr>
								<th>Sr. No.</th>
								<th>Student ID</th>
								<th>Attempt ID</th>
								<th>Start Time</th>
								<th>End Time</th>
								<th>Status</th>
								<th>Actions</th>
							</tr>
							<% 
							int j = 0;
							for(ExamAttempt attempt : attempts) {
								if(attempt != null) {
							%>
								<tr>
									<td><%=j+1 %></td>
									<td><%=attempt.getStudentId() != null ? attempt.getStudentId() : "N/A" %></td>
									<td><%=attempt.getAttemptId() != null ? attempt.getAttemptId() : "N/A" %></td>
									<td><%=attempt.getStartTime() != null ? attempt.getStartTime().toString() : "N/A" %></td>
									<td><%=attempt.getEndTime() != null ? attempt.getEndTime().toString() : "N/A" %></td>
									<td><%=attempt.getStatus() != null ? attempt.getStatus() : "N/A" %></td>
									<td>
										<a href="Grade-Short-Answers.jsp?examId=<%=examId%>&attemptId=<%=attempt.getAttemptId()%>" 
										   style="border: black solid 1px; padding: 4px 15px; margin-right: 5px;">
											View Answers
										</a>
										<% if("SUBMITTED".equals(attempt.getStatus())) { %>
											<a href="Grade-Short-Answers.jsp?examId=<%=examId%>&attemptId=<%=attempt.getAttemptId()%>" 
											   style="border: black solid 1px; padding: 4px 15px; background-color: #4CAF50; color: white;">
												Grade
											</a>
										<% } %>
									</td>
								</tr>
							<%
								j++;
								}
							}
							%>
						</table>
					<% } else { %>
						<p>No submitted attempts found for this exam.</p>
					<% } %>
				</div>
				<hr>
			</div>
		<% } else { %>
			<div class="small-containers">
				<div class="student-containt-list container-padding">
					<p>Please select an exam to view attempts.</p>
				</div>
				<hr>
			</div>
		<% } %>

	</div>
</div>

<%
	}else{
		response.sendRedirect("index.jsp");
	}
}else{
	response.sendRedirect("index.jsp");
}
%>

