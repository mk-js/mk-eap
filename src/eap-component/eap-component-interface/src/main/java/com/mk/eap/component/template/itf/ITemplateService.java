package com.mk.eap.component.template.itf;

import java.io.IOException;

public interface ITemplateService {

	String bindData(String tmplName, String tmplContent, Object data);
}
