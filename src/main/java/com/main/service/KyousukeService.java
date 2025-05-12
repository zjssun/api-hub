package com.main.service;

import java.util.List;

import com.main.entity.query.KyousukeQuery;
import com.main.entity.po.Kyousuke;
import com.main.entity.vo.PaginationResultVO;


/**
 *  业务接口
 */
public interface KyousukeService {

	/**
	 * 根据条件查询列表
	 */
	List<Kyousuke> findListByParam(KyousukeQuery param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(KyousukeQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<Kyousuke> findListByPage(KyousukeQuery param);

	/**
	 * 新增
	 */
	Integer add(Kyousuke bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<Kyousuke> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<Kyousuke> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(Kyousuke bean,KyousukeQuery param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(KyousukeQuery param);

	/**
	 * 根据Time查询对象
	 */
	Kyousuke getKyousukeByTime(String time);


	/**
	 * 根据Time修改
	 */
	Integer updateKyousukeByTime(Kyousuke bean,String time);


	/**
	 * 根据Time删除
	 */
	Integer deleteKyousukeByTime(String time);

}