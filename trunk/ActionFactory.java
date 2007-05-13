import java.util.*;
import java.io.File;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class ActionFactory
{
	private Logger myLogger;
	private HashMap<String, Action> defaultActions = new HashMap<String, Action>();
	private String[] defaultActionKeys;

	public ActionFactory(File folder, String ext, Logger _myLogger)
	{
		myLogger = _myLogger;
		File[] files = folder.listFiles(new FactoryFileFilter(ext));
		createDefaultActions(files);
	}

	public ActionFactory(File folder, Logger _myLogger)
	{
		myLogger = _myLogger;
		File[] files = folder.listFiles(new FactoryFileFilter(Constants.ACTION_EXTENSION));
		createDefaultActions(files);
	}

	public ActionFactory(Logger _myLogger)
	{
		myLogger = _myLogger;
		File folder = new File(Constants.DEFAULT_DATA_DIR);
		File[] files = folder.listFiles(new FactoryFileFilter(Constants.ACTION_EXTENSION));
		createDefaultActions(files);
	}

	public ActionFactory(File[] _files, Logger _myLogger)
	{
		myLogger = _myLogger;
		createDefaultActions(_files);
	}

	private void createDefaultActions(File[] files)
	{

		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc;
			Element actions_list, action, name;
			NodeList actions, worldNodeList, repNodeList;
			StringFunction worldFunc, repFunc;
			int i;//, j, k;//"j" or "k" is never read
			
			for(File file : files)
			{
				if(!file.exists())
				{
					myLogger.message("ActionFactory cannot find file: " + file + "\n", true);
					continue;
				}
				doc = db.parse(file);
				actions_list = (Element) doc.getElementsByTagName("actions").item(0);
				actions = actions_list.getElementsByTagName("action");
				for(i = actions.getLength()-1;i>=0; i--)
				{
					action = (Element) actions.item(i);

					name = (Element) action.getElementsByTagName("name").item(0);

					worldNodeList = action.getElementsByTagName("inworld");

					if(worldNodeList.getLength()==0)
						worldFunc = null;
					else
						worldFunc = new StringFunction((Element) worldNodeList.item(0));
					
					repNodeList = action.getElementsByTagName("inrepresentation");

					if(repNodeList.getLength()==0)
						repFunc = null;
					else
						repFunc = new StringFunction((Element) repNodeList.item(0));

					defaultActions.put(name.getTextContent(),
							new Action(name.getTextContent(), worldFunc, repFunc));
				}
			}
			
			if(defaultActions.isEmpty())
			{
				myLogger.message("ActionFactory found no actions... You can't play a game without actions!\n", true);
				System.exit(1);
			}
			// iterate through hashmap to create our array...
			defaultActionKeys = new String[] {""};
			defaultActionKeys = defaultActions.keySet().toArray(defaultActionKeys);
			Arrays.sort(defaultActionKeys);
			for(i = 0; i < defaultActionKeys.length; i++)
			{
				defaultActions.get(defaultActionKeys[i]).setId(i+1);
			}
		}
		catch(Exception e) // better error reporting!
		{
			myLogger.message("Failed to read input files! : " + e.getMessage() + " :\n", true);
			e.printStackTrace();
		}
	}

	public Action getAction(String _actionType)
	{
		return(new Action(defaultActions.get(_actionType)));
	}

	public Action getAction(int _actionTypeId)
	{
		return(getAction(defaultActionKeys[_actionTypeId-1]));
	}

	public Action getAction(Object _actionInfo)
	{
		if(_actionInfo instanceof String)
			return getAction((String) _actionInfo);

		return getAction(((Integer) _actionInfo).intValue());
	}

	public int getType(String _type)
	{
		return(defaultActions.get(_type).getId());
	}

	public String getType(int _type)
	{
		return(defaultActionKeys[_type-1]);
	}
}
