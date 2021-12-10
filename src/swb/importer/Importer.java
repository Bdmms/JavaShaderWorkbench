package swb.importer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import swb.assets.Asset;

public abstract class Importer 
{
	private static HashMap<String, Importer> IMPORTERS = new HashMap<>();
	
	static
	{
		// TODO: Make optional
		addImporter( new ImporterImage() );
		addImporter( new ImporterShader() );
	}
	
	public abstract Asset initAsset( String filepath );
	public abstract boolean loadAsset( Asset asset );
	public abstract String[] getExtensions();
	
	public Asset importFile( String filepath )
	{
		Asset asset = initAsset( filepath );
		return loadAsset( asset ) ? asset : null;
	}
	
	public static void addImporter( Importer importer )
	{
		String[] exts = importer.getExtensions();
		
		for( String ext : exts )
		{
			System.out.println( "Loaded Extension: " + ext );
			IMPORTERS.put( ext, importer ); 
		}
	}
	
	public static Asset importAsset( String filepath )
	{
		Importer importer = getImporter( filepath );
		if( importer == null ) throw new NullPointerException( "No handler for file: " + filepath );
		return importer.importFile( filepath );
	}
	
	public static Importer getImporter( String filepath )
	{
		String ext = filepath.substring( filepath.lastIndexOf( '.' ) + 1 );
		return IMPORTERS.get( ext.toUpperCase() );
	}
	
	/**
	 * Creates a String from the bytes of a file
	 * @param file - Readable file
	 * @return String containing the entire file data
	 * @throws IOException If file cannot be read
	 */
	public static String fileToString( File file ) throws IOException
	{
		byte[] data = new byte[(int)file.length()];
		FileInputStream stream = new FileInputStream( file );
		stream.read( data );
		stream.close();
		return new String( data );
	}
}
