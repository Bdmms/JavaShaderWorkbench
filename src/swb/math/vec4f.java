package swb.math;

/**
 * Extension of vecf that defines 4D vectors. This class extends behavior of vec3f and vec2f, 
 * which allow it to also be treated as a 2D vector or 3D vector.
 * @author Sean Rannie
 */
public class vec4f extends vec3f
{
	public static final vec4f ZERO = new vec4f( 0.0f ); 
	
	/**
	 * Creates a 4D vector initialized to 0
	 */
	public vec4f()
	{
		super( new float[4], 0, 4 );
	}
	
	/**
	 * Creates a new 4D vector from an existing n-dimensional vector.
	 * @param source - source vector
	 */
	public vec4f( vecf source )
	{
		super( new float[4], 0, 4 );
		System.arraycopy( source.data, source.idx, data, idx, source.dim < 4 ? source.dim : 4 );
	} 
	
	/**
	 * Creates a 4D vector from an a subset of a data buffer
	 * @param arr - data buffer
	 * @param offset - offset into data buffer
	 */
	public vec4f( float[] arr, int offset )
	{
		super( arr, offset, 4 );
	}
	
	/**
	 * Creates a 4D vector from the given array.
	 * @param arr - data array
	 */
	public vec4f( float[] arr )
	{
		super( arr, 0, 4 );
	}
	
	/**
	 * Creates a 4D vector initialized to the scalar value.
	 * @param scalar - initial value of every component
	 */
	public vec4f( float scalar )
	{
		super( new float[] { scalar, scalar, scalar, scalar }, 0, 4 );
	}
	
	/**
	 * Creates a 4D vector initialized to the specified components.
	 * @param x - 1st component
	 * @param y - 2nd component
	 * @param z - 3rd component
	 * @param w - 4th component
	 */
	public vec4f( float x, float y, float z, float w )
	{
		super( new float[] { x, y, z, w }, 0, 4 );
	}
	
	/**
	 * Constructs a 4D vector from parsed data.
	 * The data for each element is parsed from a {@link String}.
	 * @param elements - {@link String[]}
	 * @param offset - offset of parsed data in elements array
	 */
	public vec4f( String[] elements, int offset )
	{
		super( elements, offset );
	}
	
	/**
	 * Sets the components of the 4D vector to the color defined by the integer code.
	 * @param color - 32-bit ARGB color code
	 */
	public void setColor( int color )
	{
		int i = idx;
		data[i++] = ((color >> 16) & 0xFF) / 255.0f;
		data[i++] = ((color >> 8) & 0xFF) / 255.0f;
		data[i++] = (color & 0xFF) / 255.0f;
		data[i  ] = ((color >> 24) & 0xFF) / 255.0f;
	}
	
	/**
	 * Sets the components of the 4D vector.
	 * @param x - 1st component
	 * @param y - 2nd component
	 * @param z - 3rd component
	 * @param w - 4th component
	 */
	public void set( float x, float y, float z, float w )
	{
		int i = idx;
		data[i++] = x;
		data[i++] = y;
		data[i++] = z;
		data[i  ] = w;
	}
	
	/**
	 * Calculates the dot product of the 4D vector with the specified components.
	 * @param x - 1st component
	 * @param y - 2nd component
	 * @param z - 3rd component
	 * @param w - 4th component
	 * @return the result of the dot product
	 */
	public float dot( float x, float y, float z, float w )
	{
		int i = this.idx;
		return x * data[i++] + y * data[i++] + z * data[i++] + w * data[i++];
	}
	
	/**
	 * Calculates the grey of the RGB components of the 4D color vector.
	 * @return grey value from the RGB components
	 */
	public float greyValue()
	{
		return ( data[idx] + data[idx+1] + data[idx+2] ) / 3.0f;
	}
	
	/**
	 * Converts the 4D color vector into an integer color code.
	 * @return 32-bit ARGB color code
	 */
	public int toRGBA()
	{
		int i = this.idx;
		int ir = data[i] > 1.0f ? 0xFF0000 : (data[i] < 0.0f ? 0 : (int)Math.round( data[i] * 255.0f ) << 16 ); i++;
		int ig = data[i] > 1.0f ? 0xFF00 : (data[i] < 0.0f ? 0 : (int)Math.round( data[i] * 255.0f ) << 8 ); i++;
		int ib = data[i] > 1.0f ? 0xFF : (data[i] < 0.0f ? 0 : (int)Math.round( data[i] * 255.0f ) ); i++;
		int ia = data[i] > 1.0f ? 0xFF000000 : (data[i] < 0.0f ? 0 : (int)Math.round( data[i] * 255.0f ) << 24 );
		return ia | ir | ig | ib;
	}
	
	@Override
	public vec4f unit()
	{
		float len = length();
		int i = idx;
		return new vec4f( data[i++] / len, data[i++] / len, data[i++] / len, data[i] / len );
	}
	
	/**
	 * TODO: Consider moving this to the base implementation class
	 * Calculates the weight bilinear average of four 4D vectors. The first two vectors represent
	 * the top row of a 2x2 matrix and the last two vectors represent the bottom row.
	 * @param v0 - first vector (0,0)
	 * @param v1 - second vector (1,0)
	 * @param v2 - third vector (0,1)
	 * @param v3 - fourth vector (1,1)
	 * @param w0 - weight factor between columns
	 * @param w1 - weight factor between rows
	 * @return a new 4D vector from the calculated average
	 */
	public static vec4f average( vec4f v0, vec4f v1, vec4f v2, vec4f v3, float w0, float w1)
	{
		float w2 = w1 - w0 * w1;
		float w3 = w0 * w1;
		
		float rw1 = 1.0f - w1;
		w1 = w0 * rw1;
		w0 = rw1 - w0 * rw1;
		
		int i0 = v0.idx;
		int i1 = v1.idx;
		int i2 = v2.idx;
		int i3 = v3.idx;

		return new vec4f(
				v0.data[i0++] * w0 + v1.data[i1++] * w1 + v2.data[i2++] * w2 + v3.data[i3++] * w3,
				v0.data[i0++] * w0 + v1.data[i1++] * w1 + v2.data[i2++] * w2 + v3.data[i3++] * w3,
				v0.data[i0++] * w0 + v1.data[i1++] * w1 + v2.data[i2++] * w2 + v3.data[i3++] * w3,
				v0.data[i0  ] * w0 + v1.data[i1  ] * w1 + v2.data[i2  ] * w2 + v3.data[i3  ] * w3
		);
	}
	
	/**
	 * Creates a random 4D vector with each component generated randomly within
	 * a specified range or boundary.
	 * @param min - minimum value of range
	 * @param max - maximum value of range
	 * @return new randomly generated 3D vector
	 */
	public static vec4f random( float min, float max )
	{
		float diff = max - min;
		return new vec4f( 
				min + (float)Math.random() * diff,
				min + (float)Math.random() * diff,
				min + (float)Math.random() * diff,
				min + (float)Math.random() * diff );
	}
}
