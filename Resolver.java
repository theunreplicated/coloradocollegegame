import java.util.*;
import javax.script.*;
public class Resolver
{
	RuleSet rules;
	RuleFactory ruleFactory;
	ActionFactory actionFactory;
	HashMap<Integer,GameElement> elementsHash;
	ScriptEngineManager manager;
	World world;
	Logger myLogger;

	public Resolver(World _world, RuleFactory _rf, ActionFactory _af, Logger _myLogger)
	{
		ruleFactory = _rf;
		actionFactory = _af;
		rules = _rf.getRuleSet("");
		elementsHash = _world.getElementsHash();
		manager = new ScriptEngineManager();
		world = _world;
		myLogger = _myLogger;
	}

	public int parseOld(Object[] message)
	{
		switch(((Integer) message[0]).intValue())
		{
			case Constants.MOVE_TO:
				world.setPosition( message, 1);
				break;
			case Constants.ROTATE_TO:
				world.setFacing( message, 1);
				break;
			case Constants.ATTRIBUTE:
				world.setAttribute( message, 1);
				break;
			case Constants.ADD_PLAYER:
				world.addElement( message, 1);
				break;
			case Constants.REMOVE_PLAYER:
				world.removeElement( message, 1 );
				myLogger.message("Removing player: " + message[1] + "\n", false);
				break;
			case Constants.SEND_WORLD:
				world.addMultipleElements( message, 1);
				break;
			default:
				myLogger.message("Received unparsable message: " + message[0] + "\n", true);
		}

		return Constants.SUCCESS; //eventually every action in the world will return an int for whether or not it was a valid action
	}

	public int parse(Object[] _message)
	{
		int[] sentence = (int[])_message[0];

		int[] needles = new int[Constants.SENTENCE_LENGTH];
		
		needles[0] = sentence[0];
		System.out.print("Needles: " + needles[0] + ",");
		for(int i = 1; i < sentence.length; i++)
		{
			needles[i] = elementsHash.get(sentence[i]).getTypeId();
			System.out.print(needles[i]+",");
		}
		System.out.println();
		Rule[] applicable = rules.getRules(needles);
		IncrementedArray<GameElement> relevantElements = new IncrementedArray<GameElement>(Constants.DEFAULT_RELEVANT_SIZE);
		if(sentence.length > 1)
		{
			GameElement subject = elementsHash.get(sentence[1]);
			GameElement first = world.getFirstElement();
			GameElement currentElement = first;
			do
			{
				/*find relevent ojects and get rules that apply to this sentence*/
				if(currentElement != subject && subject.isRelevant(currentElement))
					relevantElements.add(currentElement);
				System.out.println(currentElement.id()+"\n");
			}
			while( (currentElement=currentElement.next) != first );
		}


		return resolve(applicable,sentence,_message,relevantElements);
	}

	@SuppressWarnings("fallthrough")
	public int resolve( Rule[] _rules, int[] _sentence, Object[] _message, IncrementedArray<GameElement> _relevantElements)
	{
		Object[] message = new Object[_message.length-1];
		System.arraycopy(_message,0,message,0,message.length);
		GameElement subject = null, directObject = null, indirectObject = null;
		GameElement[] other = null;
		Integer status;
		ActionsHashMap myActions = new ActionsHashMap(actionFactory); 
		ActionsHashMap actionsToSend = new ActionsHashMap(actionFactory);
		
		switch(_sentence.length)
		{
			default:
				other = new GameElement[_sentence.length-4];
				System.arraycopy(_sentence,4,other,0,other.length);
			case 4:
				indirectObject = elementsHash.get(_sentence[3]);
			case 3:
				directObject = elementsHash.get(_sentence[2]);
			case 2:
				subject = elementsHash.get(_sentence[1]);
				break;
			case 1:

		}

		myActions.add(new Integer(_sentence[0]),new GameElement[]{subject,directObject,indirectObject},message);

		for(Rule rule : _rules)
		{
			try {
				ScriptEngine engine = manager.getEngineByName(rule.language());
				engine.put("owner",rule.owner());
				engine.put("subject",subject);
				engine.put("directObject",directObject);
				engine.put("indirectObject",indirectObject);
				engine.put("other",other);
				engine.put("relevant",_relevantElements);
				engine.put("message",message);
				engine.put("myActions",myActions);
				engine.put("actionsToSend",actionsToSend);
				engine.eval(rule.function());
				status = ((Double) engine.get("status")).intValue();
				if(status != Constants.SUCCESS)
					return status;
			}
			catch(ScriptException se)
			{
				System.out.println("Script error: " + se.getMessage());
			}

		}

		return Constants.SUCCESS;
	}
}
