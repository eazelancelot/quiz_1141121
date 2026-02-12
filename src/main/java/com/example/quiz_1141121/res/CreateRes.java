package com.example.quiz_1141121.res;

public class CreateRes extends BasicRes {

	private int questionId;

	public CreateRes() {
		super();
	}

	public CreateRes(int code, String message) {
		super(code, message);
	}

	public CreateRes(int code, String message, int questionId) {
		super(code, message);
		this.questionId = questionId;
	}

	public int getQuestionId() {
		return questionId;
	}

	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}

}
