package com.craftinginterpreters.lox;

import java.util.List;

class LoxFunction implements LoxCallable {

  private final Stmt.Function declaration;

  private final Environment closure;

  private final boolean isInitializer;

  // ch 13 q2
  private final LoxFunction inner;

  private static final LoxCallable NO_INNER = new LoxCallable() {
    @Override public int arity() { return 0; }
    @Override public Object call(Interpreter interpreter,
                                 List<Object> arguments) {
      return null;
    }
    @Override public String toString() { return "<native fn inner>"; }
  };


  LoxFunction(Stmt.Function declaration, Environment closure,
              boolean isInitializer) {
    this(declaration, closure, isInitializer, null); 
  }

  // ch 13 q2
  private LoxFunction(Stmt.Function declaration, Environment closure,
                      boolean isInitializer, LoxFunction inner) {
    this.inner = inner;
    this.isInitializer = isInitializer;
    this.closure = closure;
    this.declaration = declaration;
  }


  LoxFunction withInner(LoxFunction inner) {
    return new LoxFunction(declaration, closure, isInitializer, inner);
  }

  LoxFunction bind(LoxInstance instance) {
    Environment environment = new Environment(closure);
    environment.define("this", instance);
    environment.define("inner",
        inner != null ? inner.bind(instance) : NO_INNER);
    return new LoxFunction(declaration, environment, isInitializer);
  }


  @Override
  public String toString() {
    return "<fn " + declaration.name.lexeme + ">";
  }


  @Override
  public int arity() {
    return declaration.params.size();
  }


  @Override
  public Object call(Interpreter interpreter,
                     List<Object> arguments) {
    Environment environment = new Environment(closure);
    for (int i = 0; i < declaration.params.size(); i++) {
      environment.define(declaration.params.get(i).lexeme,
          arguments.get(i));
    }

    try {
      interpreter.executeBlock(declaration.body, environment);
    } catch (Return returnValue) {
      if (isInitializer) return closure.getAt(0, "this");

      return returnValue.value;
    }

    if (isInitializer) return closure.getAt(0, "this");
    return null;
  }
}
