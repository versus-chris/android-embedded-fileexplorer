package de.versus.efe;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;

import android.os.Environment;

public class FilePersistence {

	public static final int FILE_TYPE_UNKNOWN = 100;
	public static final int FILE_TYPE_DIRECTORY = 101;
	public static final int FILE_TYPE_PICTURE = 102;
	// add more file types here
	
	public boolean isExternalStorageAvailable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		} else if (Environment.MEDIA_BAD_REMOVAL.equals(state)
				|| Environment.MEDIA_CHECKING.equals(state)
				|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)
				|| Environment.MEDIA_NOFS.equals(state)
				|| Environment.MEDIA_REMOVED.equals(state)
				|| Environment.MEDIA_SHARED.equals(state)
				|| Environment.MEDIA_UNMOUNTABLE.equals(state)
				|| Environment.MEDIA_UNMOUNTED.equals(state)) {
			return false;
		} else {
			return false;
		}
	}
	
	public boolean initializeFileListAdapter(FileListAdapter fileListAdapter, File directory) {
		boolean successful = false;
		if (isExternalStorageAvailable()) {
			String[] fileNames = directory.list(getPictureFileFilter());
			HashMap<String,Integer> fileTypes = getFileTypesForDirectory(directory, fileNames, FILE_TYPE_PICTURE);
			fileListAdapter.setFileNames(fileNames);
			fileListAdapter.setFileTypes(fileTypes);
			successful = true;
		}
		return successful;
	}
	
	/**
	 * Gets an indicator map to determine which files are directories.
	 * @param directory parent
	 * @param fileNames contained in the directory
	 * @param fileTypeOtherThanDirectory depends on the file filter that has been applied to get the file name
	 * @return indicator map with file types
	 */
	public HashMap<String, Integer> getFileTypesForDirectory(File directory, String[] fileNames, int fileTypeOtherThanDirectory) {
		HashMap<String, Integer> fileTypes = null;
		if (fileNames != null && fileNames.length > 0) {
			fileTypes = new HashMap<String, Integer>();
			for (String fileName : fileNames) {
				File file = new File(directory, fileName);
				if (file.exists()) {
					if (file.isDirectory()) {
						fileTypes.put(fileName, FILE_TYPE_DIRECTORY);
					} else {
						fileTypes.put(fileName, fileTypeOtherThanDirectory);
					}
				} else {
					fileTypes.put(fileName, FILE_TYPE_UNKNOWN);
				}
			}
		}
		return fileTypes;
	}
	
	public FilenameFilter getPictureFileFilter() {
		FilenameFilter filter = new FilenameFilter() {
		    public boolean accept(File dir, String filename) {
		        File sel = new File(dir, filename);
		        return !filename.startsWith(".") 
		        		&& (filename.endsWith(".png") || filename.endsWith(".jpg")
		        				|| filename.endsWith(".jpeg") || sel.isDirectory());
		    }
		};
		return filter;
	}
	
	public boolean isExternalStorageDirectoryRoot(File directory) {
		final File externalStorageDirectory = Environment.getExternalStorageDirectory();
		return directory != null && directory.isDirectory() 
				&& directory.equals(externalStorageDirectory);
	}
	
}
