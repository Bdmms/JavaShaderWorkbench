package swb.editors;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import swb.GLNode;
import swb.Shader;

public class ShaderEditor extends JTextArea implements EditorView, CaretListener
{
	private static final long serialVersionUID = 1L;
	public static final Font FONT = new Font("monospaced", Font.PLAIN, 16);
	
	private Shader shader;
	
	public ShaderEditor( String title, Shader shader )
	{
		this.shader = shader;
		
		setEditable( true );
		setLineWrap( true );
		setWrapStyleWord( true );
		setFont( FONT );
		setText( shader.getCode() );
		setName( title );
		
		addCaretListener( this );
	}

	@Override
	public JComponent createView() 
	{
		return new JScrollPane( this );
	}

	@Override
	public void caretUpdate(CaretEvent e) 
	{
		shader.setCode( getText() );
	}
	
	@Override 
	public GLNode getModelSource()
	{
		return shader;
	}
}
