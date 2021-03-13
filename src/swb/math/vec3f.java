package swb.math;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.math.Vert3fImmutable;

/**
 * Extension of vecf that defines 3D vectors. This class extends behavior of vec2f, 
 * which allow it to also be treated as a 2D vector.
 * @author Sean Rannie
 */
public class vec3f extends vec2f implements Vert3fImmutable
{
	public static final vec3f UNDEFINED = new vec3f( Float.NaN, Float.NaN, Float.NaN ); 
	public static final vec3f ZERO = new vec3f( 0.0f ); 
	public static final vec3f ONE = new vec3f( 1.0f ); 
	
	/**
	 * Protected constructor that allows overriding of size
	 * @param data - data buffer
	 * @param offset - offset into data buffer
	 * @param size - size of the vector
	 */
	protected vec3f( float[] arr, int offset, int size )
	{
		super( arr, offset, size );
	}
	
	/**
	 * Protected constructor that allows overriding of size
	 * @param elements - {@link String[]}
	 * @param offset - offset of parsed data in elements array
	 * @param size - size of the vector
	 */
	protected vec3f( String[] elements, int offset, int size )
	{
		super( elements, offset, size );
	}
	
	/**
	 * Creates a new 3D vector from an existing n-dimensional vector.
	 * @param source - source vector
	 */
	public vec3f( vecf source )
	{
		super( new float[3], 0, 3 );
		System.arraycopy( source.data, source.idx, data, idx, source.dim < 3 ? source.dim : 3 );
	} 
	
	/**
	 * Creates a 3D vector initialized to 0
	 */
	public vec3f()
	{
		super( new float[3], 0, 3 );
	}
	
	/**
	 * Creates a 3D vector from an a subset of a data buffer
	 * @param arr - data buffer
	 * @param offset - offset into data buffer
	 */
	public vec3f( float[] arr, int offset )
	{
		super( arr, offset, 3 );
	}
	
	/**
	 * Creates a 3D vector from the given array.
	 * @param arr - data array
	 */
	public vec3f( float[] arr )
	{
		super( arr, 0, 3 );
	}
	
	/**
	 * Creates a 3D vector initialized to the scalar value.
	 * @param scalar - initial value of every component
	 */
	public vec3f( float scalar )
	{
		this( scalar, scalar, scalar );
	}
	
	/**
	 * Creates a 3D vector initialized to the specified components.
	 * @param x - 1st component
	 * @param y - 2nd component
	 * @param z - 3rd component
	 */
	public vec3f( float x, float y, float z )
	{
		super( new float[] { x, y, z }, 0, 3 );
	}
	
	/**
	 * Constructs a 3D vector from parsed data.
	 * The data for each element is parsed from a {@link String}.
	 * @param elements - {@link String[]}
	 * @param offset - offset of parsed data in elements array
	 */
	public vec3f( String[] elements, int offset )
	{
		super( elements, offset, 3 );
	}
	
	/**
	 * Creates a new 3D vector from the displacement vector starting from another vector to this vector.
	 * @param vec - secondary vector
	 * @return the new displacement vector between the two vectors
	 */
	public vec3f from( vec3f vec )
	{
		int i = idx;
		int j = vec.idx;
		return new vec3f( data[i++] - vec.data[j++], data[i++] - vec.data[j++], data[i] - vec.data[j] );
	}
	
	/**
	 * Sets the components of the 3D vector.
	 * @param x - 1st component
	 * @param y - 2nd component
	 * @param z - 3rd component
	 */
	public void set( float x, float y, float z )
	{
		data[idx  ] = x;
		data[idx+1] = y;
		data[idx+2] = z;
	}
	
	/**
	 * Calculates the dot product of the 3D vector with the specified components.
	 * @param x - 1st component
	 * @param y - 2nd component
	 * @param z - 3rd component
	 * @return the result of the dot product
	 */
	public float dot( float x, float y, float z )
	{
		int i = idx;
		return data[i++] * x + data[i++] * y + data[i++] * z;
	}
	
	@Override
	public vec3f cross( vec2f vec )
	{
		return new vec3f( 
				-data[idx+2] * vec.data[idx+1], data[idx+2] * vec.data[idx  ],
				data[idx  ] * vec.data[idx+1] - data[idx+1] * vec.data[idx  ]
		);
	}
	
	@Override
	public vec3f cross( vec3f vec )
	{
		return new vec3f( 
				data[idx+1] * vec.data[idx+2] - data[idx+2] * vec.data[idx+1], 
				data[idx+2] * vec.data[idx  ] - data[idx  ] * vec.data[idx+2],
				data[idx  ] * vec.data[idx+1] - data[idx+1] * vec.data[idx  ]
		);
	}
	
	@Override
	public void upload( GL3 gl, int loc )
	{
		gl.glUniform3fv( loc, 1, data, idx );
	}
	
	@Override
	public vec3f unit()
	{
		float len = length();
		return new vec3f( data[idx] / len, data[idx+1] / len, data[idx+2] / len );
	}
	
	@Override
	public float getZ() 
	{
		return data[idx+2];
	}
	
	@Override
	public vec3f clone()
	{
		float[] cloned = new float[3];
		System.arraycopy( data, idx, cloned, 0, 3 );
		return new vec3f( cloned );
	}
	
	/**
	 * Creates a random 3D vector with each component generated randomly within
	 * a specified range or boundary.
	 * @param min - minimum value of range
	 * @param max - maximum value of range
	 * @return new randomly generated 3D vector
	 */
	public static vec3f random( float min, float max )
	{
		float diff = max - min;
		return new vec3f( 
				min + (float)Math.random() * diff,
				min + (float)Math.random() * diff,
				min + (float)Math.random() * diff );
	}
}
