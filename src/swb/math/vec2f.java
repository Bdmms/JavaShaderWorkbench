package swb.math;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.math.Vert2fImmutable;

/**
 * Extension of vecf that defines 2D vectors.
 * @author Sean Rannie
 */
public class vec2f extends vecf implements Vert2fImmutable
{
	public static final vec2f ZERO = new vec2f( 0.0f );
	
	/**
	 * Protected constructor that allows overriding of size
	 * @param data - data buffer
	 * @param offset - offset into data buffer
	 * @param size - size of the vector
	 */
	protected vec2f( float[] arr, int offset, int size )
	{
		super( arr, offset, size );
	}
	
	/**
	 * Protected constructor that allows overriding of size
	 * @param elements - {@link String[]}
	 * @param offset - offset of parsed data in elements array
	 * @param size - size of the vector
	 */
	protected vec2f( String[] elements, int offset, int size )
	{
		super( elements, offset, size );
	}
	
	/**
	 * Creates a new 2D vector from an existing n-dimensional vector.
	 * @param source - source vector
	 */
	public vec2f( vecf source )
	{
		super( new float[2], 0, 2 );
		System.arraycopy( source.data, source.idx, data, idx, source.dim < 2 ? source.dim : 2 );
	} 
	
	/**
	 * Creates a 2D vector initialized to 0
	 */
	public vec2f()
	{
		super( new float[2], 0, 2 );
	}
	
	/**
	 * Creates a 2D vector from an a subset of a data buffer
	 * @param arr - data buffer
	 * @param offset - offset into data buffer
	 */
	public vec2f( float[] arr, int offset )
	{
		super( arr, offset, 2 );
	}
	
	/**
	 * Creates a 2D vector from the given array.
	 * @param arr - data array
	 */
	public vec2f( float[] arr )
	{
		super( arr, 0, 2 );
	}
	
	/**
	 * Creates a 2D vector initialized to the scalar value.
	 * @param scalar - initial value of every component
	 */
	public vec2f( float scalar )
	{
		this( scalar, scalar );
	}
	
	/**
	 * Creates a 2D vector initialized to the specified components.
	 * @param x - 1st component
	 * @param y - 2nd component
	 */
	public vec2f( float x, float y )
	{
		super( new float[] { x, y }, 0, 2 );
	}
	
	/**
	 * Constructs a 2D vector from parsed data.
	 * The data for each element is parsed from a {@link String}.
	 * @param elements - {@link String[]}
	 * @param offset - offset of parsed data in elements array
	 */
	public vec2f( String[] elements, int offset )
	{
		super( elements, offset, 2 );
	}
	
	/**
	 * Sets the components of the 2D vector.
	 * @param x - 1st component
	 * @param y - 2nd component
	 */
	public void set( float x, float y )
	{
		data[idx  ] = x;
		data[idx+1] = y;
	}
	
	/**
	 * Calculates the dot product of the 2D vector with the specified components.
	 * @param x - 1st component
	 * @param y - 2nd component
	 * @return the result of the dot product
	 */
	public float dot( float x, float y )
	{
		return x * data[idx] + y * data[idx+1];
	}
	
	/**
	 * Calculates the cross product of this vector with another 2D vector.
	 * @param vec - secondary 2D vector
	 * @return new 3D vector from the cross product
	 */
	public vec3f cross( vec2f vec )
	{
		return new vec3f( 0.0f, 0.0f, data[idx] * vec.data[idx+1] - data[idx+1] * vec.data[idx] );
	}
	
	/**
	 * Calculates the cross product of this vector with another 3D vector.
	 * @param vec - secondary 3D vector
	 * @return new 3D vector from the cross product
	 */
	public vec3f cross( vec3f vec )
	{
		return new vec3f( 
				data[idx+1] * vec.data[idx+2], -data[idx  ] * vec.data[idx+2],
				data[idx  ] * vec.data[idx+1] - data[idx+1] * vec.data[idx  ]
		);
	}
	
	/**
	 * Sets the uniform value of the vector using a {@link GL3} instance.
	 * This operation can be reused with different shader programs. The
	 * vector is not bounded to a specific shader or location.
	 * @param gl - {@link GL3} instance
	 * @param loc - location of uniform
	 */
	public void upload( GL3 gl, int loc )
	{
		gl.glUniform2fv( loc, 1, data, idx );
	}
	
	@Override
	public vec2f unit()
	{
		float len = length();
		return new vec2f( data[idx] / len, data[idx+1] / len );
	}
	
	@Override
	public float[] getCoord() 
	{
		float[] copy = new float[dim];
		System.arraycopy( data, idx, copy, 0, dim );
		return copy;
	}
	
	@Override
	public int getCoordCount() 
	{
		return dim;
	}

	@Override
	public float getX() 
	{
		return data[idx];
	}

	@Override
	public float getY() 
	{
		return data[idx+1];
	}
	
	@Override
	public vec2f clone()
	{
		float[] cloned = new float[2];
		System.arraycopy( data, idx, cloned, 0, 2 );
		return new vec2f( cloned );
	}
}
