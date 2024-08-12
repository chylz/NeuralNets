/*
 * converts a given config file back into a big image file.
 */
import java.io.*;

public class convertback
{
   /*
    * args: numCases unitspercase filenamein filenameout
    */
   public static void main(String[] args) throws IOException
   {
      String filename = args[2];
      String file2 = args[3];
      int numCases = Integer.parseInt(args[0]);
      int numUnits = Integer.parseInt(args[1]);

      byte[] b = new byte[numCases*numUnits*8];
      File f = new File(file2);
      f.createNewFile();
      DataInputStream da = new DataInputStream(new FileInputStream(filename));

      da.read(b);

      da = new DataInputStream(new ByteArrayInputStream(b));
      ByteArrayOutputStream ba = new ByteArrayOutputStream();
      DataOutputStream out = new DataOutputStream(ba);



      for (int i = 0; i < numCases; i++)
      {
         for (int j = 0; j < numUnits; j++)
         {
            out.writeByte((int)(da.readDouble()*255.0));
         }
      }

      out = new DataOutputStream(new FileOutputStream(f));
      out.write(ba.toByteArray());


   }

}