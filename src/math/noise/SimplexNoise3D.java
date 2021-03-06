package math.noise;

import java.util.Random;

import math.map.Map3D;

public class SimplexNoise3D implements Map3D<Double> {
	
	/*
	 * An implementation of Simplex Noise
	 * 
	 * JAVA REFERENCE IMPLEMENTATION OF IMPROVED NOISE - COPYRIGHT 2002 KEN PERLIN.
	 * 
	 * Website: http://mrl.nyu.edu/~perlin/noise/
	 * Paper:   http://mrl.nyu.edu/~perlin/paper445.pdf
	 * 
	 * (Slightly cleaned up for readability)
	 */

	/* *********************************************************************************************
	 * Static Vars
	 * *********************************************************************************************/
	protected static final int originalPermutation[] = { 151,160,137,91,90,15,
		   131,13,201,95,96,53,194,233,7,225,140,36,103,30,69,142,8,99,37,240,21,10,23,
		   190, 6,148,247,120,234,75,0,26,197,62,94,252,219,203,117,35,11,32,57,177,33,
		   88,237,149,56,87,174,20,125,136,171,168, 68,175,74,165,71,134,139,48,27,166,
		   77,146,158,231,83,111,229,122,60,211,133,230,220,105,92,41,55,46,245,40,244,
		   102,143,54, 65,25,63,161, 1,216,80,73,209,76,132,187,208, 89,18,169,200,196,
		   135,130,116,188,159,86,164,100,109,198,173,186, 3,64,52,217,226,250,124,123,
		   5,202,38,147,118,126,255,82,85,212,207,206,59,227,47,16,58,17,182,189,28,42,
		   223,183,170,213,119,248,152, 2,44,154,163, 70,221,153,101,155,167, 43,172,9,
		   129,22,39,253, 19,98,108,110,79,113,224,232,178,185, 112,104,218,246,97,228,
		   251,34,242,193,238,210,144,12,191,179,162,241, 81,51,145,235,249,14,239,107,
		   49,192,214, 31,181,199,106,157,184, 84,204,176,115,121,50,45,127, 4,150,254,
		   138,236,205,93,222,114,67,29,24,72,243,141,128,195,78,66,215,61,156,180
		   };
	

	/* *********************************************************************************************
	 * Instance Vars
	 * *********************************************************************************************/
	protected final int p[] = new int[512];
	

	/* *********************************************************************************************
	 * Constructor
	 * *********************************************************************************************/
	public SimplexNoise3D()
	{
		for(int i = 0; i < 256 ; i++)
		{
			p[256+i] = p[i] = originalPermutation[i]; 
		}
	}
	
	public SimplexNoise3D(long seed)
	{
		Random rand = new Random(seed);
		
		//Assign bottom 256
		for(int i = 0; i < 256 ; i++)
		{
			p[i] = i;
		}
		
		int shuffleCount = p.length * 8;
		
		int temp;
		int a1;
		int a2;
		
		//Shuffle
		for(int i = 0; i < shuffleCount; i++)
		{
			a1 = Math.abs(rand.nextInt()) % 256;
			a2 = Math.abs(rand.nextInt()) % 256;
			
			temp = p[a1];
			p[a1] = p[a2];
			p[a2] = temp;
		}
		
		//Assign shuffled bottom 256 to top 256
		for(int i = 0; i < 256 ; i++)
		{
			p[i+256] = p[i];
		}
	}
	

	/* *********************************************************************************************
	 * Map3D Override Methods
	 * *********************************************************************************************/
	@Override
	public Double evaluate(Double x, Double y, Double z)
	{
		return noise(x, y, z);
	}
	   
	
	/* *********************************************************************************************
	 * Calculation Methods
	 * *********************************************************************************************/
	public double noise(double x, double y, double z)
	{
		//Find the unit cube that contains the point (x, y, z)
		int X = (int)Math.floor(x) & 255;
		int Y = (int)Math.floor(y) & 255;
		int Z = (int)Math.floor(z) & 255;
		
		x -= Math.floor(x);
		y -= Math.floor(y);
		z -= Math.floor(z);
		
		//Compute the fade curves of x, y, z
		double u = fade(x);
		double v = fade(y);
		double w = fade(z);
		
		//Get the hash coordinates of the 8 cube corners
		int A =  p[X]   + Y;
		int AA = p[A]   + Z;
		int AB = p[A+1] + Z;
		int B =  p[X+1] + Y;
		int BA = p[B]   + Z;
		int BB = p[B+1] + Z;

		//Linearly Interpolate the gradients at the 8 corners of the cube.
		return lerp(w, 
					lerp(v, 
						 lerp(u, 
							  grad(p[AA  ], x  , y  , z   ),
							  grad(p[BA  ], x-1, y  , z   )
							  ),
						 lerp(u, 
							  grad(p[AB  ], x  , y-1, z   ),
							  grad(p[BB  ], x-1, y-1, z   )
							  )
						),
					lerp(v, 
						 lerp(u, 
							  grad(p[AA+1], x  , y  , z-1 ),
							  grad(p[BA+1], x-1, y  , z-1 )
							  ),
						 lerp(u, 
							  grad(p[AB+1], x  , y-1, z-1 ),
							  grad(p[BB+1], x-1, y-1, z-1 )
							  )
						 )
					);
	}
	
	protected double fade(double t)
	{
		return t * t * t * (t * (t * 6 - 15) + 10);
	}
	
	protected double lerp(double t, double a, double b)
	{
		return a + t * (b - a);
	}
	
	protected double grad(int hash, double x, double y, double z)
	{
		//Convert the low 4 bits of the hash code into 12 gradient directions
		int h = hash & 15;
		double u = h<8 ? x : y;
		double v = h<4 ? y : h==12||h==14 ? x : z;
		return ((h&1) == 0 ? u : -u) + ((h&2) == 0 ? v : -v);
	}

}
