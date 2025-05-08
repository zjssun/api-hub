package com.main.service;

import java.util.List;

import com.main.entity.query.RopzQuery;
import com.main.entity.po.Ropz;
import com.main.entity.vo.PaginationResultVO;


/**
 *  业务接口
 */
public interface RopzService {

	/**
	 * 根据条件查询列表
	 */
	List<Ropz> findListByParam(RopzQuery param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(RopzQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<Ropz> findListByPage(RopzQuery param);

	/**
	 * 新增
	 */
	Integer add(Ropz bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<Ropz> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<Ropz> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(Ropz bean,RopzQuery param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(RopzQuery param);

	/**
	 * 根据Time查询对象
	 */
	Ropz getRopzByTime(String time);


	/**
	 * 根据Time修改
	 */
	Integer updateRopzByTime(Ropz bean,String time);


	/**
	 * 根据Time删除
	 */
	Integer deleteRopzByTime(String time);

}