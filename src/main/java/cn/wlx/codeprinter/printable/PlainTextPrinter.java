package cn.wlx.codeprinter.printable;

import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.print.*;
import java.awt.*;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.HashMap;
import java.util.Hashtable;

public class PlainTextPrinter implements Printable {

  private Hashtable<TextAttribute, Object> map = new Hashtable<>();

  private String text;
  private HashMap<Integer, Integer> pageOffsetMap = new HashMap<>();

  public PlainTextPrinter(String text) {
    this.text = text;
    pageOffsetMap.put(0, 0);
    map.put(TextAttribute.FAMILY, "SimHei");
    map.put(TextAttribute.SIZE, 10.0f);
  }

  public void setFont(String fontFamily, float size) {
    map.put(TextAttribute.FAMILY, fontFamily);
    map.put(TextAttribute.SIZE, size);
  }

  public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
    Graphics2D g2d = (Graphics2D) g;

    int pageOffset = pageOffsetMap.getOrDefault(page, Integer.MAX_VALUE);
    if (pageOffset >= text.length()) {
      return NO_SUCH_PAGE;
    }
    String subStr = text.substring(pageOffset);
    AttributedString stringToPrint = new AttributedString(subStr, map);
    AttributedCharacterIterator paragraph = stringToPrint.getIterator();
    int paragraphStart = paragraph.getBeginIndex();
    int paragraphEnd = paragraph.getEndIndex();
    LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(
        stringToPrint.getIterator(), g2d.getFontRenderContext()
    );

    // System.out.println(String.format("drawing page %d. pageOffset=%d, end=%d.", page, pageOffset, text.length()));
    // Set break width to width of Component.
    float breakWidth = (float) pf.getImageableWidth();
    float drawPosY = (float) pf.getImageableY();
    // Set position to the index of the first
    // character in the paragraph.
    lineMeasurer.setPosition(paragraphStart);

    // Get lines from until the entire paragraph
    // has been displayed.
    int lastPos = 0;
    while (lineMeasurer.getPosition() < paragraphEnd) {
      // handling newline
      int next = lineMeasurer.nextOffset(breakWidth);
      int limit = next;
      int charat = subStr.indexOf('\n', lineMeasurer.getPosition() + 1);
      if (next > (charat - lineMeasurer.getPosition()) && charat != -1) {
        limit = charat - lineMeasurer.getPosition();
      }
      lastPos = lineMeasurer.getPosition();
      TextLayout layout = lineMeasurer.nextLayout(
          breakWidth, lineMeasurer.getPosition() + limit, false
      );

      // Compute pen x position. If the paragraph
      // is right-to-left we will align the
      // TextLayouts to the right edge of the panel.
      float drawPosX = (float) pf.getImageableX();

      // Move y-coordinate by the ascent of the
      // layout.
      drawPosY += layout.getAscent();

      // check end of page
      if (drawPosY >= pf.getImageableY() + pf.getImageableHeight()) {
        pageOffsetMap.put(page + 1, pageOffset + lastPos);
        break;
      }

      // Draw the TextLayout at (drawPosX,drawPosY).
      layout.draw(g2d, drawPosX, drawPosY);

      // Move y-coordinate in preparation for next
      // layout.
      drawPosY += layout.getDescent() + layout.getLeading();
    }

    // tell the caller that this page is part
    // of the printed document
    return PAGE_EXISTS;
  }
}
