package swb.editors;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import swb.GLNode;
import swb.ShaderCode;
import swb.ShaderProgram.ShaderNode;

public class ShaderEditor extends JTextArea implements EditorView, CaretListener
{
	private static final long serialVersionUID = 1L;
	public static final Font FONT = new Font("monospaced", Font.PLAIN, 16);
	
	private ShaderNode shader;
	private ShaderCode code;
	
	public ShaderEditor( String title, ShaderNode shader )
	{
		this.shader = shader;
		code = shader.getShader();
		
		setEditable( true );
		setLineWrap( true );
		setWrapStyleWord( true );
		setFont( FONT );
		setText( code.getCode() );
		setName( title );
		
		addCaretListener( this );
	}

	@Override
	public JComponent createView() 
	{
		return new JScrollPane( this );
		//return new DynamicPanel( getName(), new JScrollPane( this ) );
	}

	@Override
	public void caretUpdate(CaretEvent e) 
	{
		code.setCode( getText() );
	}
	
	@Override 
	public GLNode getModelSource()
	{
		return shader;
	}
}
