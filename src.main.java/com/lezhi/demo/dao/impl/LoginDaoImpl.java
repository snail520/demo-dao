package com.lezhi.demo.dao.impl;

import org.springframework.stereotype.Repository;
import com.lezhi.demo.dao.LoginDao;
import com.lezhi.demo.model.validation.User;

@Repository
public class LoginDaoImpl extends BaseDaoImpl<User,String> implements LoginDao{
	
	public User findUser(String userName) {
		return sqlTemplate.selectOne("com.lezhi.demo.dao.mapper.UserMapper.findUser",userName);
	}
}
