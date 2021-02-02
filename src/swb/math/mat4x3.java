package swb.math;

public class mat4x3 extends mat3x3
{
	public vec3f v4;
	
	public mat4x3(vec3f v1, vec3f v2, vec3f v3, vec3f v4) 
	{
		super(v1, v2, v3);
		this.v4 = v4;
	}
	
	public vec3f intersection( Line line )
	{
		vec3f intr = toPlane().intersection( line );
		
		if( intr != null && intr != vec3f.UNDEFINED &&
				intr.y >= 0.0f && intr.z >= 0.0f && intr.y <= 1.0f && intr.z <= 1.0f )
			return line.getPoint( intr.x );
		return null;
	}

	public boolean intersects( mat2x3 edge )
	{
		vec3f svec = vec3f.sub( v2, v1 );
		vec3f tvec = vec3f.sub( v3, v1 );
		vec3f normal = svec.cross( tvec );
		
		vec3f lvec = vec3f.sub( edge.v1, edge.v2 );
		vec3f pt = vec3f.sub( edge.v1, v1 );
		float det = lvec.dot( normal );
		
		if( det == 0.0f ) return false;
		
		float t = normal.dot( pt ) / det;
		float u = tvec.cross( lvec ).dot( pt ) / det;
		float v = lvec.cross( svec ).dot( pt ) / det;
		
		return t >= 0.0f && t <= 1.0f && u >= 0.0f && v >= 0.0f && u <= 1.0f && v <= 1.0f;
	}
	
	public float area()
	{
		return normal().length();
	}
	
	public boolean containsPoint( vec3f point )
	{
		vec3f svec = vec3f.sub( v2, v1 );
		vec3f tvec = vec3f.sub( v3, v1 );
		vec3f diagonal = vec3f.sub( point, v1 );
		
		if( diagonal.dot( svec.cross( tvec ) ) != 0.0f ) return false;
		
		float s = diagonal.dot( svec ) / svec.dot( svec );
		float t = diagonal.dot( tvec ) / tvec.dot( tvec );
		
		return s >= 0.0f && t >= 0.0f && s <= 1.0f && t <= 1.0f;
	}
	
	public boolean intersects( mat4x3 quad )
	{
		boolean o0 = quad.v1 == v1 || quad.v1 == v2 || quad.v1 == v3 || quad.v1 == v4;
		boolean o1 = quad.v2 == v1 || quad.v2 == v2 || quad.v2 == v3 || quad.v2 == v4;
		boolean o2 = quad.v3 == v1 || quad.v3 == v2 || quad.v3 == v3 || quad.v3 == v4;
		boolean o3 = quad.v4 == v1 || quad.v4 == v2 || quad.v4 == v3 || quad.v4 == v4;
		boolean e0Int = !o0 && !o1 && intersects( new mat2x3( quad.v1, quad.v2 ) );
		boolean e1Int = !o0 && !o2 && intersects( new mat2x3( quad.v1, quad.v3 ) );
		boolean e2Int = !o1 && !o3 && intersects( new mat2x3( quad.v2, quad.v4 ) );
		boolean e3Int = !o2 && !o3 && intersects( new mat2x3( quad.v3, quad.v4 ) );
		return e0Int || e1Int || e2Int || e3Int;
	}
	
	public static void main( String[] args )
	{
		vec3f v1 = new vec3f( 10.0f, 10.0f, 100.0f );
		vec3f v2 = new vec3f( 10.0f, 0.0f, 100.0f );
		vec3f v3 = new vec3f( 0.0f, 10.0f, 100.0f );
		vec3f v4 = new vec3f( 0.0f, 0.0f, 100.0f );
		mat4x3 q0 = new mat4x3( v3, v2, v1, v4 );
		
		vec3f v5 = new vec3f( 0.0f, 10.0f, 100.0f );
		
		System.out.println( q0.containsPoint( v5 ) );
	}
}
