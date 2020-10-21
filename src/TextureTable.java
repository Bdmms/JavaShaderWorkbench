import java.io.File;

import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.table.TableCellEditor;

import com.jogamp.opengl.GL3;

@SuppressWarnings("serial")
public class TextureTable extends EditorTable
{
	private JComboBox<BindedTexture.TextureID> _textureIDs = new JComboBox<BindedTexture.TextureID>();
	
	public TextureTable() 
	{
		super( new TextureTableModel() );
		
		for( int i = GL3.GL_TEXTURE0; i <= GL3.GL_TEXTURE31; i++ )
			_textureIDs.addItem( new BindedTexture.TextureID( i ) );
	}
	
	@Override
	public void bindAll ( GL3 gl, ShaderProgram program )
	{
		_model.bindAll( gl, program );
	}
	
	@Override
	protected void add() 
	{
		JFileChooser fc = new JFileChooser();
		int resp = fc.showOpenDialog( null );
		
		if( resp == JFileChooser.APPROVE_OPTION && fc.getSelectedFile().exists() )
		{
			((TextureTableModel)_model).addRow( fc.getSelectedFile() );
		}
	}

	@Override
	protected void remove() 
	{
		_model.removeRow();
	}
	
	@Override
	public TableCellEditor getCellEditor( int row, int column )
	{
		if( column == 1 ) return new DefaultCellEditor( _textureIDs );
		return super.getCellEditor( row, column );
	}
	
	private static class TextureTableModel extends EditorTableModel<BindedTexture>
	{
		public TextureTableModel()
		{
			super( new TableColumn[] {
					new TableColumn( "File", String.class, false ),
					new TableColumn( "Assigned", String.class, true ),
					new TableColumn( "Texture", Icon.class, false )	
			} );
		}
		
		public void addRow( File file )
		{
			addRow( new BindedTexture( file ) );
		}
		
		@Override
		public void bindAll( GL3 gl, ShaderProgram program ) 
		{
			for( BindedTexture texture : _rows )
				texture.bind( gl );
		}
		
		@Override
		public void setValueAt(Object value, int row, int col) 
		{
			BindedTexture texture = _rows.get( row );
			
			switch( col )
			{
			case 0: break;
			case 1: texture.setBinding( (BindedTexture.TextureID) value ); break;
			case 2: break;
			}
		}

		@Override
		public Object getValueAt( int row, int col ) 
		{
			switch( col )
			{
			case 0: return _rows.get( row ).filename;
			case 1: return _rows.get( row ).getBindingID();
			case 2: return _rows.get( row ).getIcon();
			default: return null;
			}
		}
	}
}
