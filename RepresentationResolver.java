import javax.script.*;

/**
 * The RepresentationResolver class handles the "inrepresentation"
 * component of actions. It simply calls them and gives them access
 * to the Representation.
 * @author Omer Bar-or
 * @version 1.0, May 2007
 */
public class RepresentationResolver
{
	// global variables
	private Representation rep;
	private ScriptEngineManager manager;
	private Logger myLogger;

	/**
	 * The constructor for RepresentationResolver. It creates the
	 * ScriptEngineManager that will create all of the engines (each
	 * script gets its own engine currently) and passes all of the
	 * variables to the manager that all scripts will need.
	 * @param	_rep	the Representation that gets passed to the scripts
	 * @param _myLogger the Logger (also passed to scripts)
	 */
	public RepresentationResolver(Representation _rep, Logger _myLogger)
	{
		rep = _rep;
		myLogger = _myLogger;
		manager = new ScriptEngineManager();

		// Add logger and representation at global scope, plus static stuff
		manager.put("myLogger", myLogger);
		manager.put("representation", rep);
		manager.put("Constants", new Constants());
		manager.put("Quaternions", new Quaternions());
		manager.put("VectorUtils", new VectorUtils());
	}

	/* Ignore the fallthrough of our switch statement in the following
	 * function.
	 */
	@SuppressWarnings("fallthrough")

	/**
	 * Resolves the "inrepresentation" component of a particular Action.
	 * @param _action the Action to resolve
	 * @see ScriptEngine
	 */
	public void resolve(Action _action)
	{
		GameElement[] nouns = _action.getNouns();
		GameElement subject = null, directObject = null, indirectObject = null;
		GameElement[] other = null;
		nouns = _action.getNouns();

		/* For an explanation of this statement, see the same code in
		 * Resolver.java
		 */
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

		// call the representation function ("inrepresentation") of the Action.
		StringFunction repFunc = _action.getRepFunction();
		try {
			ScriptEngine engine = manager.getEngineByName(repFunc.getLanguage());
			engine.put("subject",subject);
			engine.put("directObject",directObject);
			engine.put("indirectObject",indirectObject);
			engine.put("other",other);
			engine.put("argv",_action.parameters().pack());
			engine.eval(repFunc.getFunction());
		}
		/* All scripts throw rather generic exceptions called ScriptExceptions.
		 * Here, we catch it and print the Action that caused it.
		 */
		catch(ScriptException se)
		{
			myLogger.message("Script error (in RepResolver): " + se.getMessage() + "\n",true);
			myLogger.message(_action.toString() + "\n",true);
		}

	} // public void resolve

} // public class RepresentationResolver
