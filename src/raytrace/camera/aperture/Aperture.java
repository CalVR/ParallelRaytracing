package raytrace.camera.aperture;

import java.io.Serializable;

import math.Vector3;

public abstract class Aperture implements Serializable {
	
	/*
	 * A base class for camera apertures
	 */

	/* *********************************************************************************************
	 * Static Vars
	 * *********************************************************************************************/
	private static final long serialVersionUID = -999933587348988866L;
	
	
	/* *********************************************************************************************
	 * Instance Vars
	 * *********************************************************************************************/
	protected double radius;
	
	
	/* *********************************************************************************************
	 * Interface Methods
	 * *********************************************************************************************/
	public abstract Vector3 sample();
	public abstract Vector3 sample(Vector3 sample);
	

	/* *********************************************************************************************
	 * Getters/Setters
	 * *********************************************************************************************/
	public double getRadius() {
		return radius;
	}
	public void setRadius(double radius) {
		this.radius = radius;
	}

}
