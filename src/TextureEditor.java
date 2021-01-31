import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

public class TextureEditor extends JLabel implements EditorView
{
	private static final long serialVersionUID = 1L;
	private String name;
	
	public TextureEditor( String name, Texture texture )
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
	public LeafNode getModelSource()
	{
		return null;
	}
}
