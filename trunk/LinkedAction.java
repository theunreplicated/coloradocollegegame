public class LinkedAction extends LinkedElement<LinkedAction>
{
	private Action me;

	public LinkedAction(Action _me)
	{
		me = _me;
	}

	public Action get()
	{
		return me;
	}
}
