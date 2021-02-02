package swb.math;

public class mat3x3 extends mat2x3
{
	public vec3f v3;
	
	public mat3x3()
	{
		super();
		v3 = new vec3f();
	}
	
	public mat3x3( float v1x, float v1y, float v1z, float v2x, float v2y, float v2z, float v3x, float v3y, float v3z )
	{
		super( v1x, v1y, v1z, v2x, v2y, v2z );
		v3 = new vec3f( v3x, v3y, v3z );
	}
	
	public mat3x3( vec3f v1, vec3f v2, vec3f v3 )
	{
		super( v1, v2 );
		this.v3 = v3;
	}
	
	public Plane toPlane()
	{
		return new Plane( v1, v2, v3 );
	}
	
	public mat3x3 product( mat3x3 mat )
	{
		return new mat3x3(
			new vec3f( 	v1.x * mat.v1.x + v2.x * mat.v1.y + v3.x * mat.v1.z,
					   	v1.y * mat.v1.x + v2.y * mat.v1.y + v3.y * mat.v1.z,
					   	v1.z * mat.v1.x + v2.z * mat.v1.y + v3.z * mat.v1.z ),
			new vec3f(	v1.x * mat.v2.x + v2.x * mat.v2.y + v3.x * mat.v2.z,
				   		v1.y * mat.v2.x + v2.y * mat.v2.y + v3.y * mat.v2.z,
				   		v1.z * mat.v2.x + v2.z * mat.v2.y + v3.z * mat.v2.z ),
			new vec3f(	v1.x * mat.v3.x + v2.x * mat.v3.y + v3.x * mat.v3.z,
			   			v1.y * mat.v3.x + v2.y * mat.v3.y + v3.y * mat.v3.z,
			   			v1.z * mat.v3.x + v2.z * mat.v3.y + v3.z * mat.v3.z )
		);
	}
	
	public vec3f intersection( Line line )
	{
		vec3f intr = toPlane().intersection( line );
		
		if( intr != null && intr != vec3f.UNDEFINED &&
				intr.y >= 0.0f && intr.z >= 0.0f && intr.y + intr.z <= 1.0f )
			return line.getPoint( intr.x );
		return null;
	}
	
	public vec3f intersection( mat2x3 edge )
	{
		Line line = edge.toLine();
		vec3f intr = toPlane().intersection( line );
		
		if( intr != null && intr != vec3f.UNDEFINED && intr.x >= 0.0f && intr.x <= 1.0f &&
				intr.y >= 0.0f && intr.z >= 0.0f && intr.y + intr.z <= 1.0f )
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
		
		return t >= 0.0f && t <= 1.0f && u >= 0.0f && v >= 0.0f && u + v <= 1.0f;
	}
	
	public boolean intersects( mat3x3 triangle )
	{
		boolean o0 = triangle.v1 == v1 || triangle.v1 == v2 || triangle.v1 == v3;
		boolean o1 = triangle.v2 == v1 || triangle.v2 == v2 || triangle.v2 == v3;
		boolean o2 = triangle.v3 == v1 || triangle.v3 == v2 || triangle.v3 == v3;
		boolean e0Int = !o0 && !o1 && intersects( new mat2x3( triangle.v1, triangle.v2 ) );
		boolean e1Int = !o0 && !o2 && intersects( new mat2x3( triangle.v1, triangle.v3 ) );
		boolean e2Int = !o1 && !o2 && intersects( new mat2x3( triangle.v2, triangle.v3 ) );
		return e0Int || e1Int || e2Int;
	}
	
	public float area()
	{
		return normal().length() * 0.5f;
	}
	
	public float perimeter()
	{
		return vec3f.sub( v2, v1 ).length() + vec3f.sub( v2, v3 ).length() + vec3f.sub( v3, v1 ).length();
	}
	
	public vec3f normal()
	{
		return vec3f.sub( v2, v1 ).cross( vec3f.sub( v3, v1 ) );
	}
	
	public boolean parallelTo( mat3x3 triangle )
	{
		vec3f n0 = normal();
		vec3f n1 = triangle.normal();
		return Math.abs( n0.length() * n1.length() ) == Math.abs( n0.dot( n1 ) );
	}
	
	public boolean equals( Object o )
	{
		mat3x3 t = (mat3x3)o;
		
		return  (v1 == t.v1 || v1 == t.v2 || v1 == t.v3) && 
				(v2 == t.v1 || v2 == t.v2 || v2 == t.v3) &&
				(v3 == t.v1 || v3 == t.v2 || v3 == t.v3);
	}
	
	public String toString()
	{
		return super.toString() + "\n" + v3.toString();
	}
	
	public static vec3f intersection( mat3x3 t1, mat3x3 t2, mat3x3 t3 )
	{
		vec3f n1 = t1.normal();
		vec3f n2 = t2.normal();
		vec3f n3 = t3.normal();
		
		float det = n1.x * (n2.y * n3.z - n2.z * n3.y)
				  + n1.y * (n2.x * n3.z - n2.z * n3.x)
				  + n1.z * (n2.x * n3.y - n2.y * n3.x);
		
		if( det == 0.0f ) return null;
		
		vec3f v1 = n2.cross( n3 );
		vec3f v2 = n3.cross( n1 );
		vec3f v3 = n1.cross( n2 );
		
		v1.mul( t1.v1.dot( n1 ) );
		v2.mul( t2.v1.dot( n2 ) );
		v3.mul( t3.v1.dot( n3 ) );
		
		v1.add( v2 );
		v1.add( v3 );
		v1.mul( 1.0f / det );
		return v1;
	}
	
	public static void main( String[] args )
	{
		/*
		vec3f v1 = new vec3f( 10.0f, 10.0f, 100.0f );
		vec3f v2 = new vec3f( 10.0f, 0.0f, 100.0f );
		vec3f v3 = new vec3f( 0.0f, 10.0f, 100.0f );
		mat3x3 t0 = new mat3x3( v3, v2, v1 );
		
		vec3f v4 = new vec3f( 10.0f, 0.0f, 10.0f );
		vec3f v5 = new vec3f( 10.0f, 0.0f, 0.0f );
		vec3f v6 = new vec3f( 0.0f, 0.0f, 10.0f );
		mat3x3 t1 = new mat3x3( v4, v5, v6 );
		
		vec3f v7 = new vec3f( 0.0f, 10.0f, 10.0f );
		vec3f v8 = new vec3f( 0.0f, 10.0f, 0.0f );
		vec3f v9 = new vec3f( 0.0f, 0.0f, 10.0f );
		mat3x3 t2 = new mat3x3( v7, v8, v9 );
		
		System.out.println( intersection( t0, t1, t2 ) );*/
		
		mat3x3 m0 = new mat3x3( 
				0.0f, 0.0f, 0.0f,
				0.0f, 2.0f, 0.0f,
				2.0f, 0.0f, 0.0f
		);
		
		mat3x3 m1 = new mat3x3( 
				1.0f, 0.0f, 0.0f,
				0.0f, 2.0f, 0.0f,
				0.0f, 0.0f, 1.0f
		);
		
		System.out.println( m0 + "\n" );
		System.out.println( m1 + "\n" );
		System.out.println( m0.product( m1 ) );
	}
}
