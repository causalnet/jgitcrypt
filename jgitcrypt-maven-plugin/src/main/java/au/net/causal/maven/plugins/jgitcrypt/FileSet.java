package au.net.causal.maven.plugins.jgitcrypt;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileSet
{
    private File directory;
    private File targetDirectory;
    private final List<String> includes = new ArrayList<>();
    private final List<String> excludes = new ArrayList<>();

    public File getDirectory()
    {
        return directory;
    }

    public void setDirectory(File directory)
    {
        this.directory = directory;
    }

    public File getTargetDirectory()
    {
        return targetDirectory;
    }

    public void setTargetDirectory(File targetDirectory)
    {
        this.targetDirectory = targetDirectory;
    }

    public List<String> getIncludes()
    {
        return includes;
    }

    public void setIncludes(List<String> includes)
    {
        this.includes.clear();
        this.includes.addAll(includes);
    }

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
