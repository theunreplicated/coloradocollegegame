//import javax.xml.parsers.*;//never used
import org.w3c.dom.*;
import java.io.Serializable;

public class StringFunction implements Serializable
{
	static final long serialVersionUID = 5708940171917759139L;
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
