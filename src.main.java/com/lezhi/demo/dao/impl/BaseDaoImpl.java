package com.lezhi.demo.dao.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.lang.reflect.Field;

import org.apache.commons.lang.StringUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.lezhi.demo.dao.BaseDao;
import com.lezhi.demo.model.util.GenericsUtils;
import com.lezhi.demo.model.util.Ignore;
import com.lezhi.demo.model.util.PrimaryKey;
import com.lezhi.demo.model.util.SQLGenerator;
import com.lezhi.demo.model.util.Table;
import com.lezhi.demo.model.util.TableColumn;

public abstract class BaseDaoImpl<T,ID extends Serializable> implements BaseDao<T,ID>{
	
	@Autowired
	SqlSessionTemplate sqlTemplate;
	
	private String basePackage = "com.lezhi.demo.dao.mapper.";
	private Class<T> entityClass;
	/**
     * 作cache 结构{T类的镜像,{数据库列名,实体字段名}}
     */
    private static final Map<Class<?>, Map<String, String>> classFieldMap = new HashMap<Class<?>,Map<String, String>>();
	private Map<String, String> currentColumnFieldNames;
	 //实体类主键名称
    private String pkName;
    //实体类ID字段名称
    private String idName;
    //主键的序列
    private String seq;
    private String tableName;
    private SQLGenerator<T> sqlGenerator;
    
	public BaseDaoImpl(){
		super();
		this.entityClass = (Class<T>)GenericsUtils.getSuperClassGenricType(this.getClass());
		currentColumnFieldNames = classFieldMap.get(entityClass);
		if (null == currentColumnFieldNames) {
            currentColumnFieldNames = new LinkedHashMap<String, String>();
            classFieldMap.put(entityClass, currentColumnFieldNames);
        }
		// 作cache
        Field[] fields = this.entityClass.getDeclaredFields();
        String fieldName = null;
        String columnName = null;
        for (Field field : fields) {
        	if (field.isAnnotationPresent(Ignore.class)) {
                continue;
            }
        	fieldName = field.getName();
            TableColumn tableColumn = field.getAnnotation(TableColumn.class);
            if (null != tableColumn) {
                columnName = tableColumn.value();
            } else {
                columnName = null;
            }
            //如果未标识特殊的列名，默认取字段名
            columnName = (StringUtils.isEmpty(columnName) ? StringUtils.upperCase(fieldName) : columnName);
            currentColumnFieldNames.put(columnName, fieldName);
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                // 取得ID的列名
                idName = fieldName;
                pkName = columnName;
                PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
                seq = primaryKey.seq();
            }
        }
        Table table = this.entityClass.getAnnotation(Table.class);
        if (null == table) 
        { throw new RuntimeException("类-"+ this.entityClass + ",未用@Table注解标识!!"); }
        tableName = table.value();
        sqlGenerator = new SQLGenerator<T>(currentColumnFieldNames.keySet(),tableName,pkName,seq);
	}
	public boolean insert(T obj) {
		int res = sqlTemplate.insert("create",sqlGenerator.sql_create(obj, currentColumnFieldNames));
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
