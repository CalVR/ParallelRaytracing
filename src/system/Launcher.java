package system;

import process.logging.Logger;
import raster.RasterDelegate;

public class Launcher {
	
	
	
	
	

	/* *********************************************************************************************
	 * Main
	 * *********************************************************************************************/
	
	public static void main(String[] args)
	{
		/*
		 * Args:
		 * 
		 * 		-
		 */
		
		//TODO: Parse the arguments
		//TODO: Set configuration values
		//TODO: Pass off control to the Delegate
		
		Logger.progress(-1, "Launching a Parallel Rendering Node with ID:[" + Configuration.getId() + "]...");
		
		RasterDelegate raster = new RasterDelegate(1920, 1080);
		
	}

}
