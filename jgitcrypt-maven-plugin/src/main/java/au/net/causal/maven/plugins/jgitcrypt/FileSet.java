package au.net.causal.maven.plugins.jgitcrypt;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Defines a set of files from a base directory specified by inclusion and exclusion patterns in Maven/Ant format along with a target directory.
 */
public class FileSet
{
    private File directory;
    private File targetDirectory;
    private final List<String> includes = new ArrayList<>();
    private final List<String> excludes = new ArrayList<>();

    /**
     * @return the directory containing the files.
     */
    public File getDirectory()
    {
        return directory;
    }

    public void setDirectory(File directory)
    {
        this.directory = directory;
    }

    /**
     * @return the target directory where processed files are saved to.
     */
    public File getTargetDirectory()
    {
        return targetDirectory;
    }

    public void setTargetDirectory(File targetDirectory)
    {
        this.targetDirectory = targetDirectory;
    }

    /**
     * @return Ant/Maven patterns that define which files should be included.  If not specified, all files are included.
     */
    public List<String> getIncludes()
    {
        return includes;
    }

    public void setIncludes(List<String> includes)
    {
        this.includes.clear();
        this.includes.addAll(includes);
    }

    /**
     * @return Ant/Maven patterns that define which of the included files should be excluded.
     */
    public List<String> getExcludes()
    {
        return excludes;
    }

    public void setExcludes(List<String> excludes)
    {
        this.excludes.clear();
        this.excludes.addAll(excludes);
    }

    @Override
    public String toString() {
        return "FileSet{" +
                "directory=" + directory +
                ", targetDirectory=" + targetDirectory +
                ", includes=" + includes +
                ", excludes=" + excludes +
                '}';
    }
}
