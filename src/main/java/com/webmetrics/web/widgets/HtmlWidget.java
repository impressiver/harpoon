package com.webmetrics.web.widgets;

import com.google.sitebricks.Renderable;
import com.google.sitebricks.Respond;
import com.google.sitebricks.StringBuilderRespond;
import com.google.sitebricks.rendering.control.WidgetChain;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.HTMLWriter;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Set;

public abstract class HtmlWidget implements Renderable
{
	protected final WidgetChain chain;

	public HtmlWidget(WidgetChain chain)
	{
		this.chain = chain;
	}
	
	public void render(Object bound, Respond respond)
	{
		Document document = getSourceDocument(bound);
		transform(document);
		respond.write(toHtml(document.getRootElement()));
	}
	
	protected abstract void transform(Document document);

	protected Document getSourceDocument(Object bound)
	{
		StringBuilderRespond builderRespond = new StringBuilderRespond();
		chain.render(bound, builderRespond);
		String string = builderRespond.toString();

	    SAXReader reader = new SAXReader();
	    Document document;
		try
		{
			document = reader.read(new StringReader(string));
		}
		catch (DocumentException e)
		{
			throw new RuntimeException(e);
		}
		return document;
	}

	protected String toHtml(Element select)
	{
		StringWriter writer = new StringWriter();
		HTMLWriter html = new HTMLWriter(writer);
		try
		{
			html.write(select);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		
		String string = writer.toString();
		return string;
	}

	public <T extends Renderable> Set<T> collect(Class<T> clazz)
	{
		return Collections.emptySet();
	}
}
