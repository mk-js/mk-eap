package com.mk.eap.entity.impl;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mk.eap.common.Const;
import com.mk.eap.common.domain.BusinessException;
import com.mk.eap.common.domain.DTO;
import com.mk.eap.common.domain.PageObject;
import com.mk.eap.common.domain.Token;
import com.mk.eap.common.domain.VO;
import com.mk.eap.common.utils.JarResourceUtil;
import com.mk.eap.common.utils.StringUtil;
import com.mk.eap.constant.ErrorCode;
import com.mk.eap.entity.dao.EntityMapper;
import com.mk.eap.entity.dto.InjectorConfig;
import com.mk.eap.entity.dto.PageQueryDto;
import com.mk.eap.entity.dto.PageResultDto;
import com.mk.eap.entity.itf.IEntityInjector;
import com.mk.eap.entity.itf.IEntityService;
import com.mk.eap.entity.itf.IPageService;
import com.mk.eap.entity.utils.QueryPageListUtil;

import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;
import tk.mybatis.mapper.mapperhelper.EntityHelper;

/**
 * 实体服务实现基类
 * <p>
 * 实现了实体服务基类接口 {@link IEntityService} 和分页服务基类接口 {@link IPageService}
 * 
 * @author gaoxue
 * 
 * @param <D>
 *            dto 实体类型
 * @param <V>
 *            vo 实体类型
 * @param <M>
 *            mapper 实体数据库访问接口类型
 */
public abstract class EntityServiceImpl<D extends DTO, V extends VO, M extends EntityMapper<V>>
		implements IEntityService<D>, IPageService<D> {

	protected Logger logger;

	protected List<IEntityInjector<D>> injectors;

	/** 泛型 D 的具体类型 */
	protected Class<D> dtoClazz;

	/** 泛型 V 的具体类型 */
	protected Class<V> voClazz;

	/** 操作的 dto 对象 */
	protected D innerDto;

	@Autowired
	protected M mapper;

	protected List<IEntityInjector<D>> getInjectors() {
		if (this.injectors == null) {
			this.injectors = new ArrayList<IEntityInjector<D>>();
		}
		return injectors;
	}

	protected D afterCreate(D createDto) {
		this.getInjectors().forEach(i -> i.afterCreate(createDto));
		// 进行数据库新增后执行
		return createDto;
	}

	protected List<D> afterCreateBatch(List<D> createDtos, Token token) {
		this.getInjectors().forEach(i -> i.afterCreateBatch(createDtos, token));
		// 进行数据库新增后执行
		return createDtos;
	}

	protected void afterDelete(D deleteDto) {
		this.getInjectors().forEach(i -> i.afterDelete(deleteDto));
		// 进行数据库删除后执行
	}

	protected List<D> afterQuery(List<D> resultList, InjectorConfig cfg) {
		this.getInjectors().forEach(i -> i.afterQuery(resultList, cfg));
		// 按照主键查询实体后执行
		return resultList;
	}

	protected D afterQueryByPrimaryKey(D queryDto) {
		this.getInjectors().forEach(i -> i.afterQueryByPrimaryKey(queryDto, new InjectorConfig()));
		return queryDto;
	}

	protected D afterQueryByPrimaryKey(D queryDto, InjectorConfig cfg) {
		this.getInjectors().forEach(i -> i.afterQueryByPrimaryKey(queryDto, cfg));
		// 按照主键查询实体后执行
		return queryDto;
	}

	protected D afterUpdate(D updateDto) {
		this.getInjectors().forEach(i -> i.afterUpdate(updateDto));
		// 进行数据库更新后操作
		return updateDto;
	}

	protected void beforeCreate(D createDto) {
		this.getInjectors().forEach(i -> i.beforeCreate(createDto));
		// 进行数据库新增前执行
	}

	protected void beforeCreateBatch(List<D> createDtos, Token token) {
		this.getInjectors().forEach(i -> i.beforeCreateBatch(createDtos, token));
		// 进行数据库新增前执行
	}

	protected void beforeDelete(D deleteDto) {
		this.getInjectors().forEach(i -> i.beforeDelete(deleteDto));
		// 进行数据库删除前执行
	}

	protected void beforeUpdate(D updateDto) {
		this.getInjectors().forEach(i -> i.beforeUpdate(updateDto));
		// 进行数据库更新前操作
	}

	protected void beforeQueryPageList(PageQueryDto<D> queryDto, InjectorConfig cfg) {
		// 进行数据库分页查询前操作
	}

	protected D beforeQueryByPrimaryKey(D queryDto) {
		this.getInjectors().forEach(i -> i.beforeQueryByPrimaryKey(queryDto));
		return queryDto;
	}

	@SuppressWarnings("unchecked")
	public EntityServiceImpl() {
		Class<?> clazz = getClass();
		Type genType = clazz.getGenericSuperclass();
		while (!(genType instanceof ParameterizedType) && clazz != Object.class) {
			clazz = clazz.getSuperclass();
			genType = clazz.getGenericSuperclass();
		}
		if (genType instanceof ParameterizedType) {
			Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
			dtoClazz = (Class<D>) params[0];
			voClazz = (Class<V>) params[1];
		}
		if (logger == null) {
			logger = LoggerFactory.getLogger(getClass());
		} 

		initJsInjector(clazz.getName());
	}
	private void initJsInjector(String entityName){   
		EntityJsInjector<D,V,M> jsInjector = new EntityJsInjector<>(entityName, mapper);
		if(jsInjector.exists()){
			this.getInjectors().add(jsInjector);
		}
		
	}

	@PostConstruct
	protected void postConstruct() {
		// init method
	}

	@Override
	// @Transactional
	public D create(D dto) throws BusinessException {
		this.innerDto = dto;
		checkNull(dto);
		setDefaultValue4Create(dto);
		// ValidateUtil.validate(dto, Create.class);
		beforeCreate(dto);
		V vo = dto.toVo(voClazz);
		int result;
		try {
			result = mapper.insertSelective(vo);
		} catch (Exception ex) {
			String message = "新增失败，原因：" + ex.getMessage();
			throw new BusinessException(ErrorCode.EXCEPTION_CODE_CREATE_FAIL, message, null, ex);
		}
		if (1 != result) {
			throw new BusinessException(ErrorCode.EXCEPTION_CODE_CREATE_FAIL, "新增失败，数据库执行失败");
		}
		D newDto = afterCreate(dto);
		if (newDto.existTs()) {
			newDto = queryByPrimaryKey(newDto);
		}
		return newDto;
	}

	protected void setDefaultValue4Create(D createDto) {
		// 新增设置默认数据，在新增校验前执行
		setObjectKeyValue(createDto);
	}

	abstract protected void setObjectKeyValue(D createDto);

	@Override
	@Transactional
	public D delete(D dto) throws BusinessException {
		checkNull(dto);
		// ValidateUtil.validate(dto, Delete.class);
		D deleteDto = getInstance(dtoClazz);
		Set<EntityColumn> pkColumns = EntityHelper.getPKColumns(voClazz);
		for (EntityColumn pkColumn : pkColumns) {
			String propertyName = pkColumn.getProperty();
			deleteDto.setFieldValue(propertyName, dto.getFieldValue(propertyName));
		}
		this.innerDto = queryByPrimaryKey4Update(dto);
		checkConcurrency(this.innerDto, dto);

		beforeDelete(dto);
		if (dto.existTs()) {
			deleteDto.setFieldValue(DTO.TS_FILED_NAME, dto.getFieldValue(DTO.TS_FILED_NAME));
		}
		V deleteVo = deleteDto.toVo(voClazz);
		int result;
		try {
			result = mapper.delete(deleteVo);
		} catch (Exception ex) {
			throw new BusinessException(ErrorCode.EXCEPTION_CODE_DELETE_FAIL, "删除失败，原因：" + ex.getMessage(), null, ex);
		}
		if (1 != result) {
			throw new BusinessException(ErrorCode.EXCEPTION_CODE_DELETE_FAIL, "删除失败，请检查 vo 实体的主键配置");
		}
		afterDelete(dto);
		return dto;
	}

	protected void checkConcurrency(D oldDto, D newDto) {
		if (oldDto == null) {
			throw ErrorCode.EXCEPTION_CONCURRENCY_OPERATE_FAIL;
		}
		if (oldDto.existTs()) {
			if (!oldDto.getFieldValue(DTO.TS_FILED_NAME).equals(newDto.getFieldValue(DTO.TS_FILED_NAME))) {
				throw ErrorCode.EXCEPTION_CONCURRENCY_OPERATE_FAIL;
			}
		}
	}

	@Override
	// @Transactional
	public D update(D dto) throws BusinessException {
		checkNull(dto);
		setDefaultValue4Update(dto);
		// ValidateUtil.validate(dto, Update.class);
		this.innerDto = queryByPrimaryKey4Update(dto);
		checkConcurrency(this.innerDto, dto);
		beforeUpdate(dto);
		Example example = getPrimaryKeyExample(dto, null, true);
		if (dto.getNullUpdate()) {
			// ValidateUtil.validate(dto, Create.class);
		}
		V vo = dto.toVo(voClazz);
		int result;
		try {
			if (dto.getNullUpdate()) {
				result = mapper.updateByExample(vo, example);
			} else {
				result = mapper.updateByExampleSelective(vo, example);
			}
		} catch (Exception ex) {
			throw new BusinessException(ErrorCode.EXCEPTION_CODE_UPDATE_FAIL, "更新失败，原因：" + ex.getMessage(), null, ex);
		}
		if (1 != result) {
			throw new BusinessException(ErrorCode.EXCEPTION_CODE_UPDATE_FAIL, "更新失败，请检查 vo 实体的主键配置");
		}
		D newDto = afterUpdate(dto);
		if (newDto.existTs()) {
			newDto = queryByPrimaryKey(newDto);
		}
		return newDto;
	}

	protected void setDefaultValue4Update(D updateDto) {

	}

	@SuppressWarnings("rawtypes")
	protected Example getAndExampleByDto(HashMap<String, Object> filter, Example example) {
		Example andExample = example;
		if (andExample == null) {
			andExample = new Example(voClazz, false, false);
		}
		Criteria criteria = andExample.createCriteria();
		for (String key : filter.keySet()) {
			if (key.equals("selectFields") || key.equals("orderBy") || key.equals("search")
					|| key.equals("searchFields")) {
				continue;
			}
			Object value = filter.get(key);
			if (value == null) {
				criteria.andIsNull(key);
			} else if (value instanceof Map<?, ?>) {
				Map<?, ?> map = (Map) value;
				Object $lte = map.get("$lte");
				Object $gte = map.get("$gte");
				if ($lte != null) {
					criteria.andLessThanOrEqualTo(key, $lte);
				}
				if ($gte != null) {
					criteria.andGreaterThanOrEqualTo(key, $gte);
				}
			} else if (value instanceof Iterable) {
				criteria.andIn(key, (Iterable) value);
			} else {
				criteria.andEqualTo(key, value);
			}
		}
		return andExample;
	}

	protected Example getAndExampleByDto(D dto, Example example) {
		Set<EntityColumn> columns = EntityHelper.getColumns(voClazz);
		Example andExample = example;
		if (andExample == null) {
			andExample = new Example(voClazz, false, false);
		}
		Criteria criteria = andExample.createCriteria();
		for (EntityColumn column : columns) {
			String propertyName = column.getProperty();
			criteria.andEqualTo(propertyName, dto.getFieldValue(propertyName));
		}
		return andExample;
	}

	protected Example getPrimaryKeyExample(D dto, Example example, boolean withTs) {
		Set<EntityColumn> pkColumns = EntityHelper.getPKColumns(voClazz);
		Example pkExample = example;
		if (pkExample == null) {
			pkExample = new Example(voClazz, true, true);
		}
		Criteria criteria = pkExample.createCriteria();
		for (EntityColumn pkColumn : pkColumns) {
			String propertyName = pkColumn.getProperty();
			criteria.andEqualTo(propertyName, dto.getFieldValue(propertyName));
		}
		if (withTs && dto.existTs()) {
			criteria.andEqualTo(DTO.TS_FILED_NAME, dto.getFieldValue(DTO.TS_FILED_NAME));
		}
		return pkExample;
	}

	protected Example getPrimaryKeyExample(List<D> dtos, Example example, boolean withTs) {
		Set<EntityColumn> pkColumns = EntityHelper.getPKColumns(voClazz);
		Example pkExample = example;
		if (pkExample == null) {
			pkExample = new Example(voClazz, true, true);
		}
		for (D dto : dtos) {
			Criteria criteria = pkExample.createCriteria();
			for (EntityColumn pkColumn : pkColumns) {
				String propertyName = pkColumn.getProperty();
				criteria.andEqualTo(propertyName, dto.getFieldValue(propertyName));
			}
			if (withTs && dto.existTs()) {
				criteria.andEqualTo(DTO.TS_FILED_NAME, dto.getFieldValue(DTO.TS_FILED_NAME));
			}
			pkExample.or(criteria);
		}
		return pkExample;
	}

	@Override
	public List<D> query(D dto) throws BusinessException {
		return queryByCfg(dto, InjectorConfig.RefLevel(1));
	}

	@Override
	public List<D> queryByCfg(D dto, InjectorConfig cfg) throws BusinessException {
		// checkNull(dto);
		// ValidateUtil.validate(dto);
		D d = dto;
		if (dto == null) {
			d = this.getInstance(dtoClazz);
		}
		V vo = d.toVo(voClazz);
		List<V> result = null;
		try {
			if (cfg != null && cfg.getSelectFields() != null) {
				Example example = cfg.getExample(voClazz, vo);
				if (dto != null) {
					example = getAndExampleByDto(dto, example);
				}
				result = this.mapper.selectByExample(example);
			} else {
				result = mapper.select(vo);
			}
		} catch (Exception ex) {
			throw new BusinessException(ErrorCode.EXCEPTION_CODE_QUERY_FAIL, "查询失败，原因：" + ex.getMessage(), null, ex);
		}
		List<D> resultList = d.fromVo(result);
		resultList = afterQuery(resultList, cfg);
		return resultList;
	}

	@Override
	public D findById(Long key) throws BusinessException {
		V result;
		D queryDto = getInstance(dtoClazz);
		try {
			result = mapper.selectByPrimaryKey(key);
		} catch (Exception ex) {
			throw new BusinessException(ErrorCode.EXCEPTION_CODE_QUERY_FAIL, "查询失败，原因：" + ex.getMessage(), null, ex);
		}
		queryDto = queryDto.fromVo(result);
		queryDto = afterQueryByPrimaryKey(queryDto, InjectorConfig.RefLevel(1));
		return queryDto;
	}

	@Override
	public D queryByPrimaryKey(D dto) throws BusinessException {
		return queryByPrimaryKeyCfg(dto, InjectorConfig.RefLevel(1));
	}

	@Override
	public List<D> queryByPrimaryKeysCfg(List<D> dtos, InjectorConfig cfg) throws BusinessException {
		List<V> result = null;
		try {
			if (cfg != null && cfg.getSelectFields() != null) {
				V vo = this.getInstance(voClazz);
				Example example = cfg.getExample(voClazz, vo);
				example = getPrimaryKeyExample(dtos, example, false);
				result = mapper.selectByExample(example);
				if (cfg.hasSubSelectFields()) {
					cfg.setRefLevel(1);
				}
			} else {
				Example example = getPrimaryKeyExample(dtos, null, false);
				result = mapper.selectByExample(example);
			}
		} catch (Exception ex) {
			throw new BusinessException(ErrorCode.EXCEPTION_CODE_QUERY_FAIL, "查询失败，原因：" + ex.getMessage(), null, ex);
		}
		List<D> resultDtos = this.getInstance(dtoClazz).fromVo(result);
		resultDtos = afterQuery(resultDtos, cfg);
		return resultDtos;
	}

	@Override
	public D queryByPrimaryKeyCfg(D dto, InjectorConfig cfg) throws BusinessException {
		checkNull(dto);
		// TODO ValidateUtil.validate(dto, Update.class);
		V vo = dto.toVo(voClazz);
		V result = null;
		try {
			if (cfg != null && cfg.getSelectFields() != null) {
				Example example = cfg.getExample(voClazz, vo);
				example = getPrimaryKeyExample(dto, example, false);
				List<V> list = mapper.selectByExample(example);
				if (list != null && list.size() == 1) {
					result = list.get(0);
				}
				if (cfg.hasSubSelectFields()) {
					cfg.setRefLevel(1);
				}
			} else {
				result = mapper.selectByPrimaryKey(vo);
			}
		} catch (Exception ex) {
			throw new BusinessException(ErrorCode.EXCEPTION_CODE_QUERY_FAIL, "查询失败，原因：" + ex.getMessage(), null, ex);
		}
		D queryDto = dto.fromVo(result);
		queryDto = afterQueryByPrimaryKey(queryDto, cfg);
		return queryDto;
	}

	@Override
	public PageResultDto<D> queryPageList(HashMap<String, Object> filter, PageObject pagination)
			throws BusinessException {

		PageQueryDto<D> queryDto = new PageQueryDto<>();
		queryDto.setFilter(filter);
		queryDto.setPage(pagination);
		if (filter != null && filter.containsKey("searchFields")) {
			String[] searchFields = (String[]) filter.get("searchFields");
			queryDto.setSearchFields(searchFields);
		}

		return queryPageList(queryDto);
		// TODO ValidateUtil.validate(queryDto, groups);
	}

	public PageResultDto<D> queryPageList(PageQueryDto<D> queryDto) throws BusinessException {
		// TODO ValidateUtil.validate(queryDto, groups);

		InjectorConfig cfg = InjectorConfig.RefLevel(1);
		String selectFields = null;
		HashMap<String, Object> filter = queryDto.getFilter();
		if (filter != null && filter.get("selectFields") != null) {
			selectFields = filter.get("selectFields").toString();
			cfg.setSelectFields(selectFields);
		}
		beforeQueryPageList(queryDto, cfg);
		checkNull(queryDto);
		PageObject page = queryDto.getPage();
		if (page == null) {
			page = new PageObject();
			page.setPageSize(200);// 默认页宽200条。
			queryDto.setPage(page);
		}
		D dto = queryDto.getEntity();
		Example example = queryDto.getExample();
		PageHelper.startPage(page);
		List<V> resultList = null;
		if (dto != null) {
			example = getAndExampleByDto(dto, null);
		} else {
			dto = this.getInstance(dtoClazz);
		}
		if (example == null) {
			example = getAndExampleByDto(filter, null);
		}
		if (selectFields != null) {
			List<String> fields = cfg.getJsonSelectorDto().getFields();
			if (fields != null) {
				example.selectProperties(fields.toArray(new String[fields.size()]));
			}
		}
		resultList = mapper.selectByExample(example);
		page.setSumCloum((int) ((Page<V>) resultList).getTotal());
		PageResultDto<D> pageResult = new PageResultDto<>();
		pageResult.setPage(page);
		List<D> dtoList = dto.fromVo(resultList);
		pageResult.setList(dtoList);
		afterQuery(dtoList, cfg);
		return pageResult;
	}

	/**
	 * 检查对象是否非空
	 * 
	 * @param obj
	 *            检查的对象
	 * @throws NullPointerException
	 *             如果 {@code obj} 为 {@code null}
	 */
	protected void checkNull(Object obj) throws NullPointerException {
		checkNull(obj, "");
	}

	/**
	 * 检查对象是否非空
	 * 
	 * @param obj
	 *            检查的对象
	 * @param message
	 *            异常提示信息
	 * @throws NullPointerException
	 *             如果 {@code obj} 为 {@code null}
	 */
	protected void checkNull(Object obj, String message) throws NullPointerException {
		if (StringUtil.isEmtryStr(message)) {
			message = "参数不能为空";
		}
		if (obj == null) {
			throw new NullPointerException(message);
		}
	}

	protected <T> T getInstance(Class<T> clazz) {
		try {
			return clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException ex) {
			throw new BusinessException("", "", null, ex);
		}
	}

	/**
	 * 按照主键查询实体，查询 sql 增加排他锁，没有找到返回 null
	 * 
	 * @param dto
	 *            需要传入主键对应的属性
	 * @return 查询到的实体信息
	 */
	protected D queryByPrimaryKey4Update(D dto) {
		Example example = getPrimaryKeyExample(dto, null, false);
		example.isForUpdate();
		List<V> resultList;
		try {
			resultList = mapper.selectByExample(example);
		} catch (Exception ex) {
			throw new BusinessException(ErrorCode.EXCEPTION_CODE_QUERY_FAIL, "查询失败，原因：" + ex.getMessage(), null, ex);
		}
		if (resultList.isEmpty()) {
			return null;
		}
		return dto.fromVo(resultList.get(0));
	}

	@Override
	// @Transactional
	public List<D> createBatch(List<D> dtos,Token token) throws BusinessException {
		if (dtos == null || dtos.isEmpty()) {
			return dtos;
		}
		List<V> vos = new ArrayList<>();
		for (D dto : dtos) {
			checkNull(dto);
			setDefaultValue4Create(dto);
			// ValidateUtil.validate(dto, Create.class);
			beforeCreate(dto);
			V vo = dto.toVo(voClazz);
			vos.add(vo);
		}
		beforeCreateBatch(dtos, token);
		int result = 0;
		try {
			for (int fromIndex = 0, size = dtos.size(); fromIndex < size;) {
				int toIndex = fromIndex + Const.DB_BATCH_OP_MAX_COUNT;
				if (toIndex > size) {
					toIndex = size;
				}
				result += mapper.insertBatch(vos.subList(fromIndex, toIndex));
				fromIndex = toIndex;
			}
		} catch (Exception ex) {
			String message = "批量新增失败，原因：" + ex.getMessage();
			throw new BusinessException(ErrorCode.EXCEPTION_CODE_CREATE_FAIL, message, null, ex);
		}
		if (result != dtos.size()) {
			throw new BusinessException(ErrorCode.EXCEPTION_CODE_CREATE_FAIL, "批量新增失败，原因：没有全部新增成功");
		}
		List<D> newDtos = afterCreateBatch(dtos, token);
		return newDtos;
	}

	@Override
	@Transactional
	public List<D> deleteBatch(List<D> dtos,Token token) throws BusinessException {
		if (dtos == null || dtos.isEmpty()) {
			return dtos;
		}
		List<V> vos = new ArrayList<>();
		for (D dto : dtos) {
			checkNull(dto);
			// ValidateUtil.validate(dto, Delete.class);
			beforeDelete(dto);
			V vo = dto.toVo(voClazz);
			vos.add(vo);
		}
		int result = 0;
		for (int fromIndex = 0, size = dtos.size(); fromIndex < size;) {
			int toIndex = fromIndex + Const.DB_BATCH_OP_MAX_COUNT;
			if (toIndex > size) {
				toIndex = size;
			}
			result += mapper.deleteBatch(vos.subList(fromIndex, toIndex));
			fromIndex = toIndex;
		}
		if (result != dtos.size()) {
			throw new BusinessException(ErrorCode.EXCEPTION_CODE_DELETE_FAIL, "批量删除失败");
		}
		return dtos;
	}

	@Override
	@Transactional
	public List<D> deleteBatchByPrimaryKey(List<D> dtos,Token token) throws BusinessException {
		if (dtos == null || dtos.isEmpty()) {
			return dtos;
		}
		List<V> vos = new ArrayList<>();
		for (D dto : dtos) {
			checkNull(dto);
			// ValidateUtil.validate(dto, Delete.class);
			beforeDelete(dto);
			V vo = dto.toVo(voClazz);
			vos.add(vo);
		}
		int result = 0;
		for (int fromIndex = 0, size = dtos.size(); fromIndex < size;) {
			int toIndex = fromIndex + Const.DB_BATCH_OP_MAX_COUNT;
			if (toIndex > size) {
				toIndex = size;
			}
			result += mapper.deleteBatchByPrimaryKey(vos.subList(fromIndex, toIndex));
			fromIndex = toIndex;
		}
		if (result != dtos.size()) {
			throw new BusinessException(ErrorCode.EXCEPTION_CODE_DELETE_FAIL, "批量删除失败");
		}
		return dtos;
	}

	@Override
	public D prev(Long id) {
		PageQueryDto<D> queryDto = QueryPageListUtil.queryOne(voClazz);
		queryDto.getExample().createCriteria().andLessThan("id", id);
		queryDto.getExample().setOrderByClause("id desc");
		PageResultDto<D> result = this.queryPageList(queryDto);
		if (result.getList() != null && result.getList().size() > 0) {
			return result.getList().get(0);
		}
		return null;
	}

	@Override
	public D next(Long id) {
		PageQueryDto<D> queryDto = QueryPageListUtil.queryOne(voClazz);
		queryDto.getExample().createCriteria().andGreaterThan("id", id);
		queryDto.getExample().setOrderByClause("id asc");
		PageResultDto<D> result = this.queryPageList(queryDto);
		if (result.getList() != null && result.getList().size() > 0) {
			return result.getList().get(0);
		}
		return null;
	}
}
