package file.obj.parse;

import process.logging.Logger;
import process.utils.StringUtils;
import file.StringParser;
import file.obj.ObjModelData;

public class ObjectParser extends StringParser<ObjModelData> {
	
	/* *********************************************************************************************
	 * Constructor
	 * *********************************************************************************************/
	public ObjectParser() { keyToken = "o"; }
	

	/* *********************************************************************************************
	 * Override Parsing Methods
	 * *********************************************************************************************/
	@Override
	public void parse(String str, ObjModelData pop)
	{
		//Get the tokens
		String[] tokens = tokens(str.trim(), " ");
		
		//Parse the tokens into the obj model
		try{
			
			//First make sure the first token matches the key token
			if(!tokens[0].equals(keyToken))
				throw new Exception("ObjectParser: Excepted a key token of [" + keyToken + "] but encountered [" + tokens[0] + "]");
			
			pop.addObject(tokens[1]);
			Logger.message(-11, "ObjectFileLoader: Object: " + tokens[1]);
			
		}catch(Exception e) {
			Logger.error(-1, "Failed to parse an object line.");
			Logger.error(-1, StringUtils.stackTraceToString(e));
			e.printStackTrace();
		}
	}
}
