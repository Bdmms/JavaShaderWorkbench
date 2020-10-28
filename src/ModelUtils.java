import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import math.vec3i;
import math.mat3x2;
import math.Line;
import math.Mesh;
import math.mat3x3;
import math.mat3x4;
import math.vec2f;
import math.vec3f;

public class ModelUtils 
{
	private static class Group
	{
		public String name;
		public Material material = null;
		public List<vec3i[]> faces = new ArrayList<>();
		
		public Group( String name )
		{
			this.name = name;
		}
	}
	
	public static List<mat3x4> extrude( List<vec2f> map, byte mapType )
	{
		List<mat3x4> mesh = new ArrayList<>();
		
		vec3f lastHigh = vec3f.map( map.get( 0 ), 1.0f, mapType );
		vec3f lastLow = vec3f.map( map.get( 0 ), -1.0f, mapType );
		for( int i = 1; i < map.size(); i++)
		{
			vec3f high = vec3f.map( map.get( i ), 1.0f, mapType );
			vec3f low = vec3f.map( map.get( i ), -1.0f, mapType );
			mesh.add( new mat3x4( lastLow, lastHigh, low, high ) );
			lastHigh = high;
			lastLow = low;
		}
		
		return mesh;
	}
	
	public static List<vec2f> createCircle( double dTheta )
	{
		List<vec2f> mesh = new ArrayList<vec2f>();
		
		double PI2 = Math.PI * 2.0;
		for( double theta = 0.0; theta < PI2; theta += dTheta )
		{
			mesh.add( new vec2f( (float)Math.cos( theta ), (float)Math.sin( theta ) ) );
		}
		
		return mesh;
	}
	
	public static Model createSphere( double dAngle )
	{
		Mesh mesh = new Mesh();
		
		mesh.vertices.add( new vec3f( 0.0f, 0.0f, -1.0f ) );
		mesh.vertices.add( new vec3f( 0.0f, 0.0f, 1.0f ) );
		
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
				mesh.vertices.add( new vec3f( x, y, z ) );
			}
		}
		
		mesh.completeV2();
		return createFrom( mesh );
	}
	
	public static Model createCube()
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
	
	public static Model createFromOrthographicView2( List<vec2f> xmap, List<vec2f> ymap, List<vec2f> zmap )
	{
		return createFrom( new Mesh( 
				extrude( xmap, vec2f.YZPLANE ), 
				extrude( xmap, vec2f.XZPLANE ), 
				extrude( xmap, vec2f.XYPLANE ) 
		) );
	}
	
	public static Model createFromOrthographicView( List<vec2f> xmap, List<vec2f> ymap, List<vec2f> zmap )
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
	
	public static Model createFrom( Mesh mesh )
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
			
	public static Model createFrom( String name, float[] vBuffer, int[] eBuffer )
	{
		Model model = new Model( name );
		
		model.add( new VertexBuffer( vBuffer, 8 ) );
		
		Material material = new Material( "default" );
		material.add( Material.getTexture( Material.DEFAULT ), GL3.GL_TEXTURE0 );
		
		BodyGroup mesh = new BodyGroup( "mesh" );
		mesh.add( new ElementBuffer( eBuffer ) );
		mesh.add( material );
		model.add( mesh );
		model.update();
		
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
			return "";
		}
	}
	
	public static Model readObjFileDirect( File file ) throws IOException
	{
		BufferedReader reader = new BufferedReader( new FileReader( file ) );
		
		String dir = file.getAbsolutePath().substring( 0, file.getAbsolutePath().lastIndexOf( '\\' ) ) + "\\";
		Model model = new Model( file.getName() );
		
		List<vec3f> position = new ArrayList<>();
		List<vec3f> normals = new ArrayList<>();
		List<vec3f> textCoord = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();
		BodyGroup current = null;
		
		HashMap<String, Material> materialList = null;
		
		Iterator<String> iterator = reader.lines().iterator();
		while( iterator.hasNext() )
		{
			String line = iterator.next();
			String[] parts = line.split( "\\s*( |\t)\\s*" );
			
			switch( parts[0] )
			{
			case "g":
				if( parts.length > 1 )
				{
					if( current != null )
					{
						current.add( new ElementBuffer( indices.stream().mapToInt( i -> i ).toArray() ) );
						model.add( current );
						indices.clear();
					}
					
					current = new BodyGroup( line.substring( line.indexOf( ' ' ) + 1 ) );
				}
				break;
			
			case "v":  	position.add( new vec3f( parts ) ); break;
			case "vn": 	normals.add( new vec3f( parts ) ); break;
			case "vt": 	textCoord.add( new vec3f( parts ) ); break;
				
			case "f": 
				indices.add( Integer.parseInt( parts[1].substring( 0, parts[1].indexOf( '/' ) ) ) - 1 );
				indices.add( Integer.parseInt( parts[2].substring( 0, parts[2].indexOf( '/' ) ) ) - 1 );
				indices.add( Integer.parseInt( parts[3].substring( 0, parts[3].indexOf( '/' ) ) ) - 1 );
				break;
				
			case "mtllib":
				materialList = Material.loadMtlFile( new File( dir + parts[1] ) );
				break;
				
			case "usemtl":
				if( materialList == null )
				{
					String filename = line.substring( line.lastIndexOf( '\\' ) + 1 );
					String name = filename.substring( 0, filename.lastIndexOf( '.' ) );
					Texture texture = Material.loadTexture( new File( dir + filename ) );
					current.add( new Material( name, texture, GL.GL_TEXTURE0 ) );
				}
				else
				{
					current.add( materialList.get( parts[1] ) );
				}
				break;
				
			default: break;
			}
		}
		
		// Add remaining objects
		if( current != null )
		{
			current.add( new ElementBuffer( indices.stream().mapToInt( i -> i ).toArray() ) );
			model.add( current );
			indices.clear();
		}
		
		float[] buffer = new float[position.size() * 8];
		int idx = 0;
		
		for( int i = 0; i < position.size(); i++ )
		{
			vec3f pos = position.get( i );
			vec3f nrm = normals.get( i );
			vec3f txt = textCoord.get( i );
			buffer[idx++] = pos.x;
			buffer[idx++] = pos.y;
			buffer[idx++] = pos.z;
			buffer[idx++] = nrm.x;
			buffer[idx++] = nrm.y;
			buffer[idx++] = nrm.z;
			buffer[idx++] = txt.x;
			buffer[idx++] = txt.y;
		}
		
		model.add( new VertexBuffer( buffer, 8 ) );
		model.update();
		
		reader.close();
		
		return model;
	}
	
	public static Model readObjFile( File file ) throws IOException
	{
		BufferedReader reader = new BufferedReader( new FileReader( file ) );
		
		String dir = file.getAbsolutePath().substring( 0, file.getAbsolutePath().lastIndexOf( '\\' ) ) + "\\";
		Model model = new Model( file.getName() );
		
		List<vec3f> position = new ArrayList<>();
		List<vec3f> normals = new ArrayList<>();
		List<vec3f> textCoord = new ArrayList<>();
		List<Group> groups = new ArrayList<>();
		Group current = null;
		
		HashMap<String, Material> materialList = null;
		
		Iterator<String> iterator = reader.lines().iterator();
		while( iterator.hasNext() )
		{
			String line = iterator.next();
			String[] parts = line.split( "\\s*( |\t)\\s*" );
			
			switch( parts[0] )
			{
			case "g":
				if( parts.length > 1 )
				{
					current = new Group( line.substring( line.indexOf( ' ' ) + 1 ) );
					groups.add( current );
				}
				break;
			
			case "v":  		position.add( new vec3f( parts ) ); break;
			case "vn": 		normals.add( new vec3f( parts ) ); break;
			case "vt": 		textCoord.add( new vec3f( parts ) ); break;
			
			case "f":  		
				vec3i[] face = new vec3i[3];
				face[0] = new vec3i( parts[1] );
				face[1] = new vec3i( parts[2] );
				face[2] = new vec3i( parts[3] );
				current.faces.add( face ); break;
				
			case "mtllib": 	materialList = Material.loadMtlFile( new File( dir + parts[1] ) ); break;
				
			case "usemtl":
				if( materialList == null )
				{
					String filename = line.substring( line.lastIndexOf( '\\' ) + 1 );
					String name = filename.substring( 0, filename.lastIndexOf( '.' ) );
					Texture texture = Material.loadTexture( new File( dir + filename ) );
					current.material = new Material( name, texture, GL.GL_TEXTURE0 );
				}
				else
				{
					current.material = materialList.get( parts[1] );
				}
				break;
				
			default: break;
			}
		}
		
		int vSize = groups.stream().mapToInt( g -> g.faces.size() * 3 ).sum();
		float[] buffer = new float[ vSize * 8 ];
		int vIdx = 0;
		
		for( Group group : groups )
		{
			if( group.faces.size() == 0 ) continue;
			
			int[] indices = new int[ group.faces.size() * 3 ];
			int eIdx = 0;
			
			for( vec3i[] face : group.faces )
			{
				for( vec3i vec : face )
				{
					vec3f pos = position.get( vec.x );
					vec3f txt = textCoord.get( vec.y );
					vec3f nrm = normals.get( vec.z );
					
					buffer[vIdx++] = pos.x;
					buffer[vIdx++] = pos.y;
					buffer[vIdx++] = pos.z;
					buffer[vIdx++] = nrm.x;
					buffer[vIdx++] = nrm.y;
					buffer[vIdx++] = nrm.z;
					buffer[vIdx++] = txt.x;
					buffer[vIdx++] = txt.y;
				}
				
				indices[eIdx] = eIdx;
				indices[eIdx + 1] = eIdx + 1;
				indices[eIdx + 2] = eIdx + 2;
				eIdx += 3;
			}
			
			BodyGroup mesh = new BodyGroup( group.name );
			mesh.add( new ElementBuffer( indices ) );
			mesh.add( group.material );
			model.add( mesh );
		}
		
		model.add( new VertexBuffer( buffer, 8 ) );
		model.update();
		
		reader.close();
		
		return model;
	}
}
