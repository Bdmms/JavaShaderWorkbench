package swb.math;

/**
 * TODO: Documentation
 */
public class PerlinNoise 
{
	private static final short[] permutation = { 151,160,137,91,90,15,
			   131,13,201,95,96,53,194,233,7,225,140,36,103,30,69,142,8,99,37,240,21,10,23,
			   190,6,148,247,120,234,75,0,26,197,62,94,252,219,203,117,35,11,32,57,177,33,
			   88,237,149,56,87,174,20,125,136,171,168,68,175,74,165,71,134,139,48,27,166,
			   77,146,158,231,83,111,229,122,60,211,133,230,220,105,92,41,55,46,245,40,244,
			   102,143,54,65,25,63,161,1,216,80,73,209,76,132,187,208,89,18,169,200,196,
			   135,130,116,188,159,86,164,100,109,198,173,186,3,64,52,217,226,250,124,123,
			   5,202,38,147,118,126,255,82,85,212,207,206,59,227,47,16,58,17,182,189,28,42,
			   223,183,170,213,119,248,152,2,44,154,163,70,221,153,101,155,167,43,172,9,
			   129,22,39,253,19,98,108,110,79,113,224,232,178,185,112,104,218,246,97,228,
			   251,34,242,193,238,210,144,12,191,179,162,241,81,51,145,235,249,14,239,107,
			   49,192,214,31,181,199,106,157,184,84,204,176,115,121,50,45,127,4,150,254,
			   138,236,205,93,222,114,67,29,24,72,243,141,128,195,78,66,215,61,156,180
	};
	
	private static final short[] p = permutate();
	
	public static void fill3DMatrix(int[] mat, int w, int h, double x_factor, double y_factor, double z_factor, double t_value)
	{
		// Fill matrix
		int plane = w * h;
		for(int i = 0; i < mat.length; i++)
		{
			int zi = (i % plane);
			int b = (int)((perlin4D((zi % w) * x_factor, (zi / w) * y_factor, (i / plane) * z_factor, t_value) * 0.5 + 0.5) * 0xFF);
			mat[i] = (b << 16) | (b << 8) | b;
		}
	}
	
	public static void fill2DMatrix(int[] mat, int w, double x_factor, double y_factor, double z_value)
	{
		// Fill matrix
		for(int i = 0; i < mat.length; i++)
		{
			int b = (int)((perlin3D((i % w) * x_factor, (i / w) * y_factor, z_value) * 0.5 + 0.5) * 0xFF);
			mat[i] = (b << 16) | (b << 8) | b;
		}
	}
	
	public static void fill1DMatrix(int[] mat, double x_factor, double y_value)
	{
		// Fill matrix
		for(int i = 0; i < mat.length; i++)
		{
			int b = (int)((perlin2D(i * x_factor, y_value) * 0.5 + 0.5) * 0xFF);
			mat[i] = (b << 16) | (b << 8) | b;
		}
	}
	
	public static MatrixFloat createNoise( int w, int h, double minX, double minY, double maxX, double maxY, double z )
	{
		MatrixFloat matrix = new MatrixFloat( w, h );
		double xsc = (maxX - minX) / w;
		double ysc = (maxY - minY) / h;
		
		matrix.applyEach( (x, y) -> (float)( perlin3D( minX + x * xsc, minX + y * ysc, z ) + 0.5 ) );
		
		return matrix;
	}
	
	public static double perlin4D(double x, double y, double z, double w) 
	{
		int X = (int)x & 255;
        int Y = (int)y & 255;         
        int Z = (int)z & 255;
        int T = (int)w & 255;
        x -= (int)x;
        y -= (int)y;
        z -= (int)z;
        w -= (int)w;
        double u = x * x * x * (x * (x * 6 - 15) + 10);
        double v = y * y * y * (y * (y * 6 - 15) + 10);
        double s = z * z * z * (z * (z * 6 - 15) + 10);
        double a = w * w * w * (w * (w * 6 - 15) + 10);
        int A = p[X  ]+Y;
        int AA = p[A]+Z;
        int AAA = p[AA]+T;
        int AAB = p[AA+1]+T;
        int AB = p[A+1]+Z;
        int ABA = p[AB]+T;
        int ABB = p[AB+1]+T;
        int B = p[X+1]+Y;
        int BA = p[B]+Z;		
        int BAA = p[BA]+T;
        int BAB = p[BA+1]+T;
        int BB = p[B+1]+Z;
        int BBA = p[BB]+T;
        int BBB = p[BB+1]+T;
        return  lerp(a,
        				lerp(s, 
        						lerp(v, lerp(u, grad4D(p[AAA  ], x  , y  , z  , w  ), grad4D(p[BAA  ], x-1, y  , z  , w  )), 
        								lerp(u, grad4D(p[ABA  ], x  , y-1, z  , w  ), grad4D(p[BBA  ], x-1, y-1, z  , w  ))),
        						lerp(v, lerp(u, grad4D(p[AAB  ], x  , y  , z-1, w  ), grad4D(p[BAB  ], x-1, y  , z-1, w  )), 
        								lerp(u, grad4D(p[ABB  ], x  , y-1, z-1, w  ), grad4D(p[BBB  ], x-1, y-1, z-1, w  )))),
        				lerp(s, 
        						lerp(v, lerp(u, grad4D(p[AAA+1], x  , y  , z  , w-1), grad4D(p[BAA+1], x-1, y  , z  , w-1)), 
        								lerp(u, grad4D(p[ABA+1], x  , y-1, z  , w-1), grad4D(p[BBA+1], x-1, y-1, z  , w-1))),
        						lerp(v, lerp(u, grad4D(p[AAB+1], x  , y  , z-1, w-1), grad4D(p[BAB+1], x-1, y  , z-1, w-1)), 
        								lerp(u, grad4D(p[ABB+1], x  , y-1, z-1, w-1), grad4D(p[BBB+1], x-1, y-1, z-1, w-1)))));
	}
	
	// REFERENCED BY: https://mrl.nyu.edu/~perlin/noise/
	public static double perlin3D(double x, double y, double z) 
	{
		int X = (int)x & 255;                  // FIND UNIT CUBE THAT
        int Y = (int)y & 255;                  // CONTAINS POINT.
        int Z = (int)z & 255;
        x -= (int)x;                                // FIND RELATIVE X,Y,Z
        y -= (int)y;                                // OF POINT IN CUBE.
        z -= (int)z;
        double u = x * x * x * (x * (x * 6 - 15) + 10);    // COMPUTE FADE CURVES
        double v = y * y * y * (y * (y * 6 - 15) + 10);    // FOR EACH OF X,Y,Z.
        double w = z * z * z * (z * (z * 6 - 15) + 10);
        
        int A = p[X]+Y;
        int AA = p[A]+Z;
        int AB = p[A+1]+Z;
        int B = p[X+1]+Y;
        int BA = p[B]+Z;		// HASH COORDINATES OF
        int BB = p[B+1]+Z;      // THE 8 CUBE CORNERS,

        return lerp(w, 
        		lerp(v, lerp(u, grad3D(p[AA  ], x  , y  , z   ), grad3D(p[BA  ], x-1, y  , z   )), // AND ADD BLENDED
        				lerp(u, grad3D(p[AB  ], x  , y-1, z   ), grad3D(p[BB  ], x-1, y-1, z   ))),// RESULTS FROM  8
        		lerp(v, lerp(u, grad3D(p[AA+1], x  , y  , z-1 ), grad3D(p[BA+1], x-1, y  , z-1 )), // CORNERS OF CUBE
        				lerp(u, grad3D(p[AB+1], x  , y-1, z-1 ), grad3D(p[BB+1], x-1, y-1, z-1 ))));
	}
	
	public static double perlin2D(double x, double y) 
	{
		int X = (int)x & 255;
        int Y = (int)y & 255;          
        x -= (int)x; 
        y -= (int)y;
        double u = x * x * x * (x * (x * 6 - 15) + 10);
        double v = y * y * y * (y * (y * 6 - 15) + 10);
        int A = p[X  ]+Y;
        int B = p[X+1]+Y;
        
        return lerp(v, lerp(u, grad2D(p[A  ], x  , y  ), grad2D(p[B  ], x-1, y  )),
        			   lerp(u, grad2D(p[A+1], x  , y-1), grad2D(p[B+1], x-1, y-1)));
	}
	
	private static double grad4D(int hash, double x, double y, double z, double t) 
	{
	      int h = hash & 31;	// INTO 32 GRADIENT DIRECTION              
	      double u = h < 24 ? x : y,
	             v = h < 16 ? y : z,
	             w = h < 8 ? z : t;
	      return ( ( h & 1 ) == 0 ? u : -u ) + ( ( h & 2 ) == 0 ? v : -v ) + ( ( h & 4 ) == 0 ? w : -w );
	}
	
	private static double grad3D(int hash, double x, double y, double z) 
	{
	      int h = hash & 15;                      // CONVERT LO 4 BITS OF HASH CODE
	      double u = h < 8 ? x : y,                 // INTO 12 GRADIENT DIRECTIONS.
	             v = h < 4 ? y : h == 12 || h == 14 ? x : z;
	      return ( ( h & 1 ) == 0 ? u : -u ) + ( ( h & 2 ) == 0 ? v : -v );
	}
	
	// Estimated gradient for 2 dimensions
	private static double grad2D(int hash, double x, double y) 
	{
		// INTO 4 GRADIENT DIRECTION
	    double u = ( hash & 2 ) == 0 ? x : y;
	    return ( hash & 1) == 0 ? u : -u;
	}
	
	// Linear Interpolate
	private static double lerp(double t, double a, double b) 
	{
		return a + t * (b - a);
	}
	
	// Fills permutation table
	private static short[] permutate()
	{
		short[] p = new short[512];
		for( int i = 0; i < p.length; i++)
			p[i] = permutation[i & 0xFF];
		return p;
	}
	
	/*
	public static void main( String[] args )
	{
		MatrixFloat matrix = createNoise( 1024, 1024, 0.0, 0.0, 16.0, 16.0, Math.PI );
		
		JFileChooser fc = new JFileChooser();
		int r = fc.showSaveDialog(null);
		
		if( r == JFileChooser.APPROVE_OPTION )
		{
			try 
			{
				matrix.writeTo( fc.getSelectedFile() );
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}*/
}
