package com.mindlin.jsast.impl.parser;

public enum JSFeature {
	// ES5
	GETTERS,
	SETTERS,
	TRAILING_COMMA,
	STRICT_MODE,
	
	// ES6
	BINARY_LITERALS,
	LAMBDA_FUNCTIONS,
	CLASSES("js.class"),
	CLASS_CONSTRUCTORS("js.class.constructor"),
	CLASS_EXTENDS("js.class.inheritance"),
	CLASS_STATIC,
	CLASS_SUPER,
	CLASS_THIS,
	DESTRUCTURING,
	PARAMETER_DESTRUCTURING("js.parameter.destructuring"),
	FOR_OF_LOOP,
	GENERATORS,
	YIELD,
	COMPUTED_PROPERTIES,
	CLASS_ACCESSOR("js.accessor"),
	REST_PARAMETERS("js.parameter.rest"),
	PARAMETER_INITIALIZERS("js.parameter.default"),
	SPREAD_EXPRESSIONS("js.operator.spread"),
	LET_DECLARATIONS("js.variable.let"),
	CONST_DECLARATIONS("js.variable.const"),
	NEW_TARGET,
	OCTAL_LITERALS,
	IMPLICIT_OCTAL_LITERALS,
	REGEXP_FLAG_U,
	REGEXP_FLAG_Y("regexp y/sticky flag"),
	TEMPLATE_LITERALS,
	
	// ES6 modules
	MODULES,
	
	// ES2016
	EXPONENTIATION_OPERATOR,
	
	// ES2017
	ASYNC_FUNCTION("js.function.async"),
	AWAIT("js.await"),
	
	// ES2018
	REGEXP_FLAG_S,
	REGEXP_LOOKBEHIND,
	REGEXP_NAMED_GROUPS,
	OBJECT_LITERAL_SPREAD,
	OBJECT_PATTERN_REST,
	
	// TS 1.1
	OPTIONAL_PARAMETER("ts.parameter.optional"),
	PROPERTY_VISIBILITY("ts.visibility"),
	PUBLIC_VISIBILITY("ts.visibility.public"),
	PRIVATE_VISIBILITY("ts.visibility.private"),
	TS_TYPES("ts.types"),
	TS_ENUM("ts.types.enum"),
	// TS 1.3
	TUPLE_TYPES,
	PROTECTED_VISIBILITY,
	// TS 1.4
	TYPE_UNION("ts.types.union"),
	TYPE_ALIAS("ts.types.alias"),
	TS_CONST_ENUM("ts.types.enum.const"),
	// TS 1.5
	NAMESPACES("ts.namespace"),
	DECORATORS("js.decorator"),//TODO reorganize
	TYPE_INTERSECTION("ts.types.intersection"),
	ABSTRACT_CLASSES("ts.class.abstract"),
	// TS 1.7
	THIS_TYPE("ts.types.this"),
	STRING_LITERAL_TYPE("ts.types.literal.string"),
	// TS unknown
	CLASS_IMPLEMENTS("ts.class.implements"),
	TYPE_CASTING("ts.types.cast"),
	ANGLE_CASTING("ts.types.cast.angle"),
	TS_INTERFACE("ts.interface"),
	THIS_PARAMETER("ts.parameter.this"),
	PARAMETER_ACCESS_MODIFIERS("ts.parameter.accessModifier"),
	TYPE_GENERICS,
	INDEX_SIGNATURE,
	MAP_TYPE,
	CALL_SIGNATURE,
	CONSTRUCT_SIGNATURE,
	CLASS_PROPERTY_DECLARATION,
	AMBIENT_DECLARATION,
	OPTIONAL_TUPLE_ELEMENT,
	REST_TUPLE_ELEMENT,
	;
	JSFeature() {
		
	}
	JSFeature(String description) {
		
	}
}
