package swb.math;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.math.FloatUtil;

/**
 * Represents a 2x2 matrix in row-major order.
 * |0|1|
 * |2|3|
 * 
 * @author Sean Rannie
 */
public class mat2x2 extends vecf
{
	/**
	 * Protected constructor that allows overriding of size
	 * @param data - data buffer containing matrix data stored linearly
	 * @param offset - offset of matrix in data buffer
	 * @param size - size of the matrix linearly
	 */
	protected mat2x2( float[] data, int offset, int size )
	{
		super( data, offset, size );
	}
	
	/**
	 * Constructs a 2x2 identity matrix
	 */
	public mat2x2()
	{
		super( new float[] { 1.0f, 0.0f, 0.0f, 1.0f }, 0, 4 );
	}
	
	/**
	 * Constructs the matrix using the given data array.
	 * The size of the array must be greater or equal to 4.
	 * @param data - matrix data stored linearly
	 */
	public mat2x2( float[] data )
	{
		super( data, 0, 4 );
	}
	
	/**
	 * Constructs the matrix using the given data array. An offset can
	 * be specified into the data to allow multiple matrices to be defined 
	 * in a single array. The size of the array after the offset must be at least 4.
	 * @param data - data buffer containing matrix data stored linearly
	 * @param offset - offset of matrix in data buffer
	 */
	public mat2x2( float[] data, int offset )
	{
		super( data, offset, 4 );
	}
	
	/**
	 * Performs matrix multiplication between two 2x2 matrices
	 * @param mat - secondary matrix
	 * @return a new matrix that is the product of the two original matrices
	 */
	public mat2x2 matMul( mat2x2 mat )
	{
		int i0 = mat.idx;
		int i1 = i0 + 2;
		return new mat2x2( new float[] {
			dot2( data, idx    , mat.data[i0  ], mat.data[i1  ] ),
			dot2( data, idx + 2, mat.data[i0++], mat.data[i1++] ),
			dot2( data, idx    , mat.data[i0  ], mat.data[i1  ] ),
			dot2( data, idx + 2, mat.data[i0  ], mat.data[i1  ] )
		} );
	}
	
	/**
	 * Transforms a 2D vector by the matrix transformation.
	 * @param vector - vector treated as a 2D vector
	 */
	public void transform2f( vecf vector )
	{
		transform2f( vector.data, vector.idx );
	}
	
	/**
	 * Transforms a 2D vector by the matrix transformation.
	 * This operation is performed directly on an array.
	 * @param vec - {@link float[]} treated as a 2D vector
	 * @param i - offset into array
	 */
	public void transform2f( float[] vec, int i )
	{
		float x = dot2( data, idx    , vec, i );
		float y = dot2( data, idx + 2, vec, i );
		vec[i    ] = x;
		vec[i + 1] = y;
	}
	
	/**
	 * Calculates the determinant of the matrix.
	 * @return the determinant
	 */
	public float determinant()
	{
		return data[idx] * data[idx+3] - data[idx+1] * data[idx+2];
	}
	
	/**
	 * Sets the uniform value of the matrix using a {@link GL3} instance.
	 * This operation can be reused with different shader programs. The
	 * matrix is not bounded to a specific shader or location.
	 * @param gl - {@link GL3} instance
	 * @param loc - location of uniform
	 */
	public void upload( GL3 gl, int loc )
	{
		gl.glUniformMatrix2fv( loc, 1, true, data, idx );
	}
	
	/**
	 * Sets the limited 2D transformation of the matrix. 2x2 matrices can
	 * only support rotation and scaling in 2D transformations.
	 * @param rot - rotation in radians
	 * @param scx - horizontal scaling
	 * @param scy - vertical scaling
	 */
	public void setTransform2D( float rot, float scx, float scy )
	{
		float sinx = FloatUtil.sin( rot );
		float cosx = FloatUtil.cos( rot );
		data[idx  ] = cosx * scx;	
		data[idx+1] = -sinx;
		data[idx+2] = sinx;	
		data[idx+3] = cosx * scy;
	}
	
	/**
	 * Sets the limited 2D transformation of the matrix. 2x2 matrices can
	 * only support rotation and scaling in 2D transformations.
	 * @param rot - rotation in radians
	 * @param scale - horizontal and vertical scaling
	 */
	public void setTransform2D( float rot, vec2f scale )
	{
		setTransform2D( rot, scale.getX(), scale.getY() );
	}
	
	@Override
	public String toString()
	{
		int i = idx;
		return String.format( "%f\t%f\n%f\t%f\n%f\t%f\n%f\t%f\n", 
				data[i++], data[i++],
				data[i++], data[i  ]  );
	}
}
