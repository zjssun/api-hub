package com.main.service.impl;

import java.util.List;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.main.entity.enums.PageSize;
import com.main.entity.query.TwistzzQuery;
import com.main.entity.po.Twistzz;
import com.main.entity.vo.PaginationResultVO;
import com.main.entity.query.SimplePage;
import com.main.mappers.TwistzzMapper;
import com.main.service.TwistzzService;
import com.main.utils.StringTools;


/**
 *  业务接口实现
 */
@Service("twistzzService")
public class TwistzzServiceImpl implements TwistzzService {

	@Resource
	private TwistzzMapper<Twistzz, TwistzzQuery> twistzzMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<Twistzz> findListByParam(TwistzzQuery param) {
		return this.twistzzMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(TwistzzQuery param) {
		return this.twistzzMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<Twistzz> findListByPage(TwistzzQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<Twistzz> list = this.findListByParam(param);
		PaginationResultVO<Twistzz> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(Twistzz bean) {
		return this.twistzzMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<Twistzz> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.twistzzMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<Twistzz> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.twistzzMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(Twistzz bean, TwistzzQuery param) {
		StringTools.checkParam(param);
		return this.twistzzMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(TwistzzQuery param) {
		StringTools.checkParam(param);
		return this.twistzzMapper.deleteByParam(param);
	}

	/**
	 * 根据Time获取对象
	 */
	@Override
	public Twistzz getTwistzzByTime(String time) {
		return this.twistzzMapper.selectByTime(time);
	}

	/**
	 * 根据Time修改
	 */
	@Override
	public Integer updateTwistzzByTime(Twistzz bean, String time) {
		return this.twistzzMapper.updateByTime(bean, time);
	}

	/**
	 * 根据Time删除
	 */
	@Override
	public Integer deleteTwistzzByTime(String time) {
		return this.twistzzMapper.deleteByTime(time);
	}
}