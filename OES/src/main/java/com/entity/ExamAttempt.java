package com.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "exam_attempt")
public class ExamAttempt {
	@Id
	@NotNull
	@Column(name = "attemptId")
	private String attemptId;
	
	@NotNull
	@Column(name = "examId")
	private String examId;
	
	@NotNull
	@Column(name = "studentId")
	private String studentId;
	
	@NotNull
	@Column(name = "startTime")
	private LocalDateTime startTime;
	
	@Column(name = "endTime")
	private LocalDateTime endTime;
	
	@Column(name = "questionIds", length = 2000)
	private String questionIds;
	
	@NotNull
	@Column(name = "status")
	private String status;

	public String getAttemptId() {
		return attemptId;
	}

	public void setAttemptId(String attemptId) {
		this.attemptId = attemptId;
	}

	public String getExamId() {
		return examId;
	}

	public void setExamId(String examId) {
		this.examId = examId;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public String getQuestionIds() {
		return questionIds;
	}

	public void setQuestionIds(String questionIds) {
		this.questionIds = questionIds;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ExamAttempt(String attemptId, String examId, String studentId, LocalDateTime startTime, LocalDateTime endTime,
			String questionIds, String status) {
		super();
		this.attemptId = attemptId;
		this.examId = examId;
		this.studentId = studentId;
		this.startTime = startTime;
		this.endTime = endTime;
		this.questionIds = questionIds;
		this.status = status;
	}

	public ExamAttempt() {
		super();
		// TODO Auto-generated constructor stub
	}
}

