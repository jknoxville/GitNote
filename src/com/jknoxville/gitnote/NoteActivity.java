package com.jknoxville.gitnote;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

/**
 * An activity representing a single Notebook detail screen. This activity is
 * only used on handset devices. On tablet-size devices, item details are
 * presented side-by-side with a list of items in a {@link NotebookListActivity}
 * .
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing more than
 * a {@link NoteFragment}.
 */
public class NoteActivity extends FragmentActivity {
	
	File noteFile;
	boolean saved = true;	//TODO set saved to false whenever anything is typed

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// savedInstanceState is non-null when there is fragment state
		// saved from previous configurations of this activity
		// (e.g. when rotating the screen from portrait to landscape).
		// In this case, the fragment will automatically be re-added
		// to its container so we don't need to manually add it.
		// For more information, see the Fragments API guide at:
		//
		// http://developer.android.com/guide/components/fragments.html
		//
		if (savedInstanceState == null) {
//			// Create the detail fragment and add it to the activity
//			// using a fragment transaction.
//			Bundle arguments = new Bundle();
//			arguments.putString(NoteFragment.ARG_ITEM_ID, getIntent()
//					.getStringExtra(NoteFragment.ARG_ITEM_ID));
//			NoteFragment fragment = new NoteFragment();
//			fragment.setArguments(arguments);
//			getSupportFragmentManager().beginTransaction()
//					.add(R.id.note_container, fragment).commit();
			noteFile = new File(getIntent().getStringExtra(NoteFragment.ARG_ITEM_ID));
		}
		System.out.println(findViewById(R.id.title));
//		System.out.println(findViewById(R.id.note_container).findViewById(R.id.title));
		((EditText) findViewById(R.id.title)).setText(noteFile.getName());
		((EditText) findViewById(R.id.body)).setText(getContents(noteFile));
	}
	
	private String getNoteTitle() {
		return ((EditText) findViewById(R.id.title)).getText().toString();
	}
	
	private String getNoteBody() {
		return ((EditText) findViewById(R.id.body)).getText().toString();
	}
	
	private static String getContents(File f) {
		String contents = null;
		try {
			BufferedReader bRdr = new BufferedReader(new FileReader(f));
			StringBuilder strB = new StringBuilder();
			String line;
			while((line = bRdr.readLine()) != null) {
				strB.append(line);
			}
			bRdr.close();
			strB.trimToSize();
			contents = strB.toString();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return contents;
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_note, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpTo(this, new Intent(this,
					NoteListActivity.class));
			return true;
		case com.jknoxville.gitnote.R.id.save:
			saveNote();
			return true;
		case com.jknoxville.gitnote.R.id.history:
			showNoteHistory();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void saveNote() {
		//if title has been changed, rename the file
		if(!getNoteTitle().equals(noteFile.getName())) {
			String newPath = noteFile.getAbsolutePath().replace(noteFile.getName(), "") + getNoteTitle();
			//TODO WARNING the above may rename the directories as well if they have the same name...
			File newFile = new File(newPath);
			noteFile.renameTo(newFile);
			//renameTo doesn't actually change the getName() value of noteFile
			noteFile = newFile;
			System.out.println("now name is: "+noteFile.getName());
		}
		if(!getNoteBody().equals(getContents(noteFile))) {
			System.out.println("contents differ");
			writeContents(noteFile, getNoteBody());
		}
		//TODO git commit
		saved = true;
	}
	
	private void writeContents(File f, String contents) {
		try {
			PrintWriter out = new PrintWriter(f);
			out.print(contents);
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void showNoteHistory() {
		//TODO
	}
}
