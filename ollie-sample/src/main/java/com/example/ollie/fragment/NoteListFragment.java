package com.example.ollie.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.example.ollie.R;
import com.example.ollie.widget.NoteAdapter;

public class NoteListFragment extends Fragment {
	private ListView mNoteListView;

	private NoteAdapter mNoteAdapter;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mNoteAdapter = new NoteAdapter(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_note_list, container, false);

		mNoteListView = (ListView) view.findViewById(R.id.note_list_view);
		mNoteListView.setAdapter(mNoteAdapter);

		return view;
	}
}
