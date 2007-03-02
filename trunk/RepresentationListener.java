public class RepresentationListener extends Thread
{
	private Representation rep;
	private GameElement sync;
	private Logger myLogger;

	public RepresentationListener(Representation _rep, GameElement _sync, Logger _myLogger)
	{
		rep = _rep;
		sync = _sync;
		myLogger = _myLogger;
	}

	public void run()
	{
		while(true)
		{
			try
			{
				synchronized(sync)
				{
					sync.wait();
				}
				rep.update();
			}
			catch(IllegalMonitorStateException imse)
			{
				myLogger.message("RepresentationListener hit IllegalMonitorStateException while waiting for updates: " + imse.getMessage(), true);
			}
			catch(InterruptedException ie)
			{
				myLogger.message("RepresentationListener got InterruptedException while waiting for updates: " + ie.getMessage(), true);
			}
		}
	}
}

