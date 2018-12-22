/**
 * 实体相关操作基类，基于 MyBatis Common Mapper 和  Mybatis-PageHelper 插件简单封装实现
 * <p>相关项目地址 : <a href="https://github.com/abel533/Mapper" target="_blank">https://github.com/abel533/Mapper</a>
 * <p><a href="https://github.com/pagehelper/Mybatis-PageHelper" target="_blank">https://github.com/pagehelper/Mybatis-PageHelper</a>
 * 
 * <p>dao 中定义了实体数据库访问接口基类
 * <p>impl 中定义了 实体服务实现基类
 * <p>itf 中定义了实体服务接口基类
 * <p>定义自己的 vo、dto 实体继承基类 VO、DTO
 * <p> vo 实体类增加注解 @Table(name = "数据库表名")，数据库字段名直接按照 vo 实体属性名称定义，主键字段增加注解 @Id
 * <p> dto 实体类增加验证相关的注解，设置验证分组
 * <p>定义自己的 dao、impl、ift 实现继承基类
 * <p> dao 中如果需要实现非基类的功能，需要按照 mybatis 的方式，新增 xml 文件配置对应 sql，<b>新增接口方法名称不要和基类已有接口方法重名</b>
 * <p>mybatis 配置 org.mybatis.spring.mapper.MapperScannerConfigurer 调整为 tk.mybatis.spring.mapper.MapperScannerConfigurer
 * 增加
 * <pre>
 * {@code
 * <property name="properties">
 *     <value>
 *         mappers=com.mk.eap.entity.dao.EntityMapper
 *         style=lowercase
 *     </value>
 * </property>
 * }
 * </pre>
 * 配置 org.mybatis.spring.SqlSessionFactoryBean 增加分页插件
 * <pre>
 * {@code
 * <property name="plugins">
 *     <array>
 *         <bean class="com.github.pagehelper.PageInterceptor">
 *             <property name="properties">
 *                 <value>
 *                     helperDialect=mysql
 *                     params=pageNum=currentPage;pageSize=pageSize;
 *                 </value>
 *             </property>
 *         </bean>
 *     </array>
 * </property>
 * }
 * </pre>
 * @author gaoxue
 *
 */
package com.mk.eap.entity;
