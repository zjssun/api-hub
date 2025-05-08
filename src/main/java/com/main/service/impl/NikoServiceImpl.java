package com.main.service.impl;

import java.util.List;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.main.entity.enums.PageSize;
import com.main.entity.query.NikoQuery;
import com.main.entity.po.Niko;
import com.main.entity.vo.PaginationResultVO;
import com.main.entity.query.SimplePage;
import com.main.mappers.NikoMapper;
import com.main.service.NikoService;
import com.main.utils.StringTools;


/**
 *  业务接口实现
 */
@Service("nikoService")
public class NikoServiceImpl implements NikoService {

	@Resource
	private NikoMapper<Niko, NikoQuery> nikoMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<Niko> findListByParam(NikoQuery param) {
		return this.nikoMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(NikoQuery param) {
		return this.nikoMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<Niko> findListByPage(NikoQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<Niko> list = this.findListByParam(param);
		PaginationResultVO<Niko> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(Niko bean) {
		return this.nikoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<Niko> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.nikoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<Niko> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.nikoMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(Niko bean, NikoQuery param) {
		StringTools.checkParam(param);
		return this.nikoMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(NikoQuery param) {
		StringTools.checkParam(param);
		return this.nikoMapper.deleteByParam(param);
	}

	/**
	 * 根据Time获取对象
	 */
	@Override
	public Niko getNikoByTime(String time) {
		return this.nikoMapper.selectByTime(time);
	}

	/**
	 * 根据Time修改
	 */
	@Override
	public Integer updateNikoByTime(Niko bean, String time) {
		return this.nikoMapper.updateByTime(bean, time);
	}

	/**
	 * 根据Time删除
	 */
	@Override
	public Integer deleteNikoByTime(String time) {
		return this.nikoMapper.deleteByTime(time);
	}
}