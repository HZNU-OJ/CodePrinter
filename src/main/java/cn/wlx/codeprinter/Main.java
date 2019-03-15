package cn.wlx.codeprinter;

import java.awt.*;
import java.awt.print.*;
import java.util.Scanner;

import javax.print.PrintService;

import cn.wlx.codeprinter.listener.HznuojListener;
import cn.wlx.codeprinter.listener.Listener;
import cn.wlx.codeprinter.printable.PlainTextPrinter;

public class Main {
  private static Scanner in = new Scanner(System.in);

  public static void main(String args[]) {
    Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
    for (int i = 0; i < fonts.length; ++i) {
      Font font = fonts[i];
      System.out.println(String.format("[%d] %s", i, font.getName()));
    }
    System.out.print("please choose a font: ");
    int fontIndex = in.nextInt();

    PrintService[] printServices = PrinterJob.lookupPrintServices();
    for (int i = 0; i < printServices.length; ++i) {
      PrintService service = printServices[i];
      System.out.println(String.format("[%d] %s", i, service.getName()));
    }
    System.out.print("please choose a printer service: ");
    int printerServiceIndex = in.nextInt();

    Listener hznuojListener = new HznuojListener();
    System.out.println("service started!");
    //hznuojListener.run(System.out::println);
    hznuojListener.run(text -> {
      PrinterJob job = PrinterJob.getPrinterJob();
      PlainTextPrinter printable = new PlainTextPrinter(text);
      printable.setFont(fonts[fontIndex].getFamily(), 10.0f);
      job.setPrintable(printable);
      try {
        job.setPrintService(printServices[printerServiceIndex]);
        job.print();
      } catch (PrinterException e) {
        e.printStackTrace();
      }
    });

  }
}
