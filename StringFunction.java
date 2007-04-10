import javax.xml.parsers.*;
import org.w3c.dom.*;

public class StringFunction
{
	private String language;
	private String function;

	public StringFunction(String _language, String _function)
	{
		language = _language;
		function = _function;
	}

	public StringFunction(Element _xml_element)
	{
		language = _xml_element.getAttribute("language");
		function = _xml_element.getTextContent();
	}

	public String getLanguage()
	{
		return language;
	}

	public String getFunction()
	{
		return function;
	}
}
