package com.example.quiz_1141121.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.quiz_1141121.constants.ReplyMessage;
import com.example.quiz_1141121.dao.FillinDao;
import com.example.quiz_1141121.dao.QuestionDao;
import com.example.quiz_1141121.dao.UserDao;
import com.example.quiz_1141121.entity.Fillin;
import com.example.quiz_1141121.entity.Question;
import com.example.quiz_1141121.entity.User;
import com.example.quiz_1141121.req.AnswerVo;
import com.example.quiz_1141121.req.FillinReq;
import com.example.quiz_1141121.res.BasicRes;
import com.example.quiz_1141121.res.FeedbackUserVo;
import com.example.quiz_1141121.res.GetFeedbackUserRes;

@Service
public class FillinService {
	
	@Autowired
	private FillinDao fillinDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private QuestionDao questionDao;
	
	@Transactional(rollbackFor = Exception.class)
	public BasicRes fillin(FillinReq req) {
		BasicRes res = checkParams(req);
		if(res != null) {
			return res;
		}
		/*新增資料 */
		try {
			for(AnswerVo vo : req.getAnswerVoList()) {
				fillinDao.insert(req.getQuizId(), vo.getQuestion().getQuestionId(), req.getEmail(), //
						vo.getAnswer());
			}
		} catch (Exception e) {
			throw e;
		}
		return new BasicRes(ReplyMessage.SUCCESS.getCode(), //
				ReplyMessage.SUCCESS.getMessage());		
	}
	
	private BasicRes checkParams(FillinReq req) {
		if(req.getQuizId() <= 0) {
			return new BasicRes(ReplyMessage.QUIZ_ID_ERROR.getCode(), //
					ReplyMessage.QUIZ_ID_ERROR.getMessage());	
		}
		if(!StringUtils.hasText(req.getEmail())) {
			return new BasicRes(ReplyMessage.USER_EMAIL_ERROR.getCode(), //
					ReplyMessage.USER_EMAIL_ERROR.getMessage());
		}
		if(!StringUtils.hasText(req.getName())) {
			return new BasicRes(ReplyMessage.USER_NAME_ERROR.getCode(), //
					ReplyMessage.USER_NAME_ERROR.getMessage());
		}
		if(req.getAge() < 18) {
			return new BasicRes(ReplyMessage.USER_AGE_ERROR.getCode(), //
					ReplyMessage.USER_AGE_ERROR.getMessage());
		}
		for(AnswerVo vo : req.getAnswerVoList()) {
			/*檢查必填但沒有答案(沒有選項編號或選項)*/
			if(vo.getQuestion().isRequired()) {
				if(!StringUtils.hasText(vo.getAnswer())) {
					return new BasicRes(ReplyMessage.ANSWER_REQUIRED.getCode(), //
							ReplyMessage.ANSWER_REQUIRED.getMessage());
				}
			}
		}
		return null;
	}
	
	public GetFeedbackUserRes getAllFillinUsers(int quizId) {
		if(quizId <= 0) {
			return new GetFeedbackUserRes(ReplyMessage.QUIZ_ID_ERROR.getCode(), //
					ReplyMessage.QUIZ_ID_ERROR.getMessage());
		}
		List<Fillin> list = fillinDao.getByQuizId(quizId);
		List<AnswerVo> answerVoList = new ArrayList<>();
		Map<String, FeedbackUserVo> map = new HashMap<>();
		List<FeedbackUserVo> userVoList = new ArrayList<>();
		Map<String, List<AnswerVo>> answerVomap = new HashMap<>();
		for(Fillin fillin : list) {
			if(!map.containsKey(fillin.getUserEmail())) {
				/* 使用 map 可以防止同一位使用者有多題的作答時，會產生多個 FeedbackUserVo，
				 * 因為是同一位使用者對同一張問卷作答多題*/
				/* 新的使用者必須要重置 List<AnswerVo> answerVoList */
				User user = userDao.getByEmail(fillin.getUserEmail());
				answerVoList = new ArrayList<>();
				FeedbackUserVo userVo = new FeedbackUserVo(user.getName(), user.getPhone(), //
						user.getEmail(), user.getAge(), fillin.getFillinDate(), answerVoList);
				userVoList.add(userVo);
				map.put(fillin.getUserEmail(), userVo);
				answerVomap.put(fillin.getUserEmail(), answerVoList);
			}			
			List<Question> questionList = questionDao.getByQuizId(quizId);
			for(Question question : questionList) {
				/* 比對 question_id: 一樣的就把答案放到 AnswerVo*/
				if(fillin.getQuestionId() == question.getQuestionId()) {
					AnswerVo answerVo = new AnswerVo(question, fillin.getAnswer());
					/* 取出特定使用者舊的 answerVoList */
					answerVoList = answerVomap.get(fillin.getUserEmail());
					/* 增加新的 answerVo*/
					answerVoList.add(answerVo);
					/* 把新的 answerVoList 放回到 answerVomap 中 */
					answerVomap.put(fillin.getUserEmail(), answerVoList);
				}
			}
		}
		return new GetFeedbackUserRes(ReplyMessage.SUCCESS.getCode(), //
				ReplyMessage.SUCCESS.getMessage(), userVoList);
	}

}
