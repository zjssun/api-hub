package com.main.service;

import java.util.List;

import com.main.entity.query.JlQuery;
import com.main.entity.po.Jl;
import com.main.entity.vo.PaginationResultVO;


/**
 *  业务接口
 */
public interface JlService {

	/**
	 * 根据条件查询列表
	 */
	List<Jl> findListByParam(JlQuery param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(JlQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<Jl> findListByPage(JlQuery param);

	/**
	 * 新增
	 */
	Integer add(Jl bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<Jl> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<Jl> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(Jl bean,JlQuery param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(JlQuery param);

	/**
	 * 根据Time查询对象
	 */
	Jl getJlByTime(String time);


	/**
	 * 根据Time修改
	 */
	Integer updateJlByTime(Jl bean,String time);


	/**
	 * 根据Time删除
	 */
	Integer deleteJlByTime(String time);

}