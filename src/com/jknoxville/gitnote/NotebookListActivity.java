package com.jknoxville.gitnote;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;

/**
 * An activity representing a list of Notebooks. This activity has different
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
public class NotebookListActivity extends FragmentActivity implements
		NotebookListFragment.Callbacks {

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
		setContentView(R.layout.activity_notebook_list);

		if (findViewById(R.id.notebook_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((NotebookListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.notebook_list))
					.setActivateOnItemClick(true);
		}
		GitManager.initialise(getFilesDir(), PreferenceManager.getDefaultSharedPreferences(this));
		showList();
		registerForContextMenu(((NotebookListFragment) getSupportFragmentManager().findFragmentById(R.id.notebook_list)).getListView());
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
		delete(files[(int) id]);
		GitManager.commit(Strings.notebookDel+files[(int) id].getName());
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
		inflater.inflate(R.menu.notebook_context, menu);
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
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, NoteListActivity.class);
			detailIntent.putExtra(NoteFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_notebook_list, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//Handle item selection
		switch (item.getItemId()) {
		case R.id.new_notebook:
			promptForNotebookName();
			return true;
//		case R.id.init:
//			GitManager.initialise(getFilesDir(), PreferenceManager.getDefaultSharedPreferences(this));
//			return true;
		case R.id.refresh:
			GitManager.pull();
			showList();
			return true;
		case R.id.clone:
			GitManager.clone(getFilesDir());
			return true;
		case R.id.settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	private void newNotebook(String title) {
		File file = new File(getFilesDir(), title);
		file.mkdir();
		//GitManager.add(file);	//always does commit -a so this isnt needed
		GitManager.commit(Strings.newNotebook+file.getName());
		showList();
	}
	
	private void promptForNotebookName() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle(R.string.new_notebook_prompt);

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		  String value = input.getText().toString();
		  System.out.println(value);
		  if(value!=null) {
			  newNotebook(value);
		  }
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
	
	private void showList() {
		files = getFilesDir().listFiles(nonHiddenFilter);
		//Give the fragment a reference to the notebooks directory so it can populate the list
		((NotebookListFragment) getSupportFragmentManager()
		.findFragmentById(R.id.notebook_list)).populateFiles((files));
	}
}
