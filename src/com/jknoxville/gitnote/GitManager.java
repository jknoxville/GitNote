package com.jknoxville.gitnote;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.UnmergedPathsException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class GitManager {

	static Repository repo;
	static Git git;

	public static void initialise(File gitDir) {
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
		
		try {
			git = Git.init().setDirectory(gitDir).call();
		} catch (GitAPIException e) {
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
}
