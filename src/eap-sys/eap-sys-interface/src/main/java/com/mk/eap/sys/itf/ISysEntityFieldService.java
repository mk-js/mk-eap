package com.mk.eap.sys.itf;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;

import com.mk.eap.common.annotation.ApiMapping;
import com.mk.eap.entity.itf.IEntityService;
import com.mk.eap.sys.dto.SysEntityFieldDto;

//@RequestMapping("/sys/entity/field")
//@ApiMapping("*")//create;query;update;delete
public interface ISysEntityFieldService extends IEntityService<SysEntityFieldDto> {
 
	List<SysEntityFieldDto> loadTableColumnInfo(String dbName, String tableName);
}
