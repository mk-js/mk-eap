package com.cobweb.erp.set.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.mk.eap.common.utils.DtoTreeUtil;
import com.mk.eap.component.oid.itf.IOidService;
import com.mk.eap.entity.impl.EntityCompositionInjector;
import com.mk.eap.entity.impl.EntityReflectionInjector;
import com.mk.eap.entity.impl.EntityServiceImpl;
import com.mk.eap.entity.itf.IEntityInjector;
import com.cobweb.erp.set.dao.SetDepartmentMapper;
import com.cobweb.erp.set.dto.SetOrgDto;
import com.cobweb.erp.set.dto.SetDepartmentDto;
import com.cobweb.erp.set.itf.ISetOrgService;
import com.cobweb.erp.set.itf.ISetDepartmentService;
import com.cobweb.erp.set.vo.SetDepartmentVo;

/**
 * @Title: SetDepartmentServiceImpl.java
 * @Package: com.cobweb.erp.set.impl
 * @Description: 部门接口实现类
 * @version 1.0
 * 
 *          <p>
 *          部门接口实现类
 *          </p>
 * 
 * @author lsg
 * 
 */
@Component
@Service
public class SetDepartmentServiceImpl extends EntityServiceImpl<SetDepartmentDto, SetDepartmentVo, SetDepartmentMapper>
		implements ISetDepartmentService {

	@Reference
	private IOidService idService;

	@Override
	protected void setObjectKeyValue(SetDepartmentDto createDto) {
		if (createDto.getId() == null) {
			createDto.setId(idService.generateObjectID());
		}
	}
	
	public SetDepartmentServiceImpl() {
		super();
		this.getInjectors().add(departmentRefInjector());
	}
	

	private EntityReflectionInjector<SetDepartmentDto,SetDepartmentDto> departmentRefInjector() { 
		return new EntityReflectionInjector<>("department", "departmentId", SetDepartmentDto.class,
				() -> this);
	}
/*
	@Override
	public List<SetDepartmentDto> queryTree() {
		List<SetDepartmentDto> dtos = query(new SetDepartmentDto());
		List<SetDepartmentDto> tree = DtoTreeUtil.Builder(dtos, "id", "departmentId", "children", null);
		return tree;
	} 
*/	
	

	// Logger log = LoggerFactory.getLogger(SysSetDepartmentServiceImpl.class);

}
