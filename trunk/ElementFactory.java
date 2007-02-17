import java.util.*;
import java.io.File;
import java.io.FileFilter;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class ElementFactory
{
	HashMap<String, Object> defaultElements = new HashMap<String, Object>();

	public ElementFactory(File folder, String ext)
	{
		File[] files = folder.listFiles(new EGFileFilter(ext));
		createDefaultElements(files);
	}

	public ElementFactory(File folder)
	{
		File[] files = folder.listFiles(new EGFileFilter(Constants.ELEMENT_LIST_EXTENSION));
		createDefaultElements(files);
	}

	public ElementFactory()
	{
		File folder = new File(Constants.DEFAULT_DATA_DIR);
		File[] files = folder.listFiles(new EGFileFilter(Constants.ELEMENT_LIST_EXTENSION));
		createDefaultElements(files);
	}
	public void createDefaultElements(File[] files)
	{

		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc;
			Element element, name, plural, shape;
			NodeList elements, spheres, cylinders, boxes, cones;
			VirtualShape[] shapes;
			int i, j, k;
			int[] position;
			int[] scale;
			HashMap attributes;
			
			for(File file : files)
			{
				doc = db.parse(file);
				elements = doc.getElementsByTagName("element");
				for(i = elements.getLength()-1;i>=0; i--)
				{
					position = Constants.DEFAULT_POSITION;
					scale = Constants.DEFAULT_SCALE;
					attributes = null;

					element = (Element) elements.item(i);
					name = (Element) element.getElementsByTagName("name").item(0);
					plural = (Element) element.getElementsByTagName("plural").item(0);
					shape = (Element) element.getElementsByTagName("shapes").item(0);
					spheres = shape.getElementsByTagName("sphere");
					cylinders = shape.getElementsByTagName("cylinder");
					boxes = shape.getElementsByTagName("box");
					cones = shape.getElementsByTagName("cone");
					shapes = new VirtualShape[spheres.getLength() + cylinders.getLength() + boxes.getLength() + cones.getLength()];
					j = shapes.length-1;

					k = cones.getLength()-1;
					for( ; j >= 0 && k >= 0; j--, k--)
					{
						shapes[j] = new VirtualCone(cones.item(k));
					}
	
					k = boxes.getLength()-1;
					for( ; j >= 0 && k >= 0; j--, k--)
					{
						shapes[j] = new VirtualBox(boxes.item(k));
					}

					k = cylinders.getLength()-1;
					for( ; j >= 0 && k >= 0; j--, k--)
					{
						shapes[j] = new VirtualCylinder(cylinders.item(k));
					}

					k = spheres.getLength()-1;
					for( ; j >= 0 && k >= 0; j--, k--)
					{
						shapes[j] = new VirtualSphere(spheres.item(k));
					}

					defaultElements.put(name.getTextContent(),
						new GameElement(name.getTextContent(), position, scale, shapes, attributes));
				}
			}
		}
		catch(Exception e) // better error reporting!
		{
			System.err.println("Failed to read input files! : " + e.getMessage());
		}
	}

	private class EGFileFilter implements FileFilter
	{
		private String ext;
		public EGFileFilter(String _ext)
		{
			ext = _ext;
		}

		public boolean accept(File pathname)
		{
			return(pathname.getName().endsWith(ext));
		}
	}
}
