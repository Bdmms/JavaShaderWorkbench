package swb;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL3;

import swb.importer.Importer;
import swb.math.Line;
import swb.math.Mesh;
import swb.math.mat3x3;
import swb.math.mat4x3;
import swb.math.vec2f;
import swb.math.vec3f;

public class ModelUtils 
{
	public final static int NO_WRAP 	 = 0x00;
	public final static int WRAP_S  	 = 0x01;
	public final static int WRAP_T  	 = 0x02;
	public final static int WRAP_ST 	 = 0x03;
	public final static int WRAP_CORNER  = 0x04;
	public final static int WRAP_ALL  	 = 0x07;
	
	public static List<mat4x3> extrude( List<vec2f> map, byte mapType )
	{
		List<mat4x3> mesh = new ArrayList<>();
		
		vec3f lastHigh = vec3f.map( map.get( 0 ), 1.0f, mapType );
		vec3f lastLow = vec3f.map( map.get( 0 ), -1.0f, mapType );
		for( int i = 1; i < map.size(); i++)
		{
			vec3f high = vec3f.map( map.get( i ), 1.0f, mapType );
			vec3f low = vec3f.map( map.get( i ), -1.0f, mapType );
			mesh.add( new mat4x3( lastLow, lastHigh, low, high ) );
			lastHigh = high;
			lastLow = low;
		}
		
		return mesh;
	}
	
	public static GLNode createPlane( int w, int h )
	{
		int[] iBuffer = generateSurfaceElements( w, h, NO_WRAP );
		
		int size = w * h;
		float[] vBuffer = new float[ size * 8 ];
		for( int i = 0, vIdx = 0; i < size; i++ )
		{
			vBuffer[vIdx++] = (float)(i % w) / (w - 1) - 0.5f;
			vBuffer[vIdx++] = (float)(i / w) / (h - 1) - 0.5f;
			vBuffer[vIdx++] = 0.0f;
			vBuffer[vIdx++] = 0.0f;
			vBuffer[vIdx++] = 0.0f;
			vBuffer[vIdx++] = 1.0f;
			vBuffer[vIdx++] = 0.0f;
			vBuffer[vIdx++] = 0.0f;
		}
		
		return createFrom( "Plane", vBuffer, iBuffer );
	}
	
	public static GLNode createSphere( double dAngle )
	{
		List<vec3f> vertices = new ArrayList<>();
		vertices.add( new vec3f( 0.0f, 0.0f, -1.0f ) );
		vertices.add( new vec3f( 0.0f, 0.0f, 1.0f ) );
		
		double halfPI = Math.PI / 2.0;
		double PI2 = Math.PI * 2.0;
		for( double theta = 0.0f; theta < PI2; theta += dAngle )
		{
			for( double phi = -halfPI + dAngle; phi < halfPI; phi += dAngle )
			{
				float cosPhi = (float)Math.cos( phi );
				float x = cosPhi * (float)Math.cos( theta );
				float y = cosPhi * (float)Math.sin( theta );
				float z = (float)Math.sin( phi );
				vertices.add( new vec3f( x, y, z ) );
			}
		}
		
		return createFrom( new Mesh( vertices ) );
	}
	
	public static GLNode createCube()
	{
		return createFrom( "Cube", new float[] {
				-1.0f, -1.0f, -1.0f,	-1.0f, -1.0f, -1.0f, 	0.0f, 0.0f,
				1.0f, -1.0f, -1.0f,		1.0f, -1.0f, -1.0f,		0.0f, 0.0f,
				-1.0f, 1.0f, -1.0f,		-1.0f, 1.0f, -1.0f, 	0.0f, 0.0f,
				1.0f, 1.0f, -1.0f,		1.0f, 1.0f, -1.0f, 		0.0f, 0.0f,
				-1.0f, -1.0f, 1.0f,		-1.0f, -1.0f, 1.0f, 	0.0f, 0.0f,
				1.0f, -1.0f, 1.0f,		1.0f, -1.0f, 1.0f, 		0.0f, 0.0f,
				-1.0f, 1.0f, 1.0f,		-1.0f, 1.0f, 1.0f, 		0.0f, 0.0f,
				1.0f, 1.0f, 1.0f,		1.0f, 1.0f, 1.0f, 		0.0f, 0.0f
			}, new int[] {
				0, 1, 2, 	1, 2, 3,
				4, 5, 6, 	5, 6, 7,
				0, 1, 4, 	1, 4, 5,
				0, 2, 4, 	2, 4, 6,
				3, 1, 7,	1, 7, 5,
				3, 2, 7,	2, 7, 6
			});
	}
	
	public static GLNode createSurface( int w, int h, int wrapMsk, SurfaceFunction surface )
	{
		int size = w * h;
		int[] iBuffer = generateSurfaceElements( w, h, wrapMsk );
		
		// Generate vertices
		vec3f[] vertices = new vec3f[size];
		float wf = w - 1;
		float hf = h - 1;
		
		for( int i = 0; i < size; i++ )
		{
			float x = (float)( i % w ) / wf;
			float y = (float)( i / w ) / hf;
			vertices[i] = surface.surfaceAt( x, y );
		}
		
		// Prepare normals
		vec3f[] normals = new vec3f[size];
		int[] nCounts = new int[size];
		for( int i = 0; i <  normals.length; i++ )
			normals[i] = new vec3f();
		
		// Sum the normals per face
		for( int e = 0; e < iBuffer.length; )
		{
			int i0 = iBuffer[e++];
			int i1 = iBuffer[e++];
			int i2 = iBuffer[e++];
			vec3f vs = vec3f.sub( vertices[i1], vertices[i0] );
			vec3f vt = vec3f.sub( vertices[i2], vertices[i0] );
			vec3f vn = vs.cross( vt );
			vn.normalize();
			normals[i0].add( vn );
			normals[i1].add( vn );
			normals[i2].add( vn );
			nCounts[i0]++;
			nCounts[i1]++;
			nCounts[i2]++;
		}
		
		// Scale the normal average
		for( int i = 0; i < normals.length; i++ )
			normals[i].div( nCounts[i] );
		
		// Convert vertices to buffer
		float[] vBuffer = new float[ w * h * 8 ];
		for( int i = 0, vIdx = 0; i < vertices.length; i++ )
		{
			vec3f v = vertices[i];
			vec3f vn = normals[i];
			vBuffer[vIdx++] = v.x;
			vBuffer[vIdx++] = v.y;
			vBuffer[vIdx++] = v.z;
			vBuffer[vIdx++] = vn.x;
			vBuffer[vIdx++] = vn.y;
			vBuffer[vIdx++] = vn.z;
			vBuffer[vIdx++] = 0.0f;
			vBuffer[vIdx++] = 0.0f;
		}
		
		return createFrom( "Cube", vBuffer, iBuffer );
	}
	
	private static int generateQuadElements( int[] buffer, int idx, int i0, int i1, int i2, int i3 )
	{
		buffer[idx++] = i2;
		buffer[idx++] = i0;
		buffer[idx++] = i1;
		buffer[idx++] = i1;
		buffer[idx++] = i3;
		buffer[idx++] = i2;
		return idx;
	}
	
	public static int[] generateSurfaceElements( int w, int h, int wrapMsk )
	{
		int size = 6 * (w - 1) * (h - 1);
		if( (wrapMsk & WRAP_T) != 0 ) size += 6 * (h - 1);
		if( (wrapMsk & WRAP_S) != 0 ) size += 6 * (w - 1);
		if( (wrapMsk & WRAP_CORNER) != 0 ) size += 6;
		
		int[] iBuffer = new int[size];
		int idx = 0;
		
		for( int y = 1; y < h; y++ )
		{
			for( int x = 1; x < w; x++ )
			{
				// Generate the Quads
				int i0 = (x - 1) + (y - 1) * w;
				int i1 = i0 + 1;
				idx = generateQuadElements( iBuffer, idx, i0, i1, i0 + w, i1 + w );
			}
			
			if( (wrapMsk & WRAP_T) != 0 )
			{
				// Generate the Quads that wrap around the y-axis
				int i0 = w - 1 + (y - 1) * w;
				int i1 = (y - 1) * w;
				idx = generateQuadElements( iBuffer, idx, i0, i1, i0 + w, i1 + w );
			}
		}
		
		if( (wrapMsk & WRAP_S) != 0 )
		{
			// Generate the Quads that wrap around the x-axis
			for( int x = 1; x < w; x++ )
			{
				int i0 = (x - 1) + (h - 1) * w;
				int i2 = (x - 1);
				idx = generateQuadElements( iBuffer, idx, i0, i0 + 1, i2, i2 + 1 );
			}
		}
		
		if( (wrapMsk & WRAP_CORNER) != 0 )
		{
			// Generate last Quad
			int i1 = w - 1;
			int i2 = w * (h - 1);
			idx = generateQuadElements( iBuffer, idx, 0, i1, i2, i1 + i2 );
		}
		return iBuffer;
	}
	
	public static GLNode createFromOrthographicView( List<vec2f> xmap, List<vec2f> zmap )
	{
		List<mat4x3> ext1 = extrude( xmap, vec2f.YZPLANE );
		List<mat4x3> ext0 = extrude( zmap, vec2f.XYPLANE );
		List<vec3f> vertices = new ArrayList<>();
		
		// X on Z
		for( int i = 1; i < ext1.size(); i++ )
		{
			vec3f low = ext1.get( i ).v3;
			vec3f high = ext1.get( i ).v4;
			Line line = new Line( low, vec3f.sub( high, low ) );
			
			for( mat4x3 face : ext0 )
			{
				vec3f intr = face.intersection( line );
				if( intr != null ) vertices.add( intr );
			}
		}
		
		// Z on X
		for( int i = 1; i < ext0.size(); i++ )
		{
			vec3f low = ext0.get( i ).v3;
			vec3f high = ext0.get( i ).v4;
			Line line = new Line( low, vec3f.sub( high, low ) );
			
			for( mat4x3 face : ext1 )
			{
				vec3f intr = face.intersection( line );
				if( intr != null ) vertices.add( intr );
			}
		}
		
		return createFrom( new Mesh( vertices ) );
	}
	
	public static GLNode createFromOrthographicView( List<vec2f> xmap, List<vec2f> ymap, List<vec2f> zmap )
	{
		// Apply the Z-Map to the mesh
		System.out.println( "--- Z-Map ---" );
		List<vec3f> vertices = new ArrayList<>();
		for( vec2f vertex : zmap )
		{
			vertices.add( new vec3f( vertex.x, vertex.y, -1.0f ) ); 
			vertices.add( new vec3f( vertex.x, vertex.y, 1.0f ) );
		}
		Mesh mesh0 = new Mesh( vertices );
		
		// Apply the Y-Map to the mesh
		System.out.println( "--- Y-Map ---" );
		vertices = new ArrayList<>();
		for( vec2f vertex : ymap )
		{
			vec3f vlow = new vec3f( vertex.x, -1.0f, vertex.y ); 
			vec3f vhigh = new vec3f( vertex.x, 1.0f, vertex.y );
			Line line = new Line( vlow, vec3f.sub( vhigh, vlow ) );
			
			for( mat3x3 triangle : mesh0.triangles )
			{
				vec3f vInt = triangle.intersection( line );
				if( vInt != null ) vertices.add( vInt );
			}
		}
		Mesh mesh1 = new Mesh( vertices );
		
		// Apply the X-Map to the mesh
		System.out.println( "--- X-Map ---" );
		vertices = new ArrayList<>();
		for( vec2f vertex : xmap )
		{
			vec3f vlow = new vec3f( -1.0f, vertex.x, vertex.y ); 
			vec3f vhigh = new vec3f( 1.0f, vertex.x, vertex.y );
			Line line = new Line( vlow, vec3f.sub( vhigh, vlow ) );
			
			for( mat3x3 triangle : mesh1.triangles )
			{
				vec3f vInt = triangle.intersection( line );
				if( vInt != null ) vertices.add( vInt );
			}
		}
		Mesh mesh2 = new Mesh( vertices );
		
		return createFrom( mesh2 );
	}
	
	public static GLNode join( List<vec2f> xmap, List<vec2f> ymap, List<vec2f> zmap )
	{
		List<vec3f> vertices = new ArrayList<>();
		for( vec2f v : xmap ) vertices.add( vec3f.map( v, 0.0f, vec2f.YZPLANE ) );
		for( vec2f v : ymap ) vertices.add( vec3f.map( v, 0.0f, vec2f.XZPLANE ) );
		for( vec2f v : zmap ) vertices.add( vec3f.map( v, 0.0f, vec2f.XYPLANE ) );
		return createFrom( new Mesh( vertices ) );
	}
	
	public static GLNode createFrom( Mesh mesh )
	{
		float[] vBuffer = new float[ mesh.vertices.size() * 8 ];
		int[] eBuffer = new int[ mesh.triangles.size() * 3 ];
		int idx = 0;
		
		for( mat3x3 triangle : mesh.triangles )
		{
			eBuffer[idx++] = mesh.vertices.indexOf( triangle.v1 );
			eBuffer[idx++] = mesh.vertices.indexOf( triangle.v2 );
			eBuffer[idx++] = mesh.vertices.indexOf( triangle.v3 );
		}
		
		idx = 0;
		for( vec3f vertex : mesh.vertices )
		{
			vBuffer[idx++] = vertex.x;
			vBuffer[idx++] = vertex.y;
			vBuffer[idx++] = vertex.z;
			vBuffer[idx++] = 0.0f;
			vBuffer[idx++] = 0.0f;
			vBuffer[idx++] = 0.0f;
			vBuffer[idx++] = 0.0f;
			vBuffer[idx++] = 0.0f;
		}
		
		return createFrom( "generated_mesh", vBuffer, eBuffer );
	}
			
	public static GLNode createFrom( String name, float[] vBuffer, int[] eBuffer )
	{
		GLNode model = new GLNode( name );
		
		Material material = new Material( "default" );
		material.addTexture( ITexture.loadTexture( Material.DEFAULT_DIF ), GL3.GL_TEXTURE0 );
		
		UniformList uniforms = new UniformList( "uniforms" );
		uniforms.add( "dif_texture", GLDataType.SAMP2D, "0" );
		uniforms.add( "nrm_texture", GLDataType.SAMP2D, "1" );
		
		model.add( ShaderProgram.generateProgram( "template" ) );
		model.add( uniforms );
		model.add( new VertexBuffer( vBuffer, 8 ) );
		model.add( material );
		model.add( new ElementBuffer( eBuffer ) );
		
		return model;
	}
	
	public static String fileToString( File file )
	{
		try 
		{
			byte[] data = new byte[(int)file.length()];
			FileInputStream stream = new FileInputStream( file );
			stream.read( data );
			stream.close();
			return new String( data );
		} 
		catch (IOException e) 
		{
			System.err.println( "Error - Cannot read " + file.getPath() );
			return "";
		}
	}
	
	public static GLNode read( File file ) throws IOException
	{
		String filename = file.getName();
		String ext = filename.substring( filename.lastIndexOf( '.' ) + 1 ).toUpperCase();
		
		Importer importer = Importer.getImporter( ext );
		return importer == null ? null : importer.read( file );
	}
	
	@FunctionalInterface
	public static interface SurfaceFunction
	{
		public vec3f surfaceAt( float s, float t );
	}
}
