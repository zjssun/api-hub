package com.main.service.impl;

import java.util.List;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.main.entity.enums.PageSize;
import com.main.entity.query.M0nesyQuery;
import com.main.entity.po.M0nesy;
import com.main.entity.vo.PaginationResultVO;
import com.main.entity.query.SimplePage;
import com.main.mappers.M0nesyMapper;
import com.main.service.M0nesyService;
import com.main.utils.StringTools;


/**
 *  业务接口实现
 */
@Service("m0nesyService")
public class M0nesyServiceImpl implements M0nesyService {

	@Resource
	private M0nesyMapper<M0nesy, M0nesyQuery> m0nesyMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<M0nesy> findListByParam(M0nesyQuery param) {
		return this.m0nesyMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(M0nesyQuery param) {
		return this.m0nesyMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<M0nesy> findListByPage(M0nesyQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<M0nesy> list = this.findListByParam(param);
		PaginationResultVO<M0nesy> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(M0nesy bean) {
		return this.m0nesyMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<M0nesy> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.m0nesyMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<M0nesy> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.m0nesyMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(M0nesy bean, M0nesyQuery param) {
		StringTools.checkParam(param);
		return this.m0nesyMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(M0nesyQuery param) {
		StringTools.checkParam(param);
		return this.m0nesyMapper.deleteByParam(param);
	}

	/**
	 * 根据Time获取对象
	 */
	@Override
	public M0nesy getM0nesyByTime(String time) {
		return this.m0nesyMapper.selectByTime(time);
	}

	/**
	 * 根据Time修改
	 */
	@Override
	public Integer updateM0nesyByTime(M0nesy bean, String time) {
		return this.m0nesyMapper.updateByTime(bean, time);
	}

	/**
	 * 根据Time删除
	 */
	@Override
	public Integer deleteM0nesyByTime(String time) {
		return this.m0nesyMapper.deleteByTime(time);
	}
}