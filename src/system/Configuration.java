package system;

import input.Keyboard;
import process.Environment;
import raytrace.scene.Scene;

public class Configuration {
	
	/*
	 * A configuration of this running instance
	 */

	/* *********************************************************************************************
	 * Static Vars
	 * *********************************************************************************************/
	//Environment for storing custom configuration parameters
	protected static final Environment env;
	
	//Defined parameters
	protected static String id = "Unassigned";
	protected static int screenWidth = 1024;
	protected static int screenHeight = 640;
	
	protected static boolean drawToScreen = false;
	protected static boolean isClock = false;
	protected static boolean isLeaf = false;
	
	protected static Scene masterScene = null;
	
	protected static Keyboard keyboard = null;
	protected static String workingDirectory = "/";
	protected static String modelsSubDirectory = "Models/";
	protected static String screenshotSubDirectory = "Screenshots/";
	

	/* %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	 * Network related
	 * %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
	public static class Networking
	{
		protected static int webInterfacePost = 9741;
		protected static int messageReceivePort = 9742;
		protected static int messageSendPort = 9743;
		protected static int messageThreadCount = 4;
		
		
		public static int getWebInterfacePost() { return webInterfacePost; }
		public static void setWebInterfacePost(int webInterfacePost) { Networking.webInterfacePost = webInterfacePost; }
		
		public static int getMessageReceivePort() { return messageReceivePort; }
		public static void setMessageReceivePort(int messageReceivePort) { Networking.messageReceivePort = messageReceivePort; }
		
		public static int getMessageSendPort() { return messageSendPort; }
		public static void setMessageSendPort(int messageSendPort) { Networking.messageSendPort = messageSendPort; }
		
		public static int getMessageThreadCount() { return messageThreadCount; }
		public static void setMessageThreadCount(int messageThreadCount) { Networking.messageThreadCount = messageThreadCount; }
			
	}
		

	
	/* *********************************************************************************************
	 * Static Constructor
	 * *********************************************************************************************/
	static {
		env = new Environment();
	}
	

	/* *********************************************************************************************
	 * Private Constructor to block instantiation
	 * *********************************************************************************************/
	private Configuration() { /**/ }

	/* *********************************************************************************************
	 * Static Getter/Setter Methods
	 * *********************************************************************************************/
	public static <T> T get(String key)
	{
		return env.get(key);
	}
	
	public static void set(String key, Object value)
	{
		env.set(key, value);
	}
	
	//ID
	public static String getId() { return id; }
	public static void setId(String id) { Configuration.id = id; }

	//Screen Width
	public static int getScreenWidth() { return screenWidth; }
	public static void setScreenWidth(int screenWidth) { Configuration.screenWidth = screenWidth; }

	//Screen Height
	public static int getScreenHeight() { return screenHeight; }
	public static void setScreenHeight(int screenHeight) { Configuration.screenHeight = screenHeight; }

	//Draw to screen
	public static boolean isDrawingToScreen() { return drawToScreen; }
	public static void setDrawToScreen(boolean drawToScreen) { Configuration.drawToScreen = drawToScreen; }

	//Clock
	public static boolean isClock() { return isClock; }
	public static void setClock(boolean isClock) { Configuration.isClock = isClock; }
	
	//Leaf
	public static boolean isLeaf() { return isLeaf; }
	public static void setLeaf(boolean isLeaf) { Configuration.isLeaf = isLeaf; }

	//Master Scene
	public static Scene getMasterScene() { return masterScene; }
	public static void setMasterScene(Scene masterScene) { Configuration.masterScene = masterScene; }

	//Keyboard
	public static Keyboard getKeyboard() { return keyboard; }
	public static void setKeyboard(Keyboard keyboard) { Configuration.keyboard = keyboard; }

	//Working Directory
	public static String getWorkingDirectory() { return workingDirectory; }
	public static void setWorkingDirectory(String workingDirectory) { Configuration.workingDirectory = workingDirectory; }

	//Models Sub-directory
	public static String getModelsSubDirectory() { return modelsSubDirectory; }
	public static void setModelsSubDirectory(String modelsSubDirectory) { Configuration.modelsSubDirectory = modelsSubDirectory; }

	//Screenshot Sub-directory
	public static String getScreenshotSubDirectory() { return screenshotSubDirectory; }
	public static void setScreenshotSubDirectory(String screenshotSubDirectory) { Configuration.screenshotSubDirectory = screenshotSubDirectory; }
	
}
