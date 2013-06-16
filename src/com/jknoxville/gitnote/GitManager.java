package com.jknoxville.gitnote;

import java.io.File;
import java.io.IOException;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.UnmergedPathsException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class GitManager {

	static Git git;
	static UsernamePasswordCredentialsProvider creds;
	static SharedPreferences sharedPref;

	public static void initialise(File gitDir, SharedPreferences sharedPref) {
//		FileRepositoryBuilder builder = new FileRepositoryBuilder();
//		try {
//			repo = builder.setWorkTree(gitDir)
//					.readEnvironment()
//					.findGitDir()
//					.build();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		git = new Git(repo);
		GitManager.sharedPref = sharedPref;
		try {
			git = Git.init().setDirectory(gitDir).call();
//			FS.detect();
//			File conf = new File(git.getRepository().getDirectory(), ".gnconfig");
//			FileBasedConfig config = new FileBasedConfig(conf, FS.DETECTED);
			StoredConfig config = git.getRepository().getConfig();
			config.setString("remote", "origin", "url", "https://jknoxville@bitbucket.org/jknoxville/gitnote-test.git");
			config.save();
			creds = new UsernamePasswordCredentialsProvider(sharedPref.getString("username", ""), sharedPref.getString("password", ""));
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void commit(final String noteName) {
		new Thread(new Runnable() {
			public void run() {
				try {
					git.commit().setMessage(noteName).call();
					for(RevCommit commit: git.log().call()) {
						System.out.println(commit.getFullMessage());
					}
					git.push().setCredentialsProvider(creds).call();
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
}