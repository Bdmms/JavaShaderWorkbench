package swb.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.jogamp.opengl.GL;

import swb.ElementBuffer;
import swb.GLDataType;
import swb.GLNode;
import swb.ITexture;
import swb.Material;
import swb.ShaderProgram;
import swb.UniformList;
import swb.VertexBuffer;
import swb.math.vec3f;
import swb.math.vec3i;

public class ImporterOBJ extends Importer
{
	/**
	 * Creates an Importer than handles OBJ files
	 */
	public ImporterOBJ()
	{
		super( new String[] { "OBJ" } );
	}
	
	@Override
	public GLNode read( File file ) throws IOException
	{
		BufferedReader reader = new BufferedReader( new FileReader( file ) );
		
		String dir = file.getAbsolutePath().substring( 0, file.getAbsolutePath().lastIndexOf( '\\' ) ) + "\\";
		GLNode model = new GLNode( file.getName() );
		
		model.add( ShaderProgram.generateProgram( "template" ) );
		
		UniformList uniforms = new UniformList( "uniforms" );
		uniforms.add( "dif_texture", GLDataType.SAMP2D, "0" );
		uniforms.add( "nrm_texture", GLDataType.SAMP2D, "1" );
		
		model.add( uniforms );
		
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
				vec3f pos = new vec3f( parts, 1 );
				pos.mul( 0.1f );
				position.add( pos ); 
				break;
			}
			case "vn": 		normals.add( new vec3f( parts, 1 ) ); break;
			case "vt": 		textCoord.add( new vec3f( parts, 1 ) ); break;
			
			case "f":  		
				vec3i[] face = new vec3i[3];
				face[0] = new vec3i( parts[1].split( "/" ), 0 );
				face[1] = new vec3i( parts[2].split( "/" ), 0 );
				face[2] = new vec3i( parts[3].split( "/" ), 0 );
				current.faces.add( face ); break;
				
			case "mtllib": 	
				materialList = Material.loadMtlFile( new File( dir + parts[1] ) ); 
				break;
				
			case "usemtl":
				if( materialList == null )
				{
					String filename = line.substring( line.lastIndexOf( '\\' ) + 1 );
					String name = filename.substring( 0, filename.lastIndexOf( '.' ) );
					ITexture texture = ITexture.loadTexture( dir + filename );
					current.material = new Material( name, texture, GL.GL_TEXTURE0 );
					current.material.addTexture( ITexture.loadTexture( Material.DEFAULT_NRM ), GL.GL_TEXTURE1 );
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
					vec3f pos = position.get( vec.getX() );
					vec3f txt = textCoord.get( vec.getY() );
					vec3f nrm = normals.get( vec.getZ() );
					pos.copyTo( buffer, vIdx    , 3 );
					nrm.copyTo( buffer, vIdx + 3, 3 );
					txt.copyTo( buffer, vIdx + 6, 2 );
					indices[eIdx++] = gIdx++;
					vIdx += 8;
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
	
	/**
	 * Organizes the data within the OBJ file
	 */
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
}
