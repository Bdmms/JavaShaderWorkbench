package swb.math;

public class Transform 
{
	private static float dot3( float[] v0, int i0, float x, float y, float z )
	{
		float a = v0[i0++] * x;
		float b = v0[i0++] * y;
		float c = v0[i0++] * z;
		return a + b + c;
	}
	
	public static void rotate( float[] vector, int i0, float[] rotation, int i1 )
	{
		float sinx = (float)Math.sin( rotation[i1] );
		float cosx = (float)Math.cos( rotation[i1++] );
		float siny = (float)Math.sin( rotation[i1] );
		float cosy = (float)Math.cos( rotation[i1++] );
		float sinz = (float)Math.sin( rotation[i1] );
		float cosz = (float)Math.cos( rotation[i1++] );
		float x = dot3( vector, i0, cosy * cosz, -sinz, siny );
		float y = dot3( vector, i0, sinz, cosx * cosz, -sinx );
		float z = dot3( vector, i0, -siny, sinx, cosx * cosy );
		vector[i0++] = x;
		vector[i0++] = y;
		vector[i0++] = z;
	}
	
	public static void setMatrix( float[] matrix, int i, vec3f translation, vec3f rotation )
	{
		float sinx = (float)Math.sin( rotation.x );
		float cosx = (float)Math.cos( rotation.x );
		float siny = (float)Math.sin( rotation.y );
		float cosy = (float)Math.cos( rotation.y );
		float sinz = (float)Math.sin( rotation.z );
		float cosz = (float)Math.cos( rotation.z );
		
		matrix[i++] = cosy * cosz;		matrix[i++] = -sinz;		matrix[i++] = siny;
		matrix[i++] = translation.x;
		matrix[i++] = sinz;				matrix[i++] = cosx * cosz;	matrix[i++] = -sinx;
		matrix[i++] = translation.y;
		matrix[i++] = -siny;			matrix[i++] = sinx;			matrix[i++] = cosx * cosy;
		matrix[i++] = translation.z;
	}
}
