import java.util.Collection;
import java.util.HashMap;

public class ActionsHashMap
{
	private HashMap<String, IncrementedArray<Action>> actions;
	private ActionFactory actionFactory;

	public ActionsHashMap(ActionFactory _actionFactory)
	{
		actionFactory = _actionFactory;
		actions = new HashMap<String,IncrementedArray<Action>>();
	}

	public void add(Action a, Object name)
	{
		IncrementedArray<Action> list;
		if(actions.containsKey(name))
		{
			list = actions.get(name);
		}
		else
		{
			list = new IncrementedArray<Action>(Constants.DEFAULT_ACTION_HASHMAP_ENTRY_SIZE);
			actions.put(a.getName(), list);
		}
		list.add(a);
	}

	public void add(Action a)
	{
		add(a,a.getName());
	}

	public void add(Object name, GameElement[] nouns, Object[] parameters)
	{
		Action a = actionFactory.getAction(name);
		a.setNouns(nouns);
		a.parameters(parameters);
		add(a,name);
	}

	public void add(Object name, GameElement[] nouns)
	{
		add(name, nouns, null);
	}

	public void add(Object name, Object[] parameters)
	{
		add(name, null, parameters);
	}

	public void add(Object name)
	{
		add(name, null, null);
	}

	public boolean put(Action a, Object name)
	{
		if(actions.containsKey(name))
			return false;
		
		add(a,name);
		return true;
	}

	public boolean put(Action a)
	{
		return put(a, a.getName());
	}

	public boolean put(Object name, GameElement[] nouns, Object[] parameters)
	{
		if(actions.containsKey(name))
			return false;

		add(name, nouns, parameters);
		return true;
	}

	public boolean put(Object name, GameElement[] nouns)
	{
		if(actions.containsKey(name))
			return false;

		add(name, nouns, null);
		return true;
	}

	public boolean put(Object name, Object[] parameters)
	{
		if(actions.containsKey(name))
			return false;

		add(name, null, parameters);
		return true;
	}

	public boolean put(Object name)
	{
		if(actions.containsKey(name))
			return false;

		add(name, null, null);
		return true;
	}

	public IncrementedArray<Action> get(String name, GameElement[] nouns, Object[] parameters)
	{
		IncrementedArray<Action> current = actions.get(name);
		IncrementedArray<Action> matches = new IncrementedArray<Action>(Constants.DEFAULT_ACTION_HASHMAP_ENTRY_SIZE);

		if(current == null)
			return matches;

		for(int i = current.length-1; i >= 0; i--)
		{
			if(current.get(i).equals(name,nouns,parameters))
				matches.add(current.get(i));
		}

		return matches;

	}

	public IncrementedArray<Action> get(String name, GameElement[] nouns)
	{
		return get(name,nouns,null);
	}

	public IncrementedArray<Action> get(String name, Object[] parameters)
	{
		return get(name,null,parameters);
	}

	public IncrementedArray<Action> get(String name)
	{
		return get(name,null,null);
	}

	public IncrementedArray<Action> getAll()
	{

		IncrementedArray<Action> allActions = new IncrementedArray<Action>(Constants.DEFAULT_ACTION_LIST_SIZE);
		for(IncrementedArray<Action> values : actions.values())
		{
			allActions.add(values);
		}
		return allActions;
	}

	public int size()
	{
		return actions.size();
	}

}
