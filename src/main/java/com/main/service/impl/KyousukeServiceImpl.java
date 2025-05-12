package com.main.service.impl;

import java.util.List;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.main.entity.enums.PageSize;
import com.main.entity.query.KyousukeQuery;
import com.main.entity.po.Kyousuke;
import com.main.entity.vo.PaginationResultVO;
import com.main.entity.query.SimplePage;
import com.main.mappers.KyousukeMapper;
import com.main.service.KyousukeService;
import com.main.utils.StringTools;


/**
 *  业务接口实现
 */
@Service("kyousukeService")
public class KyousukeServiceImpl implements KyousukeService {

	@Resource
	private KyousukeMapper<Kyousuke, KyousukeQuery> kyousukeMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<Kyousuke> findListByParam(KyousukeQuery param) {
		return this.kyousukeMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(KyousukeQuery param) {
		return this.kyousukeMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<Kyousuke> findListByPage(KyousukeQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<Kyousuke> list = this.findListByParam(param);
		PaginationResultVO<Kyousuke> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(Kyousuke bean) {
		return this.kyousukeMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<Kyousuke> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.kyousukeMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<Kyousuke> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.kyousukeMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(Kyousuke bean, KyousukeQuery param) {
		StringTools.checkParam(param);
		return this.kyousukeMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(KyousukeQuery param) {
		StringTools.checkParam(param);
		return this.kyousukeMapper.deleteByParam(param);
	}

	/**
	 * 根据Time获取对象
	 */
	@Override
	public Kyousuke getKyousukeByTime(String time) {
		return this.kyousukeMapper.selectByTime(time);
	}

	/**
	 * 根据Time修改
	 */
	@Override
	public Integer updateKyousukeByTime(Kyousuke bean, String time) {
		return this.kyousukeMapper.updateByTime(bean, time);
	}

	/**
	 * 根据Time删除
	 */
	@Override
	public Integer deleteKyousukeByTime(String time) {
		return this.kyousukeMapper.deleteByTime(time);
	}
}