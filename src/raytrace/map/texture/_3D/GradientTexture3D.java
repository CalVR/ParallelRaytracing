package raytrace.map.texture._3D;

import raytrace.color.Color;

public class GradientTexture3D extends Texture3D {
	
	/*
	 * A gradient texture in one dimension
	 */
	
	/* *********************************************************************************************
	 * Instance Vars
	 * *********************************************************************************************/
	protected double weight;
	protected Color firstColor;
	protected Color secondColor;
	

	/* *********************************************************************************************
	 * Constructor
	 * *********************************************************************************************/
	public GradientTexture3D()
	{
		this.firstColor = Color.black();
		this.secondColor = Color.white();
		this.weight = 1.0;
	}
	
	public GradientTexture3D(Color firstColor, Color secondColor)
	{
		this.firstColor = firstColor;
		this.secondColor = secondColor;
		this.weight = 1.0;
	}
	
	public GradientTexture3D(Color firstColor, Color secondColor, double weight)
	{
		this.firstColor = firstColor;
		this.secondColor = secondColor;
		this.weight = weight;
	}
	

	/* *********************************************************************************************
	 * Texture3D Override
	 * *********************************************************************************************/
	@Override
	public Color evaluate(Double x, Double y, Double z)
	{
		double t = Math.min(1.0, Math.max(0.0, y + 1.0));
		return Color.interpolate(firstColor, secondColor, Math.pow(t, weight));
	}
	
	
	/* *********************************************************************************************
	 * Getters/Setters
	 * *********************************************************************************************/
	public Color getFirstColor() {
		return firstColor;
	}

	public void setFirstColor(Color firstColor) {
		this.firstColor = firstColor;
	}

	public Color getSecondColor() {
		return secondColor;
	}

	public void setSecondColor(Color secondColor) {
		this.secondColor = secondColor;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

}
