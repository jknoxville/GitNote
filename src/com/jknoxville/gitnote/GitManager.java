package com.jknoxville.gitnote;

import java.io.File;
import java.io.IOException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.errors.UnmergedPathsException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import android.content.SharedPreferences;

public class GitManager {

	static Git git;
	static Repository lRepo;
	static UsernamePasswordCredentialsProvider creds;
	static SharedPreferences sharedPref;
	static long lastPushFail = 0;
	static long pushTimeout = 15*60*1000;	//15 mins constant	TODO make it exp backoff
	static boolean alreadyPushing = false;
	
	public static void initialise(File gitDir, SharedPreferences sharedPref) {
		try {
			GitManager.sharedPref = sharedPref;
			creds = new UsernamePasswordCredentialsProvider(sharedPref.getString("username", ""), sharedPref.getString("password", ""));
			
			lRepo = new FileRepository(gitDir+"/.git");
			git = new Git(lRepo);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void clone(File gitDir) {
		clone(sharedPref.getString("repoURL", ""), gitDir);
	}
	
	public static void clone(final String uri, final File gitDir) {
		new Thread(new Runnable() {
			public void run() {
				try {
					creds = new UsernamePasswordCredentialsProvider(sharedPref.getString("username", ""), sharedPref.getString("password", ""));
					Git.cloneRepository()
					.setURI(uri)
					.setDirectory(gitDir)
					.setCredentialsProvider(creds)
					.call();
				} catch (NoHeadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoMessageException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnmergedPathsException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ConcurrentRefUpdateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (WrongRepositoryStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (GitAPIException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	public static void commit(final String noteName) {
		new Thread(new Runnable() {
			public void run() {
				try {
					git.commit().setMessage(noteName).setAll(true).call();
					startPushing();
				} catch (NoHeadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoMessageException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnmergedPathsException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ConcurrentRefUpdateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (WrongRepositoryStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (GitAPIException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();

	}
	
	public static void remove(File f) {
		try {
			git.rm().addFilepattern(f.getParentFile().getName()+"/"+f.getName()).call();
		} catch (NoFilepatternException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void add(File f) {
		try {
			git.add().addFilepattern(f.getParentFile().getName()+"/"+f.getName()).call();
		} catch (NoFilepatternException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void push() throws InvalidRemoteException, TransportException, GitAPIException {
		git.push().setCredentialsProvider(creds).call();
	}

	public static void pull() {
		new Thread(new Runnable() {
			public void run() {
				try {
					git.pull().setCredentialsProvider(creds).call();
				} catch (NoHeadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoMessageException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnmergedPathsException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ConcurrentRefUpdateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (WrongRepositoryStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (GitAPIException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	public static void startPushing() {
		if(!alreadyPushing) {
			alreadyPushing = true;
			new Thread(new Runnable() {
				public void run() {
					while(true) {
						try {
							push();
							alreadyPushing = false;
							break;	//if push is successful then break from while loop.
						} catch (InvalidRemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (TransportException e) {
							lastPushFail = System.currentTimeMillis();
							try {
								Thread.sleep(pushTimeout - (System.currentTimeMillis() - lastPushFail));
							} catch (InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						} catch (GitAPIException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}).start();
		} else {
			lastPushFail = 0;
		}
	}
}
