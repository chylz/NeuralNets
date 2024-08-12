import java.io.*;
/*
 * Author: Luke Zeng
 * DOC: 5/8/2024
 *
 * Description: Converts passed command line files into activations.
 */


public class ImageFile
{
   public static final int FINALHEIGHT = 100;
   public static final int FINALWIDTH = 100;
   public static final String OUT_FILE = "out.txt";

/*
 * converts all passed files to bin files, processes them, then back to bmp and activations.
 */
   public static void main(String[] args) throws IOException
   {
      int num = 0;

      PrintWriter pw = new PrintWriter(new FileWriter(OUT_FILE));
      for (String s : args)
      {
         pw.println("Cur File: " + s);

         String[] a = {s, num+".bin"};
         BMP2OneByte.main(a);
         int height = BMP2OneByte.bmpInfoHeader_biHeight;
         int width = BMP2OneByte.bmpInfoHeader_biWidth;

         String[] b = {String.valueOf(width), String.valueOf(height), num + ".bin"};
         ToIntArray.main(b);

         String[] c = {"bw", String.valueOf(FINALWIDTH), String.valueOf(FINALHEIGHT), "modified" + num + ".bin", "processed_" + num + ".bmp"};
         BGR2BMP.main(c);

         num++;
      }
      String[] nums = new String[num];
      for (int i = 0; i < num; i++)
      {
         nums[i] = "modified" + i + ".bin";
      }
      Aggregate.main(nums);

      pw.close();
   }
}