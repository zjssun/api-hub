package com.main.service;

import java.util.List;

import com.main.entity.query.S1mpleQuery;
import com.main.entity.po.S1mple;
import com.main.entity.vo.PaginationResultVO;


/**
 *  业务接口
 */
public interface S1mpleService {

	/**
	 * 根据条件查询列表
	 */
	List<S1mple> findListByParam(S1mpleQuery param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(S1mpleQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<S1mple> findListByPage(S1mpleQuery param);

	/**
	 * 新增
	 */
	Integer add(S1mple bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<S1mple> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<S1mple> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(S1mple bean,S1mpleQuery param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(S1mpleQuery param);

	/**
	 * 根据Time查询对象
	 */
	S1mple getS1mpleByTime(String time);


	/**
	 * 根据Time修改
	 */
	Integer updateS1mpleByTime(S1mple bean,String time);


	/**
	 * 根据Time删除
	 */
	Integer deleteS1mpleByTime(String time);

}