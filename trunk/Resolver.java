import java.util.Date;
import javax.script.*;

/**
 * The Resolver class handles all Actions. It receives Actions from the
 * user (via ClientInput) and the server (via ClientIO), and throws them
 * onto a stack of Actions that need to be handled (called actionStack).
 * It is also a Thread that grabs the top Action from the stack, grabs
 * all of the rules associated with it (see the "parse" method) and then
 * calls those rules and, if appropriate, the "inworld" function of the
 * Action itself (see the "resolve" method). If this last step is done,
 * this class is also in charge of letting the RepresentationResolver
 * know that the Representation needs updating (by calling the
 * RepresentationResolver's "resolve" method and passing the Action to
 * be handled).
 *
 * @author Benjamin Thomas
 * @author Justin Pohlmann
 * @author Omer Bar-or
 * @version 1.0, May 2007
 */
public class Resolver extends Thread
{
	// global variables
	private RuleSet rules;
	private RepresentationResolver repResolver = null;
	private ActionFactory actionFactory;
	private ElementFactory elementFactory;
	private ScriptEngineManager manager;
	public World world;
	private IO io;
	private Logger myLogger;
	private ElementStack<Action> actionStack;

	/**
	 * The constructor for Resolver. It sets variables and creates the
	 * ScriptEngineManager that will create all of the engines (each
	 * script gets its own engine currently) and passes all of the
	 * variables to the manager that all scripts will need.
	 * @param _world the World of GameElements for which to look for
	 * relevant GameElements
	 * @param _rf the RuleFactory used to get the Rules that apply to this
	 * World
	 * @param _af the ActionFactory with which to create new Actions (used
	 * only in rule scripts currently)
	 * @param _ef the ElementFactory with which to create new GameElements
	 * @param _myLogger the Logger (also passed to scripts)
	 */
	public Resolver(World _world, RuleFactory _rf, ActionFactory _af, ElementFactory _ef, Logger _myLogger)
	{
		actionFactory = _af;
		elementFactory = _ef;

		/* This grabs the rules associated with _any_ Space. Since Spaces
		 * aren't implemented yet, this set of rules applies to the entire
		 * game.
		 */
		rules = _rf.getRuleSet("");

		manager = new ScriptEngineManager();
		world = _world;
		myLogger = _myLogger;

		// Create a new ElementStack of type Action (see ElementStack.java)
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

	/**
	 * Set the RepresentationResolver to notify when Actions complete.
	 *
	 * @param _repResolver the RepresentationResolver to notify
	 */
	public void setRepresentationResolver(RepresentationResolver _repResolver)
	{
		repResolver = _repResolver;
	}

	/**
	 * Set the IO which the Resolver will use to send Actions if an
	 * Action claims that it needs to send another Action to other
	 * computers. If this Resolver is the Client's Resolver, it will
	 * send messages to the Server. If it is the Server's Resolver, it
	 * will send the messages to all Clients.
	 * 
	 * @param _io the IO object to which to send Actions
	 */
	public void setIO(IO _io)
	{
		io = _io;
	}

	/**
	 * Go through the actionStack continuously and parse the Actions
	 * therein. If no Action exists, or if none of the Actions in the
	 * actionStack are ready to be run again, the Resolver thread sleeps
	 * for the lesser of: Constants.SLEEP_TIME and the amount of time
	 * until some Action currently in the actionStack will be ready to run.
	 * NOTE: currently, the only ways to stop this thread from looping
	 * infinitely are either a) kill the program, or b) send the Resolver
	 * an interrupt while it is sleeping.
	 *
	 * @see Thread
	 */
	public void run()
	{
		Action a;

		/* We should probably eventually change this to run as long as
		 * some variable is true.
		 */
		while(true)
		{
			// Grab the top element from the stack if it exists.
			while( (a = actionStack.pop()) == null)
			{
				// If it doesn't, sleep and try again!
				try
				{
					Thread.sleep(Constants.SLEEP_TIME);
				}
				catch(InterruptedException ie)
				{
					/* If we are interrupted, that (right now) means that somebody
					 * doesn't want us running anymore. Good bye!
					 */
					myLogger.message("Resolver interrupted. Quitting.\n", false);
					return;
				}
			}

			/* Grab the current time and the time that the Action hopes to
			 * sleep before running, then compare them. If the Action needs
			 * to sleep some more, go through other Actions, looking for
			 * the first one that is ready to be run.
			 */
			long now = (new Date()).getTime();
			long sleepTime = a.getSleepTime();

			if(sleepTime > now)
			{
				// The amount of time until the next Action is ready to run
				long untilNextAction = sleepTime - now;

				Action current;

				addAction(a); // Add this Action back to the (end of) the stack.

				// Go through Actions until we hit our original Action again.
				while( (current = actionStack.pop()) != a)
				{
					sleepTime = current.getSleepTime();

					/* If it can't run now, update untilNextAction if necessary
					 * and then add the Action back to the (end of) the stack.
					 */
					if(sleepTime > now)
					{
						if(sleepTime - now < untilNextAction)
						{
							untilNextAction = sleepTime - now;
						}
						addAction(current);
					}
					else // current can run now!
					{
						untilNextAction = 0; // No need to sleep
						a = current; // We want to parse current instead of a.
						break;
					}
				} // while( (current = actionStack.pop()) != a)

				/* We couldn't find an Action ready to run, so we're going to
				 * have to sleep for a while and then try again...
				 */
				if(untilNextAction != 0)
				{
					/* We accidentally took "a" off of the actionStack (while
					 * testing for other Actions), let's add it again to the
					 * beginning of the stack.
					 */
					actionStack.push(a);
					try
					{
						/* If the next available Action is too far away, sleep only
						 * for SLEEP_TIME (the idea being that a new Action might
						 * come in, waiting to be handled, and we don't want the
						 * system to appear laggy because it is waiting for some
						 * jerk Action to be ready to run.
						 */
						if(untilNextAction > Constants.SLEEP_TIME)
							untilNextAction = Constants.SLEEP_TIME;
						Thread.sleep(untilNextAction);

						continue; // start over!
					}
					catch(InterruptedException ie)
					{
						/* If we are interrupted, that (right now) means that somebody
						 * doesn't want us running anymore. Good bye!
						 */
						myLogger.message("Resolver interrupted. Quitting.\n", false);
						return;
					}
				} // if(untilNextAction != 0)
			} // if(sleepTime > now)

			// Okay, we have the Action that we want to parse. Parse it!
			myLogger.message("Resolver running on action " + a.getName() + "\n", false);
			parse(a);
		}
	}

	@SuppressWarnings("unchecked")
	/**
	 * Grab the Rules associated with an Action and then call Resolve on
	 * it.
	 *
	 * @param action the Action to parse
	 * @return whether the Action was successfully completed or had some
	 * other return code.
	 * @see RuleSet
	 */
	private int parse(Action action)
	{
		/* In the end, we're going to pass an array of Actions to the
		 * "resolve" function, rather than just one. The reason is that
		 * an Action could have other Actions on which it depends (and that
		 * depend on it). These are stored in a variable called
		 * "dependentActions" in the Action object. Because we want to test
		 * and run all of those Actions at once, we pass Resolve an array
		 * of Actions that all depend on each other.
		 * First, we grab dependentActions from them from the Action object.
		 * We then create a new array, put the original Action first and
		 * all of the dependent Actions afterwards.
		 */
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

		//"new incrementedArray[actions.length]" 
		//Type safety: The expression of Type IncrementedArray[] needs unchecked Conversion  
		//to conform to incrementedArray<GameElement>[]
		/* Currently unfixable - Java on OS X Tiger does not allow the
		 * creation of generic arrays. Luckily, the game works just fine
		 * with this bit of ugliness.
		 */
		IncrementedArray<GameElement>[] relevantElements = new IncrementedArray[actions.length];

		GameElement[][] nouns = new GameElement[actions.length][];

		// For each Action, grab all of the relevant rules and GameElements.
		for(int i = 0; i < actions.length; i++)
		{
			nouns[i] = actions[i].getNouns();

			/* The "needles" of an Action are all of the attributes of that
			 * Action that can have Rules associated with them. Each needle
			 * is an integer associated with either an Action or with a
			 * GameElement. The needles are (in order) :
			 *
			 * ActionID - A unique integer associated with this Action
			 * subjectTypeID - A unique integer associated with the type of
			 * 	GameElement that is the subject of this Action (if a subject
			 * 	exists)
			 * directObjectTypeID - A unique integer associated with the type
			 * 	of GameElement that is the direct object of this Action (if
			 * 	a direct object exists)
			 * indirectObjectTypeID - A unique integer associated with the
			 * 	type of GameElement that is the indirect object of this Action
			 * 	(if an indirect object exists)
			 *
			 * The latter three are stored in the "nouns" array in the Action.
			 */
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

			// Get the applicable rules associated with these needles
			applicable[i] = rules.getRules(needles);
			
			/* Grab all of the relevant elements associated with this Action
			 * by using the "isRelevant" function in GameElement to determine
			 * if any GameElements are relevant to the subject of this Action
			 * (if a subject exists -- if one does not, there can be no
			 * relevant elements).
			 */
			relevantElements[i] = new IncrementedArray<GameElement>(Constants.DEFAULT_RELEVANT_SIZE);
			if(nouns[i] != null)
			{
				GameElement first = world.getFirstElement();
				GameElement currentElement = first;
				do
				{
						GameElement subject = nouns[i][0];

						if(currentElement != subject && subject.isRelevant(currentElement))
							relevantElements[i].add(currentElement);
				}
				while( (currentElement = currentElement.next) != first);
			}
		} // for(int i = 0; i < actions.length; i++)

		/* We now have, in a set of arrays:
		 * 	- An array of all of the Actions we need to resolve together (all
		 * 		of the Actions that are dependent on each other)
		 * 	- An array of all of the rules applicable to each Action
		 * 	- An array of all of the GameElements that are relevant to each
		 * 		Action.
		 *
		 * (NOTE: each element in each array corresponds to the same element
		 * in the other arrays. So, applicable[2] is the set of applicable
		 * rules associated with actions[2], the relevant GameElements of
		 * which is stored in relevantElements[2]).
		 *
		 * We now run resolve on all of these Actions.
		 */
		return resolve(applicable,actions,relevantElements);
	} // public int parse

	/* Ignore the fallthrough of our switch statement in the following
	 * function.
	 */
	@SuppressWarnings("fallthrough")
	/**
	 * Process a set of Actions that are dependent upon each other, and
	 * if all of them succeed run them. Dependent actions are actions that
	 * are only allowed to run if the other ones run too (like pushing an
	 * object, which will only work if both the pusher and the pushed
	 * object can move). This method has two steps. First, it runs through
	 * all of the rules associated with each Action (as determined by the
	 * "parse" function). If all of them return Constants.SUCCESS, then
	 * it goes ahead ahead and runs the "inworld" function of the Action,
	 * passes the Action on to the RepresentationResolver to handle the
	 * "inrepresentation" function, and if the Action needs to send
	 * another Action to be handled by the other players and/or the
	 * server, that Action is added to a list of Actions to send through
	 * the IO. (An example of this last case is with the "move" Action,
	 * which determines where to move the GameElement and then sends a
	 * "move to" Action through IO so that the (other) Clients don't have
	 * to recalculate anything. They can just move the element to its new
	 * location.)
	 * If a rule does not return "Constants.SUCCESS" for one reason or
	 * another, this method looks at what the rule _did_ return and
	 * handles the situation accordingly. (See below for the different
	 * return codes.)
	 *
	 * @param _rules a two-dimensional array of each array of rules
	 * associated with each action
	 * @param _actions an array of all of the actions to resolve
	 * @param _relevantElements an array of the IncrementedArrays holding
	 * all of the GameElements that might be relevant to each action.
	 * @see ScriptEngine
	 * @see IncrementedArray
	 * @return whether the resolving was successfully completed or had
	 * some other return code.
	 */
	public int resolve( Rule[][] _rules, Action[] _actions, IncrementedArray<GameElement>[] _relevantElements)
	{
		IncrementedArray<Action> actionsToSend = new IncrementedArray<Action>(Constants.DEFAULT_ACTIONS_TO_SEND);
		IncrementedArray returnVals;
		int status;
		int i;
		Action currentAction;
		boolean runAgain = false;
		GameElement[] nouns, other;
		GameElement subject, directObject, indirectObject;

		// Run the following for each Action...
		for(i = 0; i < _actions.length; i++)
		{
			currentAction = _actions[i];

			/* Here, we grab the Action's nouns, which should be of the form:
			 *
			 * 	subject, directObject, indirectObject
			 *
			 * ... and through them into individual variables
			 * (e.g., "subject"). The idea is that we want to pass the easiest
			 * terminology onto the scripts (since those are created by
			 * advanced users). Thus, rule and action scripts receive a
			 * "subject," a "directObject," an "indirectObject," and an array
			 * of "other" GameElements (if the list is longer than three,
			 * the extras are stored as "other" just so that we don't lose
			 * them), and if any of these don't exist, they are passed as
			 * null.
			 *
			 * We do this by assuming that any Action will only have a
			 * direct object if it also has a subject, and it will only have
			 * an indirect object if it also has a direct object (since none
			 * of our actions to date can have the former without the latter).
			 * Thus, we switch on the length of the nouns array, and allow
			 * fallthrough. That is, our case defaults to a length of "more
			 * than three," and sets the "other" Object to something other
			 * than null. It then _does not break_, so it runs the code in
			 * the next case (length of 3) too. At length three, we set
			 * "indirectObject," and then again don't break. Thus, if the
			 * length of the "nouns" array is three or more, the switch will
			 * also run the code for the length of two. At that length, we
			 * set the "directObject," and again (surprise!) don't break. Now,
			 * if the array is of length two or greater, the switch will also
			 * run the code for the length of one. At that length, we set the
			 * "subject," and break. In all, we can handle any length of
			 * "nouns" array without any repetition.
			 */
			nouns = currentAction.getNouns();
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

			// Go through each rule associated with this action...
			for(Rule rule : _rules[i])
			{
				try {

					/* Put everything we need into the rule script and then run
					 * it (using "eval").
					 */

					ScriptEngine engine = manager.getEngineByName(rule.language());

					status = Constants.SUCCESS; // Assume that the action will succeed

					engine.put("action",currentAction);
					engine.put("owner",rule.owner()); // currently unused
					engine.put("subject",subject);
					engine.put("directObject",directObject);
					engine.put("indirectObject",indirectObject);
					engine.put("other",other);
					engine.put("relevant",_relevantElements[i]);
					engine.put("argv",currentAction.parameters().pack());
					engine.put("status",status);
					engine.eval(rule.function());

					/* Get the status back out of the engine. Because many scripts
					 * have dynamic typing, setting the status to an int does not
					 * guarantee that we won't get something else out, so (just to
					 * be safe), we check if the status is an Integer or a Double.
					 * Either way, we can just grab the intValue of it.
					 *
					 * The status tells us whether the rule succeeded or whether
					 * it had some complications.
					 */
					Object _status = engine.get("status");
					if(_status instanceof Integer)
						status = ((Integer) _status).intValue();
					else if(_status instanceof Double)
						status = ((Double) _status).intValue();

					/* One complication that could occur is that the rule
					 * discovers that, for this action to succeed, it depends on
					 * _another_ action that we had not previously considered. In
					 * this case, we need to start over, taking that Action into
					 * account. It is the rule's responsibility to add the Action
					 * to the "dependentActions" of the Action that is currently
					 * running. But, to avoid confusion and recoursion, we want
					 * to store all dependent Actions in one place, so we put them
					 * all in the dependentActions of the first action in our
					 * array (see the "parse" method for why this Action is
					 * special).
					 */
					if(status == Constants.NEW_DEPENDENCY)
					{
						if(i != 0) // if we're not already in the first action
						{
							/* Add the dependentActions of the currentAction to the
							 * dependentActions of the first Action in our array,
							 * then clear the dependentActions of the currentAction.
							 */
							_actions[0].getDependentActions().add(currentAction.getDependentActions());
							currentAction.clearDependentActions();
						}
						/* We need to do everything over again to take account of
						 * the new dependency... We don't simply cancel here because
						 * it's still possible that another Action will completely
						 * fail (see next check), in which case, we don't need to
						 * run anything again after all.
						 */
						runAgain = true;
					}

					/* Any other case, currently, except success, we consider to
					 * be failure. We don't need to go any further. None of these
					 * Actions will succeed (because they all depend on each
					 * other).
					 */
					else if(status != Constants.SUCCESS)
						return status;
				} // try {

				/* All scripts throw rather generic exceptions called
				 * ScriptExceptions.
				 * Here, we catch it and print what we can and that we got an
				 * error while analyzing rules.
				 */
				catch(ScriptException se)
				{
					myLogger.message("Script error: " + se.getMessage() + "\n",true);
					myLogger.message("Error in Resolver (rules)\n",true);
				}
			} // for(Rule rule : _rules[i])
		} // for(i = 0; i < _actions.length; i++)

		/* So, we've gone through every Action and none of them have
		 * completely failed. Now, if we have any new dependencies (or, for
		 * the future, any other reason to rerun the parsing/resolving of
		 * a set of dependent Actions), we put the primary Action onto the
		 * beginning of the stack and return to be rerun from the beginning.
		 */
		if(runAgain)
		{
			actionStack.push(_actions[0]);
			return Constants.NEW_DEPENDENCY;
		}

		/* Okay, so we've succeeded at all of our rule-checking. Now, lets
		 * run each action.
		 */
		myLogger.message("Resolver is starting to handle actions...\n", false);
		for(i = 0; i < _actions.length; i++)
		{
			currentAction = _actions[i];
			// If this Action has an "inworld" function that needs to run...
			if(currentAction.getWorldFunction() != null)
			{
				/* For an explanation of this code, see the comment on it when
				 * we did the same thing for rules.
				 */
				nouns = currentAction.getNouns();
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

				/* This code should look very similar to the "eval" we did on
				 * rules above. The only differences are: not as much
				 * information is passed (it is assumed, for example, that the
				 * action script does not need to know about the Action itself
				 * or about the relevant GameElements around it). It also has
				 * a "returnVals" IncrementedArray that is passed into it. The
				 * idea behind this variable is that action scripts need to
				 * communicate with the Resolver more heavily than the rule
				 * scripts did. For example, because the action scripts don't
				 * have access to the World, adding a GameElement to the World
				 * is impossible for them to do. Instead, an action script can
				 * set its status to Constants.ADD_ELEMENTS and then put as
				 * many GameElements into the IncrementedArray as it wants, and
				 * the Resolver will add them all to the World itself. For more,
				 * see the handling of "status" below.
				 */
				StringFunction worldFunc = currentAction.getWorldFunction();
				try {
					returnVals = new IncrementedArray(Constants.DEFAULT_RETURN_VALS_LENGTH);
					status = Constants.SUCCESS;
					ScriptEngine engine = manager.getEngineByName(worldFunc.getLanguage());
					engine.put("subject",subject);
					engine.put("directObject",directObject);
					engine.put("indirectObject",indirectObject);
					engine.put("other",other);
					engine.put("argv",currentAction.parameters().pack());
					engine.put("returnVals",returnVals);
					engine.put("status", status);
					engine.eval(worldFunc.getFunction());
					currentAction.setLastRun(new Date());
					Object _status = engine.get("status");
					if(_status instanceof Integer)
						status = ((Integer) _status).intValue();
					else if(_status instanceof Double)
						status = ((Double) _status).intValue();

					/* One status that an action script might return is
					 * Constants.ADD_ELEMENTS. As described above, this allows
					 * action scripts to add GameElements to the World through
					 * the Resolver. Here, we go through each Object in the
					 * returnVals array, and add it to the World. We also, in
					 * order to make life easier for script-writers can accept a
					 * new GameElement in two forms. First, we can accept the
					 * GameElement itself. Second, we can accept a "message" that
					 * contains all of the important aspects of a GameElement:
					 * the new id of the GameElement, its type, its position, and
					 * its facing. Please note that this last way of creating new
					 * GameElements hasn't been tested yet and is currently
					 * unused. When we do start using it (to allow arbitrary
					 * Actions to create their own GameElements (like a "magic
					 * mushroom" spell), it will likely change around a bit.
					 */
					if(status == Constants.ADD_ELEMENTS)
					{
						GameElement newElement = null;
						for(i = 0; i < returnVals.length; i++)
						{
							Object _message = returnVals.get(i);

							/* Info about a GameElement rather than a GameElement
							 * itself...
							 */
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
							} // if(_message instanceof Object[])

							/* The GameElement itself! */
							else if(_message instanceof GameElement)
							{
								// So much easier!
								newElement = (GameElement) _message;
							}

							// This should eventually move to the "add element" rule.
							// It adds a unique color to the GameElement if it is a
							// player character.
							if(newElement.id() < Constants.ELEMENT_ID_PADDING+Constants.MAX_CONNECTIONS+1) //so that the avatars have different colors!
								newElement.attribute("color",Constants.getColorByClientID(newElement.id()));

							// Add it!
							world.addElement(newElement);
						} // for(i = 0; i < returnVals.length; i++)
					} // if(status == Constants.ADD_ELEMENTS)

					/* Another thing that action scripts have trouble doing on
					 * their own is removing elements from the World. If they
					 * return Constants.REMOVE_ELEMENTS, we do that for them.
					 */
					else if(status == Constants.REMOVE_ELEMENTS)
					{
						for(i = 0; i < returnVals.length; i++)
						{
							Object _message = returnVals.get(i);
							world.removeElement((GameElement) _message);
						}
					}
					/* An action script might determine that it needs to run
					 * again, for example with the 'jump' Action that has two
					 * parts, an upward part and a downward part. The first is
					 * run, and a parameter is set in the Action to indicate that
					 * it has happened. Then, Constants.HANDLE_AGAIN is returned
					 * so that the Action is run again. This second time, the
					 * script sees that the first part has already run, so it runs
					 * the second (downward) part.
					 *
					 * The returnVal in this case is the amount of time in
					 * milliseconds to wait before running the Action again.
					 * (See the "run" method in this class to see how this
					 * information is used.) Again, we aren't sure what type the
					 * return will be, so we test for Integer and Double, but the
					 * value will always be an int.
					 */
					else if(status == Constants.HANDLE_AGAIN)
					{
						Object ret_value = returnVals.get(0);
						if(ret_value instanceof Integer)
							currentAction.setDelay(((Integer) ret_value).intValue());
						else if(ret_value instanceof Double)
							currentAction.setDelay(((Double) ret_value).intValue());
						addAction(currentAction);
					}
					/* An action might tell the game is over or that the user
					 * wants to quit for another reason, in which case the
					 * Resolver exits.
					 */
					else if(status == Constants.QUIT)
					{
						System.exit(0);
					}

					/* Now, that this action has succeeded, if it includes a
					 * "toSend" (an Action that it wants to send on -- to the
					 * Clients if it is the Server and to the Server if it is one
					 * of the Clients), we want to add it to our list of things to
					 * send on.
					 */
					if(currentAction.getToSend() != null)
					{
						actionsToSend.add(currentAction.getToSend());
					}
				} // try {

				/* All scripts throw rather generic exceptions called
				 * ScriptExceptions.
				 * Here, we catch it and print what we can and that we got an
				 * error while analyzing actions.
				 */
				catch(ScriptException se)
				{
					myLogger.message("Script error: " + se.getMessage() + "\n",true);
					myLogger.message("Error in Resolver (actions)\n",true);
				}

			} // if(currentAction.getWorldFunction() != null)

			/* If this Action has an "inrepresentation" function, and if the
			 * Resolver has a RepresentationResolver set, send the Action on
			 * to be handled by the RepresentationResolver.
			 */
			if(currentAction.getRepFunction() != null && repResolver != null)
			{
				repResolver.resolve(currentAction);
			}
		} // for(i = 0; i < _actions.length; i++)

		/* Great. We've gotten through all of the rules and all of the
		 * actions. Now, all that's left is sending what needs to be sent.
		 * We assume that the "io" variable is not null because both the
		 * Client and the Server (the only two things to create a Resolver)
		 * also set that variable.
		 */
		if(actionsToSend.length > 0)
		{
			/* To save us a lot of communication space, there is a condenced
			 * version of an Action called a "WritableAction." We send an
			 * IncrementedArray of those instead of an IncrementedArray of
			 * Actions.
			 * The main optimization is that it sends a GameElement's ID
			 * rather than the entire GameElement (since the other end already
			 * knows everything about the GameElement). The only exception is
			 * when we are creating new GameElements. In these cases, the
			 * WritableAction knows to write the whole GameElement to be
			 * passed along. (This too, could be optimized, to not send the
			 * Shapes, for example, but it has not been.)
			 */
			IncrementedArray<WritableAction> write_actions = new IncrementedArray<WritableAction>(actionsToSend.length);
			for(i = 0; i < actionsToSend.length; i++)
			{
				write_actions.add(new WritableAction(actionsToSend.get(i)));
			}
			io.send(write_actions);
		}

		/* We're done! And succeeded! */
		return Constants.SUCCESS;
	} // public int resolve

	/**
	 * Add an Action to the end of the actionStack.
	 *
	 * @param a the Action to add
	 * @see ElementStack
	 */
	public void addAction(Action a)
	{
		actionStack.unshift(a);
	}

	/**
	 * Add one or more Actions to the end of the actionStack.
	 *
	 * @param _actions the Action(s) we want to add - they might be stored
	 * in an IncrementedArray.
	 * @see ElementStack
	 */
	@SuppressWarnings("unchecked")
	public void addAction(Object _actions)
	{
		/* The only two options for _actions that we understand are an
		 * IncrementedArray of WritableActions or a single Action. If it is
		 * the latter, we just run the above "addAction" method to it. If
		 * it is neither, we complain.
		 */
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

		/* If it is an IncrementedArray, we just go through each
		 * WritableAction, tell it to get the non-condenced Action
		 * associated with that WritableAction and run the above "addAction"
		 * method to it.
		 */
		IncrementedArray<WritableAction> actions = (IncrementedArray<WritableAction>) _actions;
		for(int i = 0; i < actions.length; i++)
			addAction(actions.get(i).getAction(world,actionFactory));
	} // public void addAction
} // public class Resolver
