public class IncorrectClass implements IntegerSumInterface {
  @Override
  public int add(int a, int b) {
    return a + b + 1;
  }
}
