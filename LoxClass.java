package com.craftinginterpreters.lox;

import java.util.List;
import java.util.Map;

class LoxClass implements LoxCallable {
  final String name;
  final LoxClass superclass;
  private final Map<String, LoxFunction> methods;

  LoxClass(String name, LoxClass superclass,
           Map<String, LoxFunction> methods) {
    this.superclass = superclass;
    this.name = name;
    this.methods = methods;
  }

  // ch 13 q2
  LoxFunction findMethod(String name) {
    LoxFunction result = null;
    for (LoxClass c = this; c != null; c = c.superclass) {
      LoxFunction method = c.methods.get(name);
      if (method != null) result = method.withInner(result);
    }
    return result;
  }

  // ch 13 q2
  LoxFunction findSuperMethod(String name) {
    if (methods.containsKey(name)) {
      return methods.get(name);
    }

    if (superclass != null) {
      return superclass.findSuperMethod(name);  
    }

    return null;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public Object call(Interpreter interpreter,
                     List<Object> arguments) {
    LoxInstance instance = new LoxInstance(this);
    LoxFunction initializer = findMethod("init");
    if (initializer != null) {
      initializer.bind(instance).call(interpreter, arguments);
    }

    return instance;
  }

  @Override
  public int arity() {
    LoxFunction initializer = findMethod("init");
    if (initializer == null) return 0;
    return initializer.arity();
  }
}
