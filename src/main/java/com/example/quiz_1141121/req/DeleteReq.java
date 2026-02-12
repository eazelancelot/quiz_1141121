package com.example.quiz_1141121.req;

import java.util.List;

public class DeleteReq {

	private List<Integer> quizIdList;

	public DeleteReq() {
		super();
	}

	public DeleteReq(List<Integer> quizIdList) {
		super();
		this.quizIdList = quizIdList;
	}

	public List<Integer> getQuizIdList() {
		return quizIdList;
	}

	public void setQuizIdList(List<Integer> quizIdList) {
		this.quizIdList = quizIdList;
	}

}
