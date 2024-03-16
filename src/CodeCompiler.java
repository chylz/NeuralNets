import java.io.IOException;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Arrays;

/*
 * Author: Luke Zeng
 * Date of Creation: 3/12/2024
 *
 * Tool for compiling LOML into binary using a given list of commands from a file.  (assumes the bytecode assigned to each
 * command is based on the 1-based row number of the command, with 1 command on each line).
 *
 * LOML is a non-case-sensitive language.
 *
 * Command file structure is described below:
 *    Top level commands (in brackets) are to be prefixed with a "!"
 *    Lower level commands (regular lines) are to be prefixed with a "?".
 *    A top level command will be followed by all of its subcommands (lower level commands that can be used inside a top level block.
 *    The top command will determine the behavior of the block of code following it until the next top level command.
 */

public class CodeCompiler
{
   public String compileOutput; // Output string of the compiler (warnings, errors).
   public byte[] byteCode;      // stores the given bytecode as
   public boolean compileFile;  // File to compile the code to.

   public String[] commands;
   public String[][] commandargs;
   public final String CMDFILE;
   public int numCommands;

   public int line; // the current line the compiler is on when scanning (used for error reporting)

   public static void main(String[] args)
   {
      CodeCompiler cc = new CodeCompiler(args[0]);
      cc.compile(args[1]);
      System.out.println(cc.compileOutput);
   }

/*
 * Creates a new Code Compiler with the given command file.
 */
   public CodeCompiler(String command)
   {
      CMDFILE = command;
      compileOutput = "";
      boolean cp = true; // flag for if the command file is present or not.
      String[] commandlines = null;
      String[] curline; // current line split up into space separated tokens.
      int numargs;

      try
      {
         commandlines = removeEmpty(getFileString(CMDFILE).split("\n")); // splits the line-separated and lowercased command lines into array.
         numCommands = commandlines.length;
      }
      catch (IOException e)
      {
         printcline("Command file not found. Please give a valid command file");
         cp = false;
      }
      commands = new String[numCommands];
      commandargs = new String[numCommands][];

      for (int comman = 0; comman < numCommands; comman++) //loop through each line of commands, parse out the command and its args.
      {
         curline = removeEmpty(commandlines[comman].split(" "));
         numargs = curline.length;

         commandargs[comman] = new String[numargs-1];
         if(numargs > 0 && curline[0].charAt(0) == '?') //only split parse if the first token on the line is a lower level command.
         {
            commands[comman] = curline[0];

            for (int arg = 0; arg < numargs-1; arg++)
            {
               commandargs[comman][arg] = curline[arg+1]; // loop over numargs-1 because one of the arguments on the line is the command.
            }
         }
         else if (numargs>0)
         {
            commands[comman] = curline[0];
         }
         //System.out.println("DEBUG" + commands[comman]);
      }
   }

/*
 * Compiles the given file name using the LOML (Luke's Obvious Minimal Language) standard.
 * file format should be "*.loml".
 */
   public int compile(String filename)
   {
      int exit = 0;
      String code = ""; // initializes code to an empty string before trying to fill it with the contents of a file.
      boolean canCompile = true; // flag to see if the current code can be compiled (basically if the file exists).
      String[] filen = removeEmpty(filename.split("\\."));
      String outputfile = "";
      DataOutputStream out;

      if(filen.length == 0)
      {
         canCompile = false;
      }
      else
      {
         outputfile = filen[0] + ".bin";
         File compiled = new File(outputfile);
         try
         {
            compiled.createNewFile();
         }
         catch (IOException e)
         {
            printcline("error in output file creaton");
         }

      }

      if(!filen[1].equals("loml"))
      {
         canCompile = false;
         printcline("Invalid file extension " + filen[1] + " expected: .loml");
      }
      try
      {
         code = getFileString(filename);
      }
      catch (IOException e)
      {
         printcline("code file not found, please give a valid file name");
         canCompile = false;
      }

      if(canCompile)
      {
         exit = compileCode(code);
      }
      //System.out.println("DEBUG # of bytes written" + byteCode.length);
      try
      {
         out = new DataOutputStream(new FileOutputStream(outputfile));
         out.write(byteCode);
         out.close();
      }
      catch (IOException e)
      {
         printcline("Error in output file creation or output writing. ");
      }



      printcline("Code compiled with exit code " + exit); // print exit code to compile output
      return exit;
   } // public int compile(String filename)



/*
 * Compiles the given code
 */
   public int compileCode(String code)
   {
      int exit = 0;
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      DataOutputStream ds = new DataOutputStream(bytes);

      int numLines;
      String[] sections = removeEmpty(code.split("\\["));

      int numsec = sections.length; // number of "sections" in the code ([section title])
      //System.out.println("DEBUG " + numsec + " " + sections[1]);
      String[] lines;               // lines in a "section".
      String[] expr;                // expressions on a line.
      int curSection;               // command type of the current section (higher level command)
      int curCommand;               // current command type (on each line) (lower level command).
      int numExp;

      line = 0;                     // current line of the compiler.

      for (int section = 0; section < numsec; section++)
      {
         lines = removeEmpty(sections[section].split("\n"));
         numLines = lines.length;
         curSection = higherCommand(parseLine(lines[0], true)); // determine command type of higher command.
         //System.out.println("DEBUG " + parseLine(lines[0], true) + " " + curSection);
         if(curSection == -1)
         {
            continue; // don't add section if section header is not recognized.
         }

         try
         {
            ds.writeInt(curSection);
         }
         catch (IOException e)
         {
            printcline("You broke the compiler"); // this shouldn't IOException because we are writing to a bytearray output stream
         }

         for (int linesec = 1; linesec < numLines; linesec++)
         { // current line in the current section (starting from line 1 because section header is already processed)
            line++;
            expr = removeEmpty(parseLine(lines[linesec], false).split(" ")); // first parse line to remove any comments from line
            if(expr.length == 0) // don't process line if empty.
            {
               continue;
            }
            curCommand = lowerCommand(expr[0], curSection);
            //System.out.println("DEBUG " + expr.length + " " + expr[0]);

            if(curCommand == -1)
            {
               printcline("unrecognized command on line " + line);
               continue; // don't add command if unrecognized.
            }

            numExp = expr.length;

            try
            {
               ds.writeInt(curCommand);
            }
            catch (IOException e)
            {
               printcline("You broke the compiler"); // this shouldn't be called because we are writing to a bytearray
            }

/*
 * check if number of arguments matches expected number (minus 1 on expr.length because that includes command)
 */
            if (commandargs[curCommand-1].length != expr.length-1) // command args is 0-indexed, commands are 1-indexed so we -1.
            {
               continue;
            }

            for (int exp = 1; exp < numExp; exp++) // start from second token because first token is command.
            {
               try
               {
                  //System.out.println("DEBUG " + expr[exp]);
                  writeArg(ds, expr[exp], curCommand, exp);
               }
               catch (IOException e)
               {
                  printcline("this shouldn't be happening. You broke the compiler");
               }
            } // for (int exp = 1; exp < numExp; exp++)

            try // sections are opened and closed by a flag.
            {
               ds.writeInt(curSection);
            }
            catch (IOException e)
            {
               printcline("You broke the compiler"); // this shouldn't IOException because we are writing to a bytearray output stream
            }

         } // for (int linesec = 1; linesec < numLines; linesec++)

         line++;
      } // for (int section = 0; section < numsec; section++)

      try
      {
         ds.writeInt(0); // ending command, signal to interpreter that the program has ended.
      }
      catch (IOException e)
      {
         printcline("this should not be happening, contact developer with compile output and code");
      }

      byteCode = bytes.toByteArray();

      return exit;
   } // public int compileCode(String code)

/*
 * parses the given line (String) into just the expression (ignoring comments).
 * the parameter first just means if the line is the first line in a section (the one in brackets).
 */
   public String parseLine(String s, boolean first)
   {
      int index = 0;
      int endReason = 0;    // reason for termination of parse (was the end of the expression a newline (1), a bracket (2) or a pound (3))
      boolean done = false; // flag to finish parsing.

      while (!done) //keep parsing the expression until a closing bracket or newline or a pound.
      {

         if (index >= s.length()) // if end of line, simply return whole line as a
         {
            endReason = 1;
            done = true;
         }
         else if (s.charAt(index) == '#')
         {
            endReason = 3;
            done = true;
         }
         else if (s.charAt(index) == ']')
         {
            endReason = 2;
            done = true;
         }
         else
         {
            index++;
         }
      } // while(!done)

      if (endReason == 2 && !first) // report an error to compile output if an extra close bracket is found not closing the first line.
      {
         printcline("Unrecognized \']\' on line " + line);
      }
      if (endReason !=2 && first) // report an error to compile output if first line doesn't have a close bracket.
      {
         printcline("Expected \']\' after expression on line " + line);
      }
      //System.out.println("DEBUG " + s.substring(0, index));
      return s.substring(0, index);
   } // public String parseLine(String s, boolean first)

/*
 * interprets the higher-level command type of the given string in LOML to compile into LOML bytecode on the given line for error reporting.
 */
   public int higherCommand(String cmd)
   {
      int type = -1; // an impossible command value to detect if the given command is not found.

      for (int command = 1; command <= numCommands; command++)
      {
/*
 * removes the front character to compare the current command with the given command and ensures the given command is a valid
 * top level command.
 */
         //System.out.println("DEBUG" + commands[command-1].substring(0) + " " + cmd);
         if (commands[command-1].charAt(0) == '!' && cmd.equals(commands[command-1].substring(1)))
         {
            type = command;
         }
      } // for (int command = 1; command <= numCommands; command++)

      if(type == -1) // if command not found, report it to compiel output.
      {
         printcline("Top level Command " + cmd + " on line " + line + " unrecognized");
      }

      return type;
   } // public int higherCommand(String cmd)

/*
 * interprets the bytecode representations of lower level commands given the current higher level command
 * and command string.
 */
   public int lowerCommand(String cmd, int higher)
   {
      int type = -1;          // an impossible command value to detect if the given command is not found.
      int command = higher+1; //start looking for lower level command starting from higher level command.

      while (command <= numCommands && commands[command-1].charAt(0) == '?') // starts at the higher level command
      {
/*
 * removes the front character to compare the current command (which has hierarchy indicators
 * with the given command and ensures the given command is a valid top level command.
 */
         if (cmd.equals(commands[command-1].substring(1)))
         {
            type = command;
         }
         command++;
      } // while (command <= numCommands && commands[command-1].charAt(0) == '?')

      if(type == -1) // if command not found, report it to compiel output.
      {
         printcline("Lower level Command " + cmd + " on line " + line + " unrecognized");
      }

      return type;
   } // public int lowerCommand(String cmd, int higher)

/*
 * returns the contents of a file as a string.
 */
   public String getFileString(String filename) throws IOException
   {
      BufferedReader reader = new BufferedReader(new FileReader(filename));
      StringBuilder sb = new StringBuilder();

      String curline = reader.readLine();
      while (curline != null) // reads the whole file as one string.
      {
         sb.append(curline + "\n"); // Add current line to code string with newline to keep with syntax.
         curline = reader.readLine();
      }

      return sb.toString().toLowerCase(); // lowercase to de-type sensitize commands.
   } // public String getFileString(String filename)

/*
 * writes the given argument of the given command to the given output stream with the correct data typing.
 */
   public void writeArg(DataOutputStream da, String arg, int command, int argnum) throws IOException
   {
      String curarg = commandargs[command-1][argnum-1]; // commands, argnum are 1 indexed, commandargs, argnum are zero indexed so -1

      try
      {
         //printcline("DEBUG " + curarg);
         if (curarg.equals("double"))
         {
            da.writeDouble(Double.parseDouble(arg));
         }
         else if (curarg.equals("int"))
         {
            da.writeInt(Integer.parseInt(arg));
         }
         else if (curarg.equals("string"))
         {
            int strlength = arg.length();
            da.writeInt(strlength); // write string length to indicate length of string to interpreter.

            da.writeChars(arg);     // write whole string.
         }
         else
         {
            printcline("unrecognized data type for command " + commands[command-1] + " try changing commands file to one of three" +
                            " recognized data types (double, int, String).");
         }
      }
      catch (NumberFormatException e)
      {
         printcline ("data type mismatch for command " + commands[command-1] + " expected " + commandargs[command-1][argnum]);
      }
   }

/*
 * removes all empty strings from an array of strings (helps clean up string split command)
 * Also moves all nonempty strings to the front of the current array
 */
   public String[] removeEmpty(String[] arr)
   {
      int index = 0;
      int length = arr.length;
      for (int ind=0; ind < length; ind++)
         if (arr[ind].length() != 0)
         {
            arr[index++] = arr[ind];
         }

      return Arrays.copyOf(arr, index);
   }

/*
 * prints the given string to the compile output followed by line break.
 */
   public void printcline(String out)
   {
      compileOutput += out + "\n";
   }

/*
 * prints the given string to the compile output.
 */
   public void printc(String out)
   {
      compileOutput += out;
   }
}
