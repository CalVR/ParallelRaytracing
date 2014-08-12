package raytrace.material;

import math.Vector3;
import raytrace.color.Color;
import raytrace.data.IlluminationData;
import raytrace.data.ShadingData;
import raytrace.light.Light;
import raytrace.map.texture._3D.Texture3D;

public class SubSurfaceDiffusePTTestMaterial extends Material{
	
	/*
	 * A test material
	 */
	/* *********************************************************************************************
	 * Instance Vars
	 * *********************************************************************************************/
	protected Material surfaceMaterial;
	protected Texture3D volumeTexture;
	protected double scatterCoeff = 0.5;
	protected double refractiveIndex = 1.0;
	protected double roughness = 0.0;
	protected int scatterSampleCount = 1;
	

	/* *********************************************************************************************
	 * Constructor
	 * *********************************************************************************************/
	public SubSurfaceDiffusePTTestMaterial(Material surfaceMaterial, Texture3D volumeTexture, double scatterCoeff,
			double refractiveIndex, double roughness, int scatterSampleCount)
	{
		this.surfaceMaterial = surfaceMaterial;
		this.volumeTexture = volumeTexture;
		this.scatterCoeff = scatterCoeff;
		this.refractiveIndex = refractiveIndex;
		this.roughness = roughness;
		this.scatterSampleCount = scatterSampleCount;
	}


	/* *********************************************************************************************
	 * Material Override
	 * *********************************************************************************************/
	@Override
	public Color shade(ShadingData data)
	{	
		//Setup the normal
		Vector3 normal = data.getIntersectionData().getNormal();
		
		//Ray Direction
		Vector3 rayDir = (new Vector3(data.getRay().getDirection())).normalizeM();
		
		
		/*
		 * On entrance:
		 * 		Roll for surface reflection or subsurface lighting
		 * 		If surface:
		 * 			shade using given material
		 *		if sub surface:
		 * 			bend ray using index of refrection
		 * 			scatter via roughness parameter
		 * 			recurse for lighting
		 * 
		 * On exit:
		 * 		roll for surface reflection or sub surface pass through
		 * 		if surface:
		 * 			return black (no light made it in)
		 * 		if sub surface:
		 * 			collect all light for that point (direct and indirect)
		 * 				do we use a evenly weighted hemisphere for indirect?
		 * 			using the subsurface sampling parameter, 
		 * 				sample volume texture along the light ray
		 * 				for each sample segment, apply beers law to the light
		 * 				return the attenuated light
		 * 			
		 * 		
		 * 
		 */
		
		double DdotN = normal.dot(rayDir);
		
		//If the ray direction and the normal have an angle of less than Pi/2, then the ray is exiting the material
		Color recursionColor = null;
		if(DdotN > 0.0 && data.getRefractiveIndex() != Material.AIR_REFRACTIVE_INDEX) 
		{
			recursionColor = exiting(data);
			
		}else{
		
			recursionColor = entering(data);
		}
		
		return recursionColor;
	}


	/* *********************************************************************************************
	 * Entering Material
	 * *********************************************************************************************/
	private Color entering(ShadingData data)
	{
		//Caluclate the reflected light 
		Color recursionColor = null;
		if(data.getRecursionDepth() < DO_NOT_EXCEED_RECURSION_LEVEL)
		{
			double recursionDirection = Math.random();
			
			//If direction < refrectance, go reflectin
			if(recursionDirection > scatterCoeff)
			{
				//If no scatter, Sample surface material
				recursionColor = surfaceMaterial.shade(data);
					
			}else{
				//If scatter, get recursing
				//Setup the point of intersection
				Vector3 point = data.getIntersectionData().getPoint();
				
				//Setup the normal
				Vector3 normal = data.getIntersectionData().getNormal();
				
				//Ray Direction
				Vector3 rayDir = (new Vector3(data.getRay().getDirection())).normalizeM();
				
				
				double DdotN = normal.dot(rayDir);
				double thisRefractiveIndex = refractiveIndex;
				
				
				//Pre-calculate some values for refraction
				double incomingRefractiveIndex = data.getRefractiveIndex();
				double refractiveRatio = incomingRefractiveIndex / thisRefractiveIndex;
				
				//Calculate the refraction direction
				double phiDiscrim = 1.0 - (refractiveRatio * refractiveRatio) *
									(1.0 - (DdotN * DdotN));
				
				if(phiDiscrim >= 0)
				{
				
					Vector3 thetaSide = rayDir.addMultiRightM(normal, -DdotN).multiplyM(refractiveRatio);
					Vector3 refracDir = thetaSide.addMultiRightM(normal, -Math.sqrt(phiDiscrim)).normalizeM();
					
					//0.0 is no roughness, 1.0 is lots of roughness
					//TODO: Use something better here......
					if(roughness > 0.0 && refracDir.dot(normal) < 0.0)
					{
						Vector3 roughDir = new Vector3();
						Vector3 offset = new Vector3();
						do
						{
							offset = Vector3.uniformSphereSample().multiplyM(roughness);
							roughDir.set(refracDir);
							roughDir.addM(offset);
						} while(roughDir.dot(normal) > 0.0);
						
						refracDir = roughDir.normalizeM();
					}
					
					recursionColor = recurse(data, point, refracDir, refractiveIndex);
				}else{
					recursionColor = new Color();
				}
				
			}
		}else{
			recursionColor = new Color();
		}
		
		return recursionColor;
	}
	
	
	/* *********************************************************************************************
	 * Exiting Material
	 * *********************************************************************************************/
	private Color exiting(ShadingData data)
	{

		//Setup the point of intersection
		Vector3 point = data.getIntersectionData().getPoint();
		
		//Setup the normal
		Vector3 normal = data.getIntersectionData().getNormal();
		
		
		//Direct Illumination (diffuse)
		IlluminationData ildata;
		Color shade = new Color();
		Color white = Color.white();
		for(Light light : data.getRootScene().getLightManager())
		{
			//Get illumination data for the current light
			ildata = light.illuminate(data, point);
			
			shade.add3M(diffuse(white, normal, ildata.getDirection()));
		}
		
		
		Color recursionColor = null;
		if(data.getRecursionDepth() < DO_NOT_EXCEED_RECURSION_LEVEL)
		{
			//Indirect Illumination
			//Sampling
			if(data.getRecursionDepth() < DO_NOT_EXCEED_RECURSION_LEVEL)
			{
				//Sample a random point
				Vector3 sampleDir = Vector3.uniformSphereSample();
				
				//Add the direct shading and samples shading together
				shade.add3M(recurse(data, point, sampleDir, 1.0));
			}
			
			
			double segmentLength = data.getIntersectionData().getDistance() / (double)scatterSampleCount;
			Vector3 origin = data.getRay().getOrigin();
			Vector3 path = point.subtract(origin);
			
			Vector3 samplePoint = new Vector3();
			Color beerColor = new Color();
			for(int i = 0; i < scatterSampleCount; i++)
			{
				//Get an internal sample point along the path
				samplePoint.set(origin);
				samplePoint.addMultiRightM(path, ((double)(scatterSampleCount-i)) / (double) scatterSampleCount);
				double[] a = volumeTexture.evaluate(samplePoint.get(0), samplePoint.get(0), samplePoint.get(0)).getChannels();
				
				//Brew up some beer color
				beerColor.set(Math.exp(-1.0 * Math.log(a[0]) * segmentLength), 
											Math.exp(-1.0 * Math.log(a[1]) * segmentLength), 
											Math.exp(-1.0 * Math.log(a[2]) * segmentLength));
				
				//Get faded
				shade = shade.multiply3M(beerColor);
			}
			
			recursionColor = shade;
			
		}else{
			recursionColor = new Color();
		}
		
		
		return recursionColor;
	}

	
	/* *********************************************************************************************
	 * Getters/Setters
	 * *********************************************************************************************/
	public Material getSurfaceMaterial() {
		return surfaceMaterial;
	}

	public void setSurfaceMaterial(Material surfaceMaterial) {
		this.surfaceMaterial = surfaceMaterial;
	}

	public Texture3D getVolumeTexture() {
		return volumeTexture;
	}

	public void setVolumeTexture(Texture3D volumeTexture) {
		this.volumeTexture = volumeTexture;
	}

	public double getScatterCoeff() {
		return scatterCoeff;
	}

	public void setScatterCoeff(double scatterCoeff) {
		this.scatterCoeff = scatterCoeff;
	}

	public double getRefractiveIndex() {
		return refractiveIndex;
	}

	public void setRefractiveIndex(double refractiveIndex) {
		this.refractiveIndex = refractiveIndex;
	}

	public double getRoughness() {
		return roughness;
	}

	public void setRoughness(double roughness) {
		this.roughness = roughness;
	}

	public int getScatterSampleCount() {
		return scatterSampleCount;
	}

	public void setScatterSampleCount(int scatterSampleCount) {
		this.scatterSampleCount = scatterSampleCount;
	}
	
}
