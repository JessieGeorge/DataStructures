package poly;

import java.io.*;
import java.util.StringTokenizer;

/**
 * This class implements a term of a polynomial.
 * 
 * @author runb-cs112
 *
 */
class Term {
	/**
	 * Coefficient of term.
	 */
	public float coeff;
	
	/**
	 * Degree of term.
	 */
	public int degree;
	
	/**
	 * Initializes an instance with given coefficient and degree.
	 * 
	 * @param coeff Coefficient
	 * @param degree Degree
	 */
	public Term(float coeff, int degree) {
		this.coeff = coeff;
		this.degree = degree;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		return other != null &&
		other instanceof Term &&
		coeff == ((Term)other).coeff &&
		degree == ((Term)other).degree;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (degree == 0) {
			return coeff + "";
		} else if (degree == 1) {
			return coeff + "x";
		} else {
			return coeff + "x^" + degree;
		}
	}
}

/**
 * This class implements a linked list node that contains a Term instance.
 * 
 * @author runb-cs112
 *
 */
class Node {
	
	/**
	 * Term instance. 
	 */
	Term term;
	
	/**
	 * Next node in linked list. 
	 */
	Node next;
	
	/**
	 * Initializes this node with a term with given coefficient and degree,
	 * pointing to the given next node.
	 * 
	 * @param coeff Coefficient of term
	 * @param degree Degree of term
	 * @param next Next node
	 */
	public Node(float coeff, int degree, Node next) {
		term = new Term(coeff, degree);
		this.next = next;
	}
}

/**
 * This class implements a polynomial.
 * 
 * @author runb-cs112
 *
 */
public class Polynomial {
	
	/**
	 * Pointer to the front of the linked list that stores the polynomial. 
	 */ 
	Node poly;
	
	/** 
	 * Initializes this polynomial to empty, i.e. there are no terms.
	 *
	 */
	public Polynomial() {
		poly = null;
	}
	
	/**
	 * Reads a polynomial from an input stream (file or keyboard). The storage format
	 * of the polynomial is:
	 * <pre>
	 *     <coeff> <degree>
	 *     <coeff> <degree>
	 *     ...
	 *     <coeff> <degree>
	 * </pre>
	 * with the guarantee that degrees will be in descending order. For example:
	 * <pre>
	 *      4 5
	 *     -2 3
	 *      2 1
	 *      3 0
	 * </pre>
	 * which represents the polynomial:
	 * <pre>
	 *      4*x^5 - 2*x^3 + 2*x + 3 
	 * </pre>
	 * 
	 * @param br BufferedReader from which a polynomial is to be read
	 * @throws IOException If there is any input error in reading the polynomial
	 */
	public Polynomial(BufferedReader br) throws IOException {
		String line;
		StringTokenizer tokenizer;
		float coeff;
		int degree;
		
		poly = null;
		
		while ((line = br.readLine()) != null) {
			tokenizer = new StringTokenizer(line);
			coeff = Float.parseFloat(tokenizer.nextToken());
			degree = Integer.parseInt(tokenizer.nextToken());
			poly = new Node(coeff, degree, poly);
		}
	}
	
	
	/**
	 * Returns the polynomial obtained by adding the given polynomial p
	 * to this polynomial - DOES NOT change this polynomial
	 * 
	 * @param p Polynomial to be added
	 * @return A new polynomial which is the sum of this polynomial and p.
	 */
	public Polynomial add(Polynomial p) {
		/** COMPLETE THIS METHOD **/
		Polynomial sum=new Polynomial();
		float sumCo = 0;
		int sumDeg = 0;
		Node n,i,p1,p2;
		p1=this.poly;
		p2=p.poly;
		if(p1 == null)
		{
			sum.poly = p.poly;
			return sum;
		}
		if(p2 == null)
		{
			sum.poly = this.poly;
			return sum;
		}
		while(p1!=null || p2!=null)
		{
			if(p1==null)
			{
				sumCo = p2.term.coeff;
				sumDeg = p2.term.degree;
				p2 = p2.next;
			}
			else if(p2==null)
			{
				sumCo = p1.term.coeff;
				sumDeg = p1.term.degree;
				p1 = p1.next;
			}
			else if(p1.term.degree==p2.term.degree)
			{
				sumCo = p1.term.coeff+p2.term.coeff;
				sumDeg = p1.term.degree;
				p1 = p1.next;
				p2 = p2.next;
			}
			else if(p1.term.degree<p2.term.degree)
			{
				sumCo = p1.term.coeff;
				sumDeg = p1.term.degree;
				p1 = p1.next;
			}
			else
			{
				sumCo = p2.term.coeff;
				sumDeg = p2.term.degree;
				p2 = p2.next;
			}
			
			if(sumCo == 0)
				continue;
			
			n = new Node(sumCo,sumDeg,null);
			if(sum.poly==null){
				sum.poly=n;
			}
			else{
				for(i=sum.poly;i.next!=null;i=i.next)
				{
					//do nothing
				}
				
				i.next=n;
			}
		}
		return sum;
	}
	
	/**
	 * Returns the polynomial obtained by multiplying the given polynomial p
	 * with this polynomial - DOES NOT change this polynomial
	 * 
	 * @param p Polynomial with which this polynomial is to be multiplied
	 * @return A new polynomial which is the product of this polynomial and p.
	 */
	public Polynomial multiply(Polynomial p) {
		Polynomial mul = new Polynomial();
		Polynomial tmp = new Polynomial();
		Node i, k, j, n;
		float mulCo = 0;
		int mulDeg = 0;
		
		if(this.poly==null || p.poly==null)
		{
			return mul;
		}
		for(i = this.poly; i!=null; i = i.next)
		{
			for(k = p.poly; k!=null; k = k.next)
			{
			   mulCo = i.term.coeff * k.term.coeff; 
			   mulDeg = i.term.degree + k.term.degree;
			   n = new Node(mulCo, mulDeg, null);
			   if(tmp.poly == null)
			   {
				   tmp.poly = n;
			   }
			   else
			   {
				   for(j = tmp.poly; j.next!=null; j = j.next)
				   {
					   //do nothing
				   }
				   j.next = n;
			   }
			}
			mul = mul.add(tmp);
			tmp.poly = null;
		}
		return mul;
	}
	
	/**
	 * Evaluates this polynomial at the given value of x
	 * 
	 * @param x Value at which this polynomial is to be evaluated
	 * @return Value of this polynomial at x
	 */
	public float evaluate(float x) {
		float result = 0;
		Node n = this.poly;
		if(n==null)
			return 0f;
		while(n!=null)
		{
			result += n.term.coeff * Math.pow(x, n.term.degree);
			n=n.next;
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String retval;
		
		if (poly == null) {
			return "0";
		} else {
			retval = poly.term.toString();
			for (Node current = poly.next ;
			current != null ;
			current = current.next) {
				retval = current.term.toString() + " + " + retval;
			}
			return retval;
		}
	}
}
