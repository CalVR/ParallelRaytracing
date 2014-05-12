package raytrace.camera;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import process.logging.Logger;
import raytrace.camera.aperture.Aperture;
import raytrace.camera.aperture.CircleAperture;

//import math.CompositeRay;
import math.Ray;
import math.Vector4;

public class ProgrammableCamera extends Camera {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/* *********************************************************************************************
	 * Instance Vars
	 * *********************************************************************************************/
	protected Vector4 cameraX;
	protected Vector4 cameraY;
	
	protected double imagePlaneWidth;
	protected double imagePlaneHeight;
	
	protected double imagePlaneRatio;
	
	protected int superSamplingLevel = 1;
	protected double samplingDelta = 1.0;
	protected boolean stratifiedSampling = false;
	//protected Function2D<Double, Double> superSampDistroFuncX = new PassThrough2D<Double>();
	//protected Function2D superSampDistroFuncY;
	
	protected double focalPlaneDistance = 1.0;
	protected Aperture aperture = new CircleAperture();

	protected double startPixelX = 0;
	protected double startPixelY = 0;
	protected double pixelStepSize = 1;
	
	protected RayIterator iter = new RayIterator();
	

	/* *********************************************************************************************
	 * Private Instance Vars
	 * *********************************************************************************************/
	//Used for fast ray math
	private double[] vdir;
	private double[] camX;
	private double[] camY;
	
	//Used for sub ray calculations
	protected Vector4 dir = new Vector4(0,0,-1,0);	
	protected double pw;
	protected double ph;
	protected double woffset, hoffset;
	

	/* *********************************************************************************************
	 * Constructors
	 * *********************************************************************************************/
	public ProgrammableCamera()
	{
		super();
	}
	
	public ProgrammableCamera(Vector4 position, Vector4 viewingDirection, Vector4 up, double fieldOfView, double pixelWidth, double pixelHeight)
	{
		super(position, viewingDirection, up, fieldOfView, pixelWidth, pixelHeight);
		update();
	}
	
	public ProgrammableCamera(Vector4 position, Vector4 viewingDirection, Vector4 up, double fieldOfView, double pixelWidth, double pixelHeight, int superSamplingLevel)
	{
		super(position, viewingDirection, up, fieldOfView, pixelWidth, pixelHeight);
		this.superSamplingLevel = superSamplingLevel;
		update();
	}
	

	/* *********************************************************************************************
	 * Getters/Setter
	 * *********************************************************************************************/
	public int getSuperSamplingLevel() {
		return superSamplingLevel;
	}

	public void setSuperSamplingLevel(int superSamplingLevel) {
		this.superSamplingLevel = superSamplingLevel;
	}

	public boolean isStratifiedSampling() {
		return stratifiedSampling;
	}

	public void setStratifiedSampling(boolean stratifiedSampling) {
		this.stratifiedSampling = stratifiedSampling;
	}

	public double getStartPixelX() {
		return startPixelX;
	}

	public void setStartPixelX(double startPixelX) {
		this.startPixelX = startPixelX;
	}

	public double getStartPixelY() {
		return startPixelY;
	}

	public void setStartPixelY(double startPixelY) {
		this.startPixelY = startPixelY;
	}

	public double getPixelStepSize() {
		return pixelStepSize;
	}

	public void setPixelStepSize(double pixelStepSize) {
		this.pixelStepSize = pixelStepSize;
	}

	public double getFocalPlaneDistance() {
		return focalPlaneDistance;
	}

	public void setFocalPlaneDistance(double focalPlaneDistance) {
		this.focalPlaneDistance = focalPlaneDistance;
	}

	public Aperture getAperture() {
		return aperture;
	}

	public void setAperture(Aperture aperture) {
		if(aperture != null)
			this.aperture = aperture;
	}

	/* *********************************************************************************************
	 * Iteration
	 * *********************************************************************************************/
	@Override
	public Iterator<Ray> iterator()
	{
		//Reset the pre-allocated iterator and return it
		iter.reset();
		return iter;
	}


	/* *********************************************************************************************
	 * Calculation Methods
	 * *********************************************************************************************/
	@Override
	protected void update()
	{
		//Update camera axes
		cameraX = viewingDirection.cross3(up).normalize3();
		cameraY = viewingDirection.cross3(cameraX).normalize3();
		
		//Update image plane ratio
		imagePlaneRatio = pixelWidth / pixelHeight;
		
		//Update the image plane values
		imagePlaneWidth = 2*Math.tan(fieldOfView/2.0);
		imagePlaneHeight = imagePlaneWidth / imagePlaneRatio;
		
		//
		vdir = viewingDirection.getM();
		camX = cameraX.getM();
		camY = cameraY.getM();
		
		//
		samplingDelta = 1.0/(double)superSamplingLevel;
	}
	
	@Override
	protected void wasModified()
	{
		update();
	}
	
	//TODO: This is not exactly good design.....
	public void forceUpdate()
	{
		Logger.progress(-8, "PinholeCamera: Forcing Update...");
		update();
	}

	
	/* *********************************************************************************************
	 * Getter/Setter Methods
	 * *********************************************************************************************/
	@Override
	protected Ray getRay(double x, double y)
	{
		return null;
	}
	
	public void setVerticalFieldOfView(double fov)
	{
		//fieldOfView = 2.0 * Math.atan((imagePlaneWidth/imagePlaneHeight) * Math.tan(fov/2.0));
		fieldOfView = 2.0 * Math.atan((pixelWidth/pixelHeight) * Math.tan(fov/2.0));
		wasModified();
	}
	
	@Override
	public Collection<Camera> decompose(int count)
	{
		//If the count of decompositions is one or less, just use this camera
		if(count <= 1) {
			ArrayList<Camera> oneCam = new ArrayList<Camera>(1);
			oneCam.add(this);
			return oneCam;
		}
		
		//Create a collection for the decompositions
		ArrayList<Camera> cams = new ArrayList<Camera>(count);
		ProgrammableCamera newCam;
		
		//Decompose the current camera
		for(int i = 0; i < count; i++)
		{
			newCam = this.duplicate();
			newCam.setStartPixelX((startPixelX + i * pixelStepSize) % newCam.pixelWidth);
			newCam.setStartPixelY(startPixelY + (int)(startPixelX + i * pixelStepSize) / (int)newCam.pixelWidth);
			newCam.setPixelStepSize(count * pixelStepSize);
			newCam.iter.reset();
			
			cams.add(newCam);
		}
		
		return cams;
	}
	
	private ProgrammableCamera duplicate()
	{
		ProgrammableCamera cam = new ProgrammableCamera();
		cam.setPosition(position);
		cam.setViewingDirection(viewingDirection);
		cam.setUp(up);
		cam.setFieldOfView(fieldOfView);
		cam.setPixelWidth(pixelWidth);
		cam.setPixelHeight(pixelHeight);
		
		cam.setSuperSamplingLevel(superSamplingLevel);
		cam.setStratifiedSampling(stratifiedSampling);
		
		cam.setAperture(aperture);
		cam.setFocalPlaneDistance(focalPlaneDistance);
		
		cam.setStartPixelX(startPixelX);
		cam.setStartPixelY(startPixelY);
		cam.setPixelStepSize(pixelStepSize);
		
		cam.update();
		
		return cam;
	}


	/* *********************************************************************************************
	 * Private Classes
	 * *********************************************************************************************/
	private class RayIterator implements Iterator<Ray>, Serializable
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		
		/* *********************************************************************************************
		 * Instance Vars
		 * *********************************************************************************************/
		private double currentPixelX;
		private double currentPixelY;
		
		private PinholeRay ray;
		private Ray subRay;

		private Vector4 orig;
		

		/* *********************************************************************************************
		 * Constructor
		 * *********************************************************************************************/
		public RayIterator()
		{
			subRay = new Ray();
			ray = new PinholeRay(subRay);
			reset();
		}

		/* *********************************************************************************************
		 * Iterator Methods
		 * *********************************************************************************************/

		@Override
		public boolean hasNext()
		{
			return currentPixelX < pixelWidth && currentPixelY < pixelHeight;
		}

		@Override
		public Ray next()
		{
			//Get the next ray
			ray.setPixelX((int)currentPixelX);
			ray.setPixelY((int)currentPixelY);
			
			//Increment the counters
			currentPixelX+=pixelStepSize;
			if(currentPixelX >= pixelWidth) {
				currentPixelX = currentPixelX % pixelWidth;
				++currentPixelY;
			}
			
			return ray;
		}

		@Override
		public void remove()
		{
			//No
		}
		
		public void reset()
		{	
			currentPixelX = startPixelX;
			currentPixelY = startPixelY;
			
			orig = new Vector4(position);
			subRay.setOrigin(orig);
			ray.setOrigin(orig);
		}
		
	}
	
	private class PinholeRay extends Ray implements Serializable
	{
		/*
		 * A class that represents a set of implicit Rays in 3D space
		 */
	
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		
		/* *********************************************************************************************
		 * Instance Vars
		 * *********************************************************************************************/
		protected Ray subRay;
		protected RayIterator iter;
		
		
		/* *********************************************************************************************
		 * Constructor
		 * *********************************************************************************************/
		public PinholeRay(Ray subRay)
		{
			if(subRay == null)
				subRay = new Ray();
			this.subRay = subRay;
			iter = new RayIterator(subRay);
		}
		
	
		/* *********************************************************************************************
		 * Getters/Setters
		 * *********************************************************************************************/
		
		
		/* *********************************************************************************************
		 * Iteration Overrides
		 * *********************************************************************************************/
		@Override
		public Iterator<Ray> iterator()
		{
			iter.reset();
			return iter;
		}



		/* *********************************************************************************************
		 * Private Classes
		 * *********************************************************************************************/
		private class RayIterator implements Iterator<Ray>, Serializable
		{	
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			
			/* *********************************************************************************************
			 * Instance Vars
			 * *********************************************************************************************/
			private double subPixelU;
			private double subPixelV;
			private Ray ray;
			
			private Vector4 apertureSample;
			private Vector4 apertureOrigin;
			private double[] sampleM;
			private double[] origM;
			private double[] dirM;
			
	
			/* *********************************************************************************************
			 * Constructor
			 * *********************************************************************************************/
			public RayIterator(Ray ray)
			{
				subPixelU = 0;
				subPixelV = 0;
				this.ray = ray;
				apertureSample = new Vector4();
				apertureOrigin = new Vector4();
			}
			
	
			/* *********************************************************************************************
			 * Iterator Methods
			 * *********************************************************************************************/
	
			@Override
			public boolean hasNext()
			{
				return subPixelU < superSamplingLevel && subPixelV < superSamplingLevel;
			}
	
			@Override
			public Ray next()
			{
				//Get the next ray
				//Calculate sampling offsets
				woffset = subPixelU * samplingDelta + (stratifiedSampling ? Math.random()*samplingDelta : samplingDelta/2.0);
				hoffset = subPixelV * samplingDelta + (stratifiedSampling ? Math.random()*samplingDelta : samplingDelta/2.0);
				
				//Pre-calculate the axis weights
				pw = (((pixelX+woffset)/pixelWidth) - 0.5) * imagePlaneWidth;
				ph = (((pixelY+hoffset)/pixelHeight) - 0.5) * imagePlaneHeight;
				
				//Create the direction vector
				dir.set(focalPlaneDistance * (vdir[0] + camX[0] * pw + camY[0] * ph), 
						focalPlaneDistance * (vdir[1] + camX[1] * pw + camY[1] * ph), 
						focalPlaneDistance * (vdir[2] + camX[2] * pw + camY[2] * ph), 
						0);
				dirM = dir.getM();
				
				apertureSample = aperture.sample(apertureSample);
				sampleM = apertureSample.getM();
				
				apertureOrigin.set(camX[0] * sampleM[0] + camY[0] * sampleM[1],
								   camX[1] * sampleM[0] + camY[1] * sampleM[1],
								   camX[2] * sampleM[0] + camY[2] * sampleM[1],
								   0);
				
				
				//Finalize Direction
				origM = apertureOrigin.getM();
				dir.set(dirM[0] - origM[0],
						dirM[1] - origM[1],
						dirM[2] - origM[2],
						0);
				

				//Finalize Origin
				origM = position.getM();
				apertureOrigin.set(origM[0] + camX[0] * sampleM[0] + camY[0] * sampleM[1],
								   origM[1] + camX[1] * sampleM[0] + camY[1] * sampleM[1],
							   	   origM[2] + camX[2] * sampleM[0] + camY[2] * sampleM[1],
								   0);
				
				
				subRay.setDirection(dir.normalize3());
				subRay.setOrigin(apertureOrigin);
				
				//Increment the counters
				++subPixelU;
				if(subPixelU >= superSamplingLevel) {
					subPixelU = 0;
					++subPixelV;
				}
				
				
				return ray;
			}
	
			@Override
			public void remove()
			{
				//No
			}
			
			public void reset()
			{
				subPixelU = 0;
				subPixelV = 0;
			}
			
		}
	}
}