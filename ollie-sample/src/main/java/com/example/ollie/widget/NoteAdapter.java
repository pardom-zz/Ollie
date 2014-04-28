package com.example.ollie.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.ollie.R;
import com.example.ollie.model.Note;

import java.util.List;

public class NoteAdapter extends BaseAdapter {
	private LayoutInflater mLayoutInflater;
	private List<Note> mNotes;

	public NoteAdapter(Context context) {
		mLayoutInflater = LayoutInflater.from(context);
		updateDataSet();
	}

	public void updateDataSet() {

	}

	@Override
	public int getCount() {
		if (mNotes != null) {
			return mNotes.size();
		}
		return 0;
	}

	@Override
	public Note getItem(int position) {
		if (mNotes != null) {
			return mNotes.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		if (mNotes != null) {
			return mNotes.get(position).id;
		}
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.item_note, viewGroup, false);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Note note = getItem(position);
		if (note != null) {
			holder.bind(note);
		}

		return convertView;
	}

	private static final class ViewHolder {
		private TextView mTitleTextView;
		private TextView mBodyTextView;

		public ViewHolder(View root) {
			mTitleTextView = (TextView) root.findViewById(R.id.title_text_view);
			mBodyTextView = (TextView) root.findViewById(R.id.body_text_view);
		}

		public void bind(Note note) {
			mTitleTextView.setText(note.title);
			mBodyTextView.setText(note.body);
		}
	}
}
