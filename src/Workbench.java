import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;

public class Workbench extends JFrame implements KeyListener
{
	private static final long serialVersionUID = 1L;
	
	private ShaderEditor _shaderEditor = new ShaderEditor();
	private Shader _vertex = new Shader( "", GL3.GL_VERTEX_SHADER );
	private Shader _fragment = new Shader( "", GL3.GL_FRAGMENT_SHADER );
	private View3D _shaderView;
	
	public static void main( String[] args )
	{
		new Workbench();
	}
	
	public Workbench()
	{
		super( "Shader WorkBench" );
		
		final GLProfile profile = GLProfile.get(GLProfile.GL2);
	    GLCapabilities capabilities = new GLCapabilities(profile);
	    _shaderView = new View3D( capabilities, new ShaderProgram( _vertex, _fragment ), _shaderEditor );
	    
		setSize( new Dimension( 1280, 720 ) );
		this.setPreferredSize( new Dimension( 1280, 720 ) );
		
		JSplitPane splitView = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, _shaderView, _shaderEditor );
		splitView.setResizeWeight( 0.5 );
		add( splitView );
		pack();
		
		_shaderEditor.addKeyListener( this );
		
		if( !_shaderView.initializeBackend( true ) )
			System.err.println( "Failed to initialize Backend!" );
		
		setVisible( true );
	}

	@Override
	public void keyPressed(KeyEvent e) 
	{
		if( e.getKeyCode() == KeyEvent.VK_B && (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) == KeyEvent.CTRL_DOWN_MASK)
		{
			System.out.println("Re-compiling...");
			String vShader = _shaderEditor.getCode( ShaderEditor.VERT_SHDR );
			String fShader = _shaderEditor.getCode( ShaderEditor.FRAG_SHDR );
			_vertex.setCode( vShader );
			_fragment.setCode( fShader );
			_shaderView.recompile();
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
