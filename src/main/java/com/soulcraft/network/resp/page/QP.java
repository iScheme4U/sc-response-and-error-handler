package com.soulcraft.network.resp.page;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;

/**
 * 分页数据封装类
 *
 * @author scott
 * @since 2022年03月10日
 */
@Data
public class QP<T> {
	/**
	 * 当前页
	 */
	private Integer pageNum;
	/**
	 * 页面大小
	 */
	private Integer pageSize;
	/**
	 * 总页数
	 */
	private Integer totalPage;
	/**
	 * 总条目数量
	 */
	private Long total;
	/**
	 * 条目列表
	 */
	private List<T> list;

	/**
	 * <pre>
	 *     将MyBatis Plus 分页结果转化为通用结果
	 * </pre>
	 *
	 * @param pageResult 分页结果
	 * @param <T>        条目类型
	 * @return 转换后的分页结果
	 */
	public static <T> QP<T> restPage(IPage<T> pageResult) {
		QP<T> result = new QP<>();
		result.setPageNum(Convert.toInt(pageResult.getCurrent()));
		result.setPageSize(Convert.toInt(pageResult.getSize()));
		result.setTotal(pageResult.getTotal());
		if (pageResult.getTotal() % pageResult.getSize() == 0) {
			result.setTotalPage(Convert.toInt(pageResult.getTotal() / pageResult.getSize()));
		} else {
			result.setTotalPage(Convert.toInt(pageResult.getTotal() / pageResult.getSize() + 1));
		}
		result.setList(pageResult.getRecords());
		return result;
	}
}
