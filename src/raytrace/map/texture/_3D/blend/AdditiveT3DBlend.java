package raytrace.map.texture._3D.blend;

import raytrace.color.Color;
import raytrace.map.texture._3D.Texture3D;

public class AdditiveT3DBlend extends Texture3D {
	
	/*
	 * A texture blend for adding two textures together
	 */
	
	/* *********************************************************************************************
	 * Instance Vars
	 * *********************************************************************************************/
	protected Texture3D firstTexture;
	protected Texture3D secondTexture;
	

	/* *********************************************************************************************
	 * Constructor
	 * *********************************************************************************************/
	public AdditiveT3DBlend()
	{
		this.firstTexture = null;
		this.secondTexture = null;
	}
	
	public AdditiveT3DBlend(Texture3D firstTexture, Texture3D secondTexture)
	{
		this.firstTexture = firstTexture;
		this.secondTexture = secondTexture;
	}
	

	/* *********************************************************************************************
	 * Texture3D Override
	 * *********************************************************************************************/
	@Override
	public Color evaluate(Double x, Double y, Double z)
	{
		Color firstColor = firstTexture.evaluate(x, y, z);
		Color secondColor = secondTexture.evaluate(x, y, z);
		return firstColor.add3(secondColor);
	}
	
	
	/* *********************************************************************************************
	 * Getters/Setters
	 * *********************************************************************************************/
	public Texture3D getFirstTexture() {
		return firstTexture;
	}

	public void setFirstTexture(Texture3D firstTexture) {
		this.firstTexture = firstTexture;
	}

	public Texture3D getSecondTexture() {
		return secondTexture;
	}

	public void setSecondTexture(Texture3D secondTexture) {
		this.secondTexture = secondTexture;
	}

}
