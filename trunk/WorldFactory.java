import java.util.*;
import java.io.File;
import java.io.FileFilter;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class WorldFactory
{
	private Logger myLogger;
	private File[] files;
	private ElementFactory ef;

	public WorldFactory(File folder, String ext, ElementFactory _ef, Logger _myLogger)
	{
		ef = _ef;
		myLogger = _myLogger;
		files = folder.listFiles(new WGFileFilter(ext));
	}

	public WorldFactory(File folder, ElementFactory _ef, Logger _myLogger)
	{
		ef = _ef;
		myLogger = _myLogger;
		files = folder.listFiles(new WGFileFilter(Constants.WORLD_EXTENSION));
	}

	public WorldFactory(ElementFactory _ef, Logger _myLogger)
	{
		ef = _ef;
		myLogger = _myLogger;
		File folder = new File(Constants.DEFAULT_DATA_DIR);
		files = folder.listFiles(new WGFileFilter(Constants.WORLD_EXTENSION));
	}

	public void fillWorld(World w)
	{
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc;
			Element element, attributesElement, newAttribute;
			NodeList elements, positionNodes, attributesNodes;
			Node tmpNode;
			String attributeType;
			GameElement newElement;
			float[] position;
			int i;
			
			for(File file : files)
			{
				doc = db.parse(file);
				elements = doc.getElementsByTagName("element");
				for(i = elements.getLength()-1;i>=0; i--)
				{
					element = (Element) elements.item(i);

					newElement = ef.getGameElement(element.getAttribute("type"));

					positionNodes = element.getElementsByTagName("position");
					position = new float[positionNodes.getLength()];
					for(int p = position.length-1; p>=0; p--)
					{
						position[p] = Float.parseFloat(positionNodes.item(p).getTextContent());
					}
					newElement.setPosition(position);
					newElement.id(i);
					attributesElement = (Element) element.getElementsByTagName("attributes").item(0);

					// grab all of the children of attributesElement
					attributesNodes = attributesElement.getChildNodes();

					for(int a = attributesNodes.getLength()-1; a>=0; a--)
					{
						tmpNode = attributesNodes.item(a);
						if(tmpNode.getNodeType() != Node.ELEMENT_NODE) continue;
						newAttribute = (Element) tmpNode;
						attributeType = newAttribute.getAttribute("type");
						if(attributeType.equalsIgnoreCase("String"))
						{
							newElement.attribute(newAttribute.getTagName(), newAttribute.getTextContent());
						}
						else if(attributeType.equalsIgnoreCase("int"))
						{
							newElement.attribute(newAttribute.getTagName(), Integer.parseInt(newAttribute.getTextContent()));
						}
						// &c.
					}
					// add element to World
					w.addElement(newElement);
				}
			}
			
		}
		catch(Exception e) // better error reporting!
		{
			myLogger.message("Failed to read input files! : " + e.getMessage() + " :\n", true);
			e.printStackTrace();
		}

	}



	private class WGFileFilter implements FileFilter
	{
		private String ext;
		public WGFileFilter(String _ext)
		{
			ext = _ext;
		}

		public boolean accept(File pathname)
		{
			return(pathname.getName().endsWith(ext));
		}
	}
}
