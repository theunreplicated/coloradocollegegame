import javax.script.*;
public class RepresentationResolver
{
	private Representation rep;
	private ScriptEngineManager manager;
	private Logger myLogger;

	public RepresentationResolver(Representation _rep, Logger _myLogger)
	{
		rep = _rep;
		myLogger = _myLogger;
		manager = new ScriptEngineManager();

		// Add logger and representation at global scope
		manager.put("myLogger", myLogger);
		manager.put("representation", rep);
		manager.put("Quaternions", new Quaternions());
	}

	public void resolve(Action _action)
	{
		GameElement[] nouns = _action.getNouns();
		GameElement subject = null, directObject = null, indirectObject = null;
		GameElement[] other = null;
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
		catch(ScriptException se)
		{
			myLogger.message("Script error: " + se.getMessage() + "\n",true);
		}
	}
}
