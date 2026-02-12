package com.example.quiz_1141121.res;

import java.util.List;

import com.example.quiz_1141121.entity.Quiz;

public class GetQuizRes extends BasicRes {

	private List<Quiz> quizList;

	public GetQuizRes() {
		super();
	}

	public GetQuizRes(int code, String message) {
		super(code, message);
	}

	public GetQuizRes(int code, String message, List<Quiz> quizList) {
		super(code, message);
		this.quizList = quizList;
	}

	public List<Quiz> getQuizList() {
		return quizList;
	}

	public void setQuizList(List<Quiz> quizList) {
		this.quizList = quizList;
	}

}
