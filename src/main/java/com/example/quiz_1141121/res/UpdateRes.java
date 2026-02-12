package com.example.quiz_1141121.res;

/*單純只是匹配 update 方法而創建的 class，也可以直接用 CreateRes */
public class UpdateRes extends CreateRes {

	public UpdateRes() {
		super();
	}

	public UpdateRes(int code, String message, int questionId) {
		super(code, message, questionId);
	}

	public UpdateRes(int code, String message) {
		super(code, message);
	}

}
