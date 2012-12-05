package de.versus.efe;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class GenericFileExplorer {

	private final Context context;
	private final ListView fileExplorerListView;
	private final Button fileExplorerUpButton;
	private final Button fileExplorerUseButton;
	private final TextView fileExplorerNoMatchesIndicator;
	private final String no_file_system_access_message_text;
	
	private boolean isFileExplorerEnabled = false;
	private File currentDirectory;
	
	public GenericFileExplorer(Context context, View containerView, int preselectionIndex, String noFileSystemAccessMessageText) {
		this.context = context;
		
		this.fileExplorerListView = (ListView) containerView.findViewById(R.id.file_explorer_file_list);
		this.fileExplorerUpButton = (Button) containerView.findViewById(R.id.file_explorer_up_button);
		this.fileExplorerUseButton = (Button) containerView.findViewById(R.id.file_explorer_use_button);
		this.fileExplorerNoMatchesIndicator = (TextView) containerView.findViewById(R.id.file_explorer_noMatches_indicator);
		
		this.no_file_system_access_message_text = noFileSystemAccessMessageText;
		
		FileListAdapter fileListAdapter = new FileListAdapter(context);
		fileExplorerListView.setAdapter(fileListAdapter);
		fileListAdapter.setSelectedIndex(preselectionIndex);
	}
	
	public boolean isFileExplorerEnabled() {
		return isFileExplorerEnabled;
	}
	
	public void setFileExplorerEnabled(boolean isEnabled) {
		isFileExplorerEnabled = isEnabled;
	}
	
	public String getCurrentDirectoryPath() {
		return (currentDirectory != null) ? currentDirectory.getAbsolutePath() : null;
	}
	
	public void setFileBrowsingDirectory(String directoryPath) {
		if (directoryPath != null) {
			File presetDirectory = new File(directoryPath);
			if (presetDirectory.exists() && presetDirectory.isDirectory()) {
				currentDirectory = presetDirectory;
			}
		}
	}
	
	private void indicateThatFileSystemIsNotAccessible() {
		Toast.makeText(context, no_file_system_access_message_text, Toast.LENGTH_SHORT).show();
	}
	
	public void initializeFileExplorer() {
		if (currentDirectory == null) {
			currentDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
		}
		
		FilePersistence filePersistence = new FilePersistence();
		if (filePersistence.isExternalStorageDirectoryRoot(currentDirectory)) {
			fileExplorerUpButton.setEnabled(false);
		}
		
		FileListAdapter fileListAdapter = (FileListAdapter) fileExplorerListView.getAdapter();
		filePersistence.initializeFileListAdapter(fileListAdapter, currentDirectory);
		
		fileExplorerUseButton.setEnabled(fileListAdapter.getSelectedIndex() != EmbeddedFileExplorerConstants.INVALID_POSITION);
		fileExplorerListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				Object adapterObj = adapterView.getAdapter();
				if (adapterObj != null && adapterObj instanceof FileListAdapter) {
					FileListAdapter fileListAdapter = (FileListAdapter) adapterObj;
					Object selectedFileName = fileListAdapter.getItem(position);
					if (selectedFileName != null) {
						int selectedFileType = fileListAdapter.getFileType(position);
						
						if (selectedFileType == FilePersistence.FILE_TYPE_DIRECTORY) {
							fileExplorerUseButton.setEnabled(false);
							currentDirectory = new File(currentDirectory, (String) selectedFileName);
							
							FilePersistence filePersistence = new FilePersistence();
							if (!filePersistence.isExternalStorageDirectoryRoot(currentDirectory)) {
								fileExplorerUpButton.setEnabled(true);
							}
							
							if (filePersistence.initializeFileListAdapter(fileListAdapter, currentDirectory)) {
								fileListAdapter.setSelectedIndex(EmbeddedFileExplorerConstants.INVALID_POSITION);
								fileListAdapter.notifyDataSetChanged();
								setNoMatchingFilesInDirectoryIndicatorVisibility(fileListAdapter.getCount() == 0);
							} else {
								indicateThatFileSystemIsNotAccessible();
							}
						} else {
							fileListAdapter.setSelectedIndex(position);
							fileListAdapter.notifyDataSetChanged();
							fileExplorerUseButton.setEnabled(true);
						}
						
					} else {
						// log, ignore!?
					}
				}
			}
		});
	}
	
	public void goToParentDirectory() {
		if (currentDirectory != null) {
			FilePersistence filePersistence = new FilePersistence();
			if (!filePersistence.isExternalStorageDirectoryRoot(currentDirectory)) {
				fileExplorerUseButton.setEnabled(false);
				FileListAdapter fileListAdapter = (FileListAdapter) fileExplorerListView.getAdapter();
				
				currentDirectory = currentDirectory.getParentFile();
				if (filePersistence.initializeFileListAdapter(fileListAdapter, currentDirectory)) {
					fileListAdapter.setSelectedIndex(EmbeddedFileExplorerConstants.INVALID_POSITION);
					fileListAdapter.notifyDataSetChanged();
					setNoMatchingFilesInDirectoryIndicatorVisibility(fileListAdapter.getCount() == 0);
				} else {
					indicateThatFileSystemIsNotAccessible();
				}
				
				if (filePersistence.isExternalStorageDirectoryRoot(currentDirectory)) {
					fileExplorerUpButton.setEnabled(false);
				}
			}
		}
	}
	
	public int getSelectedFileIndex() {
		return ((FileListAdapter) fileExplorerListView.getAdapter()).getSelectedIndex();
	}
	
	public String getSelectedFilePath() {
		File selectedFile = getSelectedFile();
		return (selectedFile != null) ? selectedFile.getAbsolutePath() : null; 
	}
	
	public File getSelectedFile() {
		if (currentDirectory != null) {
			String selectedFileName = getSelectedFileName();
			if (selectedFileName != null) {
				return new File(currentDirectory, selectedFileName);
			}
		}
		return null;
	}
	
	public String getSelectedFileName() {
		FileListAdapter fileListAdapter = (FileListAdapter) fileExplorerListView.getAdapter();
		return fileListAdapter.getSelectedFileName();
	}
	
	public void setNoMatchingFilesInDirectoryIndicatorVisibility(boolean isDirectoryEmpty) {
		fileExplorerNoMatchesIndicator.setVisibility(isDirectoryEmpty ? View.VISIBLE : View.GONE);
		fileExplorerListView.setVisibility(isDirectoryEmpty ? View.GONE : View.VISIBLE);
	}
}
