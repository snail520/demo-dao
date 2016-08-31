package com.lezhi.demo.dao;

import com.lezhi.demo.model.validation.User;

public interface LoginDao extends BaseDao<User,String>{

	public User findUser(String userName);

}
