package network.handlers;

import network.Message;

public class ShutdownHandler extends MessageHandler {
	
	/*
	 * 
	 */
	/* *********************************************************************************************
	 * Instance Vars
	 * *********************************************************************************************/
	//
	

	/* *********************************************************************************************
	 * Constructor
	 * *********************************************************************************************/
	public ShutdownHandler()
	{
		super();
		messageType = Message.Type.Shutdown;
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