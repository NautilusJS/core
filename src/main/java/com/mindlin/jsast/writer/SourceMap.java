package com.mindlin.jsast.writer;

import java.util.Map;

import com.mindlin.nautilus.fs.SourceFile;
import com.mindlin.nautilus.fs.SourcePosition;

public class SourceMap {
	Map<String, SourceFile> sources;
	
	public void addMapping(String sourceName, long srcPos, int dstLine, int dstCol, String name) {
		SourceFile source = sources.get(sourceName);
		SourcePosition src = source.getOffsetPosotion(srcPos);
	}
	
}
