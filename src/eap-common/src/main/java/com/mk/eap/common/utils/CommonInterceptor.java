package com.mk.eap.common.utils;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSON;
import com.mk.eap.common.domain.ApiResult;
import com.mk.eap.common.domain.BusinessException; 

public class CommonInterceptor extends HandlerInterceptorAdapter {
	private final Logger log = LoggerFactory.getLogger(CommonInterceptor.class); 

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception { 
		//log.info("==============执行顺序: 3、afterCompletion================");    
		super.afterCompletion(request, response, handler, ex);
	}

	@Override
	public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception { 
		super.afterConcurrentHandlingStarted(request, response, handler);
	}

	 /** 
     * 在业务处理器处理请求执行完成后,生成视图之前执行的动作    
     * 可在modelAndView中加入数据，比如当前时间 
     */  
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception { 
		/*log.info("==============执行顺序: 2、postHandle================");    
        if(modelAndView != null){  //加入当前时间    
            modelAndView.addObject("var", "测试postHandle");    
        }   */
		super.postHandle(request, response, handler, modelAndView);
	}

	/**  
     * 在业务处理器处理请求之前被调用  
     * 如果返回false  
     *     从当前的拦截器往回执行所有拦截器的afterCompletion(),再退出拦截器链 
     * 如果返回true  
     *    执行下一个拦截器,直到所有的拦截器都执行完毕  
     *    再执行被拦截的Controller  
     *    然后进入拦截器链,  
     *    从最后一个拦截器往回执行所有的postHandle()  
     *    接着再从最后一个拦截器往回执行所有的afterCompletion()  
     */  
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		ApiResult result = null;
		  
    	String token = request.getHeader("token");
    	if(token!=null){
			try{ 
				List<Long> list= StringTokenizer.Default.getIdsArray(token);  
				request.getSession().setAttribute("userId",list.get(0)); 
				request.getSession().setAttribute("orgId",list.get(1));
			}catch(BusinessException ex){ 
	        	result = new ApiResult();
	        	result.setError(ex);
			}catch(Exception ex){
	        	result = new ApiResult();
	        	result.setError("10000", ex.getMessage(), ex); 
			}  
    	} 
        if(result != null){ 
        	//返回
            response.setCharacterEncoding("UTF-8");  
            response.getWriter().print(JSON.toJSONString(result.getResult())); 
            return false; 
        }
		return super.preHandle(request, response, handler);
	}

}
