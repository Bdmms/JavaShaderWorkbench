import java.util.List;

import com.jogamp.opengl.GL3;

public interface Node 
{
	public String toString();
	public String getPath();
	
	public EditorView createEditor();
	
	public Node findBelow( Class<?> type );
	public Node findAbove( Class<?> type );
	public Node parent();
	public List<Node> children();
	
	public void add( Node node );
	public void setParent( Node node );
	
	public void update();
	public boolean initialize( GL3 gl );
	public void render( GL3 gl );
	public void dispose( GL3 gl );
}
