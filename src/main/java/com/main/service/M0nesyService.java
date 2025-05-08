package com.main.service;

import java.util.List;

import com.main.entity.query.M0nesyQuery;
import com.main.entity.po.M0nesy;
import com.main.entity.vo.PaginationResultVO;


/**
 *  业务接口
 */
public interface M0nesyService {

	/**
	 * 根据条件查询列表
	 */
	List<M0nesy> findListByParam(M0nesyQuery param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(M0nesyQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<M0nesy> findListByPage(M0nesyQuery param);

	/**
	 * 新增
	 */
	Integer add(M0nesy bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<M0nesy> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<M0nesy> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(M0nesy bean,M0nesyQuery param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(M0nesyQuery param);

	/**
	 * 根据Time查询对象
	 */
	M0nesy getM0nesyByTime(String time);


	/**
	 * 根据Time修改
	 */
	Integer updateM0nesyByTime(M0nesy bean,String time);


	/**
	 * 根据Time删除
	 */
	Integer deleteM0nesyByTime(String time);

}