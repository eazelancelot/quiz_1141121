package com.example.quiz_1141121.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.quiz_1141121.entity.Quiz;

@Repository
public interface QuizDao extends JpaRepository<Quiz, Integer>{
	
	@Modifying
	@Transactional
	@Query(value = "insert into quiz (title, description, start_date, end_date, is_published) "//
			+ " values (?1, ?2, ?3, ?4, ?5)", nativeQuery = true)
	public void insert(String title, String description, LocalDate startDate, LocalDate endDate,//
			boolean published);
	
	@Query(value = "select max(id) from quiz", nativeQuery = true)
	public int getMaxId();	
	
	@Query(value = "select * from quiz", nativeQuery = true)
	public List<Quiz> getAll();
	
	@Query(value = "select * from quiz where id = ?1", nativeQuery = true)
	public Quiz getById(int id);
	
	@Modifying
	@Transactional
	@Query(value = "update quiz set title = ?2, description = ?3, start_date = ?4,"//
			+ " end_date = ?5, is_published = ?6 where id = ?1", nativeQuery = true)
	public void update(int id, String title, String description, LocalDate startDate, //
			LocalDate endDate, boolean published);
	
	/* idList = [1, 2, 3]
	 * SQL 語法: delete from quiz where id in (1, 2, 3) */
	@Modifying
	@Transactional
	@Query(value = "delete from quiz where id in (?1)", nativeQuery = true)
	public void delete(List<Integer> idList);
	
	@Modifying
	@Transactional
	@Query(value = "delete from quiz where id = ?1", nativeQuery = true)
	public void delete(int id);

}
