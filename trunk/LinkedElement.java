public class LinkedElement
{
	public LinkedElement next = null;
	public LinkedElement prev = null;

	public void removeFromList()
	{
		this.prev.next = this.next;
		this.next.prev = this.prev;
		this.next = this.prev = null;
	}

	public void insertBefore(LinkedElement _newElement )
	{
		this.prev.next = _newElement;
		_newElement.prev = this.prev;
		this.prev = _newElement;
		_newElement.next = this;
	}

	public void insertAfter(LinkedElement _newElement )
	{
		this.next.prev = _newElement;
		_newElement.next = this.next;
		this.next = _newElement;
		_newElement.prev = this;
	}
}
