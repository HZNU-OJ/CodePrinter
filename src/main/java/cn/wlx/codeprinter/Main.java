package cn.wlx.codeprinter;

import java.awt.*;
import java.awt.print.*;
import java.util.Random;

import javax.print.PrintService;

import cn.wlx.codeprinter.printable.HelloWorldPrinter;

public class Main {
  public static void main(String args[]) {
    for (Font font : java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()) {
      System.out.println(font.getName());
    }

    PrinterJob job = PrinterJob.getPrinterJob();
    String text = "/*\n"
        + "(1)首先利用DP + 二分法求最大降序子序列，时间复杂度是nlogn得到以下数组：\n"
        + "val[i]:用来存储输入元素\n"
        + "maxSeqLen[i]:表示以第i个元素结尾可以得到的最大降序子序列的长度;\n"
        + "maxTailVal[i]:表示当前长度为i的所有最大降序子序列中尾元素的最大值\n"
        + "最后maxTailVal数组的长度maxTailLen即为最大降序子序列的长度\n"
        + "(2)然后利用maxSeqLen来计数，技术时需要考虑重复情况\n"
        + "利用countv[i]表示以第i个元素结尾可以可以得到的长度为maxSeqLen[i]的最大降序子序列的个数\n"
        + "利用maxLenSets[i](是一个List容器)表示maxSeqLen为i的元素的下标集合\n"
        + "加入一个哨兵元素n + 1,设maxSeqLen[n+1]为maxTailLen + 1, val[n + 1] = -1;\n"
        + "\n"
        + "那么从1往n+1遍历，每次遍历采取以下策略来计算countv和维护maxLenSets\n"
        + "  ->维护maxLenSets:假设d = maxSeqLen[i],首先遍历容器maxLenSets[d]的所有元素如果存在一个\n"
        + "    元素j使得val[j] = val[i],则删除j元素;最后插入i元素\n"
        + "  ->计算countv[i]:遍历maxLenSets[d - 1]中的所有元素j,如果val[j] > val[i],则countv[i] += countv[j]\n"
        + "最后countv[n + 1]即为所求的计数结果,maxLenSets[i]中元素的唯一性确保了最后结果的唯一性.\n"
        + "*/\n"
        + "#include <stdio.h>\n"
        + "#include <iostream>\n"
        + "#include <list>\n"
        + "#define MAX_N 5005\n"
        + "using namespace std;\n"
        + "\n"
        + "int maxSeqLen[MAX_N + 1];\n"
        + "int maxTailVal[MAX_N + 1], maxTailLen;\n"
        + "int val[MAX_N + 1], n;\n"
        + "list<int> maxLenSets[MAX_N + 1];\n"
        + "int countv[MAX_N + 1];\n"
        + "\n"
        + "int main()\n"
        + "{\n"
        + "    int i;\n"
        + "    scanf(\"%d\", &n);\n"
        + "    for(i = 1; i <= n; i++)\n"
        + "    {\n"
        + "        scanf(\"%d\", &val[i]);\n"
        + "        //二分DP\n"
        + "        int l = 1, r = maxTailLen;\n"
        + "        while(l <= r)\n"
        + "        {\n"
        + "            int mid = (l + r) / 2;\n"
        + "            if(maxTailVal[mid] <= val[i]) r = mid - 1;\n"
        + "            else l = mid + 1;\n"
        + "        }\n"
        + "        maxTailVal[l] = val[i];\n"
        + "        maxSeqLen[i] = l;\n"
        + "        if(l > maxTailLen)\n"
        + "            maxTailLen = l;\n"
        + "    }\n"
        + "    //第一个元素需要特殊处理\n"
        + "    countv[1] = 1;\n"
        + "    maxLenSets[1].push_back(1);\n"
        + "    val[n + 1] = -1;\n"
        + "    maxSeqLen[n + 1] = maxTailLen + 1;\n"
        + "    //遍历后面的n个元素\n"
        + "    for(i = 2; i <= n + 1; i++)\n"
        + "    {\n"
        + "        int d = maxSeqLen[i];\n"
        + "        list<int>::iterator iter = maxLenSets[d].begin();\n"
        + "        //扫描d长度List，代替相同的元素\n"
        + "        for(; iter != maxLenSets[d].end(); ++iter) if(val[*iter] == val[i])\n"
        + "        {\n"
        + "                maxLenSets[d].erase(iter);\n"
        + "                break;\n"
        + "        }\n"
        + "        maxLenSets[d].push_back(i);\n"
        + "        //同样d = 1的元素由于没有前驱，所以需要特殊处理\n"
        + "        if(d == 1)\n"
        + "        {\n"
        + "            countv[i] = 1;\n"
        + "            continue;\n"
        + "        }\n"
        + "        //计算countv[i]\n"
        + "        iter = maxLenSets[d - 1].begin();\n"
        + "        for(; iter != maxLenSets[d - 1].end(); ++iter) if(val[*iter] > val[i])\n"
        + "            countv[i] += countv[*iter];\n"
        + "    }\n"
        + "    printf(\"%d %d\\n\", maxTailLen, countv[n + 1]);\n"
        + "    return 0;\n"
        + "}\n"
        + "\n";
    job.setPrintable(new HelloWorldPrinter(text));
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
