package math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

public class Mesh 
{
	public List<vec3f> vertices = new ArrayList<>();
	public List<vec3i> indices = new ArrayList<>();
	public List<mat2x3> edges = new ArrayList<>();
	public List<mat3x3> triangles = new ArrayList<>();
	
	public Mesh()
	{
		
	}
	
	public Mesh( List<vec3f> vertices )
	{
		this.vertices = vertices;
		complete();
	}
	
	@Deprecated
	public void completeV2()
	{
		// clean vertices list
		cleanList( vertices, (v1, v2) -> v1.equals( v2 ) );
		System.out.println( vertices.size() + " vertices");
		
		for( vec3f vertex : vertices )
		{
			float[] segments = new float[ vertices.size() - 1 ];
			int i = 0;
			for( vec3f v : vertices )
				if( vertex != v )
					segments[i++] = vec3f.sub( vertex, v ).length();
			
			float avg = percentile( segments, 0.0075f );
			
			i = 0;
			for( vec3f v : vertices )
				if( v != vertex && segments[i++] < avg )
					edges.add( new mat2x3( vertex, v ) );
		}
		
		System.out.println( edges.size() + " edges created" );
		
		List<vec3f> ec = new ArrayList<>(6);
		for( mat2x3 e1 : edges )
		{
			for( mat2x3 e2 : edges )
			{
				if( e1 == e2 || (e1.v1 != e2.v1 && e1.v1 != e2.v1)  ) continue;
				
				for( mat2x3 e3 : edges )
				{
					if( e1 == e3 || e2 == e3 ) continue;
					
					ec.clear();
					ec.add( e1.v1 );
					ec.add( e1.v2 );
					if( !ec.contains( e2.v1 ) ) ec.add( e2.v1 );
					if( !ec.contains( e2.v2 ) ) ec.add( e2.v2 );
					if( !ec.contains( e3.v1 ) ) ec.add( e3.v1 );
					if( !ec.contains( e3.v2 ) ) ec.add( e3.v2 );
					
					if( ec.size() == 3 )
					{
						triangles.add( new mat3x3( ec.get( 0 ), ec.get( 1 ), ec.get( 2 ) ) );
					}
				}
			}
		}
		
		System.out.println( triangles.size() + " triangles created" );
	}
	
	private void complete()
	{
		triangles.clear();
		
		// clean vertices list
		cleanList( vertices, (v1, v2) -> v1.equals( v2 ) );
		System.out.println( vertices.size() + " vertices");
		
		vec3f centre = vec3f.average( vertices );
		
		float MAX_LENGTH = 1.44f;
		
		int v1Size = vertices.size() - 2;
		int v2Size = vertices.size() - 1;
		int v3Size = vertices.size();
		for( int i0 = 0; i0 < v1Size; i0++ )
		{
			vec3f v0 = vertices.get( i0 );
			for( int i1 = i0 + 1; i1 < v2Size; i1++ )
			{
				vec3f v1 = vertices.get( i1 );
				vec3f vec0 = vec3f.sub( v1, v0 );
				
				if( vec0.length() > MAX_LENGTH ) continue;
				
				for( int i2 = i1 + 1; i2 < v3Size; i2++ )
				{
					vec3f v2 = vertices.get( i2 );
					vec3f vec1 = vec3f.sub( v2, v0 );
					vec3f vec2 = vec3f.sub( v2, v1 );
					
					if( vec1.length() > MAX_LENGTH || vec2.length() > MAX_LENGTH ) continue;
					
					mat3x3 triangle = new mat3x3( v0, v1, v2 );
					vec3f outward = vec3f.sub( vec3f.average( v0, v1, v2 ), centre );
					vec3f normal = triangle.normal();
					float area = triangle.area();

					if( area == 0.0f || Math.abs( outward.dot( normal ) ) > 0.5f ) continue;
					
					boolean intersects = false;
					for( mat3x3 t : triangles )
					{
						if( triangle.intersects( t ) )
						{
							intersects = true;
							break;
						}
					}
					
					if( !intersects )
						triangles.add( triangle );
				}
			}
		}
		
		System.out.println( triangles.size() + " triangles created");
	}
	
	private static float percentile( float[] arr, float percentile )
	{
		arr = arr.clone();
		Arrays.sort( arr );
		return arr[ (int)Math.ceil(percentile * arr.length) ];
	}
	
	public static <E> void cleanList( List<E> list, BiFunction<E,E,Boolean> filter )
	{
		for( int i = 0; i < list.size(); i++ )
		{
			E e0 = list.get( i );
			for( E e1 : list )
			{
				if( e0 == e1 ) continue;
				
				if( filter.apply( e0, e1 ) )
				{
					list.remove( e0 );
					i--;
					break;
				}
			}
		}
	}
}
