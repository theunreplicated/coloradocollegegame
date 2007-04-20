//SketchUpUtils.java
//@author Joel Ross & Guillermo Mendez-Kestler

import java.util.zip.*;
import java.util.*;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

/***
 A static utility class for integrating SketchUp files into the game.
 Will contain parses and so forth.
***/

public class SketchUpUtils
{
	public static double[] parseCoordinate(String c)
	{
		String[] coords = c.split(",");
		double[] f = new double[coords.length];
		for(int i=0; i<coords.length; i++)
			f[i] = Double.parseDouble(coords[i]);		
		
		return f;
	}

	public static int countKMLFiles(String filename)
	{
		int count = 0;
		try
		{
         		String zeName;
         		ZipFile kmzFile = new ZipFile(filename);
         		Enumeration e = kmzFile.entries();
         		while(e.hasMoreElements())
         		{
 				zeName = ((ZipEntry)e.nextElement()).getName();
 				if(zeName.substring(zeName.length()-4, zeName.length()).equals(".kml"))
	 				count++;
			}			
			
		}
		catch(Exception e)
		{
			System.out.println(e);
		}

		return count;
	}

	public static Document[] decompressKMZ(String filename)
	{
		Document[] docArray; 
		int count = 0;
		try
		{
         		ZipFile kmzFile = new ZipFile(filename);
         		Enumeration e1 = kmzFile.entries();
         		String zeName;         		
         		while(e1.hasMoreElements())
         		{
 				zeName = ((ZipEntry)e1.nextElement()).getName();
 				if(zeName.substring(zeName.length()-4, zeName.length()).equals(".kml"))
	 				count++;
			}
         		      		
         		docArray = new Document[count];
         		
         		Enumeration e = kmzFile.entries();
         		int i=0;
         		while(e.hasMoreElements())
         		{
          	  		docArray[i] = parseKML(kmzFile.getInputStream((ZipEntry)e.nextElement()));   			
				i++;
			}

			return docArray;
		}
		catch(Exception e)
		{
			System.out.println("Error in decompress");
			System.out.println(e);
		}

		return null;
	}

	public static Document parseKML(InputStream kml)
	{
		Document doc;
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(kml);

			return doc;			
		}
		catch(Exception e)
		{
			System.out.println("error in parse");
			System.out.println(e);
		}
		
		return null;
	}

}
