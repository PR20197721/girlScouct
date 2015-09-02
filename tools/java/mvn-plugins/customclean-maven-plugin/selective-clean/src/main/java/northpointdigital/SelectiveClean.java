package northpointdigital;

/*
 * a Maven plugin that lets you selectively delete from the target dir during mvn clean,
 * instead of deleting the whole thing.
 * 
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

@Mojo( name = "preservegems", defaultPhase = LifecyclePhase.CLEAN )
public class SelectiveClean
    extends AbstractMojo
{

    @Parameter( defaultValue = "${project.build.directory}", property = "targetDir", required = true )
	private String target;
	
    @Parameter
    private String[] preserveInTarget;

    public void execute()
        throws MojoExecutionException
    {
   	    getLog().info( "SelectiveClean maven plugin activated" );
    	
   	    if(preserveInTarget == null || preserveInTarget.length == 0) {
       	    getLog().info( "SelectiveClean: no preserveInTarget values configured, so deleting entire target dir" );
    		return;
   	    }
   	    
    	String tmpTarget = target + ".tmp";
    	    	
    	File targetDir = new File(target);
    	if(! targetDir.exists()) {
       	    getLog().info( "SelectiveClean: no target, nothing to do" );
    		return;
    	} 
    	
    	try {
	    	File tmpTargetDir = new File(tmpTarget);
			if(tmpTargetDir.exists()) {
		    	FileUtils.deleteDirectory(tmpTargetDir);
			}
			tmpTargetDir.mkdir();
    	    	
	    	for(String p:preserveInTarget) {
	       	    getLog().info( "SelectiveClean preserving " + p );
	        	Path origPath = FileSystems.getDefault().getPath(target, p);
	        	if(! Files.exists(origPath)) {
	           	    getLog().info( "SelectiveClean: path not found, skipping " + origPath );
	           	    continue;
	        	}
	        	Path tmpPath = FileSystems.getDefault().getPath(tmpTarget, p);
	            Files.move(origPath, tmpPath);	        	
	    	}
	    	
	    	//equivalent to rm -rf
	    	FileUtils.deleteDirectory(targetDir);
	    	tmpTargetDir.renameTo(targetDir);
	    	
		} catch (IOException e) {
			e.printStackTrace();
			throw new MojoExecutionException("Sorry boss, I dun screwed up", e);
		}
    	    	
    }
}
