package com.main.service.impl;

import java.util.List;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.main.entity.enums.PageSize;
import com.main.entity.query.JameQuery;
import com.main.entity.po.Jame;
import com.main.entity.vo.PaginationResultVO;
import com.main.entity.query.SimplePage;
import com.main.mappers.JameMapper;
import com.main.service.JameService;
import com.main.utils.StringTools;


/**
 *  业务接口实现
 */
@Service("jameService")
public class JameServiceImpl implements JameService {

	@Resource
	private JameMapper<Jame, JameQuery> jameMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<Jame> findListByParam(JameQuery param) {
		return this.jameMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(JameQuery param) {
		return this.jameMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<Jame> findListByPage(JameQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<Jame> list = this.findListByParam(param);
		PaginationResultVO<Jame> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(Jame bean) {
		return this.jameMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<Jame> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.jameMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<Jame> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.jameMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(Jame bean, JameQuery param) {
		StringTools.checkParam(param);
		return this.jameMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(JameQuery param) {
		StringTools.checkParam(param);
		return this.jameMapper.deleteByParam(param);
	}

	/**
	 * 根据Time获取对象
	 */
	@Override
	public Jame getJameByTime(String time) {
		return this.jameMapper.selectByTime(time);
	}

	/**
	 * 根据Time修改
	 */
	@Override
	public Integer updateJameByTime(Jame bean, String time) {
		return this.jameMapper.updateByTime(bean, time);
	}

	/**
	 * 根据Time删除
	 */
	@Override
	public Integer deleteJameByTime(String time) {
		return this.jameMapper.deleteByTime(time);
	}
}