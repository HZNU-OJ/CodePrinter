package cn.wlx.codeprinter.listener;

public interface Listener {
  public void run(ListenerCallback callback);
  public interface ListenerCallback {
    void print(String text);
  }
}
