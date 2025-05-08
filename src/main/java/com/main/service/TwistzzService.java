package com.main.service;

import java.util.List;

import com.main.entity.query.TwistzzQuery;
import com.main.entity.po.Twistzz;
import com.main.entity.vo.PaginationResultVO;


/**
 *  业务接口
 */
public interface TwistzzService {

	/**
	 * 根据条件查询列表
	 */
	List<Twistzz> findListByParam(TwistzzQuery param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(TwistzzQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<Twistzz> findListByPage(TwistzzQuery param);

	/**
	 * 新增
	 */
	Integer add(Twistzz bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<Twistzz> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<Twistzz> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(Twistzz bean,TwistzzQuery param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(TwistzzQuery param);

	/**
	 * 根据Time查询对象
	 */
	Twistzz getTwistzzByTime(String time);


	/**
	 * 根据Time修改
	 */
	Integer updateTwistzzByTime(Twistzz bean,String time);


	/**
	 * 根据Time删除
	 */
	Integer deleteTwistzzByTime(String time);

}