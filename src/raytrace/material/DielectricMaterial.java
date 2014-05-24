package raytrace.material;

import math.Vector4;
import raytrace.color.Color;
import raytrace.data.ShadingData;
import raytrace.map.Texture;

public class DielectricMaterial extends Material{
	
	/*
	 * An implementation of a material that is a dielectric
	 */
	/* *********************************************************************************************
	 * Instance Vars
	 * *********************************************************************************************/
	protected Texture beerTexture;
	double refractiveIndex = 1.0;
	

	/* *********************************************************************************************
	 * Constructor
	 * *********************************************************************************************/
	public DielectricMaterial(Texture beerTexture, double refractiveIndex)
	{
		this.beerTexture = beerTexture;
		this.refractiveIndex = refractiveIndex;
	}
	

	@Override
	public Color shade(ShadingData data)
	{
		//Get color from texture
		Color tint = beerTexture.evaluate(data.getIntersectionData());
		
		//Setup the point of intersection
		Vector4 point = data.getIntersectionData().getPoint();
		
		//Setup the normal
		Vector4 normal = data.getIntersectionData().getNormal();
		
		
		double DdotN = normal.dot3(data.getRay().getDirection());
		double thisRefractiveIndex = refractiveIndex;
		boolean exiting = false;
		
		//If the ray direction and the normal have an angle of less than Pi/2, then the ray is exiting the material
		if(DdotN > 0.0) {
			//TODO: Would it be better to use a refractiveIndex stack?
			thisRefractiveIndex = AIR_REFRACTIVE_INDEX;//TODO: Is this the right test, and right place to assume we are exiting the material?
			normal = normal.multiply3(-1.0);
			DdotN *= -1.0;
			
			exiting = true;
		}
		
		
		//Pre-calculate some values for refraction
		double incomingRefractiveIndex = data.getRefractiveIndex();
		double refractiveRatio = incomingRefractiveIndex / thisRefractiveIndex;
		
		
		//Calculate the refraction direction
		double phiDiscrim = 1.0 - (refractiveRatio * refractiveRatio) *
							(1.0 - (DdotN * DdotN));
		
		
		Color refractColor = new Color();
		if(phiDiscrim > 0.0 && data.getRecursionDepth() < DO_NOT_EXCEED_RECURSION_LEVEL)
		{
			Vector4 rayDir = data.getRay().getDirection();
			Vector4 thetaSide = rayDir.subtract3(normal.multiply3(DdotN)).multiply3(refractiveRatio);
			Vector4 phiSide = normal.multiply3(Math.sqrt(phiDiscrim));
			Vector4 refracDir = thetaSide.subtract3(phiSide);
			
			refractColor = recurse(data, point, refracDir, exiting ? AIR_REFRACTIVE_INDEX : refractiveIndex);
		}
		
		
		
		
		double reflectiveCoeff = 0.0;
		//If there was no refraction, fix reflective coeff
		if(phiDiscrim <= 0.0)
		{
			//Purely reflective
			reflectiveCoeff = 1.0;
		}else{
			//Calculate the reflective coefficient
			double baseReflectiveCoeff = (thisRefractiveIndex - 1.0) / (thisRefractiveIndex + 1.0);
			baseReflectiveCoeff *= baseReflectiveCoeff;
			
			//Schlick Approx
			reflectiveCoeff = baseReflectiveCoeff + (1.0 - baseReflectiveCoeff) * Math.pow(1.0 - Math.abs(DdotN), 5.0);
		}
		

		//If reflective, go divin'
		Color rflectColor = new Color();
		if(reflectiveCoeff != 0.0 && data.getRecursionDepth() < DO_NOT_EXCEED_RECURSION_LEVEL)
		{
			rflectColor = reflect(data, point, normal, exiting ? refractiveIndex : AIR_REFRACTIVE_INDEX);
		}
		
		
		//Scale the colors
		Color refractiveColor = refractColor.multiply3(1.0 - reflectiveCoeff);
		Color reflectiveColor = rflectColor.multiply3(reflectiveCoeff);
		
		//Add reflective and refractive colors together to get the final color
		Color totalColor = refractiveColor.add3(reflectiveColor);
					
		//If the ray is exiting the surface, then apply beers law to all light that was collected recursively
		if(exiting)
		{
			double d = data.getIntersectionData().getDistance();
			double[] a = tint.getChannels();
			Color beerColor = new Color(Math.exp(-1.0 * Math.log(a[0]) * d), 
										Math.exp(-1.0 * Math.log(a[1]) * d), 
										Math.exp(-1.0 * Math.log(a[2]) * d));

			totalColor = totalColor.multiply3(beerColor);
		}
		return totalColor;
	}

}
