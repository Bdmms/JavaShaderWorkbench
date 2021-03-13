package swb.math;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.math.FloatUtil;

/**
 * Represents a 3x3 matrix in row-major order.
 * |0|1|2|
 * |3|4|5|
 * |6|7|8|
 * 
 * @author Sean Rannie
 */
public class mat3x3 extends mat2x2
{
	/**
	 * Protected constructor that allows overriding of size
	 * @param data - data buffer containing matrix data stored linearly
	 * @param offset - offset of matrix in data buffer
	 * @param size - size of the matrix linearly
	 */
	protected mat3x3( float[] data, int offset, int size )
	{
		super( data, offset, size );
	}
	
	/**
	 * Constructs a 3x3 identity matrix
	 */
	public mat3x3()
	{
		super( new float[] { 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f }, 0, 9 );
	}
	
	/**
	 * Constructs the matrix using the given data array.
	 * The size of the array must be greater or equal to 9.
	 * @param data - matrix data stored linearly
	 */
	public mat3x3( float[] data )
	{
		super( data, 0, 9 );
	}
	
	/**
	 * Constructs the matrix using the given data array. An offset can
	 * be specified into the data to allow multiple matrices to be defined 
	 * in a single array. The size of the array after the offset must be at least 9.
	 * @param data - data buffer containing matrix data stored linearly
	 * @param offset - offset of matrix in data buffer
	 */
	public mat3x3( float[] data, int offset )
	{
		super( data, offset, 9 );
	}
	
	@Override
	public mat2x2 matMul( mat2x2 mat )
	{
		// TODO: assumption that mat is a 2x2 matrix
		int i0 = mat.idx;
		int i1 = i0 + 2;
		return new mat2x2( new float[] {
			dot2( data, idx    , mat.data[i0  ], mat.data[i1  ] ),
			dot2( data, idx + 3, mat.data[i0++], mat.data[i1++] ),
			dot2( data, idx    , mat.data[i0  ], mat.data[i1  ] ),
			dot2( data, idx + 3, mat.data[i0  ], mat.data[i1  ] )
		} );
	}
	
	/**
	 * Performs matrix multiplication between two 3x3 matrices
	 * @param mat - secondary matrix
	 * @return a new matrix that is the product of the two original matrices
	 */
	public mat3x3 matMul( mat3x3 mat )
	{
		int i0 = mat.idx;
		int i1 = i0 + 3;
		int i2 = i1 + 3;
		return new mat3x3( new float[] {
			dot3( data, idx     , mat.data[i0  ], mat.data[i1  ], mat.data[i2  ] ),
			dot3( data, idx +  3, mat.data[i0  ], mat.data[i1  ], mat.data[i2  ] ),
			dot3( data, idx +  6, mat.data[i0++], mat.data[i1++], mat.data[i2++] ),
			dot3( data, idx     , mat.data[i0  ], mat.data[i1  ], mat.data[i2  ] ),
			dot3( data, idx +  3, mat.data[i0  ], mat.data[i1  ], mat.data[i2  ] ),
			dot3( data, idx +  6, mat.data[i0++], mat.data[i1++], mat.data[i2++] ),
			dot3( data, idx     , mat.data[i0  ], mat.data[i1  ], mat.data[i2  ] ),
			dot3( data, idx +  3, mat.data[i0  ], mat.data[i1  ], mat.data[i2  ] ),
			dot3( data, idx +  6, mat.data[i0++], mat.data[i1++], mat.data[i2++] )
		} );
	}
	
	@Override
	public void transform2f( float[] vec, int i )
	{
		float x = dot2( data, idx    , vec, i ) + data[idx + 2 ];
		float y = dot2( data, idx + 3, vec, i ) + data[idx + 5 ];
		vec[i    ] = x;
		vec[i + 1] = y;
	}
	
	/**
	 * Transforms a 3D vector by the matrix transformation.
	 * @param vector - vector treated as a 3D vector
	 */
	public void transform3f( vecf vector )
	{
		transform3f( vector.data, vector.idx );
	}
	
	/**
	 * Transforms a 3D vector by the matrix transformation.
	 * This operation is performed directly on an array.
	 * @param vec - {@link float[]} treated as a 3D vector
	 * @param i - offset into array
	 */
	public void transform3f( float[] vec, int i )
	{
		float x = dot3( data, idx     , vec, i );
		float y = dot3( data, idx +  3, vec, i );
		float z = dot3( data, idx +  6, vec, i );
		vec[i    ] = x;
		vec[i + 1] = y;
		vec[i + 2] = z;
	}
	
	@Override
	public float determinant()
	{
		return  data[idx  ] * (data[idx+4] * data[idx+8] - data[idx+5] * data[idx+7]) + 
				data[idx+1] * (data[idx+3] * data[idx+8] - data[idx+5] * data[idx+6]) +
				data[idx+2] * (data[idx+3] * data[idx+7] - data[idx+4] * data[idx+6]);
	}
	
	@Override
	public void upload( GL3 gl, int loc )
	{
		gl.glUniformMatrix3fv( loc, 1, true, data, idx );
	}
	
	/**
	 * Sets the limited 3D transformation of the matrix. 3x3 matrices can
	 * only support rotation and scaling in 3D transformations.
	 * @param rx - rotation across x-axis
	 * @param ry - rotation across y-axis
	 * @param rz - rotation across z-axis
	 * @param scx - scaling along the x-axis
	 * @param scy - scaling along the y-axis
	 * @param scz - scaling along the z-axis
	 */
	public void setTransform3D( float rx, float ry, float rz, float scx, float scy, float scz )
	{
		float sinx = FloatUtil.sin( rx );
		float cosx = FloatUtil.cos( rx );
		float siny = FloatUtil.sin( ry );
		float cosy = FloatUtil.cos( ry );
		float sinz = FloatUtil.sin( rz );
		float cosz = FloatUtil.cos( rz );
		
		int i = idx;
		data[i++] = cosy * cosz * scx;	data[i++] = -sinz; 				data[i++] = siny;
		data[i++] = sinz; 				data[i++] = cosx * cosz * scy; 	data[i++] = -sinx;
		data[i++] = -siny; 				data[i++] = sinx; 				data[i  ] = cosx * cosy * scz;
	}
	
	/**
	 * Sets the limited 3D transformation of the matrix. 3x3 matrices can
	 * only support rotation and scaling in 3D transformations.
	 * @param rotation - 3D rotation across all three axes
	 * @param scale - 3D scaling across all three axes
	 */
	public void setTransform3D( vec3f rotation, vec3f scale )
	{
		setTransform3D( rotation.getX(), rotation.getY(), rotation.getZ(), scale.getX(), scale.getY(), scale.getZ() );
	}
	
	/**
	 * Sets the 2D transformation of the matrix.
	 * @param x - horizontal translation
	 * @param y - vertical translation
	 * @param rot - rotation in radians
	 * @param scx - horizontal scaling
	 * @param scy - vertical scaling
	 */
	public void setTransform2D( float x, float y, float rot, float scx, float scy )
	{
		float sinx = FloatUtil.sin( rot );
		float cosx = FloatUtil.cos( rot );
		int i = idx;
		data[i++] = cosx * scx;	
		data[i++] = -sinx;	
		data[i++] = x;
		data[i++] = sinx;	
		data[i++] = cosx * scy;	
		data[i  ] = y;
	}
	
	/**
	 * Sets the 2D transformation of the matrix.
	 * @param translation - translation
	 * @param rot - rotation in radians
	 * @param scale - horizontal and vertical scaling
	 */
	public void setTransform2D( vec2f translation, float rot, vec2f scale )
	{
		setTransform2D( translation.getX(), translation.getY(), rot, scale.getX(), scale.getY() );
	}
	
	@Override
	public void setTransform2D( float rot, float scx, float scy )
	{
		setTransform2D( 0.0f, 0.0f, rot, scx, scy );
	}
	
	@Override
	public String toString()
	{
		int i = idx;
		return String.format( "%f\t%f\t%f\n%f\t%f\t%f\n%f\t%f\t%f\n%f\t%f\t%f\n", 
				data[i++], data[i++], data[i++],
				data[i++], data[i++], data[i++],
				data[i++], data[i++], data[i  ] );
	}
}
