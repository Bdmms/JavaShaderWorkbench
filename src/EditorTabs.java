import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class EditorTabs extends JTabbedPane
{
	private static final long serialVersionUID = 1L;
	public static final Font FONT = new Font("monospaced", Font.PLAIN, 16);
	
	private HashMap<Object, EditorView> tabs = new HashMap<>();
	
	public void open( EditorView view )
	{
		final String key = view.getName();
		
		if( tabs.get( key ) == null )
		{
			JComponent viewComp = view.createView();
			viewComp.setName( key );
			addTab( key, viewComp );
			tabs.put( key, view );
			setTabComponentAt( indexOfTab( key ), createClosableTab( key, e -> close( key ) ) );
		}
	}
	
	public void close( String title )
	{
		removeTabAt( indexOfTab( title ) );
		tabs.remove( title );
	}
	
	public EditorView get( Object key )
	{
		return tabs.get( key );
	}
	
	public LeafNode getSelectedNode()
	{
		if( getSelectedComponent() == null ) return null;
		
		EditorView view = tabs.get( getSelectedComponent().getName() );
		return view == null ? null : view.getModelSource();
	}
	
	private static JComponent createClosableTab( String title, Consumer<ActionEvent> closeAction )
	{
		JLabel label = new JLabel( title );
		JButton closeButton = new JButton( "x" );
		closeButton.setBorderPainted(false); 
		closeButton.setContentAreaFilled(false); 
		closeButton.setOpaque(false);
		closeButton.addActionListener( new ActionListener() {
			@Override
		    public void actionPerformed( ActionEvent e ) 
		    {
				closeAction.accept( e );
		    }
		});
		
		JPanel tabPanel = new JPanel(new GridBagLayout());
		tabPanel.setOpaque(false);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		tabPanel.add(label, gbc);

		gbc.gridx++;
		gbc.weightx = 0;
		tabPanel.add(closeButton, gbc);

		return tabPanel;
	}
}
