package swb;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.math.FloatUtil;

import swb.dynamic.DynamicPlane;
import swb.dynamic.Sprite;
import swb.editors.EditorTabs;
import swb.math.MatrixColor;
import swb.math.vec3f;

public class Workbench extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	private EditorTabs _editor;
	private ProjectTree _modelTree;
	private View3D _view;
	
	public static GLCapabilities capabilities;
	{
		final GLProfile profile = GLProfile.get( GLProfile.GL2 );
	    capabilities = new GLCapabilities( profile );
	}
	
	public static void main( String[] args )
	{
		new Workbench();
	}
	
	public Workbench()
	{
		super( "Shader Workbench" );
		
	    _editor = new EditorTabs();
	    _modelTree = new ProjectTree( _editor );
	    _view = new View3D( capabilities, _modelTree );
	    
	    //_modelTree.addKeyListener( this );
	    setJMenuBar( createMenuBar() );
	    
		setSize( new Dimension( 1280, 720 ) );
		this.setPreferredSize( new Dimension( 1280, 720 ) );
		
		JScrollPane scrollTreeView = new JScrollPane( _modelTree );
		JSplitPane subSplitView2 = new JSplitPane( JSplitPane.VERTICAL_SPLIT, scrollTreeView, _editor );
		JSplitPane splitView = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, _view, subSplitView2 );
		splitView.setResizeWeight( 0.5 );
		add( splitView );
		pack();
		
		if( !_view.initializeBackend( true ) )
			System.err.println( "Failed to initialize Backend!" );
		
		// Create default shader
		/*
		ShaderProgram program = ShaderProgram.createFrom( "Program 0", new File( "shaders\\template.vs" ),  new File( "shaders\\template.fs" ) );
		UniformList uniforms = new UniformList( "uniforms" );
		uniforms.add( "dif_texture", GLDataType.SAMP2D, "0" );
		uniforms.add( "nrm_texture", GLDataType.SAMP2D, "1" );
		
		_modelTree.add( program );
		_modelTree.add( uniforms );*/
		
		//_modelTree.add( new VectorDrawing( "xlm.svg") );
		
		addWindowListener( new WindowAdapter() 
		{
			@Override
			public void windowClosing( WindowEvent e ) 
			{
				System.exit( 0 );
			}
		} );
		
		setVisible( true );
	}
	
	public JMenuBar createMenuBar()
	{
		JMenuBar bar = new JMenuBar();
		JMenu file = new JMenu( "File" );
		JMenu build = new JMenu( "Build" );
		JMenu utility = new JMenu( "Utility" );
		
		file.add( createMenuItem( "Open", e -> 
			chooseFile( this, System.getProperty( "user.dir" ), false, openFile ->
			{
				GLNode node = ModelUtils.read( openFile );
				
				// TODO: Show error
				if( node == null ) return;
				
				_modelTree.add( node );
				_view.recompile();
			} )
		) );
		
		file.add( createMenuItem( "Save As...", e -> 
			chooseFile( this, null, true, saveFile ->
			{
				GLNode node = _editor.getSelectedNode();
				
				if( node != null )
				{
					String name = saveFile.getName();
					node.saveTo( saveFile, name.substring( name.lastIndexOf( '.' ) + 1 ) );
				}
			} )
		) );
		
		file.add( createMenuItem( "Quit", e -> System.exit( 0 ) ) );
		
		build.add( createMenuItem( "Rebuild", e -> _view.recompile() ) );
		
		utility.add( createMenuItem( "Generate Normal Map", e -> chooseFile( this, null, false, imageFile ->
		{
			
		} ) ) );
		
		
		utility.add( createMenuItem( "Generate Normal Map", e -> 
			chooseFile( this, null, false, imageFile ->
				new MatrixColor( imageFile ).normalMap()
					.writeTo( chooseFile( this, imageFile.getParent() + "\\", true ) ) ) ) );
		
		utility.add( createMenuItem( "Smooth Image", e -> 
			chooseFile( this, null, false, imageFile ->
				new MatrixColor( imageFile ).filter( 9, part -> part.average() )
					.writeTo( chooseFile( this, imageFile.getParent() + "\\", true ) ) ) ) );
		
		utility.add( createMenuItem( "Create Sphere", e -> 
		{
			GLNode node = ModelUtils.createSurface( 50, 50, ModelUtils.WRAP_ALL, (s,t) -> 
			{
				float theta = s * FloatUtil.PI * 2.0f;
				float alpha = (t - 0.5f) * FloatUtil.PI;
				float cosa = FloatUtil.cos( alpha );
				
				float x = FloatUtil.cos( theta ) * cosa;
				float y = FloatUtil.sin( theta ) * cosa;
				float z = FloatUtil.sin( alpha );
				
				return new vec3f( x, y, z );
			} );
			
			_modelTree.add( node );
			_view.recompile();
		} ) );
		
		utility.add( createMenuItem( "Create Dynamic Plane", e -> 
		{
			_modelTree.add( new DynamicPlane( "water", 250, 250 ) );
			_view.recompile();
		} ) );
		
		utility.add( createMenuItem( "Create Surface", e -> 
		{
			GLNode node = ModelUtils.createSurface( 75, 75, ModelUtils.WRAP_ALL, (s,t) -> 
			{
				float theta = s * FloatUtil.PI * 2.0f;
				float alpha = (t - 0.5f) * FloatUtil.PI;
				float cosa = FloatUtil.cos( alpha );
				
				float a = FloatUtil.cos( (s + t) * 25.0f * FloatUtil.PI ) * 0.25f + 1.0f;
				float x = FloatUtil.cos( theta ) * cosa * a;
				float y = FloatUtil.sin( theta ) * cosa * a;
				float z = FloatUtil.sin( alpha ) * a;
				
				return new vec3f( x, y, z );
			} );
			
			_modelTree.add( node );
			_view.recompile();
		} ) );
		
		utility.add( createMenuItem( "Create Skybox", e -> 
		{
			_modelTree.add( CubeMap.generateSkybox( "assets\\cubemap\\sky", ".png" ) );
			_view.recompile();
		} ) );
		
		utility.add( createMenuItem( "Create Sprite", e -> 
		{
			_modelTree.add( Sprite.generateSprite( "Test Sprite" ) );
			_view.recompile();
		} ) );
		
		bar.add( file );
		bar.add( build );
		bar.add( utility );
		return bar;
	}
	
	public static void chooseFile( Component parent, String directory, boolean save, FileConsumer action )
	{
		JFileChooser fc = new JFileChooser();
		
		if( directory != null )
			fc.setSelectedFile( new File( directory ) );
		
		int resp = save ? fc.showSaveDialog( parent ) : fc.showOpenDialog( parent );
		if( resp == JFileChooser.APPROVE_OPTION )
		{
			try 
			{
				action.accept( fc.getSelectedFile() );
			}
			catch( IOException e ) { System.out.println( e.getMessage() ); }
		}
	}
	
	public static File chooseFile( Component parent, String directory, boolean save )
	{
		JFileChooser fc = new JFileChooser();
		
		if( directory != null )
			fc.setSelectedFile( new File( directory ) );
		
		int resp = save ? fc.showSaveDialog( parent ) : fc.showOpenDialog( parent );
		return resp == JFileChooser.APPROVE_OPTION ? fc.getSelectedFile() : null;
	}
	
	public static JMenuItem createMenuItem( String name, Consumer<ActionEvent> action )
	{
		JMenuItem item = new JMenuItem();
		item.setAction( new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) 
			{
				action.accept( e );
			}
		});
		item.setName( name );
		item.setText( name );
		return item;
	}
	
	@FunctionalInterface
	private static interface FileConsumer
	{
		public void accept( File obj ) throws IOException;
	}
}
