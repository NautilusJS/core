package com.mindlin.jsast.harness;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.mindlin.jsast.harness.CompilerOption.OptionInferenceStrategy;
import com.mindlin.jsast.i18n.DiagnosticConsumer;

public class CompilerOptions {
	@NonNull
	protected DiagnosticConsumer errorHandler;
	
	protected final Map<CompilerOption<?>, Object> options = new LinkedHashMap<>();
	
	public <T> void addOption(CompilerOption<T> option) {
		this.options.putIfAbsent(option, null);
	}
	
	@SuppressWarnings("unchecked")
	public <T> void set(CompilerOption<@Nullable T> option, T value) {
		options.merge(option, value, (p, n) -> option.reduce(this.errorHandler, (T) p, (T) n));
	}
	
	public @NonNull Set<CompilerOption<?>> getOptions() {
		return this.options.keySet();
	}
	
	protected @Nullable CompilerOption<?> getOption(String name) {
		//TODO better runtime
		return this.getOptions()
				.stream()
				.filter(option -> Objects.equals(option.name(), name))
				.findFirst()
				.orElse(null);
	}
	
	protected <T> T compute(@NonNull CompilerOption<T> option) {
		OptionInferenceStrategy<T> inference = option.getInference();
		if (inference == null)
			return null;
		Set<String> deps = new HashSet<>(inference.getDependencies());
		//TODO: compute deps first
		//TODO: compute optional deps
		
		return inference.apply(this.errorHandler, this);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(CompilerOption<? extends T> option) {
		Objects.requireNonNull(option);
		return (T) options.computeIfAbsent(option, this::compute);
	}
	
	public boolean isPresent(CompilerOption<?> option) {
		return this.options.get(option) != null;
	}
	
	public <T> T get(CompilerOption<? extends T> option, T defaultValue) {
		@SuppressWarnings("unchecked")
		T result = (T) options.get(option);
		if (result == null && !options.containsKey(option))
			result = defaultValue;
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getIfPresent(@NonNull CompilerOption<? extends @Nullable T> option, T defaultValue) {
		return (T) options.getOrDefault(option, defaultValue);
	}
	
	public <T> void clear(CompilerOption<T> option) {
		options.remove(option);
	}
}
