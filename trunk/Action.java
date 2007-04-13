import java.util.*;

public class Action
{
	private String name;
	private int id;
	private StringFunction worldFunction;
	private StringFunction repFunction;
	private IncrementedArray<Object> parameters = null;
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
		parameters = new IncrementedArray<Object>(Constants.DEFAULT_ACTION_PARAMETERS_SIZE);
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
	public GameElement[] getNouns()
	{
		return nouns;
	}
	public void setNouns(GameElement[] _nouns)
	{
		nouns = _nouns;
	}

	public IncrementedArray<Object> parameters()
	{
		return parameters;
	}
	public void parameters(Object[] _parameters)
	{
		parameters = new IncrementedArray<Object>(_parameters, Constants.DEFAULT_ACTION_PARAMETERS_SIZE);
	}

	public boolean equals(String _name, GameElement[] _nouns, Object[] _parameters)
	{
		if(!name.equals(_name))
			return false;

		if(_nouns != null)
		{
			if(nouns == null)
				return false;

			if(nouns.length < _nouns.length)
				return false;

			for(int i = _nouns.length-1; i>=0; i--)
				if(nouns[i] != _nouns[i])
					return false;
		}

		if(_parameters != null)
		{
			if(parameters == null)
				return false;

			if(parameters.length < _parameters.length)
				return false;

			for(int i = _parameters.length-1; i>=0; i--)
			{
				if(!_parameters[i].equals(parameters.get(i)))
					return false;
			}
		}

		return true;
	}

	public String toString()
	{
		String stringAction = "Information on the " + name + " action\n";
		if(worldFunction != null)
			stringAction += "World Function in language " + worldFunction.getLanguage() + ":\n=======================\n" + worldFunction.getFunction() + "\n=======================\n";
		if(repFunction != null)
			stringAction += "Represenation Function in language " + repFunction.getLanguage() + ":\n=======================\n" + repFunction.getFunction() + "\n=======================\n";
		
		return stringAction;
	}

}
