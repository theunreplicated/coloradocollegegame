import java.util.HashMap;

public class Action
{
	private String name;
	private int id;
	private StringFunction worldFunction;
	private StringFunction repFunction;
	private HashMap<String, Object> parameters = null;
	private GameElement[] nouns = null;

	public Action(String _name, StringFunction _world, StringFunction _rep)
	{
		name = _name;
		worldFunction = _world;
		repFunction = _rep;
	}

	public Action(Action a)
	{
		name = a.getName();
		id = a.getId();
		worldFunction = a.getWorldFunction();
		repFunction = a.getRepFunction();
	}

	public Object getParameter(String key)
	{
		return(parameters.get(key));
	}

	public void putParameter(String key, Object value)
	{
		parameters.put(key,value);
	}

	public String getName()
	{
		return name;
	}
	public int getId()
	{
		return id;
	}
	public void setId(int _id)
	{
		id = _id;
	}

	public StringFunction getWorldFunction()
	{
		return worldFunction;
	}
	public StringFunction getRepFunction()
	{
		return repFunction;
	}
	public HashMap<String,Object> getParameters()
	{
		return parameters;
	}
	public GameElement[] getNouns()
	{
		return nouns;
	}
	public void setNouns(GameElement[] _nouns)
	{
		nouns = _nouns;
	}
}
