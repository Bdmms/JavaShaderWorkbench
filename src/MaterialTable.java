import java.io.File;

import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.table.TableCellEditor;

import com.jogamp.opengl.GL3;

@SuppressWarnings("serial")
public class MaterialTable extends EditorTable
{
	private JComboBox<Material.BindedTexture> _textureIDs = new JComboBox<>();
	
	public MaterialTable( String title, Material material ) 
	{
		super( title, new TextureTableModel( material ) );
		
		_textureIDs.addItem( new Material.BindedTexture( null, 0 ) );
		for( int i = GL3.GL_TEXTURE0; i <= GL3.GL_TEXTURE31; i++ )
			_textureIDs.addItem( new Material.BindedTexture( null, i ) );
	}
	
	public void addTexture( File file )
	{
		((TextureTableModel)_model).addRow( file );
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
	
	private static class TextureTableModel extends ObserverTableModel
	{
		private Material material;
		
		public TextureTableModel( Material material )
		{
			super( new TableColumn[] {
					new TableColumn( "File", String.class, false ),
					new TableColumn( "Assigned", String.class, true ),
					new TableColumn( "Texture", Icon.class, false )	
			} );
			
			this.material = material;
		}
		
		public void addRow( File file )
		{
			material.add( Material.loadTexture( file ), Material.DEFAULT_ID );
		}
		
		@Override
		public void removeRow() 
		{
			material.removeLast();
		}
		
		@Override
		public void setValueAt(Object value, int row, int col) 
		{
			Material.BindedTexture texture = material.get( row );
			
			switch( col )
			{
			case 0: break;
			case 1: texture.id = ((Material.BindedTexture) value ).id; break;
			case 2: break;
			}
		}

		@Override
		public Object getValueAt( int row, int col ) 
		{
			switch( col )
			{
			case 0: return material.get( row ).texture.filename;
			case 1: return material.get( row );
			case 2: return material.get( row ).texture.getIcon();
			default: return null;
			}
		}

		@Override
		public int getRowCount() 
		{
			return material.children().size();
		}
		
		@Override 
		public LeafNode getSource()
		{
			return material;
		}
	}
}
