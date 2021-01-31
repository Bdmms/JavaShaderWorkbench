import javax.swing.JComponent;

public interface EditorView 
{
	public String getName();
	public LeafNode getModelSource();
	public JComponent createView();
}
