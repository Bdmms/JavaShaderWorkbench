package swb.editors;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import swb.GLNode;
import swb.ITexture;

public class TextureEditor extends JLabel implements EditorView
{
	private static final long serialVersionUID = 1L;
	private String name;
	
	public TextureEditor( String name, ITexture texture )
	{
		super( texture.getIcon() );
		this.name = name;
	}
	
	@Override
	public JComponent createView() 
	{
		return new JScrollPane( this );
	}

	@Override
	public String getName() 
	{
		return name;
	}
	
	@Override 
	public GLNode getModelSource()
	{
		return null;
	}
}
