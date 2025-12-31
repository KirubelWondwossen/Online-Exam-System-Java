<%-- prettier-ignore --%>
<%
response.setHeader("Cache-Control","no-store");
response.setHeader("Pragma","no-cache"); 
response.setHeader ("Expires", "0"); //prevents caching at the proxy server
%>
<%@page import="com.helper.*"%>
<%@page import="com.entity.*"%>
<%@page import="javax.servlet.http.Part"%>
<%@page import="java.util.List"%>
<%
DatabaseClass DAO = new DatabaseClass();
String resultId = request.getParameter("resultId");
String sid = (String) session.getAttribute("studId");
%>

<!-- --------------------------main body ---------------------------------------------->

<div class="main-body">
	<div class="main-containt">
		<div class="small-containers">
			<div class="dash-board container-padding">
				<div class="flex-div-center">
					<span class="material-symbols-outlined">list_alt</span> <span>Result Details</span>
				</div>
				<a href="">Today is <span id="day">day</span>, <span id="daynum">00</span>
					<span id="month">month</span> <span id="year">0000</span> <span
					class="display-time"></span>
				</a>
			</div>
			<hr>
		</div>
		
		<div class="small-containers">
			<div class="student-containt container-padding">
				<div><i class="fa fa-list" aria-hidden="true"></i>
					<span>Exam Result Details</span>
				</div>
				<div>
					<a href="stud-Page.jsp?spg=2">
						<button>Back to Results</button>
					</a>
				</div>
			</div>
			<hr>
			<div class="student-containt-list container-padding">
				<% 
				if(resultId != null && !resultId.trim().isEmpty()) {
					Result result = DAO.resultdetail(resultId);
					if(result != null) {
						Exam exam = null;
						ExamAttempt examAttempt = null;
						String attemptId = null;
						
						try {
							exam = DAO.getexamDetails(result.getExamid());
						} catch(Exception e) {
							// Handle error silently
						}
						
						// Get answers to find attemptId
						List<Answer> tempAnswers = null;
						try {
							tempAnswers = DAO.getans(result.getExamid(), result.getStudid());
							if(tempAnswers != null && !tempAnswers.isEmpty()) {
								attemptId = tempAnswers.get(0).getAttemptId();
							}
						} catch(Exception e) {
							// Handle error silently
						}
						
						// Get ExamAttempt if attemptId found
						if(attemptId != null) {
							try {
								examAttempt = DAO.getExamAttempt(attemptId);
							} catch(Exception e) {
								// Handle error silently
							}
						}
						
						// Get all answers for the attempt
						List<Answer> answers = null;
						if(attemptId != null) {
							try {
								answers = DAO.getAnswersByAttemptId(attemptId);
							} catch(Exception e) {
								// Handle error silently
							}
						}
						
						if(answers != null && !answers.isEmpty()) {
				%>
					<!-- Result Summary -->
					<div style="margin-bottom: 20px; padding: 15px; background-color: #f5f5f5; border-radius: 5px;">
						<h3>Result Summary</h3>
						<p><strong>Exam:</strong> <%=exam != null && exam.getExamtitle() != null ? exam.getExamtitle() : "N/A" %></p>
						<p><strong>Marks Obtained:</strong> <%=result.getMarks() != null ? result.getMarks() : "0" %></p>
						<p><strong>Total Marks:</strong> <%=result.getTotalmarks() != null ? result.getTotalmarks() : "0" %></p>
						<% 
						int marks = 0;
						int totalMarks = 0;
						int percentage = 0;
						try {
							if(result.getMarks() != null && !result.getMarks().trim().isEmpty()) {
								marks = Integer.parseInt(result.getMarks().trim());
							}
							if(result.getTotalmarks() != null && !result.getTotalmarks().trim().isEmpty()) {
								totalMarks = Integer.parseInt(result.getTotalmarks().trim());
							}
							if(totalMarks > 0) {
								percentage = (marks * 100) / totalMarks;
							}
						} catch(NumberFormatException e) {
							// Handle error silently
						}
						%>
						<p><strong>Percentage:</strong> <%=percentage %>%</p>
						<p><strong>Status:</strong> <%=result.getExstatus() != null ? result.getExstatus() : "N/A" %></p>
					</div>
					
					<!-- Answers Table -->
					<table>
						<tr>
							<th>Sr. No.</th>
							<th>Question</th>
							<th>Your Answer</th>
							<th>Correct Answer</th>
							<th>Marks</th>
						</tr>
						<% 
						int j = 0;
						for(Answer answer : answers) {
							if(answer != null) {
								Question question = null;
								try {
									question = DAO.getQuestionByAnswerId(answer.getAnsid());
								} catch(Exception e) {
									// Handle error silently
								}
								
								if(question != null) {
									QuestionType questionType = question.getQuestionType();
						%>
							<tr>
								<td><%=j+1 %></td>
								<td>
									<strong><%=question.getQues() != null ? question.getQues() : "N/A" %></strong>
									<% if(question.getQdesc() != null && !question.getQdesc().trim().isEmpty()) { %>
										<br><small><%=question.getQdesc() %></small>
									<% } %>
								</td>
								<td><%=answer.getOpt() != null ? answer.getOpt() : "No answer provided" %></td>
								<td>
									<% if(questionType == QuestionType.MCQ || questionType == QuestionType.TRUE_FALSE) { %>
										<%=question.getAns() != null ? question.getAns() : "N/A" %>
									<% } else if(questionType == QuestionType.SHORT_ANSWER) { %>
										<span style="color: #999;">Not available for short answer questions</span>
									<% } else { %>
										N/A
									<% } %>
								</td>
								<td><%=answer.getMark() != null ? answer.getMark() : "0" %></td>
							</tr>
						<%
								}
								j++;
							}
						}
						%>
					</table>
				<% 
						} else { 
				%>
					<p>No answers found for this result.</p>
				<% 
						}
					} else { 
				%>
					<p>Result not found.</p>
				<% 
					}
				} else { 
				%>
					<p>Invalid result ID.</p>
				<% 
				} 
				%>
			</div>
			<hr>
		</div>
	</div>
</div>

