/* The Computer Language Benchmarks Game
   http://shootout.alioth.debian.org/
 
   contributed by Leonhard Holz
   thanks to James McIlree for the fragmentation idea
*/

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class knucleotide
{
   private static final byte A = 0;
   private static final byte T = 1;
   private static final byte C = 2;
   private static final byte G = 3;
   private static final byte BITS_PER_CHAR = 2;
   private static final byte CHAR_BIT_MASK = 3;
   
   private static final int CHUNK_SIZE = 1024 * 1024 * 2;
   private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
   
   private static void writeFrequencies(SortedSet<Fragment> set, StringBuilder sb)
   {
      int n = 0;
      Iterator<Fragment> i;
      
      i = set.iterator();
      while (i.hasNext()) {
         n += i.next().count;
      }
      
      i = set.iterator();
      while (i.hasNext()) {
         Fragment fragment = i.next();
         sb.append(fragment.toString()).append(String.format(" %.3f", fragment.count * 100.0f / n)).append("\n");
      }
   
      sb.append("\n");
   }

   public static void main(String[] args) throws IOException, InterruptedException, ExecutionException
   {
      boolean inGenom = false;
      List<Encoder> encoders = new ArrayList<Encoder>();
      ExecutorService service = Executors.newFixedThreadPool(NUMBER_OF_CORES);
      
      int read = CHUNK_SIZE, i;
      while (read == CHUNK_SIZE) {
         i = 0;
         byte[] buffer = new byte[CHUNK_SIZE];
         read = System.in.read(buffer);
         if (!inGenom) {
            for (; i < read; i++) if (buffer[i] == '\n' && i + 6 < buffer.length) {
               if (buffer[++i] == '>' && buffer[++i] == 'T' && buffer[++i] == 'H' && buffer[++i] == 'R' && buffer[++i] == 'E' && buffer[++i] == 'E') {
                  while (buffer[i++] != '\n');
                  inGenom = true;
                  break;
               }
            }
         }
         if (inGenom) {
            Encoder encoder = new Encoder(buffer, i, read);
            encoders.add(encoder);
            service.execute(encoder);
         }
      }

      Counter size1 = new Counter(encoders, 1);
      service.execute(size1);
      Counter size2 = new Counter(encoders, 2);
      service.execute(size2);

      String[] fragments = { "GGT", "GGTA", "GGTATT" };
      Counter[] counters = new Counter[fragments.length];
      for (i = 0; i < fragments.length; i++) {
         counters[i] = new Counter(encoders, fragments[i].length());
         service.execute(counters[i]);
      }
      
      String[] largeFragments = { "GGTATTTTAATT", "GGTATTTTAATTTATAGT" };
      List<OffsetCounter>[] counterMap = new ArrayList[largeFragments.length];
      for (i = 0; i < largeFragments.length; i++) {
         counterMap[i] = new ArrayList<OffsetCounter>();
         for (int j = 0; j < largeFragments[i].length(); j++) {
            OffsetCounter counter = new OffsetCounter(encoders, j, largeFragments[i].length());
            counterMap[i].add(counter); 
            service.execute(counter);
         }
      }

      service.shutdown();
      
      StringBuilder out = new StringBuilder(1024);
      writeFrequencies(new TreeSet<Fragment>(size1.get().values()), out);
      writeFrequencies(new TreeSet<Fragment>(size2.get().values()), out);

      for (i = 0; i < fragments.length; i++) {
         Fragment fragment = new Fragment(fragments[i]);
         out.append(counters[i].get().get(fragment).count).append("\t").append(fragments[i]).append("\n");
      }
      
      for (i = 0; i < largeFragments.length; i++) {
         int count = 0;
         Fragment fragment = new Fragment(largeFragments[i]);
         for (int j = 0; j < largeFragments[i].length(); j++) {
            Fragment counter = counterMap[i].get(j).get().get(fragment);
            if (counter != null) {
               count += counter.count;
            }
         }
         out.append(count).append("\t").append(largeFragments[i]).append("\n");
      }
   
      System.out.print(out.toString());
   }

   private static class Encoder implements Runnable
   {
      private byte[] src;
      private int start, end;
      private boolean done = false;
      private List<byte[]> result = new ArrayList<byte[]>();
      
      private static final int CHUNK_SIZE = 1024 * 250;
      
      private Encoder(byte[] src, int start, int end)
      {
         this.src = src;
         this.start = start;
         this.end = end;
      }

      public List<byte[]> get() throws InterruptedException, ExecutionException
      {
         while (!done) try {
            Thread.sleep(100);
         } catch (InterruptedException e) {
            // ignored
         }
         return result;
      }

      public void run()
      {
         int p = 0;
         byte[] encoded = new byte[CHUNK_SIZE];
         
         for (int i = start; i < end; i++) {
            byte c = src[i];
            if (c == 'a' || c == 'A') {
               encoded[p++] = A;
               if (p == encoded.length) {
                  result.add(encoded);
                  encoded = new byte[CHUNK_SIZE];
                  p = 0;
               }
            } else if (c == 't' || c == 'T') {
               encoded[p++] = T;
               if (p == encoded.length) {
                  result.add(encoded);
                  encoded = new byte[CHUNK_SIZE];
                  p = 0;
               }
            } else if (c == 'c' || c == 'C') {
               encoded[p++] = C;
               if (p == encoded.length) {
                  result.add(encoded);
                  encoded = new byte[CHUNK_SIZE];
                  p = 0;
               }
            } else if (c == 'g' || c == 'G') {
               encoded[p++] = G;
               if (p == encoded.length) {
                  result.add(encoded);
                  encoded = new byte[CHUNK_SIZE];
                  p = 0;
               }
            }
         }
      
         if (p != 0) {
            byte[] last = new byte[p];
            System.arraycopy(encoded, 0, last, 0, p);
            result.add(last);
         }
      
         done = true;
      }
   }
   
   private static class Fragment implements Comparable<Fragment>
   {
      private int count = 1;
      private int charsInValue;
      private long value;

      public Fragment(int size)
      {
         this.charsInValue = size;
      }
      
      public Fragment(String s)
      {
         for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == 'A') {
               value = value << BITS_PER_CHAR | A;
               charsInValue++;
            } else if (c == 'T') {
               value = value << BITS_PER_CHAR | T;
               charsInValue++;
            } else if (c == 'G') {
               value = value << BITS_PER_CHAR | G;
               charsInValue++;
            } else if (c == 'C') {
               value = value << BITS_PER_CHAR | C;
               charsInValue++;
            }
         }
      }
      
      @Override
      public int hashCode()
        {
           return (int) value;
        }

      @Override
        public boolean equals(Object o)
        {
           Fragment f = (Fragment) o;
           return f.value == value;
        }
       
        public int compareTo(Fragment o)
        {
           return o.count - count;
        }
   
        public String toString()
        {
           long chars = value;
           StringBuilder s = new StringBuilder();
           for (int i = 0; i < charsInValue; i++) {
              int c = (int) (chars & CHAR_BIT_MASK);
              if (c == A) {
                 s.insert(0, 'A');
              } else if (c == T) {
                 s.insert(0, 'T');
              } else if (c == G) {
                 s.insert(0, 'G');
              } else if (c == C) {
                 s.insert(0, 'C');
              }
              chars >>= BITS_PER_CHAR;
           }
           return s.toString();
        }
   }

   private static class Counter implements Runnable
   {
      private int fragmentSize;
      private boolean done = false;
      private List<Encoder> nucleotides;
      private Map<Fragment, Fragment> fragments = new HashMap<Fragment, Fragment>();
      
      private Counter(List<Encoder> nucleotides, int fragmentSize)
      {
         this.nucleotides = nucleotides;
         this.fragmentSize = fragmentSize;
      }
      
      public Map<Fragment, Fragment> get() throws InterruptedException, ExecutionException
      {
         while (!done) try {
            Thread.sleep(100);
         } catch (InterruptedException e) {
            // ignored
         }
         return fragments;
      }

      public void run()
      {
         long dna = 0, bitmask = 0;

         for (int i = 0; i < fragmentSize; i++) {
            bitmask = bitmask << BITS_PER_CHAR | CHAR_BIT_MASK;
         }

         try {
         
            int j = 0;
            byte[] buffer = nucleotides.get(0).get().get(0);

            for (; j < fragmentSize - 1; j++) {
               dna = dna << BITS_PER_CHAR | buffer[j];
            }

            Fragment fragment = new Fragment(fragmentSize);
            Iterator<Encoder> fit = nucleotides.iterator();

            while (fit.hasNext()) {
               Encoder encoder = fit.next();
               Iterator<byte[]> bit = encoder.get().iterator();
            
               while (bit.hasNext()) {
                  buffer = bit.next();
               
                  for (; j < buffer.length; j++) {
                     dna = dna << BITS_PER_CHAR | buffer[j];
                     fragment.value = dna & bitmask;
                     Fragment counter = fragments.get(fragment);
                  
                     if (counter != null) {
                        counter.count++;
                     } else {
                        fragments.put(fragment, fragment);
                        fragment = new Fragment(fragmentSize);
                     }
                  }
                  j = 0;
               }
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
         
         done = true;
      }
   }

   private static class OffsetCounter implements Runnable
   {
      private int offset;
      private int fragmentSize;
      private boolean done = false;
      private List<Encoder> nucleotides;
      private Map<Fragment, Fragment> fragments = new HashMap<Fragment, Fragment>();
      
      private OffsetCounter(List<Encoder> nucleotides, int offset, int fragmentSize)
      {
         this.offset = offset;
         this.nucleotides = nucleotides;
         this.fragmentSize = fragmentSize;
      }
      
      public Map<Fragment, Fragment> get() throws InterruptedException, ExecutionException
      {
         while (!done) try {
            Thread.sleep(100);
         } catch (InterruptedException e) {
            // ignored
         }
         return fragments;
      }

      public void run()
      {
         long bitmask = 0;

         for (int i = 0; i < fragmentSize; i++) {
            bitmask = bitmask << BITS_PER_CHAR | CHAR_BIT_MASK;
         }

         try {
         
            byte[] buffer;
            int j = offset;
            Fragment fragment = new Fragment(fragmentSize);
            Iterator<Encoder> fit = nucleotides.iterator();

            while (fit.hasNext()) {
               Encoder encoder = fit.next();
               Iterator<byte[]> bit = encoder.get().iterator();
            
               while (bit.hasNext()) {
                  buffer = bit.next();
               
                  for (; j < buffer.length - fragmentSize; j+= fragmentSize) {
                     long dna = 0;
                     for (int i = 0; i < fragmentSize; i++) {
                        dna = dna << BITS_PER_CHAR | buffer[j + i];
                     }
                     fragment.value = dna & bitmask;
                     Fragment counter = fragments.get(fragment);
                  
                     if (counter != null) {
                        counter.count++;
                     } else {
                        fragments.put(fragment, fragment);
                        fragment = new Fragment(fragmentSize);
                     }
                  }
                  j -= buffer.length - fragmentSize;
               }
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
         
         done = true;
      }
   }
}
