package com.main.service;

import java.util.List;

import com.main.entity.query.ZywooQuery;
import com.main.entity.po.Zywoo;
import com.main.entity.vo.PaginationResultVO;


/**
 *  业务接口
 */
public interface ZywooService {

	/**
	 * 根据条件查询列表
	 */
	List<Zywoo> findListByParam(ZywooQuery param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(ZywooQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<Zywoo> findListByPage(ZywooQuery param);

	/**
	 * 新增
	 */
	Integer add(Zywoo bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<Zywoo> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<Zywoo> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(Zywoo bean,ZywooQuery param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(ZywooQuery param);

	/**
	 * 根据Time查询对象
	 */
	Zywoo getZywooByTime(String time);


	/**
	 * 根据Time修改
	 */
	Integer updateZywooByTime(Zywoo bean,String time);


	/**
	 * 根据Time删除
	 */
	Integer deleteZywooByTime(String time);

}