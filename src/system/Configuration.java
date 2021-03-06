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
	protected static String id = Constants.Default.NODE_ID;
	protected static String nodeIdPrefix = "NightSky_Node_";
	protected static String frameFileNamePrefix = "NightSky_Frame_";
	protected static String animationFolderNamePrefix = "NightSky_Animation_";
	
	protected static int screenWidth = 1024;
	protected static int screenHeight = 640;
	
	protected static int renderWidth = 1024;
	protected static int renderHeight = 640;
	
	protected static boolean drawToScreen = false;
	protected static boolean isClock = false;
	protected static boolean isLeaf = false;
	protected static boolean isController = false;
	protected static boolean isRealTime = false;
	
	protected static double frameRate = 20.1;
	
	protected static int maxAllowableRenderingCores = Integer.MAX_VALUE;
	
	protected static Scene masterScene = null;
	
	protected static Keyboard keyboard = null;
	protected static Boolean canWriteToDisk = true;
	protected static String workingDirectory = "/";
	protected static String modelsSubDirectory = "Models/";
	protected static String screenshotSubDirectory = "Screenshots/";
	protected static String animationSubDirectory = "Animations/";
	protected static String textureSubDirectory = "Textures/";
	

	/* %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	 * Network related
	 * %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
	public static class Networking
	{
		protected static int webInterfacePort = 9741;
		protected static int messageReceivePort = 9742;
		protected static int messageSendPort = 9743;
		protected static int messageThreadCount = 4;
		
		protected static String controllerHostName = "localhost";
		
		
		public static int getWebInterfacePort() { return webInterfacePort; }
		public static void setWebInterfacePort(int webInterfacePort) { Networking.webInterfacePort = webInterfacePort; }
		
		public static int getMessageReceivePort() { return messageReceivePort; }
		public static void setMessageReceivePort(int messageReceivePort) { Networking.messageReceivePort = messageReceivePort; }
		
		public static int getMessageSendPort() { return messageSendPort; }
		public static void setMessageSendPort(int messageSendPort) { Networking.messageSendPort = messageSendPort; }
		
		public static int getMessageThreadCount() { return messageThreadCount; }
		public static void setMessageThreadCount(int messageThreadCount) { Networking.messageThreadCount = messageThreadCount; }
		
		
		public static String getControllerHostName() { return controllerHostName; }
		public static void setControllerHostName(String controllerHostName) { Networking.controllerHostName = controllerHostName; }
		
		
			
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

	//Node Id Prefix
	public static String getNodeIdPrefix() { return nodeIdPrefix; }
	public static void setNodeIdPrefix(String nodeIdPrefix) { Configuration.nodeIdPrefix = nodeIdPrefix; }
	
	//Frame File Name Prefix
	public static String getFrameFileNamePrefix() { return frameFileNamePrefix; }
	public static void setFrameFileNamePrefix(String frameFileNamePrefix) { Configuration.frameFileNamePrefix = frameFileNamePrefix; }

	//Animation Folder name Prefix
	public static String getAnimationFolderNamePrefix() { return animationFolderNamePrefix; }
	public static void setAnimationFolderNamePrefix(String animationFolderNamePrefix) { Configuration.animationFolderNamePrefix = animationFolderNamePrefix; }

	//Screen Width
	public static int getScreenWidth() { return screenWidth; }
	public static void setScreenWidth(int screenWidth) { Configuration.screenWidth = screenWidth; }

	//Screen Height
	public static int getScreenHeight() { return screenHeight; }
	public static void setScreenHeight(int screenHeight) { Configuration.screenHeight = screenHeight; }
	
	//Render Width
	public static int getRenderWidth() { return renderWidth;}
	public static void setRenderWidth(int renderWidth) { Configuration.renderWidth = renderWidth;}

	//Render Height
	public static int getRenderHeight() { return renderHeight; }
	public static void setRenderHeight(int renderHeight) { Configuration.renderHeight = renderHeight; }

	//Draw to screen
	public static boolean isDrawingToScreen() { return drawToScreen; }
	public static void setDrawToScreen(boolean drawToScreen) { Configuration.drawToScreen = drawToScreen; }

	//Clock
	public static boolean isClock() { return isClock; }
	public static void setClock(boolean isClock) { Configuration.isClock = isClock; }
	
	//Leaf
	public static boolean isLeaf() { return isLeaf; }
	public static void setLeaf(boolean isLeaf) { Configuration.isLeaf = isLeaf; }

	//Controller
	public static boolean isController() { return isController; }
	public static void setController(boolean isController) { Configuration.isController = isController; }

	//Real Time
	public static boolean isRealTime() { return isRealTime; }
	public static void setRealTime(boolean isRealTime) { Configuration.isRealTime = isRealTime; }
	
	//Frame Rate
	public static double getFrameRate() { return frameRate; }
	public static void setFrameRate(double frameRate) { Configuration.frameRate = frameRate; }

	//Max Allowable Rendering Tthreads
	public static int getMaxAllowableRenderingCores() { return maxAllowableRenderingCores; }
	public static void setMaxAllowableRenderingCores(	int maxAllowableRenderingCores) { Configuration.maxAllowableRenderingCores = maxAllowableRenderingCores; }

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

	//Animation Sub-directory
	public static String getAnimationSubDirectory() { return animationSubDirectory; }
	public static void setAnimationSubDirectory(String animationSubDirectory) { Configuration.animationSubDirectory = animationSubDirectory; }

	//Texture sub-directory
	public static String getTextureSubDirectory() { return textureSubDirectory; }
	public static void setTextureSubDirectory(String textureSubDirectory) { Configuration.textureSubDirectory = textureSubDirectory; }

	//Write to Disk
	public static Boolean canWriteToDisk() { return canWriteToDisk; }
	public static void setCanWriteToDisk(Boolean canWriteToDisk) { Configuration.canWriteToDisk = canWriteToDisk; }
	
}
