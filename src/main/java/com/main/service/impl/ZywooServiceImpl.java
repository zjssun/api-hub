package com.main.service.impl;

import java.util.List;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.main.entity.enums.PageSize;
import com.main.entity.query.ZywooQuery;
import com.main.entity.po.Zywoo;
import com.main.entity.vo.PaginationResultVO;
import com.main.entity.query.SimplePage;
import com.main.mappers.ZywooMapper;
import com.main.service.ZywooService;
import com.main.utils.StringTools;


/**
 *  业务接口实现
 */
@Service("zywooService")
public class ZywooServiceImpl implements ZywooService {

	@Resource
	private ZywooMapper<Zywoo, ZywooQuery> zywooMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<Zywoo> findListByParam(ZywooQuery param) {
		return this.zywooMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(ZywooQuery param) {
		return this.zywooMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<Zywoo> findListByPage(ZywooQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<Zywoo> list = this.findListByParam(param);
		PaginationResultVO<Zywoo> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(Zywoo bean) {
		return this.zywooMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<Zywoo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.zywooMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<Zywoo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.zywooMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(Zywoo bean, ZywooQuery param) {
		StringTools.checkParam(param);
		return this.zywooMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(ZywooQuery param) {
		StringTools.checkParam(param);
		return this.zywooMapper.deleteByParam(param);
	}

	/**
	 * 根据Time获取对象
	 */
	@Override
	public Zywoo getZywooByTime(String time) {
		return this.zywooMapper.selectByTime(time);
	}

	/**
	 * 根据Time修改
	 */
	@Override
	public Integer updateZywooByTime(Zywoo bean, String time) {
		return this.zywooMapper.updateByTime(bean, time);
	}

	/**
	 * 根据Time删除
	 */
	@Override
	public Integer deleteZywooByTime(String time) {
		return this.zywooMapper.deleteByTime(time);
	}
}