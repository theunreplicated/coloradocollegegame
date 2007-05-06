import java.io.Serializable;

public class WritableAction implements Serializable
{
	static final long serialVersionUID = 5401610720963064538L;
	private int id;
	private IncrementedArray<Object> parameters;
	private int[] nouns = null;

	public WritableAction(Action _action)
	{
		id = _action.getId();
		GameElement[] _nouns = _action.getNouns();
		if(_nouns != null)
		{
			nouns = new int[_nouns.length];
			for(int i = _nouns.length-1; i >= 0; i--)
			{
				nouns[i] = _nouns[i].id();
			}
		}

		IncrementedArray<Object> _parameters = _action.parameters();
		parameters = new IncrementedArray<Object>(_parameters.length);
		for(int i = 0; i < _parameters.length; i++)
		{
			Object o = _parameters.get(i);
			if(o instanceof GameElement)
			{
				GameElement ge = (GameElement) o;
				if(ge.attribute("write through") != null)
				{
					ge.removeAttribute("write through");
					parameters.add(ge);
				}
				else
				{
					parameters.add(Constants.UNIQUE_GE_PREFIX + ge.id());
				}
			}
			else
			{
				parameters.add(o);
			}
		}
	}


	public Action getAction(World w, ActionFactory af)
	{
		Action a = af.getAction(id);
		if(nouns != null)
		{
			GameElement[] _nouns = new GameElement[nouns.length];
			for(int i = nouns.length-1; i >= 0; i--)
			{
				_nouns[i] = w.getElementById(nouns[i]);
			}
			a.setNouns(_nouns);
		}

		IncrementedArray<Object> _parameters = new IncrementedArray<Object>(parameters.length);
		for(int i = 0; i < parameters.length; i++)
		{
			Object o = parameters.get(i);
			if(o instanceof String)
			{
				if( ( (String) o).startsWith(Constants.UNIQUE_GE_PREFIX))
				{
					String id =((String) o).substring(Constants.UNIQUE_GE_PREFIX.length()); 
					_parameters.add(w.getElementById(Integer.parseInt(id)));
					continue;
				}
			}
			_parameters.add(o);
		}
		a.parameters(_parameters);
		return a;
	}
}
