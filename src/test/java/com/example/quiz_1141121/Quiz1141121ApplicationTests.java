package com.example.quiz_1141121;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.quiz_1141121.entity.Question;
import com.example.quiz_1141121.req.CreateReq;
import com.example.quiz_1141121.service.QuizService;

@SpringBootTest
class Quiz1141121ApplicationTests {
	
	@Autowired
	private QuizService quizService;

	@Test
	void contextLoads() {
		LocalDate startDate = LocalDate.of(02026, 2, 8);
		System.out.println(startDate.isBefore(LocalDate.now()));
		System.out.println(startDate.isAfter(LocalDate.now()));
	}
	
	@Test
	public void test() {
		List<Question> list = new ArrayList<>(List.of(new Question(1, "午餐吃啥?", "Single", //
				true, "AAA")));
		CreateReq req = new CreateReq("每日餐點", "每日餐點", LocalDate.of(2026, 2, 10), //
				LocalDate.of(2026, 2, 10), true, list);
		quizService.create(req);
	}

}
