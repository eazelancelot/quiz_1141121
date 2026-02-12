package com.example.quiz_1141121.req;

import com.example.quiz_1141121.entity.Question;

/* 一個 AnswerVo 表示一題問題的所有資訊以及答案 */
public class AnswerVo {

	private Question question;

	/* 答案，也是選項 */
	private String answer;

	public AnswerVo() {
		super();
	}

	public AnswerVo(Question question, String answer) {
		super();
		this.question = question;
		this.answer = answer;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

}
