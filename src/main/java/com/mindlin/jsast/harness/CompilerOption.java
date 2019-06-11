package com.mindlin.jsast.harness;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.mindlin.jsast.i18n.DiagnosticConsumer;
import com.mindlin.jsast.json.api.JSONInput;
import com.mindlin.nautilus.util.i18n.LocalizableMessage;

@NonNullByDefault
public interface CompilerOption<T> {
	/**
	 * Each option should have a unique, human-readable name.
	 * 
	 * @return Unique name for this option
	 */
	String name();
	
	/**
	 * @return Set of shorthand aliases (to use in addition to {@link #name()})
	 */
	@SuppressWarnings("null")
	default Collection<? extends String> aliases() {
		return Collections.emptySet();
	}
	
	/**
	 * @return Description text (for help)
	 */
	LocalizableMessage getDescription();
	
	/**
	 * @return Parameter description
	 */
	LocalizableMessage getParameterDescription();
	
	@SuppressWarnings("null")
	default Set<? extends Attribute> getAttributes() {
		return Collections.emptySet();
	}
	
	@Nullable T parse(DiagnosticConsumer errorHandler, String command, Iterator<? extends String> values);
	
	T read(DiagnosticConsumer errorHandler, JSONInput valueReader);
	
	/**
	 * When this option is set multiple times,
	 * 
	 * @param errorHandler
	 *            Error handler
	 * @param prev
	 *            Prevous value parsed
	 * @param current
	 *            More recent value parsed
	 * @return New value
	 */
	default @Nullable T reduce(DiagnosticConsumer errorHandler, @Nullable T prev, T current) {
		return current;
	}
	
	default int arity() {
		return 0;
	}
	
	default @Nullable OptionInferenceStrategy<T> getInference() {
		return null;//TODO
	}
	
	public static interface OptionInferenceStrategy<T> {
		@SuppressWarnings("null")
		default Collection<? extends String> getDependencies() {
			return Collections.emptyList();
		}
		
		@SuppressWarnings("null")
		default Collection<? extends String> getOptionalDependencies() {
			return Collections.emptyList();
		}
		
		@Nullable T apply(DiagnosticConsumer errorHandler, CompilerOptions options);
	}
	
	public static class DefaultValueInference<T> implements OptionInferenceStrategy<T> {
		protected final T value;

		public DefaultValueInference(T value) {
			this.value = value;
		}

		@Override
		public @Nullable T apply(DiagnosticConsumer errorHandler, CompilerOptions options) {
			return this.value;
		}
	}
	
	public static enum Attribute {
		AFFECTS_SOURCE_FILE,
		AFFECTS_MODULE_RESOLUTION,
		AFFECTS_BIND,
		AFFECTS_OUTPUT,
		/**
		 * Do not parse this option from CLI
		 */
		NO_CLI,
		/**
		 * If present, output inference when writing options to file
		 */
		STORE_INFERENCE,
		;
		@Nullable
		private static EnumSet<Attribute> EMPTY_SET;
		@SuppressWarnings("null")
		public static EnumSet<Attribute> getSetBase() {
			if (EMPTY_SET == null)
				EMPTY_SET = EnumSet.noneOf(Attribute.class);
			assert EMPTY_SET != null;
			return EMPTY_SET.clone();
		}
	}
}
