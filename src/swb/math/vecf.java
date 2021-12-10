package swb.math;

import com.jogamp.opengl.math.FloatUtil;

import swb.utils.PrintUtil;

/**
 * This class is used to create a subset of a float array.
 * This includes specifying an offset and length of the subset array.
 * The purpose of this class is to provide a base implementation for
 * data that can be packed into a single buffer while also able to manipulate
 * each subset array individually. This class is extended by vector and matrices.
 * @author Sean Rannie
 */
public class vecf
{
	/** Dimension of the vector, also the size of the array */
	protected final int dim;
	/** End location of the vector, used only to help optimize performance */
	protected final int end;
	/** Offset location of vector in data buffer */
	protected final int idx;
	/** Reference to the source data buffer */
	protected final float[] data;
	
	/**
	 * Constructs a vector with a defined offset and size.
	 * @param arr - source data buffer
	 * @param offset - location of vector in data buffer
	 * @param size - size of vector in data buffer
	 */
	public vecf( float[] arr, int offset, int size )
	{
		this.dim = size;
		this.idx = offset;
		this.data = arr;
		this.end = idx + dim;
	}
	
	/**
	 * Constructs a vector with a defined offset. The size of the vector 
	 * is defined by the remainder of the buffer size.
	 * @param arr - source data buffer
	 * @param offset - location of vector in data buffer
	 */
	public vecf( float[] arr, int offset )
	{
		this( arr, offset, arr.length - offset );
	}
	
	/**
	 * Constructs a vector from a given array. The size of the vector is
	 * defined as the size of the array. The offset will always be 0.
	 * @param arr - array of the vector
	 */
	public vecf( float[] arr )
	{
		this( arr, 0, arr.length );
	}
	
	/**
	 * Constructs a vector of a given size. A new array is allocated to
	 * the buffer based on its size. The offset will always be 0.
	 * @param size - size of the vector
	 */
	public vecf( int size )
	{
		this( new float[size], 0, size );
	}
	
	/**
	 * Constructs a vector from parsed data. The vector is created from the
	 * specified size with an offset of 0. The data for each element is parsed
	 * from a {@link String}.
	 * @param elements - {@link String[]}
	 * @param offset - offset of parsed data in elements array
	 * @param size - size of the vector
	 */
	public vecf( String[] elements, int offset, int size )
	{
		this( size );
		
		for(int i = 0; i < dim; i++)
			data[i] = Float.parseFloat( elements[i+offset] );
	}
	
	/**
	 * Retrieves a single component from the vector
	 * @param i - index of component in vector
	 * @return component located at the given index
	 */
	public float get( int i )
	{
		return data[idx + i];
	}
	
	/**
	 * Sets a single component of the vector
	 * @param i - index of component in vector
	 * @param v - value to set component to
	 */
	public void set( int i, float v )
	{
		data[idx + i] = v;
	}
	
	/**
	 * Sets all values of the vector to a single value
	 * @param scalar - value to assign to all components
	 */
	public void set( float scalar )
	{
		int i = idx;
		while( i < end )
			data[i++] = scalar;
	}
	
	/**
	 * Copies the values from another vector to this vector.
	 * The size of the other vector is assumed to be the same size.
	 * @param vector - secondary vector
	 */
	public void set( vecf vector )
	{
		System.arraycopy( vector.data, vector.idx, data, idx, dim );
	}
	
	/**
	 * Adds a scalar value to the vector
	 * @param scalar - scalar value
	 */
	public void add( float scalar )
	{
		int i = idx;
		while( i < end )
			data[i++] += scalar;
	}
	
	/**
	 * Adds the values from another vector to this vector.
	 * The size of the other vector is assumed to be the same size.
	 * @param vector - secondary vector
	 */
	public void add( vecf vector )
	{
		int i0 = idx;
		int i1 = vector.idx;
		while( i0 < end )
			data[i0++] += vector.data[i1++];
	}
	
	/**
	 * Adds the values from another scaled vector to this vector.
	 * The scaling is not permanently applied to the vector.
	 * The size of the other vector is assumed to be the same size.
	 * @param vector - secondary vector
	 * @param scalar - scaling factor
	 */
	public void addMul( vecf vector, float scalar )
	{
		int i0 = idx;
		int i1 = vector.idx;
		while( i0 < end )
			data[i0++] += vector.data[i1++] * scalar;
	}
	
	/**
	 * Subtracts this vector by the values from another scaled vector.
	 * The scaling is not permanently applied to the vector.
	 * The size of the other vector is assumed to be the same size.
	 * @param vector - secondary vector
	 * @param scalar - scaling factor
	 */
	public void subMul( vecf vector, float scalar )
	{
		int i0 = idx;
		int i1 = vector.idx;
		while( i0 < end )
			data[i0++] -= vector.data[i1++] * scalar;
	}
	
	/**
	 * Subtracts a scalar value to the vector
	 * @param scalar - scalar value
	 */
	public void sub( float scalar )
	{
		int i = idx;
		while( i < end )
			data[i++] -= scalar;
	}
	
	/**
	 * Subtracts the of this vector by another vector.
	 * The size of the other vector is assumed to be the same size.
	 * @param vector - secondary vector
	 */
	public void sub( vecf vector )
	{
		int i0 = idx;
		int i1 = vector.idx;
		while( i0 < end )
			data[i0++] -= vector.data[i1++];
	}
	
	/**
	 * Multiplies all values of this vector by a scalar.
	 * @param scalar - scaling factor
	 */
	public void mul( float scalar )
	{
		for( int i = idx; i < end; i++ )
			data[i] *= scalar;
	}
	
	/**
	 * Divides all values of this vector by a scalar.
	 * @param scalar - scaling factor
	 */
	public void div( float scalar )
	{
		for( int i = idx; i < end; i++ )
			data[i] /= scalar;
	}
	
	/**
	 * Performs dot product between this vector and another vector.
	 * The size of the other vector is assumed to be the same size.
	 * @param vector - secondary vector
	 */
	public float dot( vecf vector )
	{
		float result = 0.0f;
		int i0 = idx;
		int i1 = vector.idx;
		while( i0 < end )
			result += data[i0++] * vector.data[i1++];
		return result;
	}
	
	/**
	 * Sets every element of the vector to a random value
	 */
	public void randomize()
	{
		for( int i = idx; i < end; i++ )
			data[i] = (float)Math.random();
	}
	
	/**
	 * Calculates the sum of all values in this vector.
	 * @return the horizontal sum of the vector
	 */
	public float hSum()
	{
		float result = 0.0f;
		for( int i = idx; i < end; i++ )
			result += data[i];
		return result;
	}
	
	/**
	 * Normalizes the vector by dividing all components by 
	 * the length of this vector.
	 */
	public void normalize()
	{
		div( length() );
	}
	
	/**
	 * Normalizes a copy of this vector.
	 * @return The unit vector of this vector.
	 */
	public vecf unit()
	{
		vecf result = new vecf( dim );
		vecf.mul( result, this, 1.0f / length() );
		return result;
	}
	
	/**
	 * Projects this vector onto another vector.
	 * The size of the other vector is assumed to be the same size.
	 * @param vector - secondary vector
	 * @return copy of the projected vector
	 */
	public vecf projectOn( vecf vector )
	{
		vecf result = new vecf( dim );
		vecf.mul( result, vector, dot( vector ) / vector.dot( vector ) );
		return result;
	}
	
	/**
	 * Calculates the length of this vector
	 * @return the length of this vector
	 */
	public float length()
	{
		return FloatUtil.sqrt( length2() );
	}
	
	/*
	 * Calculates the squared length of this vector
	 * @return the squared length of this vector
	 */
	public float length2()
	{
		float result = 0.0f;
		for( int i = idx; i < end; i++ )
			result += data[i] * data[i];
		return result;
	}
	
	/**
	 * @return the number of dimensions in the vector
	 */
	public int rank()
	{
		return dim;
	}
	
	/**
	 * Compares this vector to another vector and checks if 
	 * they are equal based on a specified threshold.
	 * The size of the other vector is assumed to be the same size.
	 * @param vector - secondary vector
	 * @param threshold - threshold of matching
	 * @return Whether the vectors match
	 */
	public boolean equals( vecf vector, float threshold )
	{
		boolean result = true;
		int i0 = idx;
		int i1 = vector.idx;
		while( i0 < end )
			result &= Math.abs( vector.data[i1++] - data[i0++] ) < threshold;
		return result;
	}
	
	/**
	 * Copies this vector to a given buffer and offset.
	 * @param buffer - destination data buffer
	 * @param offset - offset into destination buffer
	 */
	public void copyTo( float[] buffer, int offset )
	{
		System.arraycopy( data, idx, buffer, offset, dim );
	}
	
	/**
	 * Copies this vector to a given buffer and offset. The size
	 * of the data copied is overwritten.
	 * @param buffer - destination data buffer
	 * @param offset - offset into destination buffer
	 * @param size - overwritten size of copied data
	 */
	public void copyTo( float[] buffer, int offset, int size )
	{
		if( size > dim ) throw new IndexOutOfBoundsException();
		System.arraycopy( data, idx, buffer, offset, size );
	}
	
	/**
	 * Copies this vector to a given buffer and offset. A stride
	 * value can be assigned to space out each component.
	 * @param buffer - destination data buffer
	 * @param offset - offset into destination buffer
	 * @param stride - the incremented index of each component
	 */
	public void copyInterleaved( float[] buffer, int offset, int stride )
	{
		for( int i = idx, j = offset; i < dim; i++, j += stride )
			buffer[j] = data[i];
	}
	
	@Override
	public vecf clone()
	{
		float[] cloned = new float[dim];
		System.arraycopy( data, idx, cloned, 0, dim );
		return new vecf( cloned, idx, dim );
	}
	
	@Override
	public String toString()
	{
		return PrintUtil.toString( "\t", data, idx, end );
	}
	
	/**
	 * Adds two vectors and stores the result into another vector.
	 * The size of every vectors is assumed to be the same size as the resulting vector.
	 * @param r - resulting vector
	 * @param a - first vector
	 * @param b - second vector
	 */
	public static void add( vecf r, vecf a, vecf b )
	{
		for( int i0 = r.idx, i1 = a.idx, i2 = b.idx; i0 < r.end; i0++, i1++, i2++ )
			r.data[i0] = a.data[i1] + b.data[i2];
	}
	
	/**
	 * Subtracts two vectors and stores the result into another vector.
	 * The size of every vectors is assumed to be the same size as the resulting vector.
	 * @param r - resulting vector
	 * @param a - first vector
	 * @param b - second vector
	 */
	public static void sub( vecf r, vecf a, vecf b )
	{
		for( int i0 = r.idx, i1 = a.idx, i2 = b.idx; i0 < r.end; i0++, i1++, i2++ )
			r.data[i0] = a.data[i1] - b.data[i2];
	}
	
	/**
	 * Multiplies a vector by a scalar and stores the result into another vector.
	 * The size of every vectors is assumed to be the same size as the resulting vector.
	 * @param r - resulting vector
	 * @param a - first vector
	 * @param scalar - scaling factor
	 */
	public static void mul( vecf r, vecf a, float scalar )
	{
		for( int i0 = r.idx, i1 = a.idx; i0 < r.end; i0++, i1++ )
			r.data[i0] = a.data[i1] * scalar;
	}
	
	/**
	 * Calculates the weighted average of two vectors and stores the result into another vector.
	 * The size of every vectors is assumed to be the same size as the resulting vector.
	 * @param avg - resulting average vector
	 * @param v0 - first vector
	 * @param v1 - second vector
	 * @param w - weight factor of first vector to second vector
	 */
	public static void average( vecf avg, vec4f v0, vec4f v1, float w )
	{
		float rw = 1.0f - w;
		mul( avg, v0, rw );
		avg.addMul( v1, w );
	}
	
	/**
	 * Calculates the linear average of all vectors and stores the result into another vector.
	 * The size of every vectors is assumed to be the same size as the resulting vector.
	 * @param avg - resulting average vector
	 * @param vectors - array of vectors
	 */
	public static void average( vecf avg, vecf ... vectors )
	{
		for( vecf vec : vectors )
			avg.add( vec );
		avg.div( vectors.length );
	}
	
	/**
	 * Calculates the 2D dot product between two data buffer subsets.
	 * The size of each subset is assumed to be greater than 1.
	 * @param v0 - buffer containing first subset
	 * @param i0 - index of first subset in buffer
	 * @param v1 - buffer containing second subset
	 * @param i1 - index of second subset in buffer
	 */
	public static float dot2( float[] v0, int i0, float[] v1, int i1 )
	{
		return v0[i0++] * v1[i1++] + v0[i0] * v1[i1];
	}
	
	/**
	 * Calculates the 2D dot product between a data buffer subset and the given components.
	 * The size of the subset is assumed to be greater than 1.
	 * @param v0 - buffer containing first subset
	 * @param i0 - index of first subset in buffer
	 * @param x - 1st component
	 * @param y - 2nd component
	 */
	public static float dot2( float[] v0, int i0, float x, float y )
	{
		return v0[i0++] * x + v0[i0] * y;
	}
	
	/**
	 * Calculates the 3D dot product between two data buffer subsets.
	 * The size of each subset is assumed to be greater than 2.
	 * @param v0 - buffer containing first subset
	 * @param i0 - index of first subset in buffer
	 * @param v1 - buffer containing second subset
	 * @param i1 - index of second subset in buffer
	 */
	public static float dot3( float[] v0, int i0, float[] v1, int i1 )
	{
		return v0[i0++] * v1[i1++] + v0[i0++] * v1[i1++] + v0[i0] * v1[i1];
	}
	
	/**
	 * Calculates the 3D dot product between a data buffer subset and the given components.
	 * The size of the subset is assumed to be greater than 2.
	 * @param v0 - buffer containing first subset
	 * @param i0 - index of first subset in buffer
	 * @param x - 1st component
	 * @param y - 2nd component
	 * @param z - 3rd component
	 */
	public static float dot3( float[] v0, int i0, float x, float y, float z )
	{
		return v0[i0++] * x + v0[i0++] * y + v0[i0] * z;
	}
	
	/**
	 * Calculates the 4D dot product between two data buffer subsets.
	 * The size of each subset is assumed to be greater than 3.
	 * @param v0 - buffer containing first subset
	 * @param i0 - index of first subset in buffer
	 * @param v1 - buffer containing second subset
	 * @param i1 - index of second subset in buffer
	 */
	public static float dot4( float[] v0, int i0, float[] v1, int i1 )
	{
		return v0[i0++] * v1[i1++] + v0[i0++] * v1[i1++] + v0[i0++] * v1[i1++] + v0[i0] * v1[i1];
	}
	
	/**
	 * Calculates the 4D dot product between a data buffer subset and the given components.
	 * The size of the subset is assumed to be greater than 3.
	 * @param v0 - buffer containing first subset
	 * @param i0 - index of first subset in buffer
	 * @param x - 1st component
	 * @param y - 2nd component
	 * @param z - 3rd component
	 * @param w - 4th component
	 */
	public static float dot4( float[] v0, int i0, float x, float y, float z, float w )
	{
		return v0[i0++] * x + v0[i0++] * y + v0[i0++] * z + v0[i0] * w;
	}
}	
