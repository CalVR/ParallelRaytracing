package file;

import java.util.Map;


public abstract class StringParser<POPULATES_THIS_TYPE> {
	
	/*
	 * A base class for parsing strings and returning an object
	 */
	/* *********************************************************************************************
	 * Instance Vars
	 * *********************************************************************************************/
	protected String keyToken = null;
	
	/* *********************************************************************************************
	 * Abstract Parsing Methods
	 * *********************************************************************************************/
	public abstract void parse(String str, POPULATES_THIS_TYPE pop);
	

	/* *********************************************************************************************
	 * Concrete Utility Methods
	 * *********************************************************************************************/
	protected String[] tokens(String str, String pattern)
	{
		return str.trim().split(pattern);
	}
	
	protected String[] splitAtFirst(String str, String character)
	{
		int firstIndex = str.indexOf(character);
		
		//Skip empty strings or strings that don't have the splitting token
		if(str.length() == 0 || firstIndex < 0) {
			String[] result = {str};
			return result;
		}
		
		//Get the line key
		String[] result = new String[2];
		result[0] = str.substring(0, firstIndex);
		
		if(firstIndex < str.length()-1) {
			result[1] =  str.substring(firstIndex+1, str.length());
		}else{
			result[1] = "";
		}
		
		return result;
	}
	
	public String getKeyToken() { return keyToken; }
	
	public void addTo(Map<String, StringParser<POPULATES_THIS_TYPE>> map)
	{
		map.put(keyToken, this);
	}
}
