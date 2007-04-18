import java.util.HashMap;

public class RuleSet
{
	HashMap<String,Rule[]> haystack = new HashMap<String,Rule[]>();

	public void addRule(String _key, Rule _rule)
	{
		Rule[] oldRules = haystack.get(_key);
		if(oldRules == null)
		{
			haystack.put(_key, new Rule[] { _rule });
			return;
		}

		Rule[] temp = new Rule[oldRules.length+1];
		System.arraycopy(oldRules, 0, temp, 0, oldRules.length);
		temp[oldRules.length] = _rule;
		oldRules = temp;
	}

	public Rule[] getRules(int[] needles)
	{
		String ruleKey;
		Rule[] rules = new Rule[Constants.DEFAULT_RULE_ARRAY_SIZE];
		Rule[] newRules;
		int numRules = 0;
		for(int i = (int) Math.pow(2,(needles.length))-1; i >= 0; i--)
		{
			ruleKey = "";
			int j;
			for( j = needles.length-1; j >= 0; j--)
			{
				if(((i >>> j ) & 1) == 1)
				{
					if(needles[j] == 0)
						break;
					ruleKey += needles[j];
				}
				ruleKey += "_";
			}
			if( j != -1 )
				continue;

			newRules = haystack.get(ruleKey);
			if(newRules == null) continue;
			if(newRules.length+numRules > rules.length)
			{
				Rule[] temp = new Rule[rules.length+newRules.length+Constants.DEFAULT_RULE_ARRAY_SIZE];
				System.arraycopy(rules,0,temp,0,rules.length);
				rules = temp;
			}
			System.arraycopy(newRules,0,rules,numRules,newRules.length);
			numRules += newRules.length;
		}
		Rule[] tmp = new Rule[numRules];
		System.arraycopy(rules,0,tmp,0,numRules);
		return tmp;
	}
}
