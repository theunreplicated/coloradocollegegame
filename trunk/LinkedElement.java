public class LinkedElement<T extends LinkedElement>
{
	public T next = null;
	public T prev = null;

	@SuppressWarnings("unchecked")
	public void removeFromList()
	{
		this.prev.next = this.next;
		this.next.prev = this.prev;
		this.next = this.prev = null;
	}

	@SuppressWarnings("unchecked")
	public void insertBefore(T _newElement )
	{
		this.prev.next = _newElement;
		_newElement.prev = this.prev;
		this.prev = _newElement;
		_newElement.next = this;
	}

	@SuppressWarnings("unchecked")
	public void insertAfter(T _newElement )
	{
		this.next.prev = _newElement;
		_newElement.next = this.next;
		this.next = _newElement;
		_newElement.prev = this;
	}
}
