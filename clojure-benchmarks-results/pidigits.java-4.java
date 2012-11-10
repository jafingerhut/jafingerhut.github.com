/**
 * The Computer Language Benchmarks Game
 * http://shootout.alioth.debian.org/

   contributed by Mike Pall
   java port by Stefan Krause
   Data Parallel adaptation by Sassa NF
*/

import java.util.concurrent.*;

public class pidigits {
   final static int FOUR = 0, BQ = 1, BR = 2, BT = 3, // plain int values
                    // GMP integers
                    V = 4, ER1 = 5, Q1 = 6, R1 = 7, T1 = 8, U1 = 9,
                    ES1 = 10, ER = 11, 
                    Q = 12, R = 13, T = 14, U = 15; // these are always available

   final static int SPIN = 1000;

   long [] values = new long[ 16 ];
   Semaphore [] sema = new Semaphore[ values.length ];
   Semaphore allDone = new Semaphore( 0 );
   Semaphore moreWork = new Semaphore( 0 );
   final static int ADD = 0, MUL = 1, DIV_Q_R = 2;

   ExecutorService executor = Executors.newFixedThreadPool( 3 );

   int i;
   StringBuilder strBuf = new StringBuilder(20);
   final int n;

   private pidigits(int n)
   {
      this.n=n;
   }

   public static void acquire( Semaphore s, int permits )
   {
     int i = SPIN;
     while( !s.tryAcquire( permits ) ) if ( --i <= 0 ) break;

     // now, if i <= 0, then the semaphore is definitely not acquired
     if ( i <= 0 )
     {
       try
       {
         s.acquire( permits );
       }
       catch( Exception e )
       {}
     }
   }

   public class exec implements Runnable
   {
     exec [] seq_tasks;
     int instr, dest, op1, op2, op3 = -1;

     public exec( exec[] tasks )
     {
       seq_tasks = tasks;
     }

     public exec( int ins, int d, int o1, int o2 )
     {
       instr = ins; dest = d; op1 = o1; op2 = o2;
     }

     public exec( int ins, int d, int o1, int o2, int o3 )
     {
       this( ins, d, o2, o3 ); op3 = o1;
     }

     public void run()
     {
       _run();
       acquire( moreWork, 1 ); // leave the thread spinning until more work arrives - unparking takes ages on some boxes
     }

     public void _run()
     {
       if ( seq_tasks != null )
       {
         for( exec r: seq_tasks ) r._run();
         allDone.release();
         return;
       }

       // the while loop makes sure the thread doesn't get preempted - don't care about the CPU going wild; it would be idle otherwise anyway
       acquire( sema[ op1 ], 1 ); sema[ op1 ].release();
       acquire( sema[ op2 ], 1 ); sema[ op2 ].release();

       if ( instr == MUL )
       {
         GmpUtil.mpz_mul_si( values[ dest ], values[ op1 ], (int)values[ op2 ] );
       }
       else if ( instr == ADD )
       {
         GmpUtil.mpz_add( values[ dest ], values[ op1 ], values[ op2 ] );
       }
       else if ( instr == DIV_Q_R )
       {
         GmpUtil.mpz_tdiv_qr( values[ dest ], values[ op3 ], values[ op1 ], values[ op2 ] );
         sema[ op3 ].release();
       }

       sema[ dest ].release();
     }
   };

   // compose_r = ( q,r; s,t ) = ( bq, br; bs, bt ) x (q,r; s,t)
   // bs == 0, hence s == 0 and multiplications involving bs and s aren't here (br*s, bt*s)
   // bt == 1 hence multiplications involving bt aren't here (s*bt, t*bt)

   // compose_l = ( q,r; s,t ) = (q,r; s,t) x ( bq, br; bs, bt )
   // extract = ( q*3 + r )/( s*3 + t ) compared to ( q*4 + r )/( s*4 + t )
   // the latter is the same as computing quotient and remainder of ( q*4 + r )/( s*4 + t ); if the remainder is greater or equal to q,
   // then the quotient is the same as of ( 3*q + r )/( s*3 + t ) since s==0
   final exec[] COMPOSE_R = new exec[]{ 
                         new exec( new exec[]{ new exec( MUL, Q1, Q, BQ ),
                                                   new exec( MUL, U1, Q1, FOUR ) } ), // now U is always Q*4
                         new exec( new exec[]{ new exec( MUL, V, T, BR ),
                                                   new exec( ADD, R1, R1, V ) } ),
                         new exec( new exec[]{ new exec( MUL, R1, R, BQ ) } )
                                              };

   final exec[] COMPOSE_L = new exec[]{ 
                         // digit extraction logic here
                         new exec( new exec[]{ new exec( ADD, ES1, U, R ),
                                                   new exec( DIV_Q_R, ER, ER1, ES1, T ) } ), // DIV_Q_R is approx the same cost as two muls
                                                   // so this splits the work roughly equally
                         // compose_l
                         new exec( new exec[]{ new exec( MUL, R1, R, BT ),
                                                   new exec( ADD, R1, R1, V ) } ),
                         new exec( new exec[]{ new exec( MUL, V, Q, BR ),
                                                   new exec( MUL, T1, T, BT ) } ),
                         new exec( new exec[]{ new exec( MUL, Q1, Q, BQ ),
                                                   new exec( MUL, U1, Q1, FOUR ) } ) // now U is always Q*4
                                              };


   private boolean multi_threaded_compute( exec[] code, int bq, int br, int bt, boolean compare )
   {
     allDone.drainPermits();

     for( int i = BQ; i < Q; ++i ) sema[ i ].drainPermits();

     values[ BQ ] = bq;
     sema[ BQ ].release();
     values[ BR ] = br;
     sema[ BR ].release();
     values[ BT ] = bt;
     sema[ BT ].release();

     for( int i = compare ? 1: 0; i < code.length; ++i )
     {
       executor.execute( code[ i ] ); // we are one thread, so skip code[ 0 ], if comparing the remainder is needed
       moreWork.release();
     }

     if ( !compare ) return false;

     code[ 0 ]._run();
     boolean r = GmpUtil.mpz_cmp( values[ ER1 ], values[ Q ] ) >= 0; // ER1 >= Q means the remainder of (4*q+r)/t contains q,
                                                                // and the quotient is the same as (3*q+r)/t
     acquire( allDone, code.length );

     return r;
   }

   /* Print one digit. Returns 1 for the last digit. */
   private boolean prdigit(int y, boolean isWarm)
   {
      strBuf.append(y);
      if (++i % 10 == 0 || i == n) {
         if (i%10!=0) for (int j=10-(i%10);j>0;j--) { strBuf.append(" "); }
         strBuf.append("\t:");
         strBuf.append(i);
        if (isWarm) System.out.println(strBuf);
        strBuf.setLength( 0 ); // clear the contents
      }
      return i == n;
   }

   /* Generate successive digits of PI. */
   void pidigits(boolean isWarm)
   {
     int k = 1;
     for( int i = V; i < values.length; ++i ) values[ i ] = GmpUtil.mpz_init();

     GmpUtil.mpz_set_si( values[ Q ], 1 );
     GmpUtil.mpz_set_si( values[ T ], 1 );
     GmpUtil.mpz_set_si( values[ R ], 0 );
     GmpUtil.mpz_set_si( values[ U ], 4 ); // U = Q*4 - invariant
     values[ FOUR ] = 4;
     for( int i = 0; i < sema.length; ++i ) sema[ i ] = new Semaphore( 0 ); // these are initially unavailable
     sema[ Q ].release(); // these are always avalable
     sema[ R ].release();
     sema[ FOUR ].release();
     sema[ T ].release();
     sema[ U ].release();
     i = 0;
     for (;;) {
       if ( multi_threaded_compute( COMPOSE_L, k, 4*k+2, 2*k+1, true ) ) {
         int y = GmpUtil.mpz_get_si( values[ ER ] );
       
         multi_threaded_compute( COMPOSE_R, 10, -10*y, 1, false ); // compare == false - computation is in background; foreground thread can print 
         boolean r = prdigit(y,isWarm);
         acquire( allDone,  COMPOSE_R.length ); // wait for the COMPOSE_R to complete

         if ( r ) {
           for( int i = V; i < values.length; ++i ) GmpUtil.mpz_clear( values[ i ] ); // don't have to be this nice in a one-shot run
           return;
         }
       } else {
         long g = values[ T ];
         values[ T ] = values[ T1 ];
         values[ T1 ] = g; // to save on init/GC costs
         k++;
       }
       long g = values[ Q ];
       values[ Q ] = values[ Q1 ];
       values[ Q1 ] = g;
       g = values[ R ];
       values[ R ] = values[ R1 ];
       values[ R1 ] = g;
       g = values[ U1 ];
       values[ U1 ] = values[ U ];
       values[ U ] = g;
     }
   }

   public static void main(String[] args){
      pidigits m = new pidigits(Integer.parseInt(args[0]));
      //for (int i=0; i<19; ++i) m.pidigits(false);
      m.pidigits(true);

      System.exit(0);
   }
}

class GmpUtil {
   static {
      System.loadLibrary("jpargmp");
   }
   static native long mpz_init();

   static native void mpz_clear(long src);

   static native void mpz_mul_si(long dest, long src,
         int val);

   static native void mpz_add(long dest, long src,
         long src2);

   static native void mpz_set_si(long src, int value);

   static native int mpz_get_si(long src);

   static native int mpz_cmp(long dest, long src);

   static native void mpz_tdiv_qr(long q, long r, long n,
         long d);
}
