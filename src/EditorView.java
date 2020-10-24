import javax.swing.JComponent;

public interface EditorView 
{
	public String getName();
	public JComponent getComponent();
	public JComponent createView();
}
