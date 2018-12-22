package com.mk.eap.entity.dao;

import com.mk.eap.common.BatchMapper;
import com.mk.eap.common.domain.VO;

import tk.mybatis.mapper.common.Mapper;

/**
 * 实体数据库访问接口基类
 * <p>具体使用时需要继承此接口并定义具体的泛型实现类，独立的 mapper 文件有独立的命名空间，可以使用到 MyBatis 的二级缓存
 * @author gaoxue
 *
 * @param <V> vo 实体类型
 */
public interface EntityMapper<V extends VO> extends Mapper<V>, BatchMapper<V> {

}
