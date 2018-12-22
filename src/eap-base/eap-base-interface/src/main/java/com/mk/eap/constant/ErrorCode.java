package com.mk.eap.constant;

import com.mk.eap.common.domain.BusinessException;

/**
 * @author gaoxue
 *
 */
public final class ErrorCode {

    /** 实体新增异常编码 */
    public static final String EXCEPTION_CODE_CREATE_FAIL = "50010";

    /** 实体查询异常编码 */
    public static final String EXCEPTION_CODE_QUERY_FAIL = "50011";

    /** 实体删除异常编码 */
    public static final String EXCEPTION_CODE_DELETE_FAIL = "50012";

    /** 实体更新异常编码 */
    public static final String EXCEPTION_CODE_UPDATE_FAIL = "50013";

    /** 单据存在并发操作，请刷新后重试 */
    public static final BusinessException EXCEPTION_CONCURRENCY_OPERATE_FAIL = new BusinessException("50014",
            "当前数据已被他人修改（或在列表操作过），请刷新页面重试！");

    /** 单据附件数量超出限制：图片最多 10 个，非图片最多 10 个 */
    public static final BusinessException EXCEPTION_ENCLOSURE_OVERFLOW = new BusinessException("50015",
            "附件数量超出限制：图片最多 10 个，非图片最多 10 个");

    /** 已经是第一条单据 */
    public static final BusinessException EXCEPTION_PREVIOUS_NOT_EXIST = new BusinessException("50016", "已经是第一条单据");

    /** 已经是最后一条单据 */
    public static final BusinessException EXCEPTION_NEXT_NOT_EXIST = new BusinessException("50017", "已经是最后一条单据");

    private ErrorCode() {
        // final const class, avoid instantiate
    }

}
