package com.mk.eap.component.template.impl;

import java.io.IOException;
import java.io.StringWriter;

import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.mk.eap.component.template.itf.ITemplateService;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

@Component
@Service
public class TemplateServiceImpl implements ITemplateService {

	private static Configuration freemarkerCfg = null;

	static {
		try {
			freemarkerCfg = new Configuration();
			freemarkerCfg.setDefaultEncoding("UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
	};

	// 使用freemarker得到html内容
	@Override
	public String bindData(String name, String content, Object data)  {

		StringTemplateLoader stringLoader = new StringTemplateLoader();
		stringLoader.putTemplate(name, content);

		freemarkerCfg.setTemplateLoader(stringLoader);
		StringWriter writer = new StringWriter();
		String html = null;
		try {
			Template tpl = freemarkerCfg.getTemplate(name, "UTF-8");
			tpl.process(data, writer);
			writer.flush();
			html = writer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return html;
	}

}
