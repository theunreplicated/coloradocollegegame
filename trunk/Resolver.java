import java.util.*;
public class Resolver
{
	RuleSet rules;
	String[] actions;
	GameElement first;
	HashMap<Integer,GameElement> elementsHash;

	public Resolver(World _world)
	{
		rules = new RuleSet();
		actions = new String[]{ "move" , "add" , "remove" };
		first = _world.getFirstElement();
		elementsHash = _world.getElementsHash();
	}

	public int parse(Object[] _message)
	{
		int[] sentence = (int[])_message[0];

		int[] needles = new int[Constants.SENTENCE_LENGTH];
		
		needles[0] = sentence[0];
		for(int i = 1; i < sentence.length; i++)
			needles[i] = elementsHash.get(sentence[i]).getTypeId();

		Rule[] applicable = rules.getRules(needles);
		
		GameElement currentElement = first;
		do 
		{
			/*find relevent ojects and get rules that apply to this sentence*/
		}
		while( (currentElement=currentElement.next) != first );

		return resolve(applicable,_message,1);
	}

	public int resolve( Rule[] _rules , Object[] _message, int _start )
	{
		return 1;
	}
}
