package com.main.service;

import java.util.List;

import com.main.entity.query.EligeQuery;
import com.main.entity.po.Elige;
import com.main.entity.vo.PaginationResultVO;


/**
 *  业务接口
 */
public interface EligeService {

	/**
	 * 根据条件查询列表
	 */
	List<Elige> findListByParam(EligeQuery param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(EligeQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<Elige> findListByPage(EligeQuery param);

	/**
	 * 新增
	 */
	Integer add(Elige bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<Elige> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<Elige> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(Elige bean,EligeQuery param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(EligeQuery param);

	/**
	 * 根据Time查询对象
	 */
	Elige getEligeByTime(String time);


	/**
	 * 根据Time修改
	 */
	Integer updateEligeByTime(Elige bean,String time);


	/**
	 * 根据Time删除
	 */
	Integer deleteEligeByTime(String time);

}