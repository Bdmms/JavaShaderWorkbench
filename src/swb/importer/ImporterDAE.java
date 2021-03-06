package swb.importer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.jogamp.opengl.GL3;

import swb.GLNode;
import swb.ITexture;
import swb.Material;
import swb.ShaderProgram;
import swb.UniformList;
import swb.VertexBuffer;
import swb.math.mat4x4;
import swb.math.vec4f;
import swb.ElementBuffer;
import swb.GLDataType;

public class ImporterDAE extends Importer
{
	private static class FloatArray
	{
		public float[] array;
		public int stride = 0;
		public int count = 0;
		
		public FloatArray( float[] arr )
		{
			array = arr;
		}
	}
	
	/**
	 * Creates an Importer than handles DAE files
	 */
	public ImporterDAE()
	{
		super( new String[] { "DAE" } );
	}
	
	@Override
	public GLNode read( File file ) throws IOException
	{
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        Document doc;
        
		try 
		{
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse( file );
		} 
		catch ( ParserConfigurationException | SAXException e ) 
		{
			throw new IOException( e );
		}
		
        Element root =  doc.getDocumentElement();
        root.normalize();
        
        String path = file.getPath();
        String directory = path.substring( 0, path.lastIndexOf( '\\' ) ) + "\\";
        List<GLNode> nodes = parseElements( root, "geometry", item -> parseGeometry( directory, root, item ) );
        
        GLNode model = new GLNode( file.getName() );
        
        UniformList uniforms = new UniformList( "uniforms" );
		uniforms.add( "dif_texture", GLDataType.SAMP2D, "0" );
		uniforms.add( "nrm_texture", GLDataType.SAMP2D, "1" );
		
		model.add( ShaderProgram.generateProgram( "template" ) );
		model.add( uniforms );
        
        for( GLNode node : nodes )
        {
        	model.add( node );
        }
        
		return model;
	}
	
	private GLNode parseGeometry( String directory, Element root, Element geometry )
	{
		Element mesh = findFirst( geometry, "mesh" );
		String name = geometry.getAttribute( "name" ); 
		
		Element triangles = findFirst( mesh, "triangles" );
		return parseBodyGroup( name, directory, root, mesh, triangles );
	}
	
	private GLNode parseBodyGroup( String name, String directory, Element root, Element mesh, Element elementArray )
	{
		Element input = findFirst( elementArray, "input" );
		String vertexID = input.getAttribute( "source" ).substring( 1 );
		Element vertexArray = find( mesh, "vertices", vertexID );
		
		String materialID = elementArray.getAttribute( "material" );
		materialID = materialID.substring( 0, materialID.length() - 1 );
		Element materialNode = find( root, "material", materialID );
		
		GLNode group = new GLNode( name );
		VertexBuffer vertices = parseVertexArray( mesh, vertexArray );
		ElementBuffer elements = parseElementArray( elementArray );
		Material material = parseMaterial( directory, materialNode );
		
		vertices.transformBy( new mat4x4( new vec4f( 0.0f ), new vec4f( 0.01f ) ), 0 );
		
		group.add( vertices );
		group.add( material );
		group.add( elements );
		
		return group;
	}
	
	private Material parseMaterial( String directory, Element material )
	{
		String name = material.getAttribute( "name" );
		Material mat = new Material( name );
		
		Element property = findFirst( material, "user_properties" );
		String filename = property.getFirstChild().getNodeValue();
		mat.addTexture( ITexture.loadTexture( directory + filename ), GL3.GL_TEXTURE0 );
		
		return mat;
	}
	
	private ElementBuffer parseElementArray( Element elementArray )
	{
		Element array = findFirst( elementArray, "p" );
		String[] vector = array.getFirstChild().getNodeValue().split( " " );
		int[] arr = new int[vector.length];
		
		for( int i = 0; i < vector.length; i++ )
		{
			arr[i] = Integer.parseInt( vector[i] );
		}
		
		return new ElementBuffer( arr );
	}
	
	private VertexBuffer parseVertexArray( Element mesh, Element vertexArray )
	{
		List<FloatArray> arrays = parseElements( vertexArray, "input", input -> 
		{
			String sourceID = input.getAttribute( "source" ).substring( 1 );
			Element source = find( mesh, "source", sourceID );
			if( source == null ) return null;
			Element fArray = findFirst( source, "float_array" );
			Element accessor = findFirst( source, "accessor" );
			return parseFloatArray( fArray, accessor );
		} );
		
		return combine( arrays );
	}
	
	private FloatArray parseFloatArray( Element floatArray, Element accessor )
	{
		String[] vector = floatArray.getFirstChild().getNodeValue().split( " " );
		float[] array = new float[vector.length];
		
		for( int i = 0; i < vector.length; i++ )
		{
			array[i] = Float.parseFloat( vector[i] );
		}
		
		FloatArray fArray = new FloatArray( array );
		fArray.stride = Integer.parseInt( accessor.getAttribute( "stride" ) );
		fArray.count = Integer.parseInt( accessor.getAttribute( "count" ) );
		return fArray;
	}
	
	private static VertexBuffer combine( List<FloatArray> arrays )
	{
		int fullStride = arrays.stream().mapToInt( arr -> arr.stride ).sum();
		int fullSize = arrays.stream().mapToInt( arr -> arr.array.length ).sum();
		int count = arrays.get( 0 ).count;
		
		int idx = 0;
		float[] buffer = new float[fullSize];
		for( int i = 0; i < count; i++ )
		{
			for( FloatArray array : arrays )
			{
				for( int j = 0; j < array.stride; j++ )
				{
					buffer[idx++] = array.array[ i * array.stride + j ];
				}
			}
		}
		
		return new VertexBuffer( buffer, fullStride );
	}
	
	private static Element findFirst( Element element, String tag )
	{
		NodeList list = element.getElementsByTagName( tag );
		return list == null ? null : (Element)list.item( 0 );
	}
	
	private static Element find( Element element, String tag, String id )
	{
		NodeList nodeList = element.getElementsByTagName( tag );
		
		for( int i = 0; i < nodeList.getLength(); i++ )
        {
			Element e = (Element)nodeList.item( i );
			
			if( e.getAttribute( "id" ).equals( id ) )
				return e;
        }
		
		return null;
	}
	
	private static <T> List<T> parseElements( Element element, String tag, Function<Element, T> function )
	{
		List<T> components = new ArrayList<>();
		NodeList nodeList = element.getElementsByTagName( tag );
		
		for( int i = 0; i < nodeList.getLength(); i++ )
        {
			T item = function.apply( (Element)nodeList.item( i ) );
			if( item != null )
				components.add( item );
        }
		
		return components;
	}
	
	public static void main( String[] args )
	{
		try 
		{
			new ImporterDAE().read( new File( "C:\\Users\\mmsra\\eclipse-workspace\\Shader_WorkBench\\assets\\Acerola\\tr0024_00.dae" ) );
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}
