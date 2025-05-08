package com.main.service;

import java.util.List;

import com.main.entity.query.DonkQuery;
import com.main.entity.po.Donk;
import com.main.entity.vo.PaginationResultVO;


/**
 *  业务接口
 */
public interface DonkService {

	/**
	 * 根据条件查询列表
	 */
	List<Donk> findListByParam(DonkQuery param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(DonkQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<Donk> findListByPage(DonkQuery param);

	/**
	 * 新增
	 */
	Integer add(Donk bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<Donk> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<Donk> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(Donk bean,DonkQuery param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(DonkQuery param);

	/**
	 * 根据Time查询对象
	 */
	Donk getDonkByTime(String time);


	/**
	 * 根据Time修改
	 */
	Integer updateDonkByTime(Donk bean,String time);


	/**
	 * 根据Time删除
	 */
	Integer deleteDonkByTime(String time);

}