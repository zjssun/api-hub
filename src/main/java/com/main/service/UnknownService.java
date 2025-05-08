package com.main.service;

import java.util.List;

import com.main.entity.query.UnknownQuery;
import com.main.entity.po.Unknown;
import com.main.entity.vo.PaginationResultVO;


/**
 *  业务接口
 */
public interface UnknownService {

	/**
	 * 根据条件查询列表
	 */
	List<Unknown> findListByParam(UnknownQuery param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(UnknownQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<Unknown> findListByPage(UnknownQuery param);

	/**
	 * 新增
	 */
	Integer add(Unknown bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<Unknown> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<Unknown> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(Unknown bean,UnknownQuery param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(UnknownQuery param);

	/**
	 * 根据Time查询对象
	 */
	Unknown getUnknownByTime(String time);


	/**
	 * 根据Time修改
	 */
	Integer updateUnknownByTime(Unknown bean,String time);


	/**
	 * 根据Time删除
	 */
	Integer deleteUnknownByTime(String time);

}