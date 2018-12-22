package com.mk.eap.entity.itf;

import com.mk.eap.common.domain.DTO;

/**
 * 
 * @author lisga
 *
 */
public interface IFunctionGetEntitiyService<D extends DTO> {

	IEntityService<D> getService();

//	IEntityService<D> getService(String name);
}
