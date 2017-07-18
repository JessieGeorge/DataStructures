package search;

import java.io.*;
import java.util.*;

/**
 * This class encapsulates an occurrence of a keyword in a document. It stores the
 * document name, and the frequency of occurrence in that document. Occurrences are
 * associated with keywords in an index hash table.
 * 
 * @author Sesh Venugopal
 * 
 */
class Occurrence {
	/**
	 * Document in which a keyword occurs.
	 */
	String document;
	
	/**
	 * The frequency (number of times) the keyword occurs in the above document.
	 */
	int frequency;
	
	/**
	 * Initializes this occurrence with the given document,frequency pair.
	 * 
	 * @param doc Document name
	 * @param freq Frequency
	 */
	public Occurrence(String doc, int freq) {
		document = doc;
		frequency = freq;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "(" + document + "," + frequency + ")";
	}
}

/**
 * This class builds an index of keywords. Each keyword maps to a set of documents in
 * which it occurs, with frequency of occurrence in each document. Once the index is built,
 * the documents can searched on for keywords.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in descending
	 * order of occurrence frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash table of all noise words - mapping is from word to itself.
	 */
	HashMap<String,String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashMap<String,String>(100,2.0f);
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.put(word,word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeyWords(docFile);
			mergeKeyWords(kws);
		}
		
	}

	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeyWords(String docFile) 
	throws FileNotFoundException {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		HashMap<String, Occurrence> kws = new HashMap<String,Occurrence>(1000,2.0f);
		Scanner sc = new Scanner(new File(docFile));
		while(sc.hasNext())   // abc text tomorrow the tomorrow abc
		{
				String word = sc.next();			
				word = getKeyWord(word);
				if(word!=null) //word is a keyword
				{
					//check if word is in hashmap
					//if yes, get word's freq and increment
					if(kws.containsKey(word))
					{
						kws.get(word).frequency++;
					}
					//if no, create occ obj with freq=1 and add to hashmap
					else
					{
						Occurrence O = new Occurrence(docFile, 1);
						kws.put(word, O);
					}
				}
		    
						
		}
		return kws;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeyWords(HashMap<String,Occurrence> kws) {
		// COMPLETE THIS METHOD
		Iterator it = kws.entrySet().iterator();
		ArrayList<Occurrence> occs; 
		ArrayList<Integer> MidPts;		
				
		while(it.hasNext())     // <abc; occ1,occ2,occ7,occ4>   // <abc;occnew> <tomorrow;occnewtom>
		{
			Map.Entry<String, Occurrence> entry = (Map.Entry<String, Occurrence>) it.next();
			//if this keyword is present in keywordsIndex
			if(keywordsIndex.containsKey(entry.getKey()))
			{
				occs = keywordsIndex.get(entry.getKey()); //occs = occ1,occ2,occ7,occ4	
			}
			//else occs is an emptylist
			else
			{
				occs = new ArrayList<Occurrence>();
			}
			//add to the end of occs it.getValue()		//occs = occ1,occ2,occ7,occ4,occnew
			occs.add(entry.getValue());
			
			MidPts = insertLastOccurrence(occs);		//occs = occ1,occnew,occ2,occ7,occ4
			keywordsIndex.put(entry.getKey(),occs); //overwrites if there is already a key
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * TRAILING punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyWord(String word) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		boolean isKeyword = true;
		
		word = word.toLowerCase(); 
		int l = word.length();
		for(int i = l-1; i>=0; i--)
		{
			char ch = word.charAt(i);
			if(ch=='.'||ch==','||ch=='?'||ch==':'||ch==';'||ch=='!') //removes trailing punctuation of word
				word = word.substring(0, word.indexOf(ch));
			else
				break;
		}
		
		l = word.length();
		for(int i=0; i<l; i++)
		{
			char ch = word.charAt(i);
			if(!Character.isLetter(ch)) //word consists of non-alphabets
			{
				isKeyword = false;
				break;
			}
		}
			
		Iterator it = noiseWords.entrySet().iterator();
		while(it.hasNext())
		{
			Map.Entry entry = (Map.Entry) it.next();
			String key = (String)entry.getKey();
			if(word.equalsIgnoreCase(key)) //word is a noise word
			{
				isKeyword = false;
				break;
			}
		}
		
		if(isKeyword)
			return word;
		else
			return null;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * same list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion of the last element
	 * (the one at index n-1) is done by first finding the correct spot using binary search, 
	 * then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		if(occs.size()==1)
			return null;
	    
		ArrayList<Integer> MidPts = new ArrayList<Integer>(occs.size()/2);//stores mid point indexes
		int high = occs.size()-2; //see instructions. exclude last index.
		int low = 0; 
		while(low<=high)
		{
			int mid = (low+high)/2;
			MidPts.add(mid);
			int f1 = occs.get(occs.size()-1).frequency; //last freq
			int f2 = occs.get(mid).frequency; //mid freq
			if(mid==0 && f1>f2)//spot for insertion
			{
				Occurrence temp = occs.get(occs.size()-1);
				occs.remove(occs.size()-1);
				occs.add(mid, temp);//adds temp to the mid spot and shifts everything to the right one step further
				break;
			}
			if(mid==0 && f1>=occs.get(mid+1).frequency)//spot for insertion
			{
				Occurrence temp = occs.get(occs.size()-1);
				occs.remove(occs.size()-1);
				occs.add(mid+1, temp);//adds temp to the mid spot and shifts everything to the right one step further
				break;
			}
			if(f1==f2||(f1<occs.get(mid-1).frequency && f1>f2)) //spot for insertion
			{
				Occurrence temp = occs.get(occs.size()-1);
				occs.remove(occs.size()-1);
				occs.add(mid, temp);//adds temp to the mid spot and shifts everything to the right one step further
				break;
			}
			else if(f1 < f2)
			{
				low++;
			}
			
			else if(f1>f2)
			{
				high--;
			}
			
		}
		
		return MidPts;
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of occurrence frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will appear before doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matching documents, the result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of NAMES of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matching documents,
	 *         the result is null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		
		ArrayList<Occurrence> occs1 = new ArrayList<Occurrence>(5); //holds first five occurrences of kw1
		ArrayList<Occurrence> occs2 = new ArrayList<Occurrence>(5); //holds first five occurrences of kw2
		int count1 = 0; //number of occurrences in occs1
		int count2 = 0; //number of occurrences in occs2
		ArrayList<String> finDocs = new ArrayList<String>(5); //final answer
		//search for kw1
		if(keywordsIndex.containsKey(kw1))
		{
			occs1 = keywordsIndex.get(kw1);
			count1 = occs1.size();
		}
		
		//search for kw2
		if(keywordsIndex.containsKey(kw2))
		{
			occs2 = keywordsIndex.get(kw2);
			count2 = occs2.size();
		}
		
		if(count1==0 && count2==0)//neither keywords were found 
			return null;
		
		if(count1==0)//only kw2 was found
		{
			for(int i=0; i<occs2.size(); i++)
			{
				finDocs.add(occs2.get(i).document);
			}
			return finDocs;
		}
		
		if(count2==0)//only kw1 was found 
		{
			for(int i=0; i<occs1.size(); i++)
			{
				finDocs.add(occs1.get(i).document);
			}
			return finDocs;
		}
		
		//both keywords were found
		int i=0,j=0,finCount=0; //counter to go through occs1, occs2, and finDocs resply
		int f1=0, f2=0; //frequency of a keyword in occs1 and occs2 reply 
		while ((i<count1 || j<count2) && finCount <5) 
		{		
			if (i<count1)
				f1=occs1.get(i).frequency;
			else
				f1=0;
			
			if(j<count2)
				f2=occs2.get(j).frequency;
			else 
				f2=0;
			
			if(f1>=f2){
				if(!finDocs.contains(occs1.get(i).document))
				{
					//add f1's occ.doc to finDocs
					finDocs.add(occs1.get(i).document);
				}
				//increment i
				i++;
			}
			else
			{
				if(!finDocs.contains(occs2.get(j).document))
				{
					//add f2's occ.doc to finDocs
					finDocs.add(occs2.get(j).document);
				}
				
			//increment j
				j++;
			}
			finCount++;
		}
		return finDocs;
	}
	
	//my driver
	public static void main(String[] args)throws IOException
	{
		LittleSearchEngine LS = new LittleSearchEngine();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Enter documents file name => ");
		String d = br.readLine();
		System.out.print("Enter noise words file name => ");
		String n = br.readLine();
		
		LS.makeIndex(d, n);
		System.out.println(LS.top5search("deep","world"));
	}
}
