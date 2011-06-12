package com.webmetrics.harpoon.web.widgets;

import com.google.inject.Inject;
import com.google.sitebricks.Evaluator;
import com.google.sitebricks.Respond;
import com.google.sitebricks.compiler.Parsing;
import com.google.sitebricks.conversion.TypeConverter;
import com.google.sitebricks.rendering.EmbedAs;
import com.google.sitebricks.rendering.control.WidgetChain;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@EmbedAs("Select")
public class SelectWidget extends HtmlWidget
{
    private final Evaluator evaluator;

	private String itemsExpression;
	private String selectedExpression;
	private String valueExpression;
	private String textExpression;
	
	@Inject TypeConverter converter;

	private Collection<?> items;

	private Object current;

	private Object bound;

    public SelectWidget(WidgetChain widgetChain, String expression, Evaluator evaluator) {
    	super(widgetChain);
    	
        this.evaluator = evaluator;

        Map<String,String> map = Parsing.toBindMap(expression);
        itemsExpression = map.get("options");
        selectedExpression = map.get("selected");
        valueExpression = map.get("value");
        if (valueExpression != null)
        	valueExpression = Parsing.stripQuotes(valueExpression);
        textExpression = map.get("text");
        if (textExpression != null)
        	textExpression = Parsing.stripQuotes(textExpression);
    }

	@Override
	public void render(Object bound, Respond respond)
	{
		this.bound = bound;
		items = (Collection<?>) evaluator.evaluate(itemsExpression, bound);
		if (selectedExpression != null)
		{
			current = evaluator.evaluate(selectedExpression, bound);
		}
		super.render(bound, respond);
	}
	
	@Override
	protected void transform(Document document)
	{
		Element select = document.getRootElement();
		if (select.getName().equals("select") == false)
		{
			throw new IllegalStateException("Expected select tag");
		}
		
		for (Object item : items)
		{
			Element option = select.addElement("option");
			String value;
			Map<String, Object> context = new HashMap<String, Object>();
			context.put("item", item);
			context.put("page", bound);
			if (valueExpression != null)
			{
				Object raw = evaluator.evaluate(valueExpression, context);
				value = converter.convert(raw, String.class);
				option.addAttribute("value", value);
			}

			String text;
			if (textExpression != null)
			{
				Object raw = evaluator.evaluate(textExpression, context);
				text = converter.convert(raw, String.class);
			}
			else
			{
				text = item.toString();
			}
			
			option.setText(text);
			
			if (item.equals(current))
			{
				option.addAttribute("selected", "selected");
			}
		}
	}

}
