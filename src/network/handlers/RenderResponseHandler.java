package network.handlers;

import network.Message;

public class RenderResponseHandler extends MessageHandler {
	
	/*
	 * A handler for messages that are the response of a render request
	 * 
	 * Ops:
	 * 
	 * 		Get reauestID and nodeID
	 * 		Check against the given node, and that node's known request id
	 * 		If match,
	 * 
	 * 			Pass all of this to network renderer
	 * 
	 * 			Scratch that, do it here
	 * 
	 * 			Get pixel data from message (width, height, int array, etc.)
	 * 			Compare to known quest params in node
	 * 			if match,
	 * 				write pixel data to pixel buffer
	 * 				update node to reflect job completion (remove quest id, etc.)
	 * 				store any extra data like timings, performance data, etc.
	 * 				if all render jobs are complete, 
	 * 					send a render response with the appropriate data
	 * 			
	 */
	/* *********************************************************************************************
	 * Instance Vars
	 * *********************************************************************************************/
	//
	

	/* *********************************************************************************************
	 * Constructor
	 * *********************************************************************************************/
	public RenderResponseHandler()
	{
		super();
		messageType = Message.Type.RenderResponse;
	}
	

	/* *********************************************************************************************
	 * MessageHandler Override
	 * *********************************************************************************************/
	@Override
	public void handle(Message message)
	{
		// TODO Auto-generated method stub
	}

}
