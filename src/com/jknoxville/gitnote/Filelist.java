package com.jknoxville.gitnote;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class Filelist {
	
	public Filelist(File[] files) {
		for(File f: files) {
			Item it  = new Item(f.getName(), f);
			ITEMS.add(it);
			ITEM_MAP.put(f.getName(), it);
		}
		
	}

	/**
	 * An array of sample (dummy) items.
	 */
	public List<Item> ITEMS = new ArrayList<Item>();

	/**
	 * A map of sample (dummy) items, by ID.
	 */
	public Map<String, Item> ITEM_MAP = new HashMap<String, Item>();

	private void addItem(Item item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.name, item);
	}

	/**
	 * A dummy item representing a piece of content.
	 */
	public static class Item {
		public String name;
		public File content;

		public Item(String name, File content) {
			this.name = name;
			this.content = content;
		}

		@Override
		public String toString() {
			return name;
		}
	}
}
