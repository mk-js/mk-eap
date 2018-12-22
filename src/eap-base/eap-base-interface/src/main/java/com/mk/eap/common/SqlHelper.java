package com.mk.eap.common;

import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.EntityHelper;

/**
 * sql 帮助类，扩展 {@link tk.mybatis.mapper.mapperhelper.SqlHelper}
 * @author gaoxue
 *
 */
public class SqlHelper extends tk.mybatis.mapper.mapperhelper.SqlHelper {

    /**
     * 获取主键 where 条件 sql，<b>会有一个" AND "的起始字符串</b>
     * @param entityClass 实体类
     * @param entityName 实体名前缀
     * @return 返回示例  " AND name = #{entityName.name} AND code = #{entityName.code}"
     */
    public static String wherePKColumns(Class<?> entityClass, String entityName) {
        StringBuilder sql = new StringBuilder();
        Set<EntityColumn> columnList = EntityHelper.getPKColumns(entityClass);
        for (EntityColumn column : columnList) {
            sql.append(" AND " + column.getColumnEqualsHolder(entityName));
        }
        return sql.toString();
    }

    /**
     * 获取所有非空属性值 where 条件 sql，通过拼写 {@code <if test=""></if>} 的形式，<b>会有一个" AND "的起始字符串</b>
     * @param entityClass 实体类
     * @param empty 字符串类型的属性 test 判断中是否判断空串的条件
     * @param entityName 实体名前缀
     * @return 返回示例 "&lt;if test="entityClass.name != null and entityClass.name != '' "&gt; AND name = #{entityName.name}&lt;/if&gt;"
     */
    public static String whereAllIfColumns(Class<?> entityClass, boolean empty, String entityName) {
        StringBuilder sql = new StringBuilder();
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        for (EntityColumn column : columnList) {
            sql.append(getIfNotNull(entityName, column, " AND " + column.getColumnEqualsHolder(entityName), empty));
        }
        return sql.toString();
    }


    /**
     * 获取单据审核更新时的 set 列
     * @param entityClass
     * @return
     */
    public static String updateSetColumnsOfAuditUpdate(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("<set>");
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        for (EntityColumn column : columnList) {
            if (!column.isId() && column.isUpdatable() && ArrayUtils.contains(Const.VOUCHER_AUDIT_COLUMN, column.getColumn())) {
                sql.append(column.getColumnEqualsHolder() + ",");
            }
        }
        sql.append("</set>");
        return sql.toString();
    }

    /**
     * 获取单据反审核更新时的 set 列
     * @param entityClass
     * @return
     */
    public static String updateSetColumnsOfUnauditUpdate(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("<set>");
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        for (EntityColumn column : columnList) {
            if (!column.isId() && column.isUpdatable() && ArrayUtils.contains(Const.VOUCHER_AUDIT_COLUMN, column.getColumn())) {
                if (column.getColumn().equals(Const.STATUS_COLUMN)) {
                    sql.append(column.getColumnEqualsHolder() + ",");
                } else {
                    sql.append(column.getColumn() + " = null,");
                }
            }
        }
        sql.append("</set>");
        return sql.toString();
    }

    /**
     * where主键条件，如果实体列包含 ts ，则此列也作为条件
     *
     * @param entityClass
     * @return
     */
    public static String wherePKColumnsWithTs(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("<where>");
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        for (EntityColumn column : columnList) {
            if (column.isId() || column.getColumn().contains("ts")) {
                sql.append(" AND " + column.getColumnEqualsHolder());
            }
        }
        sql.append("</where>");
        return sql.toString();
    }

}
