//        Oblig 2 - Johannes Christensen (johannec)
//        Hvordan kjøre og kompilere programmet:
//        > Javac Oblig2.java && Java Oblig2 <Insert number here>

import java.util.*;
import java.util.Arrays;

/*
	@Oblig2 (main)
	inneholder oppstart av de andre klassene, samt pen utprinting med tid.
*/
public class Oblig2 {
	public static void main(String[] args) {
		int length = Integer.parseInt(args[0]);
		long maxNumber = (long)length;	
		
		System.out.println("_______________SEKVENSIELL:_______________");
		long t = System.nanoTime();
		SeqEratosthenesSil seq = new SeqEratosthenesSil(length);
		System.out.println("Tid brukt paa SekvensiellEratosthenes: " + (System.nanoTime()-t)/1000000.0 + "ms.");
		byte[] seqArr = seq.getByteArr();
		for (long i = 1; i <= 100; i++) {
			ArrayList<Long> list = seq.factorize((maxNumber * maxNumber) - i);
			if(i < 6 || i > 94) {	
				System.out.print(((maxNumber * maxNumber) - i) + " = ");
				int j = 0;
				for (long l : list) {
					if(j++ == 0)
					System.out.print(l);
					else
					System.out.print(" * " + l);
				}
				System.out.println("");
			}
		}
		System.out.println("Tid brukt paa SekvensiellFaktorisering: " + (System.nanoTime()-t)/1000000.0 + "ms.");
		
		t = System.nanoTime();
		ParaEratosthenesSil par = new ParaEratosthenesSil(length);
		System.out.println("_______________PARALLELL:_______________");
		System.out.println("Tid brukt paa ParallellEratosthenes: " + (System.nanoTime()-t)/1000000.0 + "ms.");

		byte[] parArr = par.getByteArr();

		System.out.println("Are the arrays equals?: " + Arrays.equals(seqArr, parArr));


		for (long i = 1; i <= 100; i++) {
			ArrayList<Long> list = par.parFactorize((maxNumber * maxNumber) - i);
			if(i < 6 || i > 94) {
				System.out.print(((maxNumber * maxNumber) - i) + " = ");
				int j = 0;
				for (long l : list) {
					if(j++ == 0)
					System.out.print(l);
					else
					System.out.print(" * " + l);
				}
				System.out.println("");
			}
		}
		System.out.println("Tid brukt paa ParallellFaktorisering: " + (System.nanoTime()-t)/1000000.0 + "ms.");
	}
}
/**
* Implements the byteArray of length 'maxNum' [0..maxNum/16 ]
*   1 - true (is prime number)
*   0 - false
*  can be used up to 2 G Bits (integer range)
*  16 numbers, i.e. 8 odd numbers per byte (byteArr[0] represents 1,3,5,7,9,11,13,15 )
*
*	Class SeqEratosthenesSil:
*   To konstruktører: En for vanlig sekvensiell gjennomkjøring, og en for tråder som kommer i den parallelle delen.
*/ 
class SeqEratosthenesSil {
	byte [] byteArr ;           // byteArr[0] represents the 8 integers:  1,3,5,...,15, and so on
	int  maxNum;               // all primes in this bit-array is <= maxNum
	final  int [] bitMask = {1,2,4,8,16,32,64,128};  // kanskje trenger du denne
	final  int [] bitMask2 ={255-1,255-2,255-4,255-8,255-16,255-32,255-64, 255-128}; // kanskje trenger du denne
	int start, end;

	SeqEratosthenesSil (int maxNum) {
        this.maxNum = maxNum;
		byteArr = new byte [(maxNum/16)+1];
		this.start = 0;
		this.end = 0;
		setAllPrime();
        generatePrimesByEratosthenes();

    } // end konstruktor ErathostenesSil

    SeqEratosthenesSil (int maxNum, int start, int end, byte[] byteArr) {
    	this.start = start;
    	this.end = end;
    	this.maxNum = end;
    	this.byteArr = byteArr;

    	setAllPrime();
    	generatePrimesByEratosthenesWithRange();
    }	

    //Hjelpemetode for å hente byteArr-variabelen.
    byte[] getByteArr() {
    	return byteArr;
    }

    //Metode for å sette alle tallverdier i byteArr til 1 (setter alle til primtall), så vi kan krysse ut de som ikke er primtall.
	void setAllPrime() {
		for (int i = 0; i < byteArr.length; i++) {
			byteArr[i] = (byte)255;
	    }
	}

	//Krysser ut verdier i byteArr ved å sette bit-en på plassen = 0.
    void crossOut(long i) {
    	int byteNr = ((int)i / 16);
    	int bitNr = ((int) i % 16) / 2;
    	byteArr[byteNr] &= ~ (128 >> bitNr);
	} 

	//Metode for å sjekke om tallet er et primtall eller ikke.
    boolean isPrime (long i) {
      	if(i == 2) {
      		return true;
      	}
    	if(isEven(i) || isTooGreat(i)) {
    		return false;
      	}
      	int byteNr = ((int)i/16); 	//indeks til hvilket byteArr tallet i ligger i
      	int bitNr = ((int)i % 16)/2;					//indeks til bitposisjonen inni byteArrayet

      	/*
		isPrime(5);
		byteNr = 5/16 = 0;
		bitNr = 5%16/2 = 2;

		1 3 5 7 9 11 13 15
		0 1 1 1 0 1  1  0

		128 = 1000 0000
		bitshift 128 med bitNr = verdien til tallet
      	*/
		return (byteArr[byteNr] & 128 >> bitNr) != 0;
    }

    //Hjelpemetode for isPrime. Sjekker om det er et partall (fordi alle partall over 2 ikke er primtall, bruker jeg denne sjekken for å spare tid.)
	private boolean isEven (long i) {
		return (i % 2 == 0);
	}

	//Hjelpemetode for isPrime. Sjekker om verdien er kommet over maxNum, for i såfall skal programmet terminere.
	private boolean isTooGreat(long i) {
		return i > maxNum;
	}

	ArrayList<Long> factorize (long num) {
		/*
		Har et array for å holde på alle faktoriseingsverdiene(fakt), så en midlertidig verdi m som er lik num, som vil endres hele tiden.
		Så lengde m ikke er 1, dvs at den bare kan deles på seg selv, så leter jeg etter en ny faktoriserings-verdi.
		Når denne er funnet, legger jeg den til i fakt, deler m på faktoriserings-verdien som ble funnet, og kjører metoden igjen.
		Hvis primefac hadde gitt -1 ville det si at det ikke fantes noen flere faktoriseings-verdier, og vi legger m i fakt og er ferdige.
		*/
		ArrayList <Long> fakt = new ArrayList <Long>();
        long m = num;
       
        while(m != 1) {
			long primeFac = getPrimeFactorizer(m);
			if(primeFac == -1) {
				fakt.add(m);
				return fakt;
			}	
			fakt.add(primeFac);
			m /= primeFac;
        }
		return fakt;
	}// end factorize

	//Hjelpemetode for factorize. Henter verdien tallet i factorize skal faktoriseres med.
	long getPrimeFactorizer(long primeFac) {
		long i = 2;
		while(primeFac % i != 0) {
			i = nextPrime(++i);
			if(i > Math.sqrt(primeFac)) {
        		return -1;
        	}	
		}
		return i;
	}

	//Gir neste primtall.
    long nextPrime(long i) {
    	while(!isPrime(i)) {
    		i++;
    		if (i > maxNum)
    			return maxNum;
    	}
	   	// returns next prime number after number 'i'
        return i;
	}// end nextPrime

	//Metode for å printe ut alle primtall.
	void printAllPrimes(){
		for ( int i = 2; i <= maxNum; i++)
			if (isPrime(i)) System.out.println(" "+i);
	}

	//Metode som krysser ut verdier og multipler av disse etter hvert som programmet kjører for å så sitte igjen med et array som har bare primtall igjen.
	void generatePrimesByEratosthenes() {
		// krysser av alle  oddetall i 'byteArr[]' som ikke er primtall (setter de =0)
		crossOut(1);      // 1 er ikke et primtall
		//Finner et oddetall som ikke er primtall (p) og krysser ut alle multiplene (p*p, p*2p, p*4p...) av denne opp til maxNum
		double max = Math.sqrt(maxNum);
		
		//1. liste er laget: byte byteArr.
		//2. lar p være lik det først primtallet: 2.
		for(long p = 2; p < max; p = nextPrime(++p)) {		
		//3. Stryker ut alle multipler av p som er større eller lik p*p fra listen
			for(int i = 0; i < maxNum; i+=2) { //2, 4, 6, 8....
				long multiple = ((p*p)+(i*p));
				if(multiple > maxNum) {
					break;
				}
				//System.out.println("P: " + p + "\tI: " + i + "\tMultiple:" + multiple);
				if(multiple >= p*p && !isEven(p)) {
					crossOut(multiple);
				}
			}
			//gjenta til p^2 er større enn n
		}
		//4. Finn det første tallet større enn p som står igjen på listen. Dette tallet er det neste primtallet. Sett p lik dette tallet.
		//5. Gjenta trinn 3 og 4 inntil p2 er større enn n.
		//6. Alle gjenværende tall på listen er primtall.

		// < din Kode her, kryss ut multipla av alle primtall <= sqrt(maxNum),
		// og start avkryssingen av neste primtall p med p*p>

	} // end generatePrimesByEratosthenes

	//Samme som over, bare at metoden nå har et valg med start og stopp-verdi.
	void generatePrimesByEratosthenesWithRange() {
		crossOut(1);      // 1 er ikke et primtall
		//Finner et oddetall som ikke er primtall (p) og krysser ut alle multiplene (p*p, p*2p, p*4p...) av denne opp til maxNum
		double max = Math.sqrt(maxNum);
		
		//1. liste er laget: byte byteArr.
		//2. lar p være lik det først primtallet: 2.
		for(long p = 2; p < max; p = nextPrime(++p)) {		
		//3. Stryker ut alle multipler av p som er større eller lik p*p fra listen
			long pstart = start + 1;

			int mul = 0;
			while(pstart == 0 || pstart % p != 0) {
				pstart = ((p*p) + (mul * p));
				mul += 2;
			}

			int multiplicator2 = 0;
			for(long notPrime = pstart; notPrime <= end; multiplicator2 += 2) {
				if(notPrime % 2 != 0) {
					crossOut(notPrime);
				}

				notPrime = ((p*p) + (multiplicator2*p));
			}
			//gjenta til p^2 er større enn n
		}
		//4. Finn det første tallet større enn p som står igjen på listen. Dette tallet er det neste primtallet. Sett p lik dette tallet.
		//5. Gjenta trinn 3 og 4 inntil p2 er større enn n.
		//6. Alle gjenværende tall på listen er primtall.

	}
} // end class EratosthenesSil


class ParaEratosthenesSil {
	int maxNum, availableThreads, elePerThread, remainLastThread;
	byte[] byteArr;
	ArrayList<Long> facts;
	PrimeThread[] pThreads;
	FactThread[] fThreads;

	public ParaEratosthenesSil(int maxNum) {
		byteArr = new byte [(maxNum/16)+1];
		this.maxNum = maxNum;
		availableThreads = Runtime.getRuntime().availableProcessors();

		pThreads = new PrimeThread[availableThreads];

		this.elePerThread = maxNum / availableThreads;
		this.remainLastThread = maxNum % availableThreads;
		this.byteArr = new byte[(maxNum / 16) + 1];

		start();

		for(Thread t : pThreads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void start() {
		for(int i = 0; i < availableThreads; i++) {
			int start = i * elePerThread;
			int end = (i + 1) * elePerThread;

			if(i == availableThreads - 1) {
				end += remainLastThread;
			}

			pThreads[i] = new PrimeThread(i, start, end);
			pThreads[i].start();
		}
	}

	byte[] getByteArr() {
    	return byteArr;
    }

	boolean isPrime (long i) {
      	if(i == 2) {
      		return true;
      	}
    	if(isEven(i) || isTooGreat(i)) {
    		return false;
      	}
      	int byteNr = ((int)i/16); 	//indeks til hvilket byteArr tallet i ligger i
      	int bitNr = ((int)i % 16)/2;					//indeks til bitposisjonen inni byteArrayet

      	/*
		isPrime(5);
		byteNr = 5/16 = 0;
		bitNr = 5%16/2 = 2;

		1 3 5 7 9 11 13 15
		0 1 1 1 0 1  1  0

		128 = 1000 0000
		bitshift 128 med bitNr = verdien til tallet
      	*/
		return (byteArr[byteNr] & 128 >> bitNr) != 0;
    }

    void printAllPrimes(){
		for ( int i = 2; i <= maxNum; i++)
			if (isPrime(i)) System.out.println(" "+i);
	}

	public ArrayList<Long> parFactorize(long num) {
		fThreads = new FactThread[availableThreads];
		facts = new ArrayList<>();
		int counter = 0;

		for(PrimeThread pt : pThreads) {
			fThreads[counter] = new FactThread(counter, pt.start, pt.end, num);
			fThreads[counter++].start();
		}

		for(Thread t : fThreads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		long prod = 1;
		for(Long l : facts) {
			prod = prod * l;
		}

		if(prod != 1 || prod != num) {
			facts.add(num/prod);
		}

		return facts;
	}

	private boolean isEven (long i) {
		return (i % 2 == 0);
	}

	private boolean isTooGreat(long i) {
		return i > maxNum;
	}

	class PrimeThread extends Thread {
		int num, start, end;
		SeqEratosthenesSil seq;

		PrimeThread(int num, int start, int end) {
			this.num = num;
			this.start = start;
			this.end = end;
		}

		public void run() {
			seq = new SeqEratosthenesSil(num, start, end, byteArr);
		}
	}

	class FactThread extends Thread {
		int num, start, end;
		long factNum;

		FactThread(int num, int start, int end, long factNum) {
			this.num = num;
			this.start = start;
			this.end = end;
			this.factNum = factNum;
		}

		public void run() {
			for(long p = start + 1; p <= end; ) {
				if(isPrime(p) && p != factNum) {
					if(factNum % p == 0) {
						facts.add(p);
						factNum /= p;
					} else {
						p++;
					}
				} else {
					p++;
				}
			}
		}
	}
}








