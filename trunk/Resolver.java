import java.util.*;
import javax.script.*;
public class Resolver
{
	RuleSet rules;
	RuleFactory ruleFactory;
	ActionFactory actionFactory;
	GameElement first;
	HashMap<Integer,GameElement> elementsHash;
	ScriptEngineManager manager;
	World testWorld;

	public Resolver(World _world, RuleFactory _rf, ActionFactory _af)
	{
		ruleFactory = _rf;
		actionFactory = _af;
		rules = _rf.getRuleSet("");
		first = _world.getFirstElement();
		elementsHash = _world.getElementsHash();
		manager = new ScriptEngineManager();
	}

	public int parse(Object[] _message)
	{
		int[] sentence = (int[])_message[0];

		int[] needles = new int[Constants.SENTENCE_LENGTH];
		
		needles[0] = sentence[0];
		for(int i = 1; i < sentence.length; i++)
			needles[i] = elementsHash.get(sentence[i]).getTypeId();

		Rule[] applicable = rules.getRules(needles);
		GameElement[] relevantElements = null;
		if(sentence.length > 1)
		{
			GameElement subject = elementsHash.get(sentence[1]);
			GameElement currentElement = first;
			relevantElements = new GameElement[Constants.DEFAULT_RELEVANT_SIZE];
			int numRelevant = 0;
			do
			{
				/*find relevent ojects and get rules that apply to this sentence*/
				if(subject.isRelevant(currentElement))
				{
					if(numRelevant == relevantElements.length)
					{
						GameElement[] tmp = new GameElement[relevantElements.length+Constants.DEFAULT_RELEVANT_SIZE];
						System.arraycopy(relevantElements,0,tmp,0,relevantElements.length);
						relevantElements = tmp;
					}
					relevantElements[numRelevant++] = currentElement;
				}
			}
			while( (currentElement=currentElement.next) != first );
		}

		return resolve(applicable,sentence,_message,relevantElements);
	}

	public int resolve( Rule[] _rules, int[] _sentence, Object[] _message, GameElement[] _relevantElements)
	{
		Object[] message = new Object[_message.length-1];
		System.arraycopy(_message,0,message,0,message.length);
		GameElement subject = null, directObject = null, indirectObject = null;
		GameElement[] other = null;
		Integer status;
		ArrayList<Action> actions = new ArrayList<Action>();
		Action a = actionFactory.getAction(_sentence[0]);
		
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

		a.setNouns(new GameElement[]{subject,directObject,indirectObject});
		actions.add(a);

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
				engine.put("actions",actions);
				engine.put("actionFactory",actionFactory);
				engine.eval(rule.function());
				status = (Integer) engine.get("status");
				if(status != 0)
					return status;
			}
			catch(ScriptException se)
			{
				System.out.println("Script error: " + se.getMessage());
			}

		}
		return 0;
	}
}
