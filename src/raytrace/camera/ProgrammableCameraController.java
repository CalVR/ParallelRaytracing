package raytrace.camera;

import java.util.LinkedList;

import math.Spline;
import math.Vector3;

public class ProgrammableCameraController {
	
	/*
	 * A controller for Programmable Cameras
	 */
	
	/* *********************************************************************************************
	 * Instance Vars
	 * *********************************************************************************************/
	protected ProgrammableCamera camera;
	
	protected double elapsedTime;
	protected boolean paused;

	protected LinkedList<CameraSpline> positionSplines;
	protected LinkedList<CameraSpline> lookAtSplines;
	protected LinkedList<CameraSpline> fieldOfViewSplines;
	protected LinkedList<CameraSpline> focalDistanceSplines;
	protected LinkedList<CameraSpline> apertureRadiusSplines;
	
	//Currently Unsupported
	//protected LinkedList<CameraSpline> superSamplingSplines;//Seems useful
	

	/* *********************************************************************************************
	 * Constructor
	 * *********************************************************************************************/
	public ProgrammableCameraController()
	{
		camera = null;
		
		elapsedTime = 0.0;
		paused = false;
		
		positionSplines = new LinkedList<CameraSpline>();
		lookAtSplines = new LinkedList<CameraSpline>();
		fieldOfViewSplines = new LinkedList<CameraSpline>();
		focalDistanceSplines = new LinkedList<CameraSpline>();
		apertureRadiusSplines = new LinkedList<CameraSpline>();
	}
	
	public ProgrammableCameraController(ProgrammableCamera camera)
	{
		this();
		this.camera = camera;
	}
	

	/* *********************************************************************************************
	 * Update Methods
	 * *********************************************************************************************/
	public void upate(double dt)
	{
		if(paused)
			return;
		
		Vector3 position = point(positionSplines, elapsedTime);
		Vector3 lookAt = point(lookAtSplines, elapsedTime);
		Vector3 fieldOfView = point(fieldOfViewSplines, elapsedTime);
		Vector3 focalDistance = point(focalDistanceSplines, elapsedTime);
		Vector3 apertureRadius = point(apertureRadiusSplines, elapsedTime);
		
		//Update camera properties
		if(position != null)
			camera.setPosition(position);
		if(lookAt != null && position != null)
			camera.setViewingDirection(lookAt.subtractM(position));
		if(fieldOfView != null)
			camera.setFieldOfView(fieldOfView.get(0));
		if(focalDistance != null)
			camera.setFocalPlaneDistance(focalDistance.get(0));
		if(apertureRadius != null)
			camera.getAperture().setRadius(apertureRadius.get(0));
		
		//Update the camera
		camera.update();
		
		elapsedTime += dt;
	}
	
	protected Vector3 point(LinkedList<CameraSpline> splines, double time)
	{
		Vector3 p = null;
	    double summedTime = 0.0;
	    
	    //Prevent negative time
	    time = Math.max(0.0, time);
	    
	    //Find the spline used for the current time
	    for(CameraSpline spline : splines)
	    {
	    	if(time <= summedTime + spline.getDuration())
	        {
	            double tnorm = (time - summedTime) / spline.getDuration();
	            p = spline.getSpline().point(tnorm);
	            break;
	        }
	    
	        summedTime += spline.getDuration();
	    
	        //If time is greater than the end of the spline set, try again with a time in range
	        if(spline.equals(splines.peekLast()))
	        {
	            p = point(splines, time % summedTime);
	        }
	    }
	    
	    return p;
	}
	
	
	/* *********************************************************************************************
	 * Control Methods
	 * *********************************************************************************************/
	public void start()
	{
		this.paused = false;
	}
	
	public void pause()
	{
		this.paused = true;
	}
	
	public void stop()
	{
		this.paused = true;
		restart();
	}
	
	public void restart()
	{
		this.elapsedTime = 0;
	}
	
	
	/* *********************************************************************************************
	 * Mutation Methods
	 * *********************************************************************************************/
	public void addPositionSpline(Spline spline, double duration)
	{
		positionSplines.add(new CameraSpline(spline, duration));
	}
	
	public void addLookAtSpline(Spline spline, double duration)
	{
		lookAtSplines.add(new CameraSpline(spline, duration));
	}
	
	public void addFieldOfViewSpline(Spline spline, double duration)
	{
		fieldOfViewSplines.add(new CameraSpline(spline, duration));
	}
	
	public void addFocalDistanceSpline(Spline spline, double duration)
	{
		focalDistanceSplines.add(new CameraSpline(spline, duration));
	}
	
	public void addApertureRadiusSpline(Spline spline, double duration)
	{
		apertureRadiusSplines.add(new CameraSpline(spline, duration));
	}


	/* *********************************************************************************************
	 * Private Classes
	 * *********************************************************************************************/
	protected class CameraSpline {
		
		/*
		 * A camera spline that co,bines a spline with a start time
		 */

		/* *********************************************************************************************
		 * Instance Vars
		 * *********************************************************************************************/
		private Spline spline;
		private double duration;

		
		/* *********************************************************************************************
		 * Constructor
		 * *********************************************************************************************/
		public CameraSpline()
		{
			//
		}
		
		public CameraSpline(Spline spline, double duration)
		{
			this.spline = spline;
			this.duration = duration;
		}

		
		/* *********************************************************************************************
		 * Getters/Setters
		 * *********************************************************************************************/
		public Spline getSpline() {
			return spline;
		}

		public void setSpline(Spline spline) {
			this.spline = spline;
		}

		public double getDuration() {
			return duration;
		}

		public void setDuration(double duration) {
			this.duration = duration;
		}
	}
}
