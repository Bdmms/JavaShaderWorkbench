import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;

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
		
		// TODO: Remove auto model loading
		try 
		{
			_modelTree.add( Model.readObjFile( new File( "assets\\tiki.obj" ), _editor ) );
		} 
		catch (IOException e1) { e1.printStackTrace(); }
		
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
}
