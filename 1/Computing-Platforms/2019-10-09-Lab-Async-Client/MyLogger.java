public class MyLogger {

  private static MyLogger loggerInstance;

  private long startingMillisecond;

  // Inhibit external calls by declaring constructor as private
  private MyLogger() {
    this.startingMillisecond = System.currentTimeMillis();
  }

  public static MyLogger getInstance() {
    if (loggerInstance == null) {
      loggerInstance = new MyLogger();
    }
    return loggerInstance;
  }

  public void log(String message) {
    long elapsedTime = System.currentTimeMillis() - this.startingMillisecond;
    System.out.println(Long.toString(elapsedTime) + ": " + message);
  }

}
