package com.mindlin.jsast.impl.parser;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;

public class JSFeatureSet implements Serializable {
	private static final long serialVersionUID = 376385727134135913L;

	public static JSFeatureSet latest() {
		return null;
	}
	
	private @NonNull Set<JSFeature> features;
	
	public JSFeatureSet(@NonNull Set<JSFeature> features) {
		this.features = features;
	}

	public boolean supports(JSFeature feature) {
		return this.features.contains(feature);
	}
	
	public JSFeatureSet with(JSFeatureSet other) {
		Set<JSFeature> features = this.features.isEmpty() ? EnumSet.noneOf(JSFeature.class) : EnumSet.copyOf(this.features);
		features.addAll(other.features);
		return new JSFeatureSet(features);
	}
}
