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
String sid = (String) session.getAttribute("studId");
%>

<!-- --------------------------main body ---------------------------------------------->

<div class="main-body">
	<div class="main-containt">
		<div class="small-containers">
			<div class="dash-board container-padding">
				<div class="flex-div-center">
					<span class="material-symbols-outlined">list_alt</span> <span>Results</span>
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
					<span>My Results</span>
				</div>
			</div>
			<hr>
			<div class="student-containt-list container-padding">
				<% 
				if(sid != null) {
					List<Result> results = DAO.getStudentResults(sid);
					if(results != null && !results.isEmpty()) {
				%>
					<table>
						<tr>
							<th>Sr. No.</th>
							<th>Exam Title</th>
							<th>Marks</th>
							<th>Total Marks</th>
							<th>Percentage</th>
							<th>Status</th>
							<th>Action</th>
						</tr>
						<% 
						int j = 0;
						for(Result result : results) {
							if(result != null) {
								Exam exam = null;
								try {
									exam = DAO.getexamDetails(result.getExamid());
								} catch(Exception e) {
									// Handle error silently
								}
								
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
							<tr>
								<td><%=j+1 %></td>
								<td><%=exam != null && exam.getExamtitle() != null ? exam.getExamtitle() : "N/A" %></td>
								<td><%=result.getMarks() != null ? result.getMarks() : "0" %></td>
								<td><%=result.getTotalmarks() != null ? result.getTotalmarks() : "0" %></td>
								<td><%=percentage %>%</td>
								<td>Evaluated</td>
								<td>
									<a href="stud-Result-Details.jsp?resultId=<%=result.getResultid() != null ? result.getResultid() : ""%>">
										<button>View Details</button>
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
					<p>No results available</p>
				<% } 
				} else { %>
					<p>No results available</p>
				<% } %>
			</div>
			<hr>
		</div>
	</div>
</div>

