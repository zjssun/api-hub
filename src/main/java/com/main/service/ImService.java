package com.main.service;

import java.util.List;

import com.main.entity.query.ImQuery;
import com.main.entity.po.Im;
import com.main.entity.vo.PaginationResultVO;


/**
 *  业务接口
 */
public interface ImService {

	/**
	 * 根据条件查询列表
	 */
	List<Im> findListByParam(ImQuery param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(ImQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<Im> findListByPage(ImQuery param);

	/**
	 * 新增
	 */
	Integer add(Im bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<Im> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<Im> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(Im bean,ImQuery param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(ImQuery param);

	/**
	 * 根据Time查询对象
	 */
	Im getImByTime(String time);


	/**
	 * 根据Time修改
	 */
	Integer updateImByTime(Im bean,String time);


	/**
	 * 根据Time删除
	 */
	Integer deleteImByTime(String time);

}