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

/*	public static Object decompressKMZ(String filename)
	{
		try
		{
			FileInputStream fis = new FileInputStream("filename");
			ZipInputStream zin = new ZipInputStream(new BufferedInputStream(fis));         		
			ZipEntry entry;
			long offset = 0;
			InputStream stream = new InputStream(); 
			while((entry = zin.getNextEntry()) != null) 
			{
				stream.reat( 				


			}			


         		ZipFile kmzFile = new ZipFile(filename);
         		System.out.println("kmz file contains "+kmzFile.size()+" entries");
         		Enumeration e = kmzFile.entries();
         		while(e.hasMoreElements())
         		{
          	  		parseKML(kmzFile.getInputStream((ZipEntry)e.nextElement()));   			
			}


		}
		catch(Exception e)
		{
			System.out.println(e);
		}



		System.out.println("decompressKMZ() returning...");
		return null;
	}*/

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

			System.out.println("decompressKMZ() returning...");
			return docArray;
		}
		catch(Exception e)
		{
			System.out.println("Error in decompress");
			System.out.println(e);
		}



		System.out.println("decompressKMZ() returning...");
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

			System.out.println("parseKML() returning...");
			return doc;			
			//and now we can parse the doc!! Get whatever info we want!!
		}
		catch(Exception e)
		{
			System.out.println("error in parse");
			System.out.println(e);
		}
		
		
		System.out.println("parseKML() returning...");
		return null;
	}


}