package com.mk.eap.entity.dto;

import java.util.ArrayList;
import java.util.List;

import com.mk.eap.common.domain.DTO;
import com.mk.eap.common.domain.PageObject;

/**
 * 分页查询结果基类
 * @author gaoxue
 * 
 * @param <D> dto 实体类型
 */
public class PageResultDto<D extends DTO> extends DTO {

    private static final long serialVersionUID = -5221287484153071450L;

    /** 查询结果列表 */
    private List<D> list = new ArrayList<>();

    /** 分页结果 */
    private PageObject pagination;

    /**
     * 获取查询结果列表
     * @return 查询结果列表
     */
    public List<D> getList() {
        return list;
    }

    /**
     * 设置查询结果列表
     * @param list 查询结果列表
     */
    public void setList(List<D> list) {
        if (list == null) {
            this.list = new ArrayList<>();
        } else {
            this.list = list;
        }
    }

    /**
     * 获取分页结果
     * @return 分页结果
     */
    public PageObject getPage() {
        return pagination;
    }

    /**
     * 设置分页结果
     * @param page 分页结果
     */
    public void setPage(PageObject page) {
        this.pagination = page;
    }

}
