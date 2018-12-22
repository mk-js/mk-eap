package com.mk.eap.entity.itf;

import java.util.HashMap;

import org.springframework.web.bind.annotation.RequestMapping;

import com.mk.eap.common.domain.BusinessException;
import com.mk.eap.common.domain.DTO;
import com.mk.eap.common.domain.PageObject;
import com.mk.eap.entity.dto.PageResultDto;

/**
 * 分页服务接口基类
 * <p>
 * 定义了基本的分页查询方法
 * 
 * @author gaoxue
 * 
 * @param <D>
 *            dto 实体类型
 * @param <Q>
 *            实体分页查询条件对象类型
 * @param <R>
 *            实体分页查询结果对象类型
 */
public interface IPageService<D extends DTO> {

	/**
	 * 分页查询实体
	 * 
	 * @param queryDto
	 *            分页查询条件
	 * @return 分页查询结果
	 * @throws NullPointerException
	 *             如果 {@code queryDto} 为 {@code null}
	 * @throws BusinessException
	 */
	PageResultDto<D> queryPageList(HashMap<String, Object> filter, PageObject pagination) throws BusinessException;

}
