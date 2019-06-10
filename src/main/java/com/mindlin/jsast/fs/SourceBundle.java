package com.mindlin.jsast.fs;

import java.util.List;

import com.mindlin.nautilus.fs.SourceFile;

public interface SourceBundle {
	List<SourceFile> getSources();
	SourceFile getSource(String name);
}
