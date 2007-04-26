public class MovingElement extends Thread
{
	private Resolver r;
	private ActionFactory actionFactory;
	private GameElement me;
	private Object[] commands;
	private int wait_time;
	private Logger myLogger;

	public MovingElement(Resolver _r, ActionFactory _actionFactory, GameElement _me, Object[] _commands, int _wait_time, Logger _myLogger)
	{
		r = _r;
		actionFactory = _actionFactory;
		me = _me;
		commands = _commands;
		wait_time = _wait_time;
		myLogger = _myLogger;
	}

	public void run()
	{
		int command_id = 0;
		while(true)
		{
			Object[] command = (Object[]) commands[command_id];
			Action a;
			switch( (Integer) command[0])
			{
				case Constants.MOVE_TO:
					a = actionFactory.getAction("move");
					a.setNouns(new GameElement[]{me});
					a.parameters().add(Constants.MOVE_ABSOLUTE);
					a.parameters().add((float[]) command[1]);
					if(r.parse(a) != Constants.SUCCESS)
					//if(w.nudgeInternal(me.id(), (float[]) command[1]))
						command_id = (command_id+1)%commands.length;
					break;
				case Constants.ROTATE_TO:
					a = actionFactory.getAction("rotate");
					a.setNouns(new GameElement[]{me});
					a.parameters().add((float[]) command[1]);
					if(r.parse(a) != Constants.SUCCESS)
					//if(w.rotateElement(me.id(), (float[]) command[1]))
						command_id = (command_id+1)%commands.length;
					break;
				default:
					myLogger.message("Moving thread for GameElement " + me.id() + " received unparsable message: " + command[0] + "\n", true);
			}

			try
			{
				Thread.sleep(wait_time);
			}
			catch(InterruptedException ie)
			{
				myLogger.message("Moving thread for GameElement " + me.id() + " interrupted: " + ie + "\n", true);
			}
		}
	}
}
