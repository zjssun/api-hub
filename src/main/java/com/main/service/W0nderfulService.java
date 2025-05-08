package com.main.service;

import java.util.List;

import com.main.entity.query.W0nderfulQuery;
import com.main.entity.po.W0nderful;
import com.main.entity.vo.PaginationResultVO;


/**
 *  业务接口
 */
public interface W0nderfulService {

	/**
	 * 根据条件查询列表
	 */
	List<W0nderful> findListByParam(W0nderfulQuery param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(W0nderfulQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<W0nderful> findListByPage(W0nderfulQuery param);

	/**
	 * 新增
	 */
	Integer add(W0nderful bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<W0nderful> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<W0nderful> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(W0nderful bean,W0nderfulQuery param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(W0nderfulQuery param);

	/**
	 * 根据Time查询对象
	 */
	W0nderful getW0nderfulByTime(String time);


	/**
	 * 根据Time修改
	 */
	Integer updateW0nderfulByTime(W0nderful bean,String time);


	/**
	 * 根据Time删除
	 */
	Integer deleteW0nderfulByTime(String time);

}