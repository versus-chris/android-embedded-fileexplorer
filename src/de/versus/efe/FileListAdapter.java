package de.versus.efe;

import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FileListAdapter extends BaseAdapter {

	private final LayoutInflater inflator;
	private final int row_background_color;
	private final int row_background_color_selected;
	private final int filename_label_color;
	private final int filename_label_color_selected;

	// list adapter model
	private String[] fileNames;
	private HashMap<String, Integer> fileTypes;
	private int selectedIndex = EmbeddedFileExplorerConstants.INVALID_POSITION;
	
	public FileListAdapter(Context context) {
		this.inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		row_background_color = EmbeddedFileExplorerConstants.DEFAULT_ROW_BACKGROUND_COLOR;
		row_background_color_selected = EmbeddedFileExplorerConstants.DEFAULT_SELECTED_ROW_BACKGROUND_COLOR;
		filename_label_color = EmbeddedFileExplorerConstants.DEFAULT_FILENAME_LABEL_COLOR;
		filename_label_color_selected = EmbeddedFileExplorerConstants.DEFAULT_SELECTED_FILENAME_LABEL_COLOR;
	}
	
	public FileListAdapter(Context context, 
			int rowBackgroundColor, int rowBackgroundColorSelected,
			int filenameLabelColor, int filenameLabelColorSelected) {
		this.inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		row_background_color = rowBackgroundColor;
		row_background_color_selected = rowBackgroundColorSelected;
		filename_label_color = filenameLabelColor;
		filename_label_color_selected = filenameLabelColorSelected;
	}
	
	
	public void setFileNames(String[] fileNames) {
		this.fileNames = fileNames;
	}
	
	public void setFileTypes(HashMap<String, Integer> fileTypes) {
		this.fileTypes = fileTypes;
	}
	
	public void setSelectedIndex(int index) {
		this.selectedIndex = index;
	}
	
	public int getFileType(int position) {
		int fileType = FilePersistence.FILE_TYPE_UNKNOWN;
		if (fileNames != null && fileTypes != null) {
			Integer fileTypeValue = fileTypes.get(fileNames[position]);
			fileType = fileTypeValue != null ? fileTypeValue.intValue() : fileType; 
		}
		return fileType;
	}
	
	public int getSelectedIndex() {
		return selectedIndex;
	}
	
	public String getSelectedFileName() {
		String selectedFileName = null;
		if (selectedIndex != EmbeddedFileExplorerConstants.INVALID_POSITION) {
			if (fileNames != null && fileNames.length > selectedIndex) {
				selectedFileName  = fileNames[selectedIndex];
			}
		}
		return selectedFileName;
	}
	
	@Override
	public int getCount() {
		return (fileNames != null) ? fileNames.length : 0;
	}

	@Override
	public Object getItem(int position) {
		return (fileNames != null && position < fileNames.length) ? fileNames[position] : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflator.inflate(R.layout.generic_file_list_item, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		final String fileName = fileNames[position];
		holder.getFileNameTextView().setText(fileName);
		holder.getFileTypeImageView().setImageResource(getFileTypeImageResource(fileName));
		holder.getRowView().setBackgroundColor((position != selectedIndex) 
				? row_background_color
				: row_background_color_selected);
		holder.getFileNameTextView().setTextColor((position != selectedIndex)
				? filename_label_color
				: filename_label_color_selected);
		
		return convertView;
	}

	private int getFileTypeImageResource(String fileName) {
		int fileType = FilePersistence.FILE_TYPE_UNKNOWN;
		Integer fileTypeValue = fileTypes.get(fileName);
		if (fileTypeValue != null) {
			fileType = fileTypeValue.intValue();
		}
		
		switch (fileType) {
			case FilePersistence.FILE_TYPE_DIRECTORY:
				return R.drawable.holo_collections_collection;
			case FilePersistence.FILE_TYPE_PICTURE:
				return R.drawable.holo_content_picture;
			case FilePersistence.FILE_TYPE_UNKNOWN:
			default:
				return android.R.drawable.ic_menu_help;
		}
	}

	
	class ViewHolder {
		private View row;
		private ImageView fileTypeImageView;
		private TextView fileNameTextView;
		
		public ViewHolder(View existingView) {
			this.row = existingView;
		}
		
		public View getRowView() {
			return row;
		}
		
		public ImageView getFileTypeImageView() {
			if (fileTypeImageView == null) {
				fileTypeImageView = (ImageView) row.findViewById(R.id.file_list_item_fileType);
			}
			return fileTypeImageView;
		}

		public TextView getFileNameTextView() {
			if (fileNameTextView == null) {
				fileNameTextView = (TextView) row.findViewById(R.id.file_list_item_fileName);
			}
			return fileNameTextView;
		}
	}
}
