public class Rule
{
	private String function;
	private String language;
	public Rule(String _function, String _language)
	{
		function = _function;
		language = _language;
	}

	public String function()
	{
		return function;
	}

	public String language()
	{
		return language;
	}
}
