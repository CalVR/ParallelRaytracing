package file.obj.parse;

import process.logging.Logger;
import process.utils.StringUtils;
import file.StringParser;
import file.obj.ObjModelData;

public class FaceParser extends StringParser<ObjModelData> {
	
	/* *********************************************************************************************
	 * Constructor
	 * *********************************************************************************************/
	public FaceParser() { keyToken = "v"; }
	

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
				throw new Exception("FaceParser: Excepted a key token of [" + keyToken + "] but encountered [" + tokens[0] + "]");
			
			pop.addFace();
			
			//For all of the face tokens
			String[] faceComponents;
			for(int i = 1; i < tokens.length; ++i)
			{
				faceComponents = tokens(tokens[i], "/");
				
				if(faceComponents.length != 3)
					throw new Exception("FaceParser: The line [" + str + "] has an improperly formatted part [" + tokens[i] + "].");
				
				for(int j = 0; j < faceComponents.length; ++j)
				{
					if(faceComponents[j].length() == 0)
						faceComponents[j] = "0";
				}
				
				pop.addVertexToFace(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
			}
			
		}catch(Exception e) {
			Logger.error(-1, "Failed to parse a face line.");
			Logger.error(-1, StringUtils.stackTraceToString(e));
			e.printStackTrace();
		}
	}
}