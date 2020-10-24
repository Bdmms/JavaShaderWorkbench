import java.awt.Font;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;

public class EditorTabs extends JTabbedPane
{
	private static final long serialVersionUID = 1L;
	
	public static final Font FONT = new Font("monospaced", Font.PLAIN, 16);
	
	private HashMap<Object, JComponent> tabs = new HashMap<>();
	
	public void open( EditorView view )
	{
		/*
		for( int i = 0; i < getTabCount(); i++ )
		{
			System.out.println( getTabComponentAt( i ) );
			
			if( this.getTabComponentAt( i ).getName().equals( view.getName() ) )
			{
				
			}
		}*/
		
		if( tabs.get( view.getName() ) == null )
		{
			addTab( view.getName(), view.createView() );
			tabs.put( view.getName(), view.getComponent() );
		}
	}
	
	public JComponent get( Object key )
	{
		return tabs.get( key );
	}
}
