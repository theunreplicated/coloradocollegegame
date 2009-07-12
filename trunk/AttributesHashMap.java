import java.util.*;
import java.io.*;
public class AttributesHashMap extends HashMap<String, Object> implements Serializable
{
	static final long serialVersionUID = 2737635924714266494L;
	private World w = null;

	public void setWorld(World _w)
	{
		w = _w;
	}
	private synchronized void writeObject(java.io.ObjectOutputStream out) throws IOException
	{
		out.writeInt(size());
		Set<Map.Entry<String,Object>> entries = entrySet();
		Iterator<Map.Entry<String,Object>> it = entries.iterator();
		Map.Entry<String,Object> entry;
		while(it.hasNext())
		{
			entry = it.next();
			if(entry.getValue() instanceof GameElement) // to prevent infinite loops
				entry.setValue(Constants.UNIQUE_GE_PREFIX + ((GameElement) entry.getValue()).id());
			out.writeObject(entry.getKey());
			out.writeObject(entry.getValue());
		}
	}

	private synchronized void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		int size = in.readInt();
		@SuppressWarnings("unused")
		Map.Entry<String,Object> entry;//"entry" is never read
		String key;
		Object value;
		for(int i = size-1; i >= 0; i--)
		{
			key = (String) in.readObject();
			value = in.readObject();
			
			if(value instanceof String)
			{
				if(((String) value).startsWith(Constants.UNIQUE_GE_PREFIX))
				{
					String id =((String) value).substring(Constants.UNIQUE_GE_PREFIX.length()); 
					value = w.getElementById(Integer.parseInt(id));
				}
			}
			put(key, value);
		}

	}
}
