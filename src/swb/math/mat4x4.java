package swb.math;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.math.FloatUtil;

/**
 * Represents a 4x4 matrix in row-major order.
 * | 0| 1| 2| 3|
 * | 4| 5| 6| 7|
 * | 8| 9|10|11|
 * |12|13|14|15|
 * 
 * @author Sean Rannie
 */
public class mat4x4 extends mat3x3
{
	/**
	 * Constructs a 4x4 identity matrix
	 */
	public mat4x4()
	{
		super( new float[] { 
				1.0f, 0.0f, 0.0f, 0.0f, 
				0.0f, 1.0f, 0.0f, 0.0f, 
				0.0f, 0.0f, 1.0f, 0.0f, 
				0.0f, 0.0f, 0.0f, 1.0f 
		}, 0, 16 );
	}
	
	/**
	 * Constructs the matrix using the given data array.
	 * The size of the array must be greater or equal to 16.
	 * @param data - matrix data stored linearly
	 */
	public mat4x4( float[] data )
	{
		super( data, 0, 16 );
	}
	
	/**
	 * Constructs the matrix using the given data array. An offset can
	 * be specified into the data to allow multiple matrices to be defined 
	 * in a single array. The size of the array after the offset must be at least 16.
	 * @param data - data buffer containing matrix data stored linearly
	 * @param offset - offset of matrix in data buffer
	 */
	public mat4x4( float[] data, int offset )
	{
		super( data, offset, 16 );
	}
	
	@Override
	public mat2x2 matMul( mat2x2 mat )
	{
		// TODO: assumption that mat is a 2x2 matrix
		int i0 = mat.idx;
		int i1 = i0 + 4;
		return new mat2x2( new float[] {
			dot2( data, idx    , mat.data[i0  ], mat.data[i1  ] ),
			dot2( data, idx + 4, mat.data[i0++], mat.data[i1++] ),
			dot2( data, idx    , mat.data[i0  ], mat.data[i1  ] ),
			dot2( data, idx + 4, mat.data[i0  ], mat.data[i1  ] )
		} );
	}
	
	@Override
	public mat3x3 matMul( mat3x3 mat )
	{
		// TODO: assumption that mat is a 3x3 matrix
		int i0 = mat.idx;
		int i1 = i0 + 4;
		int i2 = i1 + 4;
		return new mat3x3( new float[] {
			dot3( data, idx     , mat.data[i0  ], mat.data[i1  ], mat.data[i2  ] ),
			dot3( data, idx +  4, mat.data[i0  ], mat.data[i1  ], mat.data[i2  ] ),
			dot3( data, idx +  8, mat.data[i0++], mat.data[i1++], mat.data[i2++] ),
			dot3( data, idx     , mat.data[i0  ], mat.data[i1  ], mat.data[i2  ] ),
			dot3( data, idx +  4, mat.data[i0  ], mat.data[i1  ], mat.data[i2  ] ),
			dot3( data, idx +  8, mat.data[i0++], mat.data[i1++], mat.data[i2++] ),
			dot3( data, idx     , mat.data[i0  ], mat.data[i1  ], mat.data[i2  ] ),
			dot3( data, idx +  4, mat.data[i0  ], mat.data[i1  ], mat.data[i2  ] ),
			dot3( data, idx +  8, mat.data[i0++], mat.data[i1++], mat.data[i2++] )
		} );
	}
	
	/**
	 * Performs matrix multiplication between two 4x4 matrices
	 * @param mat - secondary matrix
	 * @return a new matrix that is the product of the two original matrices
	 */
	public mat4x4 matMul( mat4x4 mat )
	{
		int i0 = mat.idx;
		int i1 = i0 + 4;
		int i2 = i1 + 4;
		int i3 = i2 + 4;
		return new mat4x4( new float[] {
			dot4( data, idx     , mat.data[i0  ], mat.data[i1  ], mat.data[i2  ], mat.data[i3  ]),
			dot4( data, idx +  4, mat.data[i0  ], mat.data[i1  ], mat.data[i2  ], mat.data[i3  ]),
			dot4( data, idx +  8, mat.data[i0  ], mat.data[i1  ], mat.data[i2  ], mat.data[i3  ]),
			dot4( data, idx + 12, mat.data[i0++], mat.data[i1++], mat.data[i2++], mat.data[i3++]),
			dot4( data, idx     , mat.data[i0  ], mat.data[i1  ], mat.data[i2  ], mat.data[i3  ]),
			dot4( data, idx +  4, mat.data[i0  ], mat.data[i1  ], mat.data[i2  ], mat.data[i3  ]),
			dot4( data, idx +  8, mat.data[i0  ], mat.data[i1  ], mat.data[i2  ], mat.data[i3  ]),
			dot4( data, idx + 12, mat.data[i0++], mat.data[i1++], mat.data[i2++], mat.data[i3++]),
			dot4( data, idx     , mat.data[i0  ], mat.data[i1  ], mat.data[i2  ], mat.data[i3  ]),
			dot4( data, idx +  4, mat.data[i0  ], mat.data[i1  ], mat.data[i2  ], mat.data[i3  ]),
			dot4( data, idx +  8, mat.data[i0  ], mat.data[i1  ], mat.data[i2  ], mat.data[i3  ]),
			dot4( data, idx + 12, mat.data[i0++], mat.data[i1++], mat.data[i2++], mat.data[i3++]),
			dot4( data, idx     , mat.data[i0  ], mat.data[i1  ], mat.data[i2  ], mat.data[i3  ]),
			dot4( data, idx +  4, mat.data[i0  ], mat.data[i1  ], mat.data[i2  ], mat.data[i3  ]),
			dot4( data, idx +  8, mat.data[i0  ], mat.data[i1  ], mat.data[i2  ], mat.data[i3  ]),
			dot4( data, idx + 12, mat.data[i0  ], mat.data[i1  ], mat.data[i2  ], mat.data[i3  ]),
		} );
	}
	
	@Override
	public void transform2f( float[] vec, int i )
	{
		float x = dot2( data, idx    , vec, i ) + data[idx + 2 ];
		float y = dot2( data, idx + 4, vec, i ) + data[idx + 6 ];
		vec[i    ] = x;
		vec[i + 1] = y;
	}
	
	@Override
	public void transform3f( float[] vec, int i )
	{
		float x = dot3( data, idx    , vec, i ) + data[idx + 3 ];
		float y = dot3( data, idx + 4, vec, i ) + data[idx + 7 ];
		float z = dot3( data, idx + 8, vec, i ) + data[idx + 11];
		vec[i    ] = x;
		vec[i + 1] = y;
		vec[i + 2] = z;
	}
	
	/**
	 * Transforms a 4D vector by the matrix transformation.
	 * @param vector - vector treated as a 4D vector
	 */
	public void transform4f( vecf vector )
	{
		transform4f( vector.data, vector.idx );
	}
	
	/**
	 * Transforms a 4D vector by the matrix transformation.
	 * This operation is performed directly on an array.
	 * @param vec - {@link float[]} treated as a 4D vector
	 * @param i - offset into array
	 */
	public void transform4f( float[] vec, int i )
	{
		float x = dot4( data, idx     , vec, i );
		float y = dot4( data, idx +  4, vec, i );
		float z = dot4( data, idx +  8, vec, i );
		float w = dot4( data, idx + 12, vec, i );
		vec[i    ] = x;
		vec[i + 1] = y;
		vec[i + 2] = z;
		vec[i + 3] = w;
	}
	
	@Override
	public float determinant()
	{
		float t01 = data[idx+ 8] * data[idx+13] - data[idx+ 9] * data[idx+12];
		float t02 = data[idx+ 8] * data[idx+14] - data[idx+10] * data[idx+12];
		float t03 = data[idx+ 8] * data[idx+15] - data[idx+11] * data[idx+12];
		float t12 = data[idx+ 9] * data[idx+14] - data[idx+10] * data[idx+13];
		float t13 = data[idx+ 9] * data[idx+15] - data[idx+11] * data[idx+13];
		float t23 = data[idx+10] * data[idx+15] - data[idx+11] * data[idx+14];
		return  data[idx  ] * ( data[idx+5] * t23 + data[idx+6] * t13 + data[idx+7] * t12 ) +
				data[idx+1] * ( data[idx+4] * t23 + data[idx+6] * t03 + data[idx+7] * t02 ) +
				data[idx+2] * ( data[idx+4] * t13 + data[idx+5] * t03 + data[idx+7] * t01 ) +
				data[idx+3] * ( data[idx+4] * t12 + data[idx+5] * t02 + data[idx+6] * t01 );
	}
	
	@Override
	public void upload( GL3 gl, int loc )
	{
		gl.glUniformMatrix4fv( loc, 1, true, data, idx );
	}
	
	/**
	 * Sets the transformation of the camera based on view parameters.
	 * @param eye - position of view
	 * @param front - direction of view
	 * @param up - orthogonal direction of view upwards
	 */
	public void lookAt( vec3f eye, vec3f front, vec3f up )
	{
		vec3f f = front.unit();
		vec3f s = f.cross( up.unit() );
		s.normalize();
		vec3f u = s.cross( f );
		f.mul( -1.0f );
		
		s.copyTo( data, idx );
		u.copyTo( data, idx + 4 );
		f.copyTo( data, idx + 8 );
		data[idx+ 3] = -s.dot( eye );
		data[idx+ 7] = -u.dot( eye );
		data[idx+11] = -f.dot( eye );
	}
	
	/**
	 * Sets the projection transformation of the matrix.
	 * @param fov - field of view
	 * @param ratio - aspect ratio
	 * @param near - near plane
	 * @param far - far plane
	 */
	public void setPerspective( float fov, float ratio, float near, float far )
	{
		int i = idx;
		float tanHalf = FloatUtil.atan( fov / 2.0f );
		data[i++] = 1 / ( ratio * tanHalf );		data[i++] = 0.0f;			
		data[i++] = 0.0f;							data[i++] = 0.0f;
		data[i++] = 0.0f;							data[i++] = 1 / tanHalf;	
		data[i++] = 0.0f;							data[i++] = 0.0f;
		data[i++] = 0.0f;							data[i++] = 0.0f;			
		data[i++] = (far + near) / (near - far);	data[i++] = -2.0f * far * near / (far - near);
		data[i++] = 0.0f;							data[i++] = 0.0f;			
		data[i++] = -1.0f;							data[i  ] = 0.0f;
	}
	
	public void setQuaternionRotation( vec4f quat )
	{
		int i = quat.idx;
		float s = 2.0f / quat.length2();
		float qx = quat.data[i++];
		float qy = quat.data[i++];
		float qz = quat.data[i++];
		float q0 = quat.data[i  ];
		i = idx;
		data[i++] = 1.0f - s * (qy * qy + qz * qz);
		data[i++] = s * (qx * qy - qz * q0);
		data[i++] = s * (qx * qz + qy * q0);
		data[i++] = 0.0f;
		data[i++] = s * (qx * qy + qz * q0);
		data[i++] = 1.0f - s * (qx * qx + qz * qz);
		data[i++] = s * (qy * qz - qx * q0);
		data[i++] = 0.0f;
		data[i++] = s * (qx * qz - qy * q0);
		data[i++] = s * (qy * qz + qx * q0);
		data[i++] = 1.0f - s * (qx * qx + qy * qy);
		data[i  ] = 0.0f;
	}
	
	public void setQuaternionRotation( vec3f quat )
	{
		int i = quat.idx;
		float s = 2.0f / quat.length2();
		float qx = quat.data[i++];
		float qy = quat.data[i++];
		float qz = quat.data[i  ];
		i = idx;
		data[i++] = 1.0f - s * (qy * qy + qz * qz);
		data[i++] = s * qx * qy;
		data[i++] = s * qx * qz;
		data[i++] = 0.0f;
		data[i++] = s * qx * qy;
		data[i++] = 1.0f - s * (qx * qx + qz * qz);
		data[i++] = s * qy * qz ;
		data[i++] = 0.0f;
		data[i++] = s * qx * qz;
		data[i++] = s * qy * qz;
		data[i++] = 1.0f - s * (qx * qx + qy * qy);
		data[i  ] = 0.0f;
	}
	
	/**
	 * Sets the 3D transformation of the matrix.
	 * @param x - translation across x-axis
	 * @param y - translation across y-axis
	 * @param z - translation across z-axis
	 * @param rx - rotation across x-axis
	 * @param ry - rotation across y-axis
	 * @param rz - rotation across z-axis
	 * @param scx - scaling along the x-axis
	 * @param scy - scaling along the y-axis
	 * @param scz - scaling along the z-axis
	 */
	public void setTransform3D( float x, float y, float z, float rx, float ry, float rz, float scx, float scy, float scz )
	{
		float sinx = FloatUtil.sin( rx );
		float cosx = FloatUtil.cos( rx );
		float siny = FloatUtil.sin( ry );
		float cosy = FloatUtil.cos( ry );
		float sinz = FloatUtil.sin( rz );
		float cosz = FloatUtil.cos( rz );
		int i = idx;
		data[i++] = scx * cosy * cosz; data[i++] = -sinz; data[i++] = siny; data[i++] = x;
		data[i++] = sinz; data[i++] = scy * cosx * cosz; data[i++] = -sinx; data[i++] = y;
		data[i++] = -siny; data[i++] = sinx; data[i++] = scz * cosx * cosy; data[i  ] = z;
	}
	
	/**
	 * Sets the 3D transformation of the matrix.
	 * @param translation - 3D translation across all three axes
	 * @param rotation - 3D rotation across all three axes
	 * @param scale - 3D scaling across all three axes
	 */
	public void setTransform3D( vec3f translation, vec3f rotation, vec3f scale )
	{
		setTransform3D( translation.getX(), translation.getY(), translation.getZ(),
				rotation.getX(), rotation.getY(), rotation.getZ(),
				scale.getX(), scale.getY(), scale.getZ() );
	}
	
	@Override
	public void setTransform3D( float rx, float ry, float rz, float scx, float scy, float scz )
	{
		setTransform3D( 0.0f, 0.0f, 0.0f, rx, ry, rz, scx, scy, scz );
	}
	
	@Override
	public void setTransform2D( float x, float y, float rot, float scx, float scy )
	{
		float sinx = FloatUtil.sin( rot );
		float cosx = FloatUtil.cos( rot );
		data[idx  ] = cosx * scx;	
		data[idx+1] = -sinx;	
		data[idx+2] = x;
		data[idx+4] = sinx;	
		data[idx+5] = cosx * scy;	
		data[idx+6] = y;
	}
	
	@Override
	public String toString()
	{
		int i = idx;
		return String.format( "%f\t%f\t%f\t%f\n%f\t%f\t%f\t%f\n%f\t%f\t%f\t%f\n%f\t%f\t%f\t%f\n", 
				data[i++], data[i++], data[i++], data[i++],
				data[i++], data[i++], data[i++], data[i++],
				data[i++], data[i++], data[i++], data[i++],
				data[i++], data[i++], data[i++], data[i  ] );
	}
}