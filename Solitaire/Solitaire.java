package solitaire;

import java.io.IOException;
import java.util.Scanner;
import java.util.Random;
import java.util.NoSuchElementException;

/**
 * This class implements a simplified version of Bruce Schneier's Solitaire Encryption algorithm.
 * 
 * @author RU NB CS112
 */
public class Solitaire {
	
	/**
	 * Circular linked list that is the deck of cards for encryption
	 */
	CardNode deckRear;
	
	/**
	 * Makes a shuffled deck of cards for encryption. The deck is stored in a circular
	 * linked list, whose last node is pointed to by the field deckRear
	 */
	public void makeDeck() {
		// start with an array of 1..28 for easy shuffling
		int[] cardValues = new int[28];
		// assign values from 1 to 28
		for (int i=0; i < cardValues.length; i++) {
			cardValues[i] = i+1;
		}
		
		// shuffle the cards
		Random randgen = new Random();
 	        for (int i = 0; i < cardValues.length; i++) {
	            int other = randgen.nextInt(28);
	            int temp = cardValues[i];
	            cardValues[i] = cardValues[other];
	            cardValues[other] = temp;
	        }
	     
	    // create a circular linked list from this deck and make deckRear point to its last node
	    CardNode cn = new CardNode();
	    cn.cardValue = cardValues[0];
	    cn.next = cn;
	    deckRear = cn;
	    for (int i=1; i < cardValues.length; i++) {
	    	cn = new CardNode();
	    	cn.cardValue = cardValues[i];
	    	cn.next = deckRear.next;
	    	deckRear.next = cn;
	    	deckRear = cn;
	    }
	}
	
	/**
	 * Makes a circular linked list deck out of values read from scanner.
	 */
	public void makeDeck(Scanner scanner) 
	throws IOException {
		CardNode cn = null;
		if (scanner.hasNextInt()) {
			cn = new CardNode();
		    cn.cardValue = scanner.nextInt();
		    cn.next = cn;
		    deckRear = cn;
		}
		while (scanner.hasNextInt()) {
			cn = new CardNode();
	    	cn.cardValue = scanner.nextInt();
	    	cn.next = deckRear.next;
	    	deckRear.next = cn;
	    	deckRear = cn;
		}
	}
	
	/**
	 * Implements Step 1 - Joker A - on the deck.
	 */
	void jokerA() {
		// COMPLETE THIS METHOD
		CardNode cn;
		
		//search for jokerA
		for(cn = deckRear.next; cn != deckRear; cn = cn.next)
		{
			if(cn.cardValue==27)
				break;
		}
		
		//swap jokerA 
		cn.cardValue = cn.next.cardValue;
		cn.next.cardValue = 27;
		
	}
	
	/**
	 * Implements Step 2 - Joker B - on the deck.
	 */
	void jokerB() {
	    // COMPLETE THIS METHOD
		CardNode cn;
		
		//search for jokerB
		for(cn = deckRear.next; cn != deckRear; cn = cn.next)
		{
			if(cn.cardValue==28)
				break;
		}
		
		//swap jokerB
		cn.cardValue = cn.next.cardValue;
		cn.next.cardValue = cn.next.next.cardValue;
		cn.next.next.cardValue = 28;
		
	}
	
	/**
	 * Implements Step 3 - Triple Cut - on the deck.
	 */
	void tripleCut() {
		// COMPLETE THIS METHOD
		CardNode cn, fj, sj, beforeFJ, afterSJ; 
		//cn to traverse list, fj is first joker, sj is second joker
		//beforeFJ is the node before the first joker, afterSJ is the node after the second joker
		
		cn = fj = sj = beforeFJ = afterSJ = deckRear; 
		/*this is just to avoid the error that says local variable may not be initialized.
		 * humanly, we know these local variables will get their values in the loops because
		 * there HAS to be two jokers in the deck so the if conditions WILL be true.
		 */
		
		//to find the first joker
		for(cn = deckRear; cn.next != deckRear; cn = cn.next)
		{
			if(cn.next.cardValue==27||cn.next.cardValue==28)
			{
				beforeFJ = cn;
				fj = beforeFJ.next;
				break;
			}
		}
		
		//to find the second joker
		for(cn = fj.next; cn != deckRear.next; cn = cn.next) 
		{
			if(cn.cardValue==27||cn.cardValue==28)
			{
				sj = cn;
				afterSJ = sj.next;
				break;
			}
		}
		
		
		if(deckRear.next==fj && deckRear==sj)//two jokers at extreme ends
		{
			//do nothing
		}
		else if(deckRear.next==fj)//first joker is first card
		{
			deckRear = sj;
		}
		else if(deckRear==sj)//second joker is last card
		{
			deckRear = beforeFJ;
		}
		else//two jokers anywhere in the pile
		{
			sj.next = deckRear.next;
			deckRear.next = fj;
			deckRear = beforeFJ;
			deckRear.next = afterSJ;
		}
		
	}
	
	/**
	 * Implements Step 4 - Count Cut - on the deck.
	 */
	void countCut() {		
		// COMPLETE THIS METHOD
		int lastCardVal;
		if(deckRear.cardValue == 28)
			lastCardVal = 27;
		else
			lastCardVal = deckRear.cardValue;
		
		if(lastCardVal == 27) //if last card is 27 nothing moves
			return;
		
		CardNode cn, firstCard, NthCard, afterNthCard, secondLastCard;
		
		cn = firstCard = deckRear.next;
		
		//to count out N cards where N is the lastCardVal
		for(int i=1; i<lastCardVal; i++)
		{
			cn = cn.next;
		}
		
		NthCard = cn;
		afterNthCard = cn.next;
		
		//to get the secondLastCard
		for(cn = deckRear.next; cn.next != deckRear; cn = cn.next)
		{
			//do nothing
		}
		secondLastCard = cn;
		
		//to move cards
		secondLastCard.next = firstCard;
		NthCard.next = deckRear;
		deckRear.next = afterNthCard;
		
	}
	
	/**
	 * Gets a key. Calls the four steps - Joker A, Joker B, Triple Cut, Count Cut, then
	 * counts down based on the value of the first card and extracts the next card value 
	 * as key. But if that value is 27 or 28, repeats the whole process (Joker A through Count Cut)
	 * on the latest (current) deck, until a value less than or equal to 26 is found, which is then returned.
	 * 
	 * @return Key between 1 and 26
	 */
	int getKey() {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		int firstCardVal;
		if(deckRear.next.cardValue == 28)
			firstCardVal = 27;
		else
			firstCardVal = deckRear.next.cardValue;
		
		CardNode cn;
		
		cn = deckRear.next;
		
		//to get key
		for(int i=1; i<firstCardVal; i++)
		{
			cn = cn.next;
		}
		
		return cn.next.cardValue;
	}
	
	/**
	 * Utility method that prints a circular linked list, given its rear pointer
	 * 
	 * @param rear Rear pointer
	 */
	private static void printList(CardNode rear) {
		if (rear == null) { 
			return;
		}
		System.out.print(rear.next.cardValue);
		CardNode ptr = rear.next;
		do {
			ptr = ptr.next;
			System.out.print("," + ptr.cardValue);
		} while (ptr != rear);
		System.out.println("\n");
	}

	/**
	 * Encrypts a message, ignores all characters except upper case letters
	 * 
	 * @param message Message to be encrypted
	 * @return Encrypted message, a sequence of upper case letters only
	 */
	public String encrypt(String message) {	
		// COMPLETE THIS METHOD
	    // THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		String e = ""; //the encrypted message
		int len = message.length();
		int i, posInAlph, key, sum;
		for(i=0; i<len; i++)
		{
			char ch = message.charAt(i);
			if(Character.isLetter(ch))
			{
				posInAlph = ch - 'A' +1; //position in alphabet
				
				do {
					jokerA();
					jokerB();
					tripleCut();
					countCut();
					key = getKey();
					
				} while (key == 27 || key == 28);
				
				sum = posInAlph + key;
				if(sum>26)
					sum -= 26;
				
				e += (char)(sum - 1 + 'A');
				
			}
		}
	    return e;
	}
	
	/**
	 * Decrypts a message, which consists of upper case letters only
	 * 
	 * @param message Message to be decrypted
	 * @return Decrypted message, a sequence of upper case letters only
	 */
	public String decrypt(String message) {	
		// COMPLETE THIS METHOD
	    // THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		String d = ""; //the encrypted message
		int len = message.length();
		int i, posInAlph, key, diff;
		for(i=0; i<len; i++)
		{
			char ch = message.charAt(i);
			if(Character.isLetter(ch))
			{
				posInAlph = ch - 'A' +1; //position in alphabet
				
				do {
					jokerA();
					jokerB();
					tripleCut();
					countCut();
					key = getKey();
					
				} while (key == 27 || key == 28);
				
				if(posInAlph <= key)
					diff = posInAlph + 26 - key;
				else
					diff = posInAlph - key;
				
				d += (char)(diff - 1 + 'A');
				
			}
		}
	    return d;
	}
}
