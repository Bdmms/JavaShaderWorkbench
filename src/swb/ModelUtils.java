package swb;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import swb.math.vec3i;
import swb.math.Line;
import swb.math.Mesh;
import swb.math.mat3x3;
import swb.math.mat4x3;
import swb.math.vec2f;
import swb.math.vec3f;

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
		material.add( Material.loadTexture( Material.DEFAULT_DIF ), GL3.GL_TEXTURE0 );
		
		GLNode mesh = new GLNode( "mesh" );
		mesh.add( new ElementBuffer( eBuffer ) );
		mesh.add( material );
		
		model.add( new VertexBuffer( vBuffer, 8 ) );
		model.add( mesh );
		
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
	
	public static GLNode read( File file ) throws IOException, ParserConfigurationException, SAXException
	{
		String filename = file.getName();
		String ext = filename.substring( filename.lastIndexOf( '.' ) + 1 ).toUpperCase();
		
		switch( ext )
		{
		case "OBJ": return readOBJ( file );
		case "DAE": return readDAE( file );
		default: return null;
		}
	}
	
	public static GLNode readFBX( File file ) throws IOException
	{
		return null;
	}
	
	public static GLNode readDAE( File file ) throws IOException, ParserConfigurationException, SAXException
	{
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse( file );
        doc.getDocumentElement().normalize();
        
        
        
		return null;
	}
	
	public static GLNode readOBJ( File file ) throws IOException
	{
		BufferedReader reader = new BufferedReader( new FileReader( file ) );
		
		String dir = file.getAbsolutePath().substring( 0, file.getAbsolutePath().lastIndexOf( '\\' ) ) + "\\";
		GLNode model = new GLNode( file.getName() );
		
		List<vec3f> position = new ArrayList<>();
		List<vec3f> normals = new ArrayList<>();
		List<vec3f> textCoord = new ArrayList<>();
		List<Group> groups = new ArrayList<>();
		List<GLNode> meshes = new ArrayList<>();
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
					System.out.println(current.name);
					groups.add( current );
				}
				break;
			
			case "v":  		
			{
				vec3f pos = new vec3f( parts );
				pos.mul( 0.1f );
				position.add( pos ); 
				break;
			}
			case "vn": 		normals.add( new vec3f( parts ) ); break;
			case "vt": 		textCoord.add( new vec3f( parts ) ); break;
			
			case "f":  		
				vec3i[] face = new vec3i[3];
				face[0] = new vec3i( parts[1] );
				face[1] = new vec3i( parts[2] );
				face[2] = new vec3i( parts[3] );
				current.faces.add( face ); break;
				
			case "mtllib": 	
				materialList = Material.loadMtlFile( new File( dir + parts[1] ) ); 
				break;
				
			case "usemtl":
				if( materialList == null )
				{
					String filename = line.substring( line.lastIndexOf( '\\' ) + 1 );
					String name = filename.substring( 0, filename.lastIndexOf( '.' ) );
					Texture texture = Material.loadTexture( dir + filename );
					current.material = new Material( name, texture, GL.GL_TEXTURE0 );
					current.material.add( Material.loadTexture( Material.DEFAULT_NRM ), GL.GL_TEXTURE1 );
				}
				else
				{
					current.material = materialList.get( parts[1] );
				}
				break;
				
			default: break;
			}
		}
		
		// Compile the OBJ file into the buffers
		int vSize = groups.stream().mapToInt( g -> g.faces.size() * 3 ).sum();
		float[] buffer = new float[ vSize * 8 ];
		int vIdx = 0;
		int gIdx = 0;
		
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
					indices[eIdx++] = gIdx++;
				}
			}
			
			GLNode mesh = new GLNode( group.name );
			mesh.add( group.material );
			mesh.add( new ElementBuffer( indices ) );
			meshes.add( mesh );
		}
		
		model.add( new VertexBuffer( buffer, 8 ) );
		
		for( GLNode group : meshes )
			model.add( group );
		
		reader.close();
		
		return model;
	}
}
