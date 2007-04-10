import java.io.File;
import java.io.FileFilter;

public class FactoryFileFilter implements FileFilter
{
	private String ext;
	public FactoryFileFilter(String _ext)
	{
		ext = _ext;
	}

	public boolean accept(File pathname)
	{
		return(pathname.getName().endsWith(ext));
	}
}

