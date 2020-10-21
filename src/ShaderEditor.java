import java.awt.Font;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

public class ShaderEditor extends JTabbedPane
{
	public enum ShaderType {
		VERTEX_SHADER,
		GEOMETRY_SHADER,
		FRAGMENT_SHADER,
		VERTEX_DATA
	}
	
	public static final Font FONT = new Font("monospaced", Font.PLAIN, 16);
	
	private static final long serialVersionUID = 1L;
	public static final String VERTEX_BUFFER_TEMPLATE = "0, 0.5, 0.0\n1, 0.0, 0.5\n2, 0.0, -0.5\n3, -0.5, 0.0\ni, 0, 1, 2, 3, 1, 2";
	public static final String VERTEX_SHADER_TEMPLATE = "#version 330 core\nlayout (location = 0) in vec2 position;\n\nvoid main()\n{\n\tgl_Position = vec4(position, 0.0, 1.0);\n}";
	public static final String FRAGMENT_SHADER_TEMPLATE = "#version 330 core\nout vec4 FragColor;\n\nvoid main()\n{\n\tFragColor = vec4(1.0, 1.0, 1.0, 1.0);\n}";
	
	public static final int VERT_BUFF = 0;
	public static final int VERT_SHDR = 1;
	public static final int FRAG_SHDR = 2;
	public static final int UNIFORMS = 3;
	public static final int TEXTURES = 4;
	
	private List<JComponent> tabs = new ArrayList<JComponent>();
	
	public ShaderEditor()
	{
		addEditor( VERT_BUFF, "Vertex Buffer", null, VERTEX_BUFFER_TEMPLATE );
		addEditor( VERT_SHDR, "Vertex Shader", null, VERTEX_SHADER_TEMPLATE );
		addEditor( FRAG_SHDR, "Fragment Shader", null, FRAGMENT_SHADER_TEMPLATE );
		
		UniformTable uniform = new UniformTable();
		addComp( UNIFORMS, "Uniform List", null, EditorTable.createEditor( uniform ), uniform );
		
		TextureTable texture = new TextureTable();
		addComp( TEXTURES, "Texture List", null, EditorTable.createEditor( texture ), texture );
	}
	
	public void addComp( int key, String title, Icon icon, JComponent comp, JComponent editor )
	{
		comp.setName( title );
		addTab( title, icon, comp );
		tabs.add( key, editor );
	}
	
	public void addEditor( int key, String title, Icon icon, String template )
	{
		JTextArea codeEditor = new JTextArea();
		codeEditor.setEditable( true );
		codeEditor.setLineWrap( true );
		codeEditor.setWrapStyleWord( true );
		codeEditor.setFont( FONT );
		codeEditor.setText( template );
		codeEditor.setName( title );
		
		JScrollPane scrollPane = new JScrollPane( codeEditor );
		
		addTab( title, icon, scrollPane );
		tabs.add( key, codeEditor );
	}
	
	public void addKeyListener( KeyListener l )
	{
		super.addKeyListener( l );
		
		for( JComponent tab : tabs )
			tab.addKeyListener( l );
	}
	
	public JComponent getEditor( int key )
	{
		JComponent tab = tabs.get( key );
		return tab != null ? tab : null;
	}
	
	public String getCode( int key )
	{
		JComponent tab = tabs.get( key );
		return tab instanceof JTextArea ? ((JTextArea)tab).getText() : null;
	}
}
