public class ElementStack<K>
{
	private LinkedHolder<K> first = null;

	public synchronized void push(K _newElement)
	{
		LinkedHolder<K> newElement = new LinkedHolder<K>(_newElement);
		if(first == null)
		{
			first = newElement;
			first.next = first.prev = first;
		}
		else
		{
			first.insertBefore(newElement);
			first = newElement;
		}
	}

	public synchronized void unshift(K _newElement)
	{
		LinkedHolder<K> newElement = new LinkedHolder<K>(_newElement);
		if(first == null)
		{
			first = newElement;
			first.next = first.prev = first;
		}
		else
		{
			first.insertBefore(newElement);
		}
	}

	@SuppressWarnings("unchecked")
	public synchronized K pop()
	{
		if(first == null)
			return null;

		LinkedHolder<K> popped = first;
		if(first.next == first)
		{
			first = null;
		}
		else
		{
			first = first.next;
			popped.removeFromList();
		}
		return popped.get();
	}


	@SuppressWarnings("unchecked")
	public synchronized int size()
	{
		int i = 0;
		if(first == null)  return 0;
		LinkedHolder<K> current = first;
		do
		{
			i++;
			/**
			 * current.next
			 * Type safety: The expression of tyhpe LinkedHolder needs 
			 * unchecked conversion to conform to LinkedHolder<K>
			 */
		}while( (current = (LinkedHolder<K>) current.next) != first);
		return i; 
	}
}
