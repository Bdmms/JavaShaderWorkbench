package swb.editors;
import javax.swing.JComponent;

import swb.GLNode;

public interface EditorView 
{
	public String getName();
	public GLNode getModelSource();
	public JComponent createView();
}
