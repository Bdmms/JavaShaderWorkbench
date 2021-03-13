package swb.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import swb.math.mat4x4;

@SuppressWarnings("serial")
public class TransformDialog extends JDialog
{
	private TransformTableModel model = new TransformTableModel();
	private mat4x4 finalMatrix = null;
	
	public TransformDialog()
	{
		super( (Frame)null, "", true );
		
		JPanel panel = new JPanel();
		JPanel buttonPanel = new JPanel();
		
		panel.setLayout( new BorderLayout() );
		panel.add( new JTable( model ), BorderLayout.CENTER );
		panel.add( buttonPanel, BorderLayout.SOUTH );
		
		buttonPanel.setLayout( new FlowLayout() );
		buttonPanel.add( new JButton( new CancelAction() ) );
		buttonPanel.add( new JButton( new AcceptAction() ) );
		
		add( panel );
		pack();
	}
	
	private static class TransformTableModel extends AbstractTableModel
	{
		private static final long serialVersionUID = 1L;
		public mat4x4 matrix = new mat4x4();
		
		@Override
		public int getColumnCount() 
		{
			return 4;
		}

		@Override
		public int getRowCount() 
		{
			return 4;
		}

		@Override
		public Object getValueAt( int row, int col ) 
		{
			return matrix.get( col + row * 4 );
		}
		
		public void setValueAt( Object value, int row, int col )
		{
			try
			{
				matrix.set( col + row * 4, Float.parseFloat( value.toString() ) );
			}
			catch( NumberFormatException e ) {}
		}
		
		@Override
		public boolean isCellEditable( int row, int col ) 
		{
			return true;
		}
	}
	
	private class AcceptAction extends AbstractAction
	{
		public AcceptAction()
		{
			putValue( Action.NAME, "Accept" );
		}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			finalMatrix = model.matrix;
			setVisible( false );
		}
	}
	
	private class CancelAction extends AbstractAction
	{
		public CancelAction()
		{
			putValue( Action.NAME, "Cancel" );
		}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			setVisible( false );
		}
	}
	
	public mat4x4 getMatrix()
	{
		return finalMatrix;
	}
	
	public static mat4x4 openTransformDialog()
	{
		TransformDialog dialog = new TransformDialog();
		dialog.setVisible( true );
		System.out.println( dialog.getMatrix() );
		return dialog.getMatrix();
	}
}
