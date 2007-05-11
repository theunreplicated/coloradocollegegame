import java.util.Date;
import javax.script.*;
public class Resolver extends Thread
{
	private RuleSet rules;
	private RuleFactory ruleFactory;//never read locally
	private RepresentationResolver repResolver = null;
	private ActionFactory actionFactory;
	private ElementFactory elementFactory;
	private ScriptEngineManager manager;
	public World world;
	private IO io;
	private Logger myLogger;
	private ElementStack<Action> actionStack;

	public Resolver(World _world, RuleFactory _rf, ActionFactory _af, ElementFactory _ef, Logger _myLogger)
	{
		ruleFactory = _rf;
		actionFactory = _af;
		elementFactory = _ef;
		rules = _rf.getRuleSet("");
		manager = new ScriptEngineManager();
		world = _world;
		myLogger = _myLogger;
		actionStack = new ElementStack<Action>();

		// Add logger at global scope
		manager.put("myLogger", myLogger);

		// Add other useful stuff at global scope
		manager.put("Constants", new Constants());
		manager.put("Quaternions", new Quaternions());
		manager.put("VectorUtils", new VectorUtils());
		manager.put("GameElement", GameElement.class);
		manager.put("actionStack", actionStack);
		manager.put("actionFactory", actionFactory);
	}

	public void setRepresentationResolver(RepresentationResolver _repResolver)
	{
		repResolver = _repResolver;
	}
	public void setIO(IO _io)
	{
		io = _io;
	}

	public void run()
	{
		Action a;
		while(true)
		{
			while( (a = actionStack.pop()) == null)
			{
				try
				{
					Thread.sleep(Constants.SLEEP_TIME);
				}
				catch(InterruptedException ie)
				{
					myLogger.message("Resolver interrupted. Quitting.\n", false);
					return;
				}
			}
			long now = (new Date()).getTime();
			long sleepTime = a.getSleepTime();
			if(sleepTime > now)
			{
				long untilNextAction = sleepTime - now;
				Action _a;
				addAction(a);
				while( (_a = actionStack.pop()) != a)
				{
					sleepTime = _a.getSleepTime();
					if(sleepTime > now)
					{
						if(sleepTime - now < untilNextAction)
						{
							untilNextAction = sleepTime - now;
						}
						addAction(_a);
					}
					else
					{
						sleepTime = 0;
						a = _a;
						break;
					}
				}
				if(sleepTime != 0)
				{
					try
					{
						Thread.sleep(sleepTime);
						continue;
					}
					catch(InterruptedException ie)
					{
						myLogger.message("Resolver interrupted. Quitting.\n", false);
						return;
					}
				}
			}
			myLogger.message("Resolver running on action " + a.getName(), false);
			parse(a);
		}
	}

  /* depracated 
	@SuppressWarnings("unchecked")
	private int[] parse(Object _actions)
	{
		if(!(_actions instanceof IncrementedArray))
		{
			if(_actions instanceof Action)
				return new int[]{parse((Action) _actions)};

			myLogger.message("OLD Resolver parse received a bad message, ignoring: " + _actions + "\n", true);
			return null;
		}

		IncrementedArray<WritableAction> actions = (IncrementedArray<WritableAction>) _actions;
		int[] results = new int[actions.length];

		for(int i = 0; i < actions.length; i++)
			results[i] = parse(actions.get(i).getAction(world,actionFactory));

		return results;
	}
	*/

	private int parse(Action action)
	{
		IncrementedArray<Action> _actions = action.getDependentActions();
		Action[] actions = new Action[_actions.length+1];
		actions[0] = action;
		if(_actions.length != 0)
		{
			for(int i = 1; i < actions.length; i++)
			{
				actions[i] = _actions.get(i-1);
			}
		}
		Rule[][] applicable = new Rule[actions.length][];
		IncrementedArray<GameElement>[] relevantElements = new IncrementedArray[actions.length];
		GameElement[][] nouns = new GameElement[actions.length][];
		for(int i = 0; i < actions.length; i++)
		{
			nouns[i] = actions[i].getNouns();

			int[] needles = new int[Constants.SENTENCE_LENGTH];
			needles[0] = actionFactory.getType(actions[i].getName());
			myLogger.message("Resolver parsing with needles: " + needles[0] + ",", false);
			if(nouns[i] != null)
			{
				for(int j = nouns[i].length-1; j >= 0; j--)
				{
					needles[j+1] = nouns[i][j].getTypeId();
					myLogger.message(needles[j+1]+",",false);
				}
			}
			myLogger.message("\n", false);

			applicable[i] = rules.getRules(needles);
			relevantElements[i] = new IncrementedArray<GameElement>(Constants.DEFAULT_RELEVANT_SIZE);
			if(nouns[i] != null)
			{
				GameElement first = world.getFirstElement();
				GameElement currentElement = first;
				do
				{
						GameElement subject = nouns[i][0];
						/*find relevent ojects and get rules that apply to this sentence*/
						if(currentElement != subject && subject.isRelevant(currentElement))
							relevantElements[i].add(currentElement);
				}
				while( (currentElement = currentElement.next) != first);
			}
		}
		return resolve(applicable,actions,relevantElements);
	}

	@SuppressWarnings("fallthrough")
	public int resolve( Rule[][] _rules, Action[] _actions, IncrementedArray<GameElement>[] _relevantElements)
	{
		ActionsHashMap actionsToSend = new ActionsHashMap(actionFactory);
		IncrementedArray returnVals;
		int status;
		int i;
		Action _action;
		Object[] message;
		GameElement[] nouns, other;
		GameElement subject, directObject, indirectObject;
		boolean runAgain = false;
		for(i = 0; i < _actions.length; i++)
		{
			_action = _actions[i];
			message = _action.parameters().pack();
			nouns = _action.getNouns();
			subject = directObject = indirectObject = null;
			other = null;
		
			if(nouns != null)
			{
				switch(nouns.length)
				{
					default:
						other = new GameElement[nouns.length-3];
						System.arraycopy(nouns,3,other,0,other.length);
					case 3:
						indirectObject = nouns[2];
					case 2:
						directObject = nouns[1];
					case 1:
						subject = nouns[0];
						break;
				}
			}

			for(Rule rule : _rules[i])
			{
				try {
					ScriptEngine engine = manager.getEngineByName(rule.language());
					status = Constants.SUCCESS;
					engine.put("action",_action);
					engine.put("owner",rule.owner());
					engine.put("subject",subject);
					engine.put("directObject",directObject);
					engine.put("indirectObject",indirectObject);
					engine.put("other",other);
					engine.put("relevant",_relevantElements[i]);
					engine.put("argv",message);
					engine.put("status",status);
					engine.eval(rule.function());
					Object _status = engine.get("status");
					if(_status instanceof Integer)
						status = ((Integer) _status).intValue();
					else if(_status instanceof Double)
						status = ((Double) _status).intValue();
					if(status == Constants.NEW_DEPENDENCY)
					{
						_actions[0].getDependentActions().add(_action.getDependentActions());
						_action.clearDependentActions();
						runAgain = true;
					}
					else if(status != Constants.SUCCESS)
						return status;
				}
				catch(ScriptException se)
				{
					myLogger.message("Script error: " + se.getMessage() + "\n",true);
					System.out.println("error in Resolver (1)");
				}
			}
		}
		if(runAgain)
		{
			actionStack.push(_actions[0]);
			return Constants.NEW_DEPENDENCY;
		}

		myLogger.message("Resolver is starting to handle actions...\n", false);
		for(i = 0; i < _actions.length; i++)
		{
			_action = _actions[i];
			if(_action.getWorldFunction() != null)
			{
				nouns = _action.getNouns();
				other = null;
				subject = directObject = indirectObject = null;
				if(nouns != null)
				{
					switch(nouns.length)
					{
						default:
							other = new GameElement[nouns.length-3];
							System.arraycopy(nouns,3,other,0,other.length);
						case 3:
							indirectObject = nouns[2];
						case 2:
							directObject = nouns[1];
						case 1:
							subject = nouns[0];
							break;
					}
				}

				StringFunction worldFunc = _action.getWorldFunction();
				try {
					returnVals = new IncrementedArray(Constants.DEFAULT_RETURN_VALS_LENGTH);
					status = Constants.SUCCESS;
					ScriptEngine engine = manager.getEngineByName(worldFunc.getLanguage());
					engine.put("subject",subject);
					engine.put("directObject",directObject);
					engine.put("indirectObject",indirectObject);
					engine.put("other",other);
					engine.put("argv",_action.parameters().pack());
					engine.put("returnVals",returnVals);
					engine.put("status", status);
					engine.eval(worldFunc.getFunction());
					_action.setLastRun(new Date());
					Object _status = engine.get("status");
					if(_status instanceof Integer)
						status = ((Integer) _status).intValue();
					else if(_status instanceof Double)
						status = ((Double) _status).intValue();
					if(status == Constants.ADD_ELEMENTS)
					{
						GameElement newElement = null;
						for(i = 0; i < returnVals.length; i++)
						{
							Object _message = returnVals.get(i);
							if(_message instanceof Object[])
							{
								Object[] elementInfo = (Object[]) _message;
								int start = 0;
								int _id = ((Integer) elementInfo[start++]).intValue();
								int _type;
								if(elementInfo[start] instanceof String)
								{
									_type = elementFactory.getType((String) elementInfo[start++]);
								}
								else
								{
									_type = ((Integer) elementInfo[start++]).intValue();
								}
								float[] _pos = (float[]) elementInfo[start++];
								float[] _fac = (float[]) elementInfo[start++];
								newElement = elementFactory.getGameElement(_type);
								newElement.id(_id);
								newElement.setPosition(_pos);
								newElement.setFacing(_fac);
							}
							else if(_message instanceof GameElement)
							{
								newElement = (GameElement) _message;
							}

							// this should eventually move to the "add element" rule
							if(newElement.id() < Constants.ELEMENT_ID_PADDING+Constants.MAX_CONNECTIONS+1) //so that the avatars have different colors!
								newElement.attribute("color",Constants.getColorByClientID(newElement.id()));

							world.addElement(newElement);
						}
					}
					else if(status == Constants.REMOVE_ELEMENTS)
					{
						for(i = 0; i < returnVals.length; i++)
						{
							Object _message = returnVals.get(i);
							world.removeElement((GameElement) _message);
						}
					}
					else if(status == Constants.HANDLE_AGAIN)
					{
						addAction(_action);
					}

					if(_action.getToSend() != null)
					{
						actionsToSend.add(_action.getToSend());
					}
				}
				catch(ScriptException se)
				{
					myLogger.message("Script error: " + se.getMessage() + "\n",true);
					System.out.println("error in Resolver (2)");

				}

			}

			if(_action.getRepFunction() != null && repResolver != null)
			{
				repResolver.resolve(_action);
			}
		}

		if(actionsToSend.size() > 0)
		{
			IncrementedArray<Action> act = actionsToSend.getAll();
			IncrementedArray<WritableAction> write_act = new IncrementedArray<WritableAction>(act.length);
			for(i = 0; i < act.length; i++)
			{
				write_act.add(new WritableAction(act.get(i)));
			}
			io.send(write_act);
		}

		return Constants.SUCCESS;
	}

	public void addAction(Action a)
	{
		actionStack.unshift(a);
	}

	@SuppressWarnings("unchecked")
	public void addAction(Object _actions)
	{
		if(!(_actions instanceof IncrementedArray))
		{
			if(_actions instanceof Action)
			{
				addAction((Action) _actions);
				return;
			}

			myLogger.message("NEW Resolver parse received a bad message, ignoring: " + _actions + "\n", true);
			return;
		}

		IncrementedArray<WritableAction> actions = (IncrementedArray<WritableAction>) _actions;
		for(int i = 0; i < actions.length; i++)
			addAction(actions.get(i).getAction(world,actionFactory));

	}
}
