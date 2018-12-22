package com.mk.eap.common;

import java.util.Set;

import org.apache.ibatis.mapping.MappedStatement;

import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;

/**
 * 实体批量操作 sql 生成实现类
 * @author gaoxue
 *
 */
public class BatchProvider extends MapperTemplate {

    public BatchProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    /**
     * 批量插入，null 的属性也会保存，不会使用数据库默认值
     * @param ms
     * @return
     */
    public String insertBatch(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.insertIntoTable(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.insertColumns(entityClass, false, false, false));
        sql.append(" VALUES ");
        sql.append("<foreach collection=\"list\" item=\"record\" separator=\",\" >");
        sql.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        for (EntityColumn column : columnList) {
            if (!column.isInsertable()) {
                continue;
            }
            sql.append(column.getColumnHolder("record") + ",");
        }
        sql.append("</trim>");
        sql.append("</foreach>");
        return sql.toString();
    }

    /**
     * 批量删除，根据实体属性作为条件进行删除，查询条件使用等号
     * @param ms
     * @return
     */
    public String deleteBatch(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        // 删除 sql 拼写为 where (list.get(0) condition) or (list.get(1) condition)
        sql.append(SqlHelper.deleteFromTable(entityClass, tableName(entityClass)));
        sql.append("<where>");
        sql.append("<if test=\"list == null or list.isEmpty()\"> 1 = 0 </if>");
        sql.append("<if test=\"list != null and !list.isEmpty()\">");
        sql.append("<foreach collection=\"list\" item=\"record\" separator=\" or \">");
        // mybatis 在构建 trim 节点的 sql 语句时会先将内容字符串截取空格 trim()，所以 prefixOverrides 的值不能包含前置的空格
        sql.append("<trim prefix=\"(\" suffix=\")\" prefixOverrides=\"AND \">");
        sql.append(SqlHelper.whereAllIfColumns(entityClass, isNotEmpty(), "record"));
        sql.append("</trim>");
        sql.append("</foreach>");
        sql.append("</if>");
        sql.append("</where>");
        /** 删除 sql 拼写为 delete from x where list.get(0) condition; delte from x where list.get(1) condition
        sql.append("<foreach collection=\"list\" item=\"record\" separator=\";\">");
        sql.append(SqlHelper.deleteFromTable(entityClass, tableName(entityClass)));
        sql.append("<where>");
        sql.append("<trim prefix=\"(\" suffix=\")\" prefixOverrides=\"AND \">");
        sql.append(SqlHelper.whereAllIfColumns(entityClass, isNotEmpty(), "record"));
        sql.append("</trim>");
        sql.append("</where>");
        sql.append("</foreach>");
        */
        return sql.toString();
    }

    /**
     * 按照主键批量删除，需要传入主键
     * @param ms
     * @return
     */
    public String deleteBatchByPrimaryKey(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        // 删除 sql 拼写为 where (list.get(0) condition) or (list.get(1) condition)
        sql.append(SqlHelper.deleteFromTable(entityClass, tableName(entityClass)));
        sql.append("<where>");
        sql.append("<if test=\"list == null or list.isEmpty()\"> 1 = 0 </if>");
        sql.append("<if test=\"list != null and !list.isEmpty()\">");
        sql.append("<foreach collection=\"list\" item=\"record\" separator=\" or \">");
        sql.append("<trim prefix=\"(\" suffix=\")\" prefixOverrides=\"AND \">");
        sql.append(SqlHelper.wherePKColumns(entityClass, "record"));
        sql.append("</trim>");
        sql.append("</foreach>");
        sql.append("</if>");
        sql.append("</where>");
        /** 删除 sql 拼写为 delete from x where list.get(0) condition; delte from x where list.get(1) condition
        sql.append("<foreach collection=\"list\" item=\"record\" separator=\";\">");
        sql.append(SqlHelper.deleteFromTable(entityClass, tableName(entityClass)));
        sql.append("<where>");
        sql.append("<trim prefix=\"(\" suffix=\")\" prefixOverrides=\"AND \">");
        sql.append(SqlHelper.wherePKColumns(entityClass, "record"));
        sql.append("</trim>");
        sql.append("</where>");
        sql.append("</foreach>");
        */
        return sql.toString();
    }

}
