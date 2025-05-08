package com.main.service.impl;

import java.util.List;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.main.entity.enums.PageSize;
import com.main.entity.query.EligeQuery;
import com.main.entity.po.Elige;
import com.main.entity.vo.PaginationResultVO;
import com.main.entity.query.SimplePage;
import com.main.mappers.EligeMapper;
import com.main.service.EligeService;
import com.main.utils.StringTools;


/**
 *  业务接口实现
 */
@Service("eligeService")
public class EligeServiceImpl implements EligeService {

	@Resource
	private EligeMapper<Elige, EligeQuery> eligeMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<Elige> findListByParam(EligeQuery param) {
		return this.eligeMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(EligeQuery param) {
		return this.eligeMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<Elige> findListByPage(EligeQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<Elige> list = this.findListByParam(param);
		PaginationResultVO<Elige> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(Elige bean) {
		return this.eligeMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<Elige> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.eligeMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<Elige> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.eligeMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(Elige bean, EligeQuery param) {
		StringTools.checkParam(param);
		return this.eligeMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(EligeQuery param) {
		StringTools.checkParam(param);
		return this.eligeMapper.deleteByParam(param);
	}

	/**
	 * 根据Time获取对象
	 */
	@Override
	public Elige getEligeByTime(String time) {
		return this.eligeMapper.selectByTime(time);
	}

	/**
	 * 根据Time修改
	 */
	@Override
	public Integer updateEligeByTime(Elige bean, String time) {
		return this.eligeMapper.updateByTime(bean, time);
	}

	/**
	 * 根据Time删除
	 */
	@Override
	public Integer deleteEligeByTime(String time) {
		return this.eligeMapper.deleteByTime(time);
	}
}