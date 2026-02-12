package com.example.quiz_1141121.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.quiz_1141121.req.CreateReq;
import com.example.quiz_1141121.req.DeleteReq;
import com.example.quiz_1141121.req.FillinReq;
import com.example.quiz_1141121.req.UpdateReq;
import com.example.quiz_1141121.res.BasicRes;
import com.example.quiz_1141121.res.CreateRes;
import com.example.quiz_1141121.res.GetQuestionRes;
import com.example.quiz_1141121.res.GetQuizRes;
import com.example.quiz_1141121.res.UpdateRes;
import com.example.quiz_1141121.service.FillinService;
import com.example.quiz_1141121.service.QuizService;

@CrossOrigin
@RestController
public class QuizController {

	@Autowired
	private QuizService quizService;
	
	@Autowired
	private FillinService fillinService;
	
	@PostMapping("quiz/create")
	public CreateRes create(@RequestBody CreateReq req) {
		return quizService.create(req);
	}
	
	@GetMapping("quiz/getAll")
	public GetQuizRes getQuizList() {
		return quizService.getQuizList();
	}
	
	/*API 的路徑: http://localhost:8080/quiz/get_question_list?quizId=1 
	 * ? 後面的 quizId 指的是 @RequestParam 中的字串值 */
	@GetMapping("quiz/get_question_list")
	public GetQuestionRes getQuestionList(@RequestParam("quizId") int quizId) {
		return quizService.getQuestionList(quizId);
	}
	
	@PostMapping("quiz/update")
	public UpdateRes update(@RequestBody UpdateReq req) {
		return quizService.update(req);
	}
	
	@PostMapping("quiz/delete")
	public BasicRes delete(@RequestBody DeleteReq req) {
		return quizService.delete(req);
	}
	
	/*API 的路徑: http://localhost:8080/quiz/delete?quizId=1*/
	@GetMapping("quiz/delete")
	public BasicRes delete(@RequestParam("quizId") int quizId) {
		return quizService.delete(quizId);
	}
	
	@PostMapping("quiz/fillin")
	public BasicRes fillin(@RequestBody FillinReq req) {
		return fillinService.fillin(req);
	}
}
