package com.mk.eap.common;

/**
 * @author gaoxue
 *
 */
public class Const {

    /** 单据附件最大数量 */
    public static final int VOUCHER_MAX_ENCLOSURE_COUNT = 20;

    /** 单据图片附件最大数量 */
    public static final int VOUCHER_MAX_IMG_ENCLOSURE_COUNT = 10;

    /** 单据非图片附件最大数量 */
    public static final int VOUCHER_MAX_OTHER_ENCLOSURE_COUNT = 10;

    /** 单据审核相关更新字段 */
    public static final String[] VOUCHER_AUDIT_COLUMN = { "auditorId", "auditorName", "auditTime", "status", "docId" };

    /** 单据状态字段 */
    public static final String STATUS_COLUMN = "status";

    /** 数据库批量操作最大记录数 */
    public static final int DB_BATCH_OP_MAX_COUNT = 1000;

}
