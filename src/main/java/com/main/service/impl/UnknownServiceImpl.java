package com.main.service.impl;

import java.util.List;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.main.entity.enums.PageSize;
import com.main.entity.query.UnknownQuery;
import com.main.entity.po.Unknown;
import com.main.entity.vo.PaginationResultVO;
import com.main.entity.query.SimplePage;
import com.main.mappers.UnknownMapper;
import com.main.service.UnknownService;
import com.main.utils.StringTools;


/**
 *  业务接口实现
 */
@Service("unknownService")
public class UnknownServiceImpl implements UnknownService {

	@Resource
	private UnknownMapper<Unknown, UnknownQuery> unknownMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<Unknown> findListByParam(UnknownQuery param) {
		return this.unknownMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(UnknownQuery param) {
		return this.unknownMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<Unknown> findListByPage(UnknownQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<Unknown> list = this.findListByParam(param);
		PaginationResultVO<Unknown> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(Unknown bean) {
		return this.unknownMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<Unknown> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.unknownMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<Unknown> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.unknownMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(Unknown bean, UnknownQuery param) {
		StringTools.checkParam(param);
		return this.unknownMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(UnknownQuery param) {
		StringTools.checkParam(param);
		return this.unknownMapper.deleteByParam(param);
	}

	/**
	 * 根据Time获取对象
	 */
	@Override
	public Unknown getUnknownByTime(String time) {
		return this.unknownMapper.selectByTime(time);
	}

	/**
	 * 根据Time修改
	 */
	@Override
	public Integer updateUnknownByTime(Unknown bean, String time) {
		return this.unknownMapper.updateByTime(bean, time);
	}

	/**
	 * 根据Time删除
	 */
	@Override
	public Integer deleteUnknownByTime(String time) {
		return this.unknownMapper.deleteByTime(time);
	}
}