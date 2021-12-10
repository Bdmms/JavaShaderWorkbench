package swb.importer;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import swb.assets.Asset;
import swb.assets.TextureAsset;

public class ImporterImage extends Importer
{
	@Override
	public Asset initAsset( String filepath ) 
	{
		return new TextureAsset( filepath );
	}

	@Override
	public boolean loadAsset( Asset asset ) 
	{
		TextureAsset image = (TextureAsset)asset;
		
		try 
		{
			image.setImage( ImageIO.read( new File( asset.path ) ) );
			asset.setInternalState( Asset.State.LOADED );
			return true;
		} 
		catch( IOException e ) 
		{
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public String[] getExtensions() 
	{
		return new String[] { "BMP", "PNG", "JPEG", "TGA" };
	}
	
}
