package com.craftinginterpreters.lox;

import java.util.List;

// ch 13 q3
class LoxArray extends LoxInstance {
  private final Object[] elements;

  LoxArray(int size) {
    super(null);
    elements = new Object[size];
  }

  @Override
  Object get(Token name) {
    if (name.lexeme.equals("length")) return (double)elements.length;

    if (name.lexeme.equals("get")) {
      return new LoxCallable() {
        @Override
        public int arity() { return 1; }

        @Override
        public Object call(Interpreter interpreter,
                           List<Object> arguments) {
          return elements[indexFrom(name, arguments.get(0))];
        }

        @Override
        public String toString() { return "<native fn>"; }
      };
    }

    if (name.lexeme.equals("set")) {
      return new LoxCallable() {
        @Override
        public int arity() { return 2; }

        @Override
        public Object call(Interpreter interpreter,
                           List<Object> arguments) {
          int index = indexFrom(name, arguments.get(0));
          return elements[index] = arguments.get(1);
        }

        @Override
        public String toString() { return "<native fn>"; }
      };
    }

    throw new RuntimeError(name,
        "Undefined property '" + name.lexeme + "'.");
  }

  @Override
  void set(Token name, Object value) {
    throw new RuntimeError(name, "Can't add properties to arrays.");
  }

  //Checks that the index is a whole number within the array bounds.
  private int indexFrom(Token name, Object value) {
    if (!(value instanceof Double)) {
      throw new RuntimeError(name, "Array index must be a number.");
    }

    double d = (Double)value;
    if (d != Math.floor(d) || d < 0 || d >= elements.length) {
      throw new RuntimeError(name, "Array index out of bounds.");
    }
    return (int)d;
  }

  @Override
  public String toString() {
    StringBuilder buffer = new StringBuilder();
    buffer.append("[");
    for (int i = 0; i < elements.length; i++) {
      if (i != 0) buffer.append(", ");
      Object element = elements[i];
      if (element == null) {
        buffer.append("nil");
      } else if (element instanceof Double
                 && ((Double)element) % 1 == 0) {
        buffer.append(String.valueOf(((Double)element).longValue()));
      } else {
        buffer.append(element);
      }
    }
    buffer.append("]");
    return buffer.toString();
  }
}
