package com.mk.eap.common;

import java.util.List;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;

import com.mk.eap.common.domain.VO;

/**
 * 实体批量操作的通用 Mapper 接口
 * @author gaoxue
 *
 * @param <V> vo 实体类型
 */
public interface BatchMapper<V extends VO> {

    /**
     * 批量插入，null 的属性也会保存，不会使用数据库默认值
     * @param vos 需要插入的实体
     * @return 插入的记录数
     */
    @InsertProvider(type = BatchProvider.class, method = "dynamicSQL")
    int insertBatch(List<V> vos);

    /**
     * 批量删除，根据实体属性作为条件进行删除，查询条件使用等号
     * <p>删除 sql 拼写为 where (list.get(0) condition) or (list.get(1) condition)
     * @param vos 需要删除的实体
     * @return 删除的记录数
     */
    @DeleteProvider(type = BatchProvider.class, method = "dynamicSQL")
    int deleteBatch(List<V> vos);

    /**
     * 按照主键批量删除，需要传入主键
     * <p>删除 sql 拼写为 where (list.get(0) condition) or (list.get(1) condition)
     * @param vos 需要删除的实体
     * @return 删除的记录数
     */
    @DeleteProvider(type = BatchProvider.class, method = "dynamicSQL")
    int deleteBatchByPrimaryKey(List<V> vos);

}
