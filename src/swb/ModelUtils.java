package swb;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.jogamp.opengl.GL3;

import swb.importer.Importer;
import swb.math.vec3f;

public class ModelUtils 
{
	public final static int NO_WRAP 	 = 0x00;
	public final static int WRAP_S  	 = 0x01;
	public final static int WRAP_T  	 = 0x02;
	public final static int WRAP_ST 	 = 0x03;
	public final static int WRAP_CORNER  = 0x04;
	public final static int WRAP_ALL  	 = 0x07;
	
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
		
		return createMesh( "Plane", vBuffer, iBuffer );
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
			
			vec3f vs = vertices[i1].from( vertices[i0] );
			vec3f vt = vertices[i2].from( vertices[i0] );
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
			v.copyTo( vBuffer, vIdx );
			vn.copyTo( vBuffer, vIdx + 3 );
			vIdx += 8;
		}
		
		return createMesh( "Cube", vBuffer, iBuffer );
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
			
	public static GLNode createMesh( String name, float[] vBuffer, int[] eBuffer )
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
