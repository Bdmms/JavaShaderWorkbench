import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;

import math.vec2f;

public class Workbench extends JFrame implements KeyListener
{
	private static final long serialVersionUID = 1L;
	
	private EditorTabs _editor;
	private ProjectTree _modelTree;
	private View3D _view;
	
	public static void main( String[] args )
	{
		new Workbench();
	}
	
	public Workbench()
	{
		super( "Shader Workbench" );
		
		final GLProfile profile = GLProfile.get( GLProfile.GL2 );
	    GLCapabilities capabilities = new GLCapabilities( profile );
	    
	    _editor = new EditorTabs();
	    _modelTree = new ProjectTree( _editor );
	    _view = new View3D( capabilities, _modelTree );
	    
	    _modelTree.addKeyListener( this );
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
		ShaderProgram program = new ShaderProgram( "Program 0" );
		Shader vertex = new Shader( "shader.vs", ModelUtils.fileToString( new File( "shaders\\template.vs" ) ), GL3.GL_VERTEX_SHADER );
		Shader fragment = new Shader( "shader.fs", ModelUtils.fileToString( new File( "shaders\\template.fs" ) ), GL3.GL_FRAGMENT_SHADER );
		UniformList uniforms = new UniformList( "uniforms" );
		uniforms.add( "diffuse", GLDataType.SAMP2D, "0" );
		program.add( vertex );
		program.add( fragment );
		program.add( uniforms );
		
		_modelTree.add( program );
		
		//List<vec2f> circle = ModelUtils.createCircle( Math.PI / 16.0 );
		//_modelTree.add( ModelUtils.createFromOrthographicView2( circle, circle, circle ) );
		_modelTree.add( ModelUtils.createSphere( Math.PI / 16.0 ) );
		
		setVisible( true );
	}

	@Override
	public void keyPressed(KeyEvent e) 
	{
		if( e.getKeyCode() == KeyEvent.VK_B && (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) == KeyEvent.CTRL_DOWN_MASK)
		{
			System.out.println("Re-compiling...");
			_view.recompile();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) 
	{

	}

	@Override
	public void keyTyped(KeyEvent e) 
	{

	}
	
	public JMenuBar createMenuBar()
	{
		JMenuBar bar = new JMenuBar();
		JMenu file = new JMenu( "File" );
		
		file.add( createMenuItem( "Load", e -> {
			JFileChooser fc = new JFileChooser();
			fc.setSelectedFile( new File( System.getProperty( "user.dir" ) ) );
			
			int resp = fc.showOpenDialog( this );
			if( resp == JFileChooser.APPROVE_OPTION )
			{
				try 
				{
					_modelTree.add( ModelUtils.readObjFile( fc.getSelectedFile() ) );
					_view.recompile();
				} 
				catch (IOException e1) { e1.printStackTrace(); }
			}
		} ) );
		file.add( createMenuItem( "Quit", e -> System.exit( 0 ) ) );
		
		bar.add( file );
		
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
