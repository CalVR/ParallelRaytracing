package raytrace.material;

import math.Vector4;
import raytrace.color.Color;
import raytrace.data.ShadingData;
import raytrace.map.texture.Texture;

public class PassThroughMaterial extends Material{
	
	/*
	 * An implementation of a pass-through material
	 */
	/* *********************************************************************************************
	 * Instance Vars
	 * *********************************************************************************************/
	protected Texture texture;
	protected double refractiveIndex;
	

	/* *********************************************************************************************
	 * Constructor
	 * *********************************************************************************************/
	public PassThroughMaterial()
	{
		this.texture = Color.white();
		this.refractiveIndex = AIR_REFRACTIVE_INDEX;
	}
	
	public PassThroughMaterial(Texture texture)
	{
		this.texture = texture;
		this.refractiveIndex = AIR_REFRACTIVE_INDEX;
	}
	
	public PassThroughMaterial(Texture texture, double refractiveIndex)
	{
		this.texture = texture;
		this.refractiveIndex = refractiveIndex;
	}
	

	/* *********************************************************************************************
	 * Material Override
	 * *********************************************************************************************/
	@Override
	public Color shade(ShadingData data)
	{
		//Get color from texture
		Color tint = texture.evaluate(data.getIntersectionData());
		
		//Setup the point of intersection
		Vector4 point = data.getIntersectionData().getPoint();
		
		//Setup the normal
		Vector4 normal = data.getIntersectionData().getNormal();
		
		//Ray Direction
		Vector4 rayDir = (new Vector4(data.getRay().getDirection())).normalize3();
		
		//If the ray direction and the normal have an angle of less than Pi/2, then the ray is exiting the material
		if(normal.dot3(rayDir) > 0.0)
		{
			normal = normal.multiply3(-1.0);
		}
		
		
		Color recursionColor = null;
		if(data.getRecursionDepth() < DO_NOT_EXCEED_RECURSION_LEVEL)
		{
			//Pass right on through
			recursionColor = recurse(data, point, rayDir, refractiveIndex);
		}else{
			//Or terminate
			recursionColor = new Color();
		}
		
		return recursionColor.multiply3(tint);
	}
	
	
	/* *********************************************************************************************
	 * Getters/Setters
	 * *********************************************************************************************/
	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	public double getRefractiveIndex() {
		return refractiveIndex;
	}

	public void setRefractiveIndex(double refractiveIndex) {
		this.refractiveIndex = refractiveIndex;
	}
	
	

}