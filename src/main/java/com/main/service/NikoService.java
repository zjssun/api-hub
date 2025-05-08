package com.main.service;

import java.util.List;

import com.main.entity.query.NikoQuery;
import com.main.entity.po.Niko;
import com.main.entity.vo.PaginationResultVO;


/**
 *  业务接口
 */
public interface NikoService {

	/**
	 * 根据条件查询列表
	 */
	List<Niko> findListByParam(NikoQuery param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(NikoQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<Niko> findListByPage(NikoQuery param);

	/**
	 * 新增
	 */
	Integer add(Niko bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<Niko> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<Niko> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(Niko bean,NikoQuery param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(NikoQuery param);

	/**
	 * 根据Time查询对象
	 */
	Niko getNikoByTime(String time);


	/**
	 * 根据Time修改
	 */
	Integer updateNikoByTime(Niko bean,String time);


	/**
	 * 根据Time删除
	 */
	Integer deleteNikoByTime(String time);

}