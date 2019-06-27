package com.mindlin.jsast.harness;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.mindlin.jsast.harness.CompilerOption.OptionInferenceStrategy;
import com.mindlin.jsast.harness.deps.AbstractDependencyGraph;
import com.mindlin.jsast.i18n.DiagnosticConsumer;

/**
 * Collection of {@link CompilerOption}s.
 * 
 * This collection manages a set of {@link CompilerOption}s that are available and their values.
 * 
 * @author mailmindlin
 */
public class CompilerOptions implements Serializable {
	private static final long serialVersionUID = -2234415265154393332L;

	@NonNull
	protected DiagnosticConsumer errorHandler;
	
	protected final Map<CompilerOption<?>, Object> options = new LinkedHashMap<>();
	
	/**
	 * Register option.
	 * 
	 * @param <T>
	 *            Option type
	 * @param option
	 *            Option to register
	 * @return If option was added to set
	 * @throws NullPointerException
	 *             If {@code option} was null
	 */
	public <T> boolean register(CompilerOption<T> option) throws NullPointerException {
		Objects.requireNonNull(option);
		return this.options.putIfAbsent(option, null) == null;
	}
	
	public boolean isRegistered(CompilerOption<?> option) {
		return this.options.containsKey(option);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T set(CompilerOption<@Nullable T> option, T value) {
		Objects.requireNonNull(option);
		return (T) options.merge(option, value, (p, n) -> option.reduce(this.errorHandler, (T) p, (T) n));
	}
	
	/**
	 * @return Set of registered options. Please don't modify this.
	 */
	public @NonNull Set<CompilerOption<?>> getOptions() {
		return this.options.keySet();//TODO unmodifiable view?
	}
	
	/**
	 * @param name
	 *            Option name
	 * @return Option if registered with this collection, else {@code null}.
	 */
	protected @Nullable CompilerOption<?> getOption(String name) {
		//TODO better runtime
		for (CompilerOption<?> option : this.getOptions())
			if (Objects.equals(option.name(), name))
				return option;
		return null;
	}
	
	protected @Nullable CompilerOption<?> resolveDep(Object dep) {
		if (dep instanceof CompilerOption) {
			return (CompilerOption<?>) dep;
		} else if (dep instanceof String) {
			return this.getOption((String) dep);
		} else {
			throw new IllegalArgumentException("Unknown dependency type");
		}
	}
	
	protected OptionDependencyGraph buildDependencyGraph(CompilerOption<?> base) {
		//TODO
		return new OptionDependencyGraph();
	}
	
	protected <T> T compute(@NonNull CompilerOption<T> option) {
		OptionInferenceStrategy<T> inference = option.getInference();
		if (inference == null)
			return null;
		
		OptionDependencyGraph deps = this.buildDependencyGraph(option);
		//TODO: compute deps first
		//TODO: compute optional deps
		
		return inference.apply(this.errorHandler, this);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(CompilerOption<? extends T> option) {
		Objects.requireNonNull(option);
		return (T) options.computeIfAbsent(option, this::compute);
	}
	
	/**
	 * @param option
	 * @return If option has computed value
	 */
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
	
	protected static enum DependencyType {
		
	}
	
	protected class OptionDependencyGraph extends AbstractDependencyGraph<CompilerOption<?>, DependencyType> {
		
	}
}
