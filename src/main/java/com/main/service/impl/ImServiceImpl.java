package com.main.service.impl;

import java.util.List;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.main.entity.enums.PageSize;
import com.main.entity.query.ImQuery;
import com.main.entity.po.Im;
import com.main.entity.vo.PaginationResultVO;
import com.main.entity.query.SimplePage;
import com.main.mappers.ImMapper;
import com.main.service.ImService;
import com.main.utils.StringTools;


/**
 *  业务接口实现
 */
@Service("imService")
public class ImServiceImpl implements ImService {

	@Resource
	private ImMapper<Im, ImQuery> imMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<Im> findListByParam(ImQuery param) {
		return this.imMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(ImQuery param) {
		return this.imMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<Im> findListByPage(ImQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<Im> list = this.findListByParam(param);
		PaginationResultVO<Im> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(Im bean) {
		return this.imMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<Im> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.imMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<Im> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.imMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(Im bean, ImQuery param) {
		StringTools.checkParam(param);
		return this.imMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(ImQuery param) {
		StringTools.checkParam(param);
		return this.imMapper.deleteByParam(param);
	}

	/**
	 * 根据Time获取对象
	 */
	@Override
	public Im getImByTime(String time) {
		return this.imMapper.selectByTime(time);
	}

	/**
	 * 根据Time修改
	 */
	@Override
	public Integer updateImByTime(Im bean, String time) {
		return this.imMapper.updateByTime(bean, time);
	}

	/**
	 * 根据Time删除
	 */
	@Override
	public Integer deleteImByTime(String time) {
		return this.imMapper.deleteByTime(time);
	}
}