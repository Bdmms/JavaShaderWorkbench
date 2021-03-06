package swb.editors;

import java.awt.Container;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

@SuppressWarnings("serial")
public class DynamicPanel extends JComponent implements MouseMotionListener, AncestorListener
{
	JFrame undockedFrame;
	Container parent = null;
	boolean docked = true;
	
	public DynamicPanel( String name, JComponent component )
	{
		JMenuBar bar = new JMenuBar();
		JMenuItem item = new JMenuItem( "click" );
		bar.add( item );
		
		setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
		add( bar );
		//add( item );
		//add( component );
		
		undockedFrame = new JFrame( name );
		undockedFrame.setJMenuBar( bar );
		undockedFrame.add( this );
		undockedFrame.pack();
		
		undockedFrame.addWindowListener( new WindowAdapter() 
		{
			@Override
			public void windowClosing( WindowEvent e ) 
			{
				parent.add( DynamicPanel.this );
				docked = true;
			}
		} );
		
		addMouseMotionListener( this );
		addAncestorListener( this );
	}

	@Override
	public void mouseDragged(MouseEvent e) 
	{
		if( docked )
		{
			parent = getParent();
			parent.remove( this );
			undockedFrame.setLocation( e.getPoint() );
			undockedFrame.setVisible( true );
			docked = false;
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) 
	{
		
	}

	@Override
	public void ancestorAdded(AncestorEvent e) 
	{
		
	}

	@Override
	public void ancestorMoved(AncestorEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ancestorRemoved(AncestorEvent e) {
		// TODO Auto-generated method stub
		
	}
}
