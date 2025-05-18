package com.main.service;

import java.util.List;

import com.main.entity.query.FrozenQuery;
import com.main.entity.po.Frozen;
import com.main.entity.vo.PaginationResultVO;


/**
 *  业务接口
 */
public interface FrozenService {

	/**
	 * 根据条件查询列表
	 */
	List<Frozen> findListByParam(FrozenQuery param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(FrozenQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<Frozen> findListByPage(FrozenQuery param);

	/**
	 * 新增
	 */
	Integer add(Frozen bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<Frozen> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<Frozen> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(Frozen bean,FrozenQuery param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(FrozenQuery param);

	/**
	 * 根据Time查询对象
	 */
	Frozen getFrozenByTime(String time);


	/**
	 * 根据Time修改
	 */
	Integer updateFrozenByTime(Frozen bean,String time);


	/**
	 * 根据Time删除
	 */
	Integer deleteFrozenByTime(String time);

}