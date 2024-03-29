package com.ontimize.jee.server.spring.namespace;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

public class OntimizeDMSConfigurationBeanDefinitionParser extends AbstractSimpleBeanDefinitionParser {

    @Override
    protected Class<?> getBeanClass(Element element) {
        return OntimizeDMSConfiguration.class;
    }

    @Override
    protected void doParse(Element element, ParserContext ctx, BeanDefinitionBuilder builder) {

        // We want any parsing to occur as a child of this tag so we need to
        // make
        // a new one that has this as it's owner/parent
        ParserContext nestedCtx = new ParserContext(ctx.getReaderContext(), ctx.getDelegate(),
                builder.getBeanDefinition());

        // Support for report
        Element report = DomUtils.getChildElementByTagName(element, "dms");
        if (report != null) {
            // Just make a new Parser for each one and let the parser do the
            // work
            DMSBeanDefinitionParser ro = new DMSBeanDefinitionParser();
            builder.addPropertyValue("dmsConfiguration", ro.parse(report, nestedCtx));
        }
        builder.setLazyInit(true);
    }

}
