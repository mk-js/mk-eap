/*      						
 * Copyright 2015 Beijing T-star, Inc. All rights reserved.
 * 
 * History:
 * ------------------------------------------------------------------------------
 * Date    			|  		Who  			|  		What  
 * 2015-01-12		| 	 lihaitao 			| 	create the file                       
 */
package com.mk.eap.common.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * 分页功能实体类
 * 
 * <p>
 * 	分页功能实体
 * </p> 
 * 
 * @author lihaitao
 * 
 */

public class PageObject implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 855778573334816193L;
	 
    /**
     * 定义常量分页偏移量
     */
    public static final String OFFSET = "offset";

    /**
     * 定义常量分页显示条数
     */
    public static final String PAGESIZE = "pageSize";
    

	private int pageSize = 200; 		// 每页显示记录数
	
	private int current = 1;	// 当前页码
	
	private int totalPage = 1;		// 总页数
	
	private int total = 0;		// 数据总数量
	
	@JsonIgnore
	private int offset = 0;			// 分页时需要偏移的数据总量(MySQL、PgSQL 等特殊数据库)

    /**
     * 分页合理化参数，默认false
     * <p>当该参数设置为 true 时，方法 {@code getCurrentPage()} 在 current < 1 时返回 1， current > totalPage 时返回 totalPage
     */
    @JsonIgnore
    private Boolean reasonable = false;

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		if(pageSize < 1){
			this.pageSize = 1;
		}else{
			this.pageSize = pageSize;
		}
	}

    /**
     * 获取当前页码
     * <p>当参数 reasonable 设置为 true 时，current < 1 返回 1， current > totalPage 返回 totalPage
     * @return
     */
    public int getCurrentPage() {
        if (reasonable) {
            if (current < 1) {
                return 1;
            } else if (current > totalPage) {
                return totalPage;
            }
        }
        return current;
    }

	public void setCurrentPage(int current) {
		if(current < 1){
			this.current = 1;
		}else{
			this.current = current;
		}
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getSumCloum() {
		return total;
	}

	public void setSumCloum(int sumCloum) {
		this.total = sumCloum;
		if(sumCloum > 0) {
			int totalPageTmp = sumCloum/pageSize;
			this.setTotalPage(sumCloum % pageSize == 0 ? totalPageTmp : totalPageTmp + 1);
			this.getOffset();
		}
	}

	public int getOffset() {
		this.setOffset((getCurrentPage()-1)*pageSize);
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

    /**
     * 获取分页合理化参数，默认false
     * <p>当参数设置为 true 时，方法 {@code getCurrentPage()} 在 current < 1 时返回 1， current > totalPage 时返回 totalPage
     * @return reasonable 分页合理化参数
     */
    public Boolean getReasonable() {
        return reasonable;
    }

    /**
     * 设置分页合理化参数
     * <p>当参数设置为 true 时，方法 {@code getCurrentPage()} 在 current < 1 时返回 1， current > totalPage 时返回 totalPage
     * @param reasonable 分页合理化参数
     */
    public void setReasonable(Boolean reasonable) {
        this.reasonable = reasonable;
    }

}
