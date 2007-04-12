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
			GameElement currentElement = first;
			do
			{
				/*find relevent ojects and get rules that apply to this sentence*/
				if(subject.isRelevant(currentElement))
					relevantElements.add(currentElement);
			}
			while( (currentElement=currentElement.next) != first );
		}


		System.out.println("***APPLICABLE RULES***");
		for(Rule r : applicable)
		{
			System.out.println(r);
		}
		System.out.println("***SENTENCE***");
		for(int i : sentence)
		{
			System.out.println(i);
		}
		System.out.println("***RELEVANT ELEMENTS***");

		for(int i = relevantElements.length-1; i>=0; i--)
		{
			System.out.println(relevantElements.get(i));
		}
		return 1;
		//return resolve(applicable,sentence,_message,relevantElements);
	}

	@SuppressWarnings("fallthrough")
	public int resolve( Rule[] _rules, int[] _sentence, Object[] _message, IncrementedArray<GameElement> _relevantElements)
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
