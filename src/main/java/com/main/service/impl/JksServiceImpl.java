package com.main.service.impl;

import java.util.List;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.main.entity.enums.PageSize;
import com.main.entity.query.JksQuery;
import com.main.entity.po.Jks;
import com.main.entity.vo.PaginationResultVO;
import com.main.entity.query.SimplePage;
import com.main.mappers.JksMapper;
import com.main.service.JksService;
import com.main.utils.StringTools;


/**
 *  业务接口实现
 */
@Service("jksService")
public class JksServiceImpl implements JksService {

	@Resource
	private JksMapper<Jks, JksQuery> jksMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<Jks> findListByParam(JksQuery param) {
		return this.jksMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(JksQuery param) {
		return this.jksMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<Jks> findListByPage(JksQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<Jks> list = this.findListByParam(param);
		PaginationResultVO<Jks> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(Jks bean) {
		return this.jksMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<Jks> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.jksMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<Jks> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.jksMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(Jks bean, JksQuery param) {
		StringTools.checkParam(param);
		return this.jksMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(JksQuery param) {
		StringTools.checkParam(param);
		return this.jksMapper.deleteByParam(param);
	}

	/**
	 * 根据Time获取对象
	 */
	@Override
	public Jks getJksByTime(String time) {
		return this.jksMapper.selectByTime(time);
	}

	/**
	 * 根据Time修改
	 */
	@Override
	public Integer updateJksByTime(Jks bean, String time) {
		return this.jksMapper.updateByTime(bean, time);
	}

	/**
	 * 根据Time删除
	 */
	@Override
	public Integer deleteJksByTime(String time) {
		return this.jksMapper.deleteByTime(time);
	}
}