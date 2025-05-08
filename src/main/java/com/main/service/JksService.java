package com.main.service;

import java.util.List;

import com.main.entity.query.JksQuery;
import com.main.entity.po.Jks;
import com.main.entity.vo.PaginationResultVO;


/**
 *  业务接口
 */
public interface JksService {

	/**
	 * 根据条件查询列表
	 */
	List<Jks> findListByParam(JksQuery param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(JksQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<Jks> findListByPage(JksQuery param);

	/**
	 * 新增
	 */
	Integer add(Jks bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<Jks> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<Jks> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(Jks bean,JksQuery param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(JksQuery param);

	/**
	 * 根据Time查询对象
	 */
	Jks getJksByTime(String time);


	/**
	 * 根据Time修改
	 */
	Integer updateJksByTime(Jks bean,String time);


	/**
	 * 根据Time删除
	 */
	Integer deleteJksByTime(String time);

}