package com.jknoxville.gitnote;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;

/**
 * An activity representing a list of Notes of some notebook. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link NoteActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link NotebookListFragment} and the item details (if present) is a
 * {@link NoteFragment}.
 * <p>
 * This activity also implements the required
 * {@link NotebookListFragment.Callbacks} interface to listen for item
 * selections.
 */
public class NoteListActivity extends FragmentActivity implements
		NoteListFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	private File[] files;
	private FilenameFilter nonHiddenFilter = new FilenameFilter() {
		
		@Override
		public boolean accept(File dir, String filename) {
			return !filename.startsWith(".");
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// savedInstanceState is non-null when there is fragment state
				// saved from previous configurations of this activity
				// (e.g. when rotating the screen from portrait to landscape).
				// In this case, the fragment will automatically be re-added
				// to its container so we don't need to manually add it.
				// For more information, see the Fragments API guide at:
				//
				// http://developer.android.com/guide/components/fragments.html
				//
					// Create the detail fragment and add it to the activity
					// using a fragment transaction.
					Bundle arguments = new Bundle();
					System.out.println("intent extra: "+getIntent()
							.getStringExtra(NoteFragment.ARG_ITEM_ID));
					arguments.putString(NoteFragment.ARG_ITEM_ID, getIntent()
							.getStringExtra(NoteFragment.ARG_ITEM_ID));
					NoteListFragment fragment = new NoteListFragment();
					fragment.setArguments(arguments);
//					getSupportFragmentManager().beginTransaction()
//							.add(R.id.note_list, fragment).commit();
				setContentView(R.layout.activity_note_list);
				setTitle(getIntent()
							.getStringExtra(NoteFragment.ARG_ITEM_ID));
				registerForContextMenu(((NoteListFragment) getSupportFragmentManager().findFragmentById(R.id.note_list)).getListView());
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch(item.getItemId()) {
		case R.id.rename:
			//rename(info.id);
			return true;
		case R.id.delete:
			delete(info.id);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}
	
	private void delete(long id) {
		GitManager.remove(files[(int) id]);
		//delete(files[(int) id]);
		files[(int) id].delete();
		GitManager.commit(Strings.noteDel+files[(int) id].getName());
		showList();
	}
	
	private void delete(File f) {
		if(!f.delete()) {
			for(File child: f.listFiles()) {
				delete(child);
			}
		}
		f.delete();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.note_context, menu);
	}

	/**
	 * Callback method from {@link NotebookListFragment.Callbacks} indicating
	 * that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String id) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(NoteFragment.ARG_ITEM_ID, id);
			NoteFragment fragment = new NoteFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.notebook_detail_container, fragment).commit();

		} else {
			
			//straight to editing view
			
//			// In single-pane mode, simply start the detail activity
//			// for the selected item ID.
//			Intent detailIntent = new Intent(this, NoteActivity.class);
//			//use getCurrecntDir() and id to locate file object, then get absolute path and put this in intent.
//			//so that the next activity can load up that file.
//			File selectedFile = getCurrentDir().listFiles(new Filter(id))[0];
//			detailIntent.putExtra(NoteFragment.ARG_ITEM_ID, selectedFile.getAbsolutePath());
//			startActivity(detailIntent);
			
			//to noteView
			
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, NoteViewActivity.class);
			//use getCurrecntDir() and id to locate file object, then get absolute path and put this in intent.
			//so that the next activity can load up that file.
			File selectedFile = getCurrentDir().listFiles(new Filter(id))[0];
			detailIntent.putExtra(NoteFragment.ARG_ITEM_ID, selectedFile.getAbsolutePath());
			startActivity(detailIntent);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_note_list, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//Handle item selection
		switch (item.getItemId()) {
		case R.id.new_note:
			promptForTitle();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void newNote(String title) {
		File file = new File(getCurrentDir(), title);
		try {
			file.createNewFile();
			//GitManager.add(file);	//always does commit -a so this isnt needed
			GitManager.commit(Strings.newNote+file.getName());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		showList();
	}
	
	private void promptForTitle() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle(R.string.new_note_prompt);

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		  String value = input.getText().toString();
		  System.out.println(value);
		  newNote(value);
		  }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Cancelled.
		  }
		});

		alert.show();
		// see http://androidsnippets.com/prompt-user-input-with-an-alertdialog
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		showList();
	}
	
	private File getCurrentDir() {
		String bookname = getIntent().getStringExtra(NoteFragment.ARG_ITEM_ID);
		System.out.println("bookname: "+bookname);
		return getFilesDir().listFiles(new Filter(bookname))[0];
	}
	
	private void showList() {
		files = getCurrentDir().listFiles(nonHiddenFilter);
		//Give the fragment a reference to the notebooks directory so it can populate the list
		((NoteListFragment) getSupportFragmentManager()
		.findFragmentById(R.id.note_list)).populateFiles((getCurrentDir().listFiles()));
		
	}
}
