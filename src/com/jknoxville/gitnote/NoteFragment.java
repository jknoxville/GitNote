package com.jknoxville.gitnote;

import java.io.File;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jknoxville.gitnote.dummy.DummyContent;

/**
 * A fragment representing a single Notebook detail screen. I.e a list of notes contained in this notebook.
 *  This fragment is either contained in a {@link NotebookListActivity} in two-pane mode (on
 * tablets) or a {@link NoteActivity} on handsets.
 */
public class NoteFragment extends Fragment {
	
	File noteFile;
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private DummyContent.DummyItem mItem;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public NoteFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			noteFile = new File(getArguments().getString(ARG_ITEM_ID));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_notebook_detail,
				container, false);

		return rootView;
	}
}
