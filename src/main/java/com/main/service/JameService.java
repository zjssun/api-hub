package com.main.service;

import java.util.List;

import com.main.entity.query.JameQuery;
import com.main.entity.po.Jame;
import com.main.entity.vo.PaginationResultVO;


/**
 *  业务接口
 */
public interface JameService {

	/**
	 * 根据条件查询列表
	 */
	List<Jame> findListByParam(JameQuery param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(JameQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<Jame> findListByPage(JameQuery param);

	/**
	 * 新增
	 */
	Integer add(Jame bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<Jame> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<Jame> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(Jame bean,JameQuery param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(JameQuery param);

	/**
	 * 根据Time查询对象
	 */
	Jame getJameByTime(String time);


	/**
	 * 根据Time修改
	 */
	Integer updateJameByTime(Jame bean,String time);


	/**
	 * 根据Time删除
	 */
	Integer deleteJameByTime(String time);

}