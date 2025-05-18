package com.main.service.impl;

import java.util.List;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.main.entity.enums.PageSize;
import com.main.entity.query.FrozenQuery;
import com.main.entity.po.Frozen;
import com.main.entity.vo.PaginationResultVO;
import com.main.entity.query.SimplePage;
import com.main.mappers.FrozenMapper;
import com.main.service.FrozenService;
import com.main.utils.StringTools;


/**
 *  业务接口实现
 */
@Service("frozenService")
public class FrozenServiceImpl implements FrozenService {

	@Resource
	private FrozenMapper<Frozen, FrozenQuery> frozenMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<Frozen> findListByParam(FrozenQuery param) {
		return this.frozenMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(FrozenQuery param) {
		return this.frozenMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<Frozen> findListByPage(FrozenQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<Frozen> list = this.findListByParam(param);
		PaginationResultVO<Frozen> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(Frozen bean) {
		return this.frozenMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<Frozen> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.frozenMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<Frozen> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.frozenMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(Frozen bean, FrozenQuery param) {
		StringTools.checkParam(param);
		return this.frozenMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(FrozenQuery param) {
		StringTools.checkParam(param);
		return this.frozenMapper.deleteByParam(param);
	}

	/**
	 * 根据Time获取对象
	 */
	@Override
	public Frozen getFrozenByTime(String time) {
		return this.frozenMapper.selectByTime(time);
	}

	/**
	 * 根据Time修改
	 */
	@Override
	public Integer updateFrozenByTime(Frozen bean, String time) {
		return this.frozenMapper.updateByTime(bean, time);
	}

	/**
	 * 根据Time删除
	 */
	@Override
	public Integer deleteFrozenByTime(String time) {
		return this.frozenMapper.deleteByTime(time);
	}
}