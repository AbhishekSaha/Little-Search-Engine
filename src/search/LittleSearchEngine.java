package search;

import java.io.*;
import java.util.*;


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
	public HashMap<String,ArrayList<Occurrence>> keywordsIndex;

	
	/**
	 * The hash table of all noise words - mapping is from word to itself.
	 */
	public HashMap<String,String> noiseWords;

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

		HashMap<String, Occurrence> fin = new HashMap<String, Occurrence>(1000,2.0f);
		Scanner sci = new Scanner(new File(docFile));
		while(sci.hasNext()){
			String nu = sci.next(); nu = getKeyWord(nu);
			if(nu!=null){
				if(fin.containsKey(nu)){
					Occurrence ge = fin.get(nu);
					ge.frequency++;
					fin.put(nu, ge);
				}
				else{
					Occurrence ge = new Occurrence(docFile, 1);
					fin.put(nu, ge);
				}


			}

		}
		return fin;
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
		for (String key: kws.keySet()) {
			if(keywordsIndex.containsKey(key)==false){
				ArrayList<Occurrence> temp = new ArrayList<Occurrence>();
				Occurrence ben = kws.get(key);
				temp.add(ben);
				insertLastOccurrence(temp);
				keywordsIndex.put(key, temp );
			}
			else{
				ArrayList<Occurrence> ni = keywordsIndex.get(key);
				Occurrence hen = kws.get(key);
				ni.add(hen);
				insertLastOccurrence(ni);
				keywordsIndex.put(key, ni );
			}

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
		int i= 0; boolean gru = false;
		int siz = word.length();
		for(i= 0; i<siz; i++){
			if(i+1==siz){ gru = true;}
			//wor%d
			if(Character.isLetter(word.charAt(i))==false && word.charAt(i)!='.' && word.charAt(i)!=',' && word.charAt(i)!='?' && word.charAt(i)!=':' && word.charAt(i)!=';' && word.charAt(i)!='!'){
				return null;
			}
			if(word.charAt(i)==',' || word.charAt(i)==':' || word.charAt(i)==';'){
				if(i+1!=siz){
					return null;
				}
				else{
					word = word.substring(0,siz-1);
					break;
				}
			}
			if(word.charAt(i)=='.'||word.charAt(i)=='?' || word.charAt(i)=='!'){
				int j = 0;
				for(j = i; j<siz; j++){
					//worl.d
					if(word.charAt(j)!='.' && word.charAt(j)!='!' && word.charAt(j)!='.'){
						return null;
					}
					if(j+1==siz){
						gru = true;
					}
				}
				if(gru==true){
					word = word.substring(0,i);
				}

				break;
			}
		}

		word = word.toLowerCase();

		String temp = noiseWords.get(word);
		if(temp==null){
			return word;
		}
		else{
			return null;}
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
		ArrayList<Integer> arr = new ArrayList<Integer>(5);
		int size = occs.size();
		if(size==1){
			return null;
		}
		else{	
			Occurrence tempy = occs.get(occs.size()-1);
			occs.remove(occs.size()-1);
			int high = 0;
			int low = occs.size()-1;
			int middle = 0;
			int	i = tempy.frequency;
			while(high <= low) {
				middle = (low + high) / 2;
				Occurrence flo = occs.get(middle);
				int end = flo.frequency;
				if(end == i) {
					arr.add(middle);
					break;
				}
				if(end < i) {
					low = middle -1;
					arr.add(middle);
				}
				if(end > i) {
					high = middle + 1;
					arr.add(middle);
					middle = middle+1;
				}

			}

			occs.add(middle, tempy);
;

			return arr;}
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
		int x = 0; int y = 0; int count = 0;
		ArrayList<String> hom = new ArrayList<String>(5);
		ArrayList<Integer> him;
		HashMap<String, Integer> nee = new HashMap<String, Integer>(100, 2.0f);
		ArrayList<Occurrence> jim = keywordsIndex.get(kw1); int jimz = jim.size();
		ArrayList<Occurrence> kim = keywordsIndex.get(kw2); int kimz = kim.size();

		while(x<jimz && y<kimz){
			Occurrence a  = jim.get(x); Occurrence b = kim.get(y);
			int r = a.frequency - b.frequency;
			if(r==0){ x++; y++;
				String jer = a.document;
				if(nee.containsKey(jer)==true){

				}
				else{
					nee.put(jer, a.frequency);
					hom.add(jer); count++;
				}
				if(count>5){
					return hom;
				}
				String ber = b.document;
				if(nee.containsKey(ber)==true){

				}
				else{
					nee.put(ber, b.frequency);
					hom.add(ber); count++;
				}
				if(count>5){
					return hom;
				}
			}
			else if(r>0){ x++;
				String jer = a.document;
				if(nee.containsKey(jer)==true){

				}
				else{
					nee.put(jer, a.frequency);
					hom.add(jer); count++;
				}
				if(count>5){
					return hom;
				}
			}
			else if(r<0){ y++;
				String ber = b.document;
				if(nee.containsKey(ber)==true){

				}
				else{
					nee.put(ber, b.frequency);
					hom.add(ber); count++;
				}
				if(count>5){
					return hom;
				}
			}
		}

		return hom;
	}
}
