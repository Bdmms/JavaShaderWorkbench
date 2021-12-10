package swb.debug;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import com.jogamp.opengl.GL3;

import swb.Camera;
import swb.IRenderer;
import swb.VertexAttribute;
import swb.View3D;
import swb.assets.Asset;
import swb.assets.AssetLibrary;
import swb.assets.AssetStateEvent;
import swb.assets.GLStream;
import swb.assets.ProgramAsset;
import swb.assets.VertexArrayAsset;

public class DebugUtils 
{
	public static void debugView( IRenderer renderer, int fps )
	{
		JFrame frame = new JFrame( "DEBUG UTILS" );
		frame.setSize( new Dimension( 720, 480 ) );
		frame.setPreferredSize( new Dimension( 720, 480 ) );
		
		View3D view = new View3D( new Camera(), renderer, fps );
		frame.add( view );
		frame.pack();
		
		if( !view.initializeBackend( true ) )
			System.err.println( "Failed to initialize Backend!" );
		
		frame.addWindowListener( new WindowAdapter() 
		{
			@Override
			public void windowClosing( WindowEvent e ) 
			{
				System.exit( 0 );
			}
		} );
		
		frame.setVisible( true );
	}
	
	public static void debugShader()
	{
		DebugRenderer dr = new DebugRenderer();
		ProgramAsset program = ProgramAsset.streamShaders( "frameShader" );
		dr.addTarget( program );
		
		program.addListener( e -> 
		{
			if( e.location != AssetStateEvent.ASSET_LOCAL || e.state != Asset.State.LOADED ) return;
			
			VertexAttribute atr = VertexAttribute.parse( program.getShader( 0 ).code.getCode() );
			
			float[] vArray = 
			{ 
					1.0f, 1.0f,
					-1.0f, 1.0f,
					1.0f, -1.0f,
					-1.0f, 1.0f,
					1.0f, -1.0f,
					-1.0f, -1.0f
			};
			
			VertexArrayAsset vertices = new VertexArrayAsset( "frame", vArray, atr, true );
			dr.addTarget( vertices );
		});
		
		debugView( dr, 60 );
	}
	
	public static void debugMultiThreadTest()
	{
		long t0 = System.currentTimeMillis();
		
		GLStream.setUploadRate( 512 );
		DebugRenderer dr = new DebugRenderer();
		ProgramAsset program = ProgramAsset.streamShaders( "frameShader" );
		dr.addTarget( program );
		
		float[] vArray = new float[10000];
		for( int i = 0; i < vArray.length; i++ )
			vArray[i] = (float)(Math.random() * 2.0 - 1.0);
		
		program.addListener( e -> 
		{
			if( e.location != AssetStateEvent.ASSET_LOCAL || e.state != Asset.State.LOADED ) return;
			
			VertexAttribute atr = VertexAttribute.parse( program.getShader( 0 ).code.getCode() );
			VertexArrayAsset vertices = new VertexArrayAsset( "frame", vArray, atr, true );
			
			vertices.addListener( a -> System.out.println( String.format( "%d ms", System.currentTimeMillis() - t0 ) ) );
			
			dr.addTarget( vertices );
		});
		
		debugView( dr, 60 );
	}
	
	private static class DebugRenderer implements IRenderer
	{
		List<Asset> objects = new ArrayList<>();
		
		public void addTarget( Asset asset )
		{
			synchronized( objects )
			{
				objects.add( asset );
			}
		}
		
		@Override
		public void init( GL3 gl ) 
		{
			gl.glDisable( GL3.GL_CULL_FACE );
		}
		
		@Override
		public void render( GL3 gl, Camera camera ) 
		{
			synchronized( objects )
			{
				for( Asset asset : objects )
				{
					asset.render( gl );
				}
			}
		}
	}
	
	public static void main( String[] args )
	{
		debugShader();
		//debugMultiThreadTest();
	}
}
