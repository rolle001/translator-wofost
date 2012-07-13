package org.agmip.translators.wofost;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.agmip.util.MapUtil;

public class WofostOutputController {
	
		File[] listOfFiles;

		private void zipFiles(String zipFile) {
			byte[] buf = new byte[1024];
			try {
			    // Create the ZIP file
			    String outFilename = zipFile;
			    ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFilename));

			    // Compress the files
			    for (int i=0; i<listOfFiles.length; i++) {
			    	
			    	File f = listOfFiles[i];
			    	FileInputStream in = new FileInputStream(f.getAbsolutePath());

			        // Add ZIP entry to output stream
			        out.putNextEntry(new ZipEntry(f.getName()));

			        // Transfer bytes from the file to the ZIP file
			        int len;
			        while ((len = in.read(buf)) > 0) {
			            out.write(buf, 0, len);
			        }

			        // Complete the entry
			        out.closeEntry();
			        in.close();
			    }

			    // Complete the ZIP file
			    out.close();
			} catch (IOException e) {
			}
       
		}
		 
	
	    public void writeFiles(String filePath, Map input) {
	        
	    	WofostOutput.expName = MapUtil.getValueOr(input, "exname", "default");
	    	String outputFileName = filePath + WofostOutput.expName + "_wofost.zip";
	    	
	    	File thePath = new File(filePath);
	    	if (!thePath.exists())
	    		thePath.mkdir();
	    	
	    	String tempFilePath = filePath + "$temp\\";
	    	File theTempPath = new File(tempFilePath);
	    	theTempPath.mkdir();
	    	
	    	new WofostOutputSoil().writeFile(tempFilePath, input);
	    	new WofostOutputWeather().writeFile(tempFilePath, input);
	    	new WofostOutputSite().writeFile(tempFilePath, input);
	    	new WofostOutputTimer().writeFile(tempFilePath, input);
	    	new WofostOutputRerun().writeFile(tempFilePath, input);
	    	new WofostOutputRunopt().writeFile(tempFilePath, input);
	    	
	    	// zip all files
	    	listOfFiles = theTempPath.listFiles(); 
	    	zipFiles(outputFileName);
	    	
	    	//remove temp directory 
	    	for (File f : listOfFiles)
	    		f.delete();
	    	theTempPath.delete();
	    	
	    }
}
