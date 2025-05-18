package com.main.mappers;

import org.apache.ibatis.annotations.Param;

/**
 *  数据库操作接口
 */
public interface FrozenMapper<T,P> extends BaseMapper<T,P> {

	/**
	 * 根据Time更新
	 */
	 Integer updateByTime(@Param("bean") T t,@Param("time") String time);


	/**
	 * 根据Time删除
	 */
	 Integer deleteByTime(@Param("time") String time);


	/**
	 * 根据Time获取对象
	 */
	 T selectByTime(@Param("time") String time);


}
