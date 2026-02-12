package com.example.quiz_1141121.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.quiz_1141121.entity.User;

@Repository
public interface UserDao extends JpaRepository<User, String> {

	@Modifying
	@Transactional
	@Query(value = "insert into user (email, name, password, phone, age) "
			+ " values (?1, ?2, ?3, ?4, ?5)", nativeQuery = true)
	public void insert(String email, String name, String password, String phone, int age);
	
	/* 取得特定 email 的次數 --> 檢查 email 是否已存在 --> 檢查 email 是否已被註冊過 
	 * count(欄位名稱)*/
	@Query(value = "select count(email) from user where email = ?1", nativeQuery = true)
	public int getEmailCount(String userEmail);
	
	@Query(value = "select * from user where email = ?1", nativeQuery = true)
	public User getByEmail(String email);
}
