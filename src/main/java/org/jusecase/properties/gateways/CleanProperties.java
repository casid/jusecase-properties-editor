package org.jusecase.properties.gateways;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;


/**
 * Copied internals from java.util.Properties
 * - omits creation date line
 * - does not escape !, :, #,
 */
public class CleanProperties extends Properties {

   /** A table of hex digits */
   private static final char[] hexDigit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

   /**
    * Convert a nibble to a hex character
    * @param   nibble  the nibble to convert.
    */
   private static char toHex( int nibble ) {
      return hexDigit[(nibble & 0xF)];
   }

   @Override
   public synchronized Enumeration<Object> keys() {
      return Collections.enumeration(new TreeSet<>(super.keySet()));
   }

   public void storeSpecial( OutputStream out, String lineSeparator ) throws IOException {
      store0(new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8)), false, lineSeparator);
   }

   /**
    * Converts unicodes to encoded &#92;uxxxx and escapes
    * special characters with a preceding slash
    */
   private String saveConvert( String theString, boolean escapeSpace, boolean escapeUnicode ) {
      int len = theString.length();
      int bufLen = len * 2;
      if ( bufLen < 0 ) {
         bufLen = Integer.MAX_VALUE;
      }
      StringBuilder outBuffer = new StringBuilder(bufLen);

      for ( int x = 0; x < len; x++ ) {
         char aChar = theString.charAt(x);
         // Handle common case first, selecting largest block that
         // avoids the specials below
         if ( (aChar > 61) && (aChar < 127) ) {
            if ( aChar == '\\' ) {
               outBuffer.append('\\');
               outBuffer.append('\\');
               continue;
            }
            outBuffer.append(aChar);
            continue;
         }
         switch ( aChar ) {
         case ' ':
            if ( x == 0 || escapeSpace )
               outBuffer.append('\\');
            outBuffer.append(' ');
            break;
         case '\t':
            outBuffer.append('\\');
            outBuffer.append('t');
            break;
         case '\n':
            outBuffer.append('\\');
            outBuffer.append('n');
            break;
         case '\r':
            outBuffer.append('\\');
            outBuffer.append('r');
            break;
         case '\f':
            outBuffer.append('\\');
            outBuffer.append('f');
            break;
         case '=': // Fall through
         case ':': // Fall through
         case '#': // Fall through
         case '!':
            outBuffer.append(aChar);
            break;
         default:
            if ( ((aChar < 0x0020) || (aChar > 0x007e)) & escapeUnicode ) {
               outBuffer.append('\\');
               outBuffer.append('u');
               outBuffer.append(toHex((aChar >> 12) & 0xF));
               outBuffer.append(toHex((aChar >> 8) & 0xF));
               outBuffer.append(toHex((aChar >> 4) & 0xF));
               outBuffer.append(toHex(aChar & 0xF));
            } else {
               outBuffer.append(aChar);
            }
         }
      }
      return outBuffer.toString();
   }

   private void store0( BufferedWriter bw, boolean escUnicode, String lineSeparator ) throws IOException {
      for ( Enumeration<?> e = keys(); e.hasMoreElements(); ) {
         String key = (String)e.nextElement();
         String val = (String)get(key);
         key = saveConvert(key, true, escUnicode);
         /* No need to escape embedded and trailing spaces for value, hence
          * pass false to flag.
          */
         val = saveConvert(val, false, escUnicode);
         bw.write(key);
         bw.write("=");
         bw.write(val.trim());
         bw.write(lineSeparator);
      }

      bw.flush();
   }
}
