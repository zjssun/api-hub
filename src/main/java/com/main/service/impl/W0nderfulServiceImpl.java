package com.main.service.impl;

import java.util.List;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.main.entity.enums.PageSize;
import com.main.entity.query.W0nderfulQuery;
import com.main.entity.po.W0nderful;
import com.main.entity.vo.PaginationResultVO;
import com.main.entity.query.SimplePage;
import com.main.mappers.W0nderfulMapper;
import com.main.service.W0nderfulService;
import com.main.utils.StringTools;


/**
 *  业务接口实现
 */
@Service("w0nderfulService")
public class W0nderfulServiceImpl implements W0nderfulService {

	@Resource
	private W0nderfulMapper<W0nderful, W0nderfulQuery> w0nderfulMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<W0nderful> findListByParam(W0nderfulQuery param) {
		return this.w0nderfulMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(W0nderfulQuery param) {
		return this.w0nderfulMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<W0nderful> findListByPage(W0nderfulQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<W0nderful> list = this.findListByParam(param);
		PaginationResultVO<W0nderful> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(W0nderful bean) {
		return this.w0nderfulMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<W0nderful> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.w0nderfulMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<W0nderful> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.w0nderfulMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(W0nderful bean, W0nderfulQuery param) {
		StringTools.checkParam(param);
		return this.w0nderfulMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(W0nderfulQuery param) {
		StringTools.checkParam(param);
		return this.w0nderfulMapper.deleteByParam(param);
	}

	/**
	 * 根据Time获取对象
	 */
	@Override
	public W0nderful getW0nderfulByTime(String time) {
		return this.w0nderfulMapper.selectByTime(time);
	}

	/**
	 * 根据Time修改
	 */
	@Override
	public Integer updateW0nderfulByTime(W0nderful bean, String time) {
		return this.w0nderfulMapper.updateByTime(bean, time);
	}

	/**
	 * 根据Time删除
	 */
	@Override
	public Integer deleteW0nderfulByTime(String time) {
		return this.w0nderfulMapper.deleteByTime(time);
	}
}