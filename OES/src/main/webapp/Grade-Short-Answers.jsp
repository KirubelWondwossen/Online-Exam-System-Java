<%@page import="com.helper.*"%>
<%@page import="com.entity.*"%>
<%@page import="javax.servlet.http.Part"%>
<%@page import="java.util.List"%>
<%
DatabaseClass DAO = new DatabaseClass();
String examId = request.getParameter("examId");
String attemptId = request.getParameter("attemptId");
String msg = request.getParameter("msg");
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
					<span>Grade Short Answer Questions</span>
				</div>
			</div>
			<hr>
		</div>

		<!-- Success/Error Messages -->
		<% if(msg != null) { %>
			<div style="padding: 10px; margin: 10px 0; background-color: <%=msg.contains("success") ? "#d4edda" : "#f8d7da"%>; color: <%=msg.contains("success") ? "#155724" : "#721c24"%>; border: 1px solid <%=msg.contains("success") ? "#c3e6cb" : "#f5c6cb"%>;">
				<%=msg.equals("success") ? "Answer graded successfully!" : "Error grading answer. Please try again." %>
			</div>
		<% } %>

		<!-- Exam Selection -->
		<div class="small-containers batchlist" id="batchlist">
			<div class="student-containt container-padding">
				<div><i class="fa fa-list" aria-hidden="true"></i>
					<span>Select Exam</span>
				</div>
				<div>
					<select name="exam-dropdown" class="batch-dropdown" id="exam" onchange="location = this.value;">
						<option value="Grade-Short-Answers.jsp">--- Select Exam ---</option>
						<% 
						List<Exam> Listexam = DAO.getAllexam(id);
						if(Listexam != null) {
							for(Exam exam : Listexam) {
								String selected = (examId != null && examId.equals(exam.getExamid())) ? "selected" : "";
						%>
							<option value="Grade-Short-Answers.jsp?examId=<%=exam.getExamid()%>" <%=selected%>>
								<%=exam.getExamtitle()%>
							</option>
						<%	}
						} %>
					</select>
				</div>
			</div>
			<hr>
		</div>

		<!-- Submitted Attempts List -->
		<% if(examId != null && !examId.trim().isEmpty() && attemptId == null) { 
			List<ExamAttempt> attempts = DAO.getSubmittedAttemptsByExam(examId);
		%>
			<div class="small-containers">
				<div class="student-containt container-padding">
					<div><i class="fa fa-list" aria-hidden="true"></i>
						<span>Submitted Attempts</span>
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
								<th>End Time</th>
								<th>Action</th>
							</tr>
							<% 
							int j = 0;
							for(ExamAttempt attempt : attempts) {
								if(attempt != null) {
									Student student = null;
									try {
										student = DAO.getStudentDetails(attempt.getStudentId());
									} catch(Exception e) {
										// Handle error silently
									}
							%>
								<tr>
									<td><%=j+1 %></td>
									<td><%=attempt.getStudentId() != null ? attempt.getStudentId() : "N/A" %></td>
									<td><%=attempt.getAttemptId() != null ? attempt.getAttemptId() : "N/A" %></td>
									<td><%=attempt.getEndTime() != null ? attempt.getEndTime().toString() : "N/A" %></td>
									<td>
										<a href="Grade-Short-Answers.jsp?examId=<%=examId%>&attemptId=<%=attempt.getAttemptId()%>">
											<button>Grade</button>
										</a>
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
		<% } %>

		<!-- Short Answer Grading View -->
		<% if(attemptId != null && !attemptId.trim().isEmpty()) {
			List<Answer> shortAnswers = DAO.getShortAnswerAnswers(attemptId);
		%>
			<div class="small-containers">
				<div class="student-containt container-padding">
					<div><i class="fa fa-edit" aria-hidden="true"></i>
						<span>Grade Short Answer Questions</span>
					</div>
					<div>
						<a href="Grade-Short-Answers.jsp?examId=<%=examId != null ? examId : ""%>">
							<button>Back to Attempts</button>
						</a>
					</div>
				</div>
				<hr>
				<div class="student-containt-list container-padding">
					<% if(shortAnswers != null && !shortAnswers.isEmpty()) { %>
						<table>
							<tr>
								<th>Sr. No.</th>
								<th>Question</th>
								<th>Student Answer</th>
								<th>Current Mark</th>
								<th>Assign Mark</th>
								<th>Action</th>
							</tr>
							<% 
							int k = 0;
							for(Answer answer : shortAnswers) {
								if(answer != null) {
									Question question = null;
									try {
										question = DAO.getQuestionByAnswerId(answer.getAnsid());
									} catch(Exception e) {
										// Handle error silently
									}
							%>
								<tr>
									<td><%=k+1 %></td>
									<td>
										<% if(question != null) { %>
											<strong><%=question.getQues() != null ? question.getQues() : "N/A" %></strong>
											<% if(question.getQdesc() != null && !question.getQdesc().trim().isEmpty()) { %>
												<br><small><%=question.getQdesc() %></small>
											<% } %>
										<% } else { %>
											Question not found
										<% } %>
									</td>
									<td><%=answer.getOpt() != null ? answer.getOpt() : "No answer provided" %></td>
									<td><%=answer.getMark() != null ? answer.getMark() : "0" %></td>
									<td>
										<form action="Controller.jsp" method="post" style="display: inline;">
											<input type="hidden" name="page" value="gradeShortAnswer">
											<input type="hidden" name="answerId" value="<%=answer.getAnsid()%>">
											<input type="hidden" name="examId" value="<%=examId != null ? examId : ""%>">
											<input type="hidden" name="attemptId" value="<%=attemptId != null ? attemptId : ""%>">
											<input type="number" name="mark" value="<%=answer.getMark() != null ? answer.getMark() : "0"%>" 
												min="0" step="0.5" required style="width: 80px;">
											<input type="submit" value="Submit">
										</form>
									</td>
									<td>
										<% if(answer.getMark() != null && !answer.getMark().equals("0")) { %>
											<span style="color: green;">Graded</span>
										<% } else { %>
											<span style="color: orange;">Pending</span>
										<% } %>
									</td>
								</tr>
							<%
								k++;
								}
							}
							%>
						</table>
					<% } else { %>
						<p>No short answer questions found for this attempt.</p>
					<% } %>
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

