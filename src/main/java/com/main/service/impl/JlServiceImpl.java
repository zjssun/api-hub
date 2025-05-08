package com.main.service.impl;

import java.util.List;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.main.entity.enums.PageSize;
import com.main.entity.query.JlQuery;
import com.main.entity.po.Jl;
import com.main.entity.vo.PaginationResultVO;
import com.main.entity.query.SimplePage;
import com.main.mappers.JlMapper;
import com.main.service.JlService;
import com.main.utils.StringTools;


/**
 *  业务接口实现
 */
@Service("jlService")
public class JlServiceImpl implements JlService {

	@Resource
	private JlMapper<Jl, JlQuery> jlMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<Jl> findListByParam(JlQuery param) {
		return this.jlMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(JlQuery param) {
		return this.jlMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<Jl> findListByPage(JlQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<Jl> list = this.findListByParam(param);
		PaginationResultVO<Jl> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(Jl bean) {
		return this.jlMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<Jl> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.jlMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<Jl> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.jlMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(Jl bean, JlQuery param) {
		StringTools.checkParam(param);
		return this.jlMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(JlQuery param) {
		StringTools.checkParam(param);
		return this.jlMapper.deleteByParam(param);
	}

	/**
	 * 根据Time获取对象
	 */
	@Override
	public Jl getJlByTime(String time) {
		return this.jlMapper.selectByTime(time);
	}

	/**
	 * 根据Time修改
	 */
	@Override
	public Integer updateJlByTime(Jl bean, String time) {
		return this.jlMapper.updateByTime(bean, time);
	}

	/**
	 * 根据Time删除
	 */
	@Override
	public Integer deleteJlByTime(String time) {
		return this.jlMapper.deleteByTime(time);
	}
}