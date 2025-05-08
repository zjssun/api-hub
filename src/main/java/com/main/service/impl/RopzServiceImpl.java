package com.main.service.impl;

import java.util.List;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.main.entity.enums.PageSize;
import com.main.entity.query.RopzQuery;
import com.main.entity.po.Ropz;
import com.main.entity.vo.PaginationResultVO;
import com.main.entity.query.SimplePage;
import com.main.mappers.RopzMapper;
import com.main.service.RopzService;
import com.main.utils.StringTools;


/**
 *  业务接口实现
 */
@Service("ropzService")
public class RopzServiceImpl implements RopzService {

	@Resource
	private RopzMapper<Ropz, RopzQuery> ropzMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<Ropz> findListByParam(RopzQuery param) {
		return this.ropzMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(RopzQuery param) {
		return this.ropzMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<Ropz> findListByPage(RopzQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<Ropz> list = this.findListByParam(param);
		PaginationResultVO<Ropz> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(Ropz bean) {
		return this.ropzMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<Ropz> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.ropzMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<Ropz> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.ropzMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(Ropz bean, RopzQuery param) {
		StringTools.checkParam(param);
		return this.ropzMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(RopzQuery param) {
		StringTools.checkParam(param);
		return this.ropzMapper.deleteByParam(param);
	}

	/**
	 * 根据Time获取对象
	 */
	@Override
	public Ropz getRopzByTime(String time) {
		return this.ropzMapper.selectByTime(time);
	}

	/**
	 * 根据Time修改
	 */
	@Override
	public Integer updateRopzByTime(Ropz bean, String time) {
		return this.ropzMapper.updateByTime(bean, time);
	}

	/**
	 * 根据Time删除
	 */
	@Override
	public Integer deleteRopzByTime(String time) {
		return this.ropzMapper.deleteByTime(time);
	}
}