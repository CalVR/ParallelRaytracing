package raytrace.light;

import math.Vector3;
import math.ray.Ray;
import raytrace.data.IlluminationData;
import raytrace.data.IntersectionData;
import raytrace.scene.Scene;

public class PointLight extends Light {
	
	/*
	 * A basic point Light
	 */
	/* *********************************************************************************************
	 * Overrides
	 * *********************************************************************************************/

	@Override
	public IlluminationData illuminate(Scene scene, Vector3 point)
	{
		IlluminationData ildata = new IlluminationData();
		Vector3 toPoint = point.subtract(position);
		double toPointMagSqrd = toPoint.magnitudeSqrd();
		double distance = Math.sqrt(toPointMagSqrd);
		
		double attenuation = constantAttenuation +
							 linearAttenuation * distance +
							 quadraticAttenuation * toPointMagSqrd;
		
		
		IntersectionData shadowData = shadowed(scene, 
				new Ray(point, toPoint.multiply(-1.0), 0, 0), distance);
		
		double blocked = shadowData == null ? 1.0 : 0.0;
		
		
		ildata.setColor(color.multiply3( intensity * ( 1.0 / attenuation) * blocked));
		ildata.setDirection(toPoint.normalizeM());
		ildata.setDistance(distance);
		
		return ildata;
	}

}
