import java.util.*;
import java.io.File;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class RuleFactory
{
	private Logger myLogger;
	private ActionFactory af;
	private ElementFactory ef;
	private HashMap<String,RuleSet> ruleSets = new HashMap<String,RuleSet>();

	public RuleFactory(File folder, String ext, ActionFactory _af, ElementFactory _ef, Logger _myLogger)
	{
		af = _af;
		ef = _ef;
		myLogger = _myLogger;
		File[] files = folder.listFiles(new FactoryFileFilter(ext));
		createRuleSets(files);
	}

	public RuleFactory(File folder, ActionFactory _af, ElementFactory _ef, Logger _myLogger)
	{
		af = _af;
		ef = _ef;
		myLogger = _myLogger;
		File[] files = folder.listFiles(new FactoryFileFilter(Constants.RULE_EXTENSION));
		createRuleSets(files);
	}

	public RuleFactory(ActionFactory _af, ElementFactory _ef, Logger _myLogger)
	{
		af = _af;
		ef = _ef;
		myLogger = _myLogger;
		File folder = new File(Constants.DEFAULT_DATA_DIR);
		File[] files = folder.listFiles(new FactoryFileFilter(Constants.RULE_EXTENSION));
		createRuleSets(files);
	}

	public RuleFactory(File[] _files, ActionFactory _af, ElementFactory _ef, Logger _myLogger)
	{
		af = _af;
		ef = _ef;
		myLogger = _myLogger;
		createRuleSets(_files);
	}

	public void createRuleSets(File[] files)
	{
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc;
			Element rules_list, rule;
			NodeList rules, ruleNodes, subjectNodes, verbNodes, directobjectNodes, indirectobjectNodes, spaceNodes;
			String[] subjects, verbs, directobjects, indirectobjects, spaces;
			String key;
			Rule newRule;
			int i;
			
			for(File file : files)
			{

				if(!file.exists())
				{
					myLogger.message("RuleFactory cannot find file: " + file + "\n", true);
					continue;
				}
				doc = db.parse(file);

				rules_list = (Element) doc.getElementsByTagName("rules").item(0);
				rules = rules_list.getElementsByTagName("rule");
				for(i = rules.getLength()-1;i>=0; i--)
				{
					rule = (Element) rules.item(i);

					ruleNodes = rule.getElementsByTagName("function");

					if(ruleNodes.getLength()==0)
						newRule = null;
					else
						newRule = new Rule(new StringFunction((Element) ruleNodes.item(0)));

					subjectNodes = rule.getElementsByTagName("subject");
					verbNodes = rule.getElementsByTagName("verb");
					directobjectNodes = rule.getElementsByTagName("directobject");
					indirectobjectNodes = rule.getElementsByTagName("indirectobject");
					spaceNodes = rule.getElementsByTagName("space");

					subjects = getContents(subjectNodes);
					verbs = getContents(verbNodes);
					directobjects = getContents(directobjectNodes);
					indirectobjects = getContents(indirectobjectNodes);
					spaces = getContents(spaceNodes);

					for(String subject : subjects)
					{
						if(!subject.equals(""))
							subject = ""+ef.getType(subject);

						for(String verb : verbs)
						{
							if(!verb.equals(""))
								verb = ""+af.getType(verb);

							for(String directobject : directobjects)
							{
								if(!directobject.equals(""))
									directobject = ""+ef.getType(directobject);

								for(String indirectobject : indirectobjects)
								{
									if(!indirectobject.equals(""))
											indirectobject = ""+ef.getType(indirectobject);

									key = indirectobject+"_"+directobject+"_"+subject+"_"+verb+"_";

									for(String space : spaces)
									{
										addRuleToSet(space, key, newRule);
									}
								}
							}
						}
					}
				}
			}
			
		}
		catch(Exception e) // better error reporting!
		{
			myLogger.message("Failed to read input files! : " + e.getMessage() + " :\n", true);
			e.printStackTrace();
		}

	}

	public void addRuleToSet(String _setName, String _key, Rule _rule)
	{
		RuleSet set;
		System.out.println("Adding rule with key " + _key);
		if( (set = ruleSets.get(_setName)) != null)
		{
			set.addRule(_key,_rule);
		}
		else
		{
			set = new RuleSet();
			set.addRule(_key,_rule);
			ruleSets.put(_setName,set);
		}
	}

	public static String[] getContents(NodeList _nodes) throws Exception
	{
		Element e;
		int i;

		if(_nodes.getLength() == 0)
		{
			return(new String[]{ "" });
		}
		e = (Element) _nodes.item(0);
		if(e.getTextContent().equals("*"))
		{
			return(new String[]{ "" });
		}

		String[] contents = new String[_nodes.getLength()];
		for(i = _nodes.getLength()-1; i >= 0; i--)
		{
			e = (Element) _nodes.item(i);
			contents[i] = e.getTextContent();
		}
		return contents;
	}

	public RuleSet getRuleSet(String _space)
	{
		return ruleSets.get(_space);
	}
}
