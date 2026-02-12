package com.example.quiz_1141121.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.example.quiz_1141121.constants.ReplyMessage;
import com.example.quiz_1141121.constants.Type;
import com.example.quiz_1141121.dao.QuestionDao;
import com.example.quiz_1141121.dao.QuizDao;
import com.example.quiz_1141121.entity.Question;
import com.example.quiz_1141121.entity.Quiz;
import com.example.quiz_1141121.req.CreateReq;
import com.example.quiz_1141121.req.DeleteReq;
import com.example.quiz_1141121.req.UpdateReq;
import com.example.quiz_1141121.res.BasicRes;
import com.example.quiz_1141121.res.CreateRes;
import com.example.quiz_1141121.res.GetQuestionRes;
import com.example.quiz_1141121.res.GetQuizRes;
import com.example.quiz_1141121.res.UpdateRes;

@Service
public class QuizService {
	
	@Autowired
	private QuizDao quizDao;
	
	@Autowired
	private QuestionDao questionDao;

	/* 一個方法中若有使用到多個 Dao 或是同一個 Dao 有呼叫多次去對資料作變更(新增、修改、刪除)，
	 * 必須要用@Transactional，因為這些 Dao 的操作，都屬於同一次的操作，因此資料的變更要嘛全部成功，
	 * 不然就全部失敗，回溯到尚未變更之前*/
	@Transactional(rollbackFor = Exception.class)
	public CreateRes create(CreateReq req) {
		/* 參數檢查 */
		CreateRes res = checkParams(req);
		if(res != null) {
			return res;
		}
		try {
			/* 新增問卷 */
			quizDao.insert(req.getTitle(), req.getDescription(), req.getStartDate(), //
					req.getEndDate(), req.isPublished());
			/* 取得新增問卷的 id*/
			int quizId = quizDao.getMaxId();
			/* 新增問題 */
			for(Question item : req.getQuestionList()) {
				questionDao.insert(quizId, item.getQuestionId(), item.getQuestion(), item.getType(), //
						item.isRequired(), item.getOptions());
			}
		} catch (Exception e) {
			throw e;
		}
		return new CreateRes(ReplyMessage.SUCCESS.getCode(), //
				ReplyMessage.SUCCESS.getMessage());
	}

	private CreateRes checkParams(CreateReq req) {
		if (!StringUtils.hasText(req.getTitle())) {
			return new CreateRes(ReplyMessage.TITLE_ERROR.getCode(), //
					ReplyMessage.TITLE_ERROR.getMessage());
		}
		if (!StringUtils.hasText(req.getDescription())) {
			return new CreateRes(ReplyMessage.DESCRIPTION_ERROR.getCode(), //
					ReplyMessage.DESCRIPTION_ERROR.getMessage());
		}
		/* 1. 開始時間不能比今天早 2. 開始時間不能比結束時間晚 */
		if (req.getStartDate() == null || req.getStartDate().isBefore(LocalDate.now())
				|| req.getStartDate().isAfter(req.getEndDate())) {
			return new CreateRes(ReplyMessage.START_DATE_ERROR.getCode(), //
					ReplyMessage.START_DATE_ERROR.getMessage());
		}
		/* 1. 結束時間不能比今天早 */
		if (req.getEndDate() == null || req.getEndDate().isBefore(LocalDate.now())) {
			return new CreateRes(ReplyMessage.END_DATE_ERROR.getCode(), //
					ReplyMessage.END_DATE_ERROR.getMessage());
		}
		for (Question item : req.getQuestionList()) {
			if (item.getQuestionId() <= 0) {
				return new CreateRes(ReplyMessage.QUESTION_ID_ERROR.getCode(), //
						ReplyMessage.QUESTION_ID_ERROR.getMessage(), item.getQuestionId());
			}
			if (!StringUtils.hasText(item.getQuestion())) {
				return new CreateRes(ReplyMessage.QUESTION_ERROR.getCode(), //
						ReplyMessage.QUESTION_ERROR.getMessage(), item.getQuestionId());
			}
			if (!Type.check(item.getType())) {
				return new CreateRes(ReplyMessage.TYPE_ERROR.getCode(), //
						ReplyMessage.TYPE_ERROR.getMessage(), item.getQuestionId());
			}
			/*
			 * 檢查選項: 因為上面有檢查過 type 一定會是 單、多選、簡答 這3種之一， 所以下面的判斷式可以檢查 type 是 非簡答題 就可以
			 */
			if (!item.getType().equalsIgnoreCase(Type.TEXT.getType())) {
				if (!StringUtils.hasText(item.getOptions())) {
					return new CreateRes(ReplyMessage.OPTIONS_ERROR.getCode(), //
							ReplyMessage.OPTIONS_ERROR.getMessage(), item.getQuestionId());
				}
			}
		}
		return null;
	}
	
	public GetQuizRes getQuizList() {
		return new GetQuizRes(ReplyMessage.SUCCESS.getCode(), //
				ReplyMessage.SUCCESS.getMessage(), quizDao.getAll());
	}
	
	public GetQuestionRes getQuestionList(int quizId) {
		return new GetQuestionRes(ReplyMessage.SUCCESS.getCode(), //
				ReplyMessage.SUCCESS.getMessage(), questionDao.getByQuizId(quizId));
	}
	
	@Transactional(rollbackFor = Exception.class)
	public UpdateRes update(UpdateReq req) {
		/* 更新要檢查 quizId，因為存在 DB 中的 quizId 一定是 大於 0*/
		if(req.getQuizId() <= 0) {
			return new UpdateRes(ReplyMessage.QUIZ_ID_ERROR.getCode(), //
					ReplyMessage.QUIZ_ID_ERROR.getMessage());
		}
		/* 檢查 req 中的 quizId 和每個 Question 中的 quizId 是否相同*/
		for(Question item : req.getQuestionList()) {
			if(req.getQuizId() != item.getQuizId()) {
				return new UpdateRes(ReplyMessage.QUIZ_ID_MISMATCH.getCode(), //
						ReplyMessage.QUIZ_ID_MISMATCH.getMessage());
			}
		}
		/* 檢查更新的問卷是否有存在: 主要是取得問卷的發佈狀態*/
		Quiz quiz = quizDao.getById(req.getQuizId());
		if(quiz == null) {
			return new UpdateRes(ReplyMessage.QUIZ_NOT_FOUND.getCode(), //
					ReplyMessage.QUIZ_NOT_FOUND.getMessage());
		}
		/* 檢查是否可以被修改的狀態: 1. 未發佈 或 2. 已發佈且尚未開始(開始日期比現在日期晚)，
		 * 要排除: 已發佈且狀態是進行中或已結束 --> 開始日期不是在今天之後 --> 開始日期在今天或今天之前*/
		if(quiz.isPublished() && !req.getStartDate().isAfter(LocalDate.now())) {
			return new UpdateRes(ReplyMessage.QUIZ_UPDATE_FORBIDDEN.getCode(), //
					ReplyMessage.QUIZ_UPDATE_FORBIDDEN.getMessage());
		}
		/*檢查其餘的參數*/
		CreateRes res = checkParams(req);
		if(res != null) {
			/* 不能直接將父類別(CreateRes)強制轉型成子類別(UpdateRes)，會報錯，
			 * 所以不能寫成 return (UpdateRes)res; */
			return new UpdateRes(res.getCode(), res.getMessage());
		}
		try {
			/* 1. 更新問卷*/
			quizDao.update(req.getQuizId(), req.getTitle(), req.getDescription(), //
					req.getStartDate(), req.getEndDate(), req.isPublished());			
			/* 2. 刪除同一個 quizId 下的所有 Question*/
			questionDao.delete(req.getQuizId());
			/* 3. 新增更新後的問題*/
			for(Question item : req.getQuestionList()) {
				questionDao.insert(req.getQuizId(), item.getQuestionId(), item.getQuestion(), //
						item.getType(), item.isRequired(), item.getOptions());
			}
		} catch (Exception e) {
			throw e;
		}
		return new UpdateRes(ReplyMessage.SUCCESS.getCode(), //
				ReplyMessage.SUCCESS.getMessage());
	}
	
	@Transactional(rollbackFor = Exception.class)
	public BasicRes delete(DeleteReq req) {
		/*檢查參數*/
		if(CollectionUtils.isEmpty(req.getQuizIdList())) {
			return new BasicRes(ReplyMessage.QUIZ_ID_ERROR.getCode(), //
					ReplyMessage.QUIZ_ID_ERROR.getMessage());
		}
		for(int id : req.getQuizIdList()) {
			if(id <= 0) {
				return new BasicRes(ReplyMessage.QUIZ_ID_ERROR.getCode(), //
						ReplyMessage.QUIZ_ID_ERROR.getMessage());
			}
		}
		try {
			quizDao.delete(req.getQuizIdList());
			questionDao.delete(req.getQuizIdList());
		} catch (Exception e) {
			throw e;
		}
		return new BasicRes(ReplyMessage.SUCCESS.getCode(), //
				ReplyMessage.SUCCESS.getMessage());
	}
	
	@Transactional(rollbackFor = Exception.class)
	public BasicRes delete(int quizId) {
		// 呼叫 delete(DeleteReq req) 的方法
//		List<Integer> list = List.of(quizId);
//		DeleteReq req = new DeleteReq(list);
//		BasicRes res = delete(req);
//		return res;
		/* 上述程式碼可以濃縮成一行 */
		return delete(new DeleteReq(List.of(quizId)));
		
		//==========================================
		// 原本的寫法
//		/*參數檢查*/
//		if (quizId <= 0){
//			return new BasicRes(ReplyMessage.QUIZ_ID_ERROR.getCode(), //
//					ReplyMessage.QUIZ_ID_ERROR.getMessage());
//		}
//		try {
//			quizDao.delete(quizId);
//			questionDao.delete(quizId);
//		} catch (Exception e) {
//			throw e;
//		}
//		return new BasicRes(ReplyMessage.SUCCESS.getCode(), //
//				ReplyMessage.SUCCESS.getMessage());
	}
}
