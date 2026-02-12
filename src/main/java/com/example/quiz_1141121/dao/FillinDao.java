package com.example.quiz_1141121.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.quiz_1141121.entity.Fillin;
import com.example.quiz_1141121.entity.FillinId;

@Repository
public interface FillinDao extends JpaRepository<Fillin, FillinId> {

	@Modifying
	@Transactional
	@Query(value = "insert into fillin (quiz_id, question_id, user_email, answer) "
			+ " values (?1, ?2, ?3, ?4)", nativeQuery = true)
	public void insert(int quizId, int questionId, String email, String answer);
	
	@Query(value = "select * from fillin where quiz_id = ?1", nativeQuery = true)
	public List<Fillin> getByQuizId(int quizId);
	
	
}
