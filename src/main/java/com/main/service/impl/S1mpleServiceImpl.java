package com.main.service.impl;

import java.util.List;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.main.entity.enums.PageSize;
import com.main.entity.query.S1mpleQuery;
import com.main.entity.po.S1mple;
import com.main.entity.vo.PaginationResultVO;
import com.main.entity.query.SimplePage;
import com.main.mappers.S1mpleMapper;
import com.main.service.S1mpleService;
import com.main.utils.StringTools;


/**
 *  业务接口实现
 */
@Service("s1mpleService")
public class S1mpleServiceImpl implements S1mpleService {

	@Resource
	private S1mpleMapper<S1mple, S1mpleQuery> s1mpleMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<S1mple> findListByParam(S1mpleQuery param) {
		return this.s1mpleMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(S1mpleQuery param) {
		return this.s1mpleMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<S1mple> findListByPage(S1mpleQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<S1mple> list = this.findListByParam(param);
		PaginationResultVO<S1mple> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(S1mple bean) {
		return this.s1mpleMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<S1mple> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.s1mpleMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<S1mple> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.s1mpleMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(S1mple bean, S1mpleQuery param) {
		StringTools.checkParam(param);
		return this.s1mpleMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(S1mpleQuery param) {
		StringTools.checkParam(param);
		return this.s1mpleMapper.deleteByParam(param);
	}

	/**
	 * 根据Time获取对象
	 */
	@Override
	public S1mple getS1mpleByTime(String time) {
		return this.s1mpleMapper.selectByTime(time);
	}

	/**
	 * 根据Time修改
	 */
	@Override
	public Integer updateS1mpleByTime(S1mple bean, String time) {
		return this.s1mpleMapper.updateByTime(bean, time);
	}

	/**
	 * 根据Time删除
	 */
	@Override
	public Integer deleteS1mpleByTime(String time) {
		return this.s1mpleMapper.deleteByTime(time);
	}
}