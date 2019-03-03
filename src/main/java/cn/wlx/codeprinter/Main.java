package cn.wlx.codeprinter;

import java.awt.print.*;
import java.util.Random;

import cn.wlx.codeprinter.printable.HelloWorldPrinter;

public class Main {
  public static void main(String args[]) {
    PrinterJob job = PrinterJob.getPrinterJob();

    // gen test string
    StringBuilder sb = new StringBuilder();
    Random random = new Random();
    for (int i = 0; i < 10000; ++i) {
      //sb.append(i);
      //sb.append(' ');
      //if (random.nextInt(10) == 0) {
      //  sb.append('\n');
      //}
      sb.append(i%2);
    }

    job.setPrintable(new HelloWorldPrinter(sb.toString()));
    //job.setPrintable(new HelloWorldPrinter("asdasd"));
    boolean doPrint = job.printDialog();
    if (doPrint) {
      try {
        job.print();
      } catch (PrinterException e) {
        // The job did not successfully
        // complete
      }
    }
  }
}
