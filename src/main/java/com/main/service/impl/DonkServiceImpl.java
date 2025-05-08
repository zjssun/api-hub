package com.main.service.impl;

import java.util.List;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.main.entity.enums.PageSize;
import com.main.entity.query.DonkQuery;
import com.main.entity.po.Donk;
import com.main.entity.vo.PaginationResultVO;
import com.main.entity.query.SimplePage;
import com.main.mappers.DonkMapper;
import com.main.service.DonkService;
import com.main.utils.StringTools;


/**
 *  业务接口实现
 */
@Service("donkService")
public class DonkServiceImpl implements DonkService {

	@Resource
	private DonkMapper<Donk, DonkQuery> donkMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<Donk> findListByParam(DonkQuery param) {
		return this.donkMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(DonkQuery param) {
		return this.donkMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<Donk> findListByPage(DonkQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<Donk> list = this.findListByParam(param);
		PaginationResultVO<Donk> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(Donk bean) {
		return this.donkMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<Donk> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.donkMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<Donk> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.donkMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(Donk bean, DonkQuery param) {
		StringTools.checkParam(param);
		return this.donkMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(DonkQuery param) {
		StringTools.checkParam(param);
		return this.donkMapper.deleteByParam(param);
	}

	/**
	 * 根据Time获取对象
	 */
	@Override
	public Donk getDonkByTime(String time) {
		return this.donkMapper.selectByTime(time);
	}

	/**
	 * 根据Time修改
	 */
	@Override
	public Integer updateDonkByTime(Donk bean, String time) {
		return this.donkMapper.updateByTime(bean, time);
	}

	/**
	 * 根据Time删除
	 */
	@Override
	public Integer deleteDonkByTime(String time) {
		return this.donkMapper.deleteByTime(time);
	}
}