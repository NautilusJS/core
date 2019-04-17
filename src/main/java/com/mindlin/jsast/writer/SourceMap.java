package com.mindlin.jsast.writer;

import java.util.Map;

import com.mindlin.jsast.fs.SourceFile;
import com.mindlin.jsast.fs.SourcePosition;

public class SourceMap {
	Map<String, SourceFile> sources;
	
	public void addMapping(String sourceName, long srcPos, int dstLine, int dstCol, String name) {
		SourceFile source = sources.get(sourceName);
		SourcePosition src = source.getOffsetPosotion(srcPos);
	}
	
}
