package com.lezhi.demo.dao.impl;

import java.io.Serializable;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.lezhi.demo.dao.BaseDao;

public abstract class BaseDaoImpl<T,ID extends Serializable> implements BaseDao<T,ID>{
	
	@Autowired
	SqlSessionTemplate sqlTemplate;
	
	private String basePackage = "com.lezhi.demo.dao.mapper.";
	
	public boolean insert(T obj) {
		String entityName = obj.getClass().getSimpleName();
		int res = sqlTemplate.insert(basePackage + entityName + "Mapper.insert",obj);
		return returnResult(res);
	}
	
	public boolean update(T obj) {
		String entityName = obj.getClass().getSimpleName();
		int res = sqlTemplate.update(basePackage + entityName + "Mapper.update",obj);
		return returnResult(res);
	}
	
	public boolean delete(Class<?> c,String id) {
		String entityName = c.getSimpleName();
		int res = sqlTemplate.update(basePackage + entityName + "Mapper.delete",id);
		return returnResult(res);
	}
	
	public T findById(Class<?> c,String id) {
		String entityName = c.getSimpleName();
		return sqlTemplate.selectOne(basePackage + entityName + "Mapper.findById",id);
	}
	
	public boolean returnResult(int result){
		if(result>0){
			return true;
		}else{
			return false;
		}
	}
}
