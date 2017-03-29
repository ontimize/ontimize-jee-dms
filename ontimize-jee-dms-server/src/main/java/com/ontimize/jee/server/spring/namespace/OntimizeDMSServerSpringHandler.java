package com.ontimize.jee.server.spring.namespace;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class OntimizeDMSServerSpringHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		this.registerBeanDefinitionParser("ontimize-dms-configuration", new OntimizeDMSConfigurationBeanDefinitionParser());
		this.registerBeanDefinitionParser("fixed-property", new FixedPropertyBeanDefinitionParser());
		this.registerBeanDefinitionParser("database-property", new DatabasePropertyBeanDefinitionParser());
	}

}
