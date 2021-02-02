package swb;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
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

import swb.editors.EditorTabs;
import swb.math.Matrix;
import swb.math.MatrixColor;
import swb.math.vec4f;

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
		JSplitPane subSplitView = new JSplitPane( JSplitPane.VERTICAL_SPLIT, scrollTreeView, _editor );
		JSplitPane splitView = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, _view, subSplitView );
		splitView.setResizeWeight( 0.5 );
		add( splitView );
		pack();
		
		if( !_view.initializeBackend( true ) )
			System.err.println( "Failed to initialize Backend!" );
		
		// Create default shader
		ShaderProgram program = ShaderProgram.createFrom( "Program 0", new File( "shaders\\template.vs" ),  new File( "shaders\\template.fs" ) );
		UniformList uniforms = new UniformList( "uniforms" );
		uniforms.add( "dif_texture", GLDataType.SAMP2D, "0" );
		uniforms.add( "nrm_texture", GLDataType.SAMP2D, "1" );
		
		_modelTree.add( program );
		_modelTree.add( uniforms );
		
		//_modelTree.add( new VectorDrawing( "xlm.svg") );
		
		//List<vec2f> circle = ModelUtils.createCircle( Math.PI / 16.0 );
		//_modelTree.add( ModelUtils.join( circle, circle, circle ) );
		//_modelTree.add( ModelUtils.createSphere( Math.PI / 16.0 ) );
		
		setVisible( true );
	}
	
	public static void chooseFile( Component parent, String directory, boolean save, Consumer<File> action )
	{
		JFileChooser fc = new JFileChooser();
		
		if( directory != null )
			fc.setSelectedFile( new File( directory ) );
		
		int resp = save ? fc.showSaveDialog( parent ) : fc.showOpenDialog( parent );
		if( resp == JFileChooser.APPROVE_OPTION )
		{
			action.accept( fc.getSelectedFile() );
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
	
	public JMenuBar createMenuBar()
	{
		JMenuBar bar = new JMenuBar();
		JMenu file = new JMenu( "File" );
		JMenu build = new JMenu( "Build" );
		JMenu utility = new JMenu( "Utility" );
		
		file.add( createMenuItem( "Open", e -> 
		{
			chooseFile( this, System.getProperty( "user.dir" ), false, openFile ->
			{
				try 
				{
					GLNode node = ModelUtils.read( openFile );
					
					// TODO: Show error
					if( node == null ) return;
					
					_modelTree.add( node );
					_view.recompile();
				} 
				catch (Exception e1) { e1.printStackTrace(); }
			} );
		} ) );
		
		file.add( createMenuItem( "Save As...", e -> 
		{
			chooseFile( this, null, true, saveFile ->
			{
				try 
				{
					GLNode node = _editor.getSelectedNode();
					
					if( node != null )
					{
						String name = saveFile.getName();
						node.saveTo( saveFile, name.substring( name.lastIndexOf( '.' ) + 1 ) );
					}
				} 
				catch (IOException e1) { e1.printStackTrace(); }
			} );
		} ) );
		
		file.add( createMenuItem( "Quit", e -> System.exit( 0 ) ) );
		
		build.add( createMenuItem( "Rebuild", e -> _view.recompile() ) );
		
		utility.add( createMenuItem( "Generate Normal Map", e -> {
			chooseFile( null, null, false, imageFile ->
			{
				try 
				{
					MatrixColor image = new MatrixColor( imageFile );
					Matrix<vec4f> nrm = image.normalMap();
					nrm.writeTo( chooseFile( null, null, true ) );
				} 
				catch (IOException e1) { e1.printStackTrace(); }
			} );
		} ) );
		
		bar.add( file );
		bar.add( build );
		bar.add( utility );
		return bar;
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
}
