//import java.util.*;//never used
import javax.script.*;
public class Resolver
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

	public Resolver(World _world, RuleFactory _rf, ActionFactory _af, ElementFactory _ef, Logger _myLogger)
	{
		ruleFactory = _rf;
		actionFactory = _af;
		elementFactory = _ef;
		rules = _rf.getRuleSet("");
		manager = new ScriptEngineManager();
		world = _world;
		myLogger = _myLogger;

		// Add logger at global scope
		manager.put("myLogger", myLogger);

		// Add static stuff at global scope
		manager.put("Constants", new Constants());
		manager.put("Quaternions", new Quaternions());
		manager.put("VectorUtils", new VectorUtils());
		manager.put("GameElement", GameElement.class);
	}

	public void setRepresentationResolver(RepresentationResolver _repResolver)
	{
		repResolver = _repResolver;
	}
	public void setIO(IO _io)
	{
		io = _io;
	}

	@SuppressWarnings("unchecked")
	public int[] parse(Object _actions)
	{
		if(!(_actions instanceof IncrementedArray))
		{
			if(_actions instanceof Action)
				return new int[]{parse((Action) _actions)};

			myLogger.message("Resolver parse received a bad message, ignoring: " + _actions + "\n", true);
			return null;
		}

		IncrementedArray<WritableAction> actions = (IncrementedArray<WritableAction>) _actions;
		int[] results = new int[actions.length];

		for(int i = 0; i < actions.length; i++)
			results[i] = parse(actions.get(i).getAction(world,actionFactory));

		return results;
	}

	public int parse(Action action)
	{
		GameElement[] nouns = action.getNouns();

		int[] needles = new int[Constants.SENTENCE_LENGTH];
		needles[0] = actionFactory.getType(action.getName());
		myLogger.message("Resolver parsing with needles: " + needles[0] + ",", false);
		if(nouns != null)
		{
			for(int i = nouns.length-1; i >= 0; i--)
			{
				needles[i+1] = nouns[i].getTypeId();
				myLogger.message(needles[i+1]+",",false);
			}
		}
		myLogger.message("\n", false);

		Rule[] applicable = rules.getRules(needles);
		IncrementedArray<GameElement> relevantElements = new IncrementedArray<GameElement>(Constants.DEFAULT_RELEVANT_SIZE);
		if(nouns != null)
		{
			GameElement subject = nouns[0];
			GameElement first = world.getFirstElement();
			GameElement currentElement = first;
			do
			{
				/*find relevent ojects and get rules that apply to this sentence*/
				if(currentElement != subject && subject.isRelevant(currentElement))
					relevantElements.add(currentElement);
			}
			while( (currentElement=currentElement.next) != first );
		}


		return resolve(applicable,action,relevantElements);
	}

	@SuppressWarnings("fallthrough")
	public int resolve( Rule[] _rules, Action _action, IncrementedArray<GameElement> _relevantElements)
	{
		Object[] message = _action.parameters().pack();
		GameElement[] nouns = _action.getNouns();
		GameElement subject = null, directObject = null, indirectObject = null;
		GameElement[] other = null;
		int status;
		IncrementedArray returnVals;
		ActionsHashMap myActions = new ActionsHashMap(actionFactory); 
		ActionsHashMap actionsToSend = new ActionsHashMap(actionFactory);
		int i;
		
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

		myActions.add(_action);

		for(Rule rule : _rules)
		{
			try {
				ScriptEngine engine = manager.getEngineByName(rule.language());
				status = Constants.SUCCESS;
				engine.put("owner",rule.owner());
				engine.put("subject",subject);
				engine.put("directObject",directObject);
				engine.put("indirectObject",indirectObject);
				engine.put("other",other);
				engine.put("relevant",_relevantElements);
				engine.put("argv",message);
				engine.put("myActions",myActions);
				engine.put("actionsToSend",actionsToSend);
				engine.put("status",status);
				engine.eval(rule.function());
				Object _status = engine.get("status");
				if(_status instanceof Integer)
					status = ((Integer) _status).intValue();
				else if(_status instanceof Double)
					status = ((Double) _status).intValue();
				if(status != Constants.SUCCESS)
					return status;
			}
			catch(ScriptException se)
			{
				myLogger.message("Script error: " + se.getMessage() + "\n",true);
				System.out.println("error in Resolver (1)");
				System.out.println(se);
			}

		}

		myLogger.message("Resolver is starting to handle actions...\n", false);
		IncrementedArray<Action> actionList = myActions.getAll();
		for(i = 0; i < actionList.length; i++)
		{
			_action = actionList.get(i);
			if(_action.getWorldFunction() != null)
			{
				nouns = _action.getNouns();
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
					Object _status = engine.get("status");
					if(_status instanceof Integer)
						status = ((Integer) _status).intValue();
					else if(_status instanceof Double)
						status = ((Double) _status).intValue();
					if(status == Constants.ADD_ELEMENTS)
					{
						GameElement newElement = null;
						//GameElement first = world.getFirstElement();//needed?
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
				}
				catch(ScriptException se)
				{
					myLogger.message("Script error: " + se.getMessage() + "\n",true);
					System.out.println("error in Resolver (2)");
					System.out.println(se);

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
}
