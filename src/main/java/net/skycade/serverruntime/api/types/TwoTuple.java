package net.skycade.serverruntime.api.types;

/**
 * Represents a tuple of two objects.\
 *
 * @param <A> the first object
 * @param <B> the second object
 * @author Jacob Cohen
 */
public record TwoTuple<A, B>(A first, B second) {

  /**
   * Returns the first value.
   *
   * @return the first value
   */
  @Override
  public A first() {
    return first;
  }

  /**
   * Returns the second value.
   *
   * @return the second value
   */
  @Override
  public B second() {
    return second;
  }
}
