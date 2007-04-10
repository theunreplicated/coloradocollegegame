public class Rule
{
	private StringFunction func;
  private GameElement owner = null;

	public Rule(StringFunction _func)
	{
		func = _func;
	}

	public Rule(StringFunction _func, GameElement _owner)
	{
		func = _func;
		owner = _owner;
	}

	public String function()
	{
		return func.getFunction();
	}

	public String language()
	{
		return func.getLanguage();
	}

	public GameElement owner()
	{
		return owner;
	}
}
