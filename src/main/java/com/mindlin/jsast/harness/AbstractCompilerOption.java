package com.mindlin.jsast.harness;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.mindlin.jsast.i18n.DiagnosticConsumer;
import com.mindlin.jsast.i18n.DiagnosticType;
import com.mindlin.jsast.json.api.JSONInput;
import com.mindlin.nautilus.util.i18n.LocalizableMessage;

public abstract class AbstractCompilerOption<T> implements CompilerOption<T> {
	public static <T, O extends AbstractCompilerOption<T>> Builder<T, O> builder(Function<OptionConfig, O> builderFn, String name) {
		return new Builder<>(builderFn, name);
	}
	
	protected final String name;
	protected final LocalizableMessage description;
	protected final LocalizableMessage parameter;
	protected final Collection<? extends String> aliases;
	protected final Set<? extends CompilerOption.Attribute> attributes;

	protected AbstractCompilerOption(OptionConfig config) {
		this(config.name, config.description, config.parameter,
				config.aliases.isEmpty() ? Collections.emptyList() : config.aliases,
				config.attributes.isEmpty() ? Collections.emptySet() : config.attributes);
	}
	
	public AbstractCompilerOption(String name, LocalizableMessage description, LocalizableMessage parameter, String...aliases) {
		this(name, description, parameter, (aliases == null || aliases.length == 0) ? Collections.emptyList() : Arrays.asList(aliases));
	}
	
	public AbstractCompilerOption(String name, LocalizableMessage description, LocalizableMessage parameter, Collection<? extends String> aliases) {
		this(name, description, parameter, aliases, Collections.emptySet());
	}
	
	public AbstractCompilerOption(String name, LocalizableMessage description, LocalizableMessage parameter, Collection<? extends String> aliases, Set<? extends CompilerOption.Attribute> attributes) {
		this.name = Objects.requireNonNull(name);
		this.description = description;
		this.parameter = parameter;
		this.aliases = Objects.requireNonNull(aliases);
		this.attributes = Objects.requireNonNull(attributes);
	}
	
	@Override
	public String name() {
		return this.name;
	}
	
	@Override
	public Collection<? extends String> aliases() {
		return this.aliases;
	}

	@Override
	public LocalizableMessage getDescription() {
		return this.description;
	}

	@Override
	public LocalizableMessage getParameterDescription() {
		return this.parameter;
	}
	
	@Override
	public Set<? extends CompilerOption.Attribute> getAttributes() {
		return this.attributes;
	}

	@Override
	public T read(DiagnosticConsumer errorHandler, String key, JSONInput valueReader) {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected static class OptionConfig {
		protected String name;
		protected LocalizableMessage description;
		protected LocalizableMessage parameter;
		protected Collection<String> aliases = new ArrayList<>();
		protected Set<CompilerOption.Attribute> attributes = CompilerOption.Attribute.getSetBase();
	}
	
	@NonNullByDefault
	protected static class Builder<T, O extends AbstractCompilerOption<T>> {
		protected final Function<OptionConfig, O> builderFn;
		protected OptionConfig config = new OptionConfig();
		
		public Builder(Function<OptionConfig, O> builderFn, String name) {
			this.builderFn = builderFn;
			this.config.name = Objects.requireNonNull(name);
		}

		public Builder<T, O> setDescription(String description) {
			return this;
		}
		
		public Builder<T, O> setParameter(String parameter) {
			return this;
		}
		
		public Builder<T, O> addAlias(String alias) {
			config.aliases.add(alias);
			return this;
		}
		
		public Builder<T, O> addAttribute(CompilerOption.Attribute attribute) {
			config.attributes.add(attribute);
			return this;
		}
		
		public O build() {
			return this.builderFn.apply(config);
		}
	}
	
	public static class Flag extends AbstractCompilerOption<Boolean> {
		public Flag(OptionConfig config) {
			super(config);
		}

		public Flag(String name, LocalizableMessage description, LocalizableMessage parameter, String...aliases) {
			super(name, description, parameter, aliases);
		}
	
		@Override
		public Boolean parse(DiagnosticConsumer errorHandler, String command, Iterator<? extends String> values) {
			return true;
		}
	}
	
	public static class Flags extends AbstractCompilerOption<Collection<String>> {
		Set<String> options;
		public Flags(String name, LocalizableMessage description, String[] aliases, String...options) {
			super(name, description, null, aliases);
			this.options = new HashSet<>(Arrays.asList(options));
		}
		@Override
		public int arity() {
			return 1;
		}
		@Override
		public @Nullable Collection<String> parse(DiagnosticConsumer errorHandler, String command, Iterator<? extends String> values) {
			String value = values.next();
			if (!options.contains(value)) {
				//TODO: log
				return null;
			}
			return Arrays.asList(value);
		}
		@Override
		public @Nullable Collection<String> reduce(DiagnosticConsumer errorHandler, @Nullable Collection<String> prev, Collection<String> current) {
			if (prev == null)
				return current;
			if (current == null)
				return Collections.emptySet();
			Set<String> result = new LinkedHashSet<>(prev);
			result.addAll(current);
			return result;
		}
	}

	public static abstract class SimpleField<T> extends AbstractCompilerOption<T> {
		public SimpleField(OptionConfig config) {
			super(config);
		}
		
		public SimpleField(String name, LocalizableMessage description, LocalizableMessage parameter, String... aliases) {
			super(name, description, parameter, aliases);
		}
		
		@Override
		public int arity() {
			return 1;
		}
		
		protected abstract @Nullable T parse(@NonNull DiagnosticConsumer errorHandler, @NonNull String command, String value);
		
		@Override
		public T parse(DiagnosticConsumer errorHandler, String command, Iterator<? extends String> values) {
			if (!values.hasNext()) {
				//TODO: emit error
				return null;
			}
			String value = values.next();
			return this.parse(errorHandler, command, value);
		}
		
		@Override
		public T reduce(DiagnosticConsumer errorHandler, T prev, T current) {
			if (prev != null) {
				//TODO: warn about repeated option
			}
			
			return super.reduce(errorHandler, prev, current);
		}
	}
	
	public static class IntegerField extends SimpleField<Integer> {
		public static final DiagnosticType VALUE_NOT_INTEGER = DiagnosticType.error("com.mindlin.jsast.harness.IntegerField#VALUE_NOT_INTEGER");
		public IntegerField(OptionConfig config) {
			super(config);
		}

		public IntegerField(String name, LocalizableMessage description, LocalizableMessage parameter, String... aliases) {
			super(name, description, parameter, aliases);
		}
	
		@Override
		protected Integer parse(DiagnosticConsumer errorHandler, String command, String value) {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException e) {
				//TODO: attach exception
				errorHandler.report(VALUE_NOT_INTEGER, value);
				return null;
			}
		}
	}

	public static class StringField extends SimpleField<String> {
		public StringField(OptionConfig config) {
			super(config);
		}

		public StringField(String name, LocalizableMessage description, LocalizableMessage parameter, String... aliases) {
			super(name, description, parameter, aliases);
		}
	
		@Override
		protected String parse(DiagnosticConsumer errorHandler, String command, String value) {
			return value;
		}
	}
	
	public static class CharsetField extends SimpleField<Charset> {
		public CharsetField(OptionConfig config) {
			super(config);
		}
		
		public CharsetField(String name, LocalizableMessage description, LocalizableMessage parameter, String... aliases) {
			super(name, description, parameter, aliases);
		}
	
		@Override
		protected Charset parse(DiagnosticConsumer errorHandler, String command, String value) {
			try {
				return Charset.forName(value);
			} catch (IllegalCharsetNameException | UnsupportedCharsetException e) {
				//TODO: emit diagnostic
				return null;
			}
		}
	}
	
	public static class PathField extends SimpleField<Path> {
		public PathField(OptionConfig config) {
			super(config);
		}
		
		public PathField(String name, LocalizableMessage description, LocalizableMessage parameter, String... aliases) {
			super(name, description, parameter, aliases);
		}
		
		@Override
		protected Path parse(DiagnosticConsumer errorHandler, String command, String value) {
			try {
				return Paths.get(value);
			} catch (InvalidPathException e) {
				//TODO: log
				return null;
			}
		}
	}
}