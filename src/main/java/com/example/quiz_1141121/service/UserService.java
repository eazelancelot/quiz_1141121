package com.example.quiz_1141121.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.quiz_1141121.constants.ReplyMessage;
import com.example.quiz_1141121.dao.UserDao;
import com.example.quiz_1141121.req.RegisterReq;
import com.example.quiz_1141121.res.BasicRes;

@Service
public class UserService {
	
	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	
	@Autowired
	private UserDao userDao;
	
	/*新增 user*/
	public BasicRes register(RegisterReq req) {
		/* 參數檢查 */
		BasicRes res = checkParams(req);
		if(res != null) {
			return res;
		}
		/* 檢查 email 是否已存在於 DB*/
		if(userDao.getEmailCount(req.getEmail()) == 1) {
			return new BasicRes(ReplyMessage.USER_EMAIL_EXISTED.getCode(), //
					ReplyMessage.USER_EMAIL_EXISTED.getMessage());
		}
		/* 新增: 要把密碼加密 */
		try {
			userDao.insert(req.getEmail(), req.getName(), encoder.encode(req.getPassword()), //
					req.getPhone(), req.getAge());
		} catch (Exception e) {
			throw e;
		}
		return new BasicRes(ReplyMessage.SUCCESS.getCode(), //
				ReplyMessage.SUCCESS.getMessage());
	}
	
	private BasicRes checkParams(RegisterReq req) {
		if(!StringUtils.hasText(req.getEmail())) {
			return new BasicRes(ReplyMessage.USER_EMAIL_ERROR.getCode(), //
					ReplyMessage.USER_EMAIL_ERROR.getMessage());
		}
		if(!StringUtils.hasText(req.getPassword())) {
			return new BasicRes(ReplyMessage.USER_PASSWORD_ERROR.getCode(), //
					ReplyMessage.USER_PASSWORD_ERROR.getMessage());
		}
		if(!StringUtils.hasText(req.getName())) {
			return new BasicRes(ReplyMessage.USER_NAME_ERROR.getCode(), //
					ReplyMessage.USER_NAME_ERROR.getMessage());
		}
		if(req.getAge() < 18) {
			return new BasicRes(ReplyMessage.USER_AGE_ERROR.getCode(), //
					ReplyMessage.USER_AGE_ERROR.getMessage());
		}
		return null;
	}

}
