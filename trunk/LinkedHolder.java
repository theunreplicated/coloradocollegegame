public class LinkedHolder<T> extends LinkedElement<LinkedHolder>
{
	private T me;

	public LinkedHolder(T _me)
	{
		me = _me;
	}

	public T get()
	{
		return me;
	}
}
