package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {
	
	/**
	 * Root node
	 */
	TagNode root=null;
	
	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;
	
	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}
	
	/**
	 * Builds the DOM tree from input HTML file. The root of the 
	 * tree is stored in the root field.
	 */
	public void build() {
		/** COMPLETE THIS METHOD **/
		root = new TagNode("", null, null);
        TagNode ptr = null;
        String remBrac; //to remove angle brackets
        Stack<TagNode> DomStk = new Stack<TagNode>();

        while (sc.hasNextLine()) {

            String words = sc.nextLine();

            if (words.equals("<html>")) {
            	
                root = new TagNode("html", null, null); 
                DomStk.push(root);

            }

            else if (words.charAt(0) == '<') {

                if (words.charAt(1) == '/') {

                    DomStk.pop();

                }

                else if (DomStk.peek().firstChild == null) {

                    remBrac = words.replaceAll("<", "");
                    remBrac = remBrac.replaceAll(">", "");
                    ptr = new TagNode(remBrac, null, null);
                    DomStk.peek().firstChild = ptr;
                    DomStk.push(ptr);

                }

                else {

                    TagNode temp = DomStk.peek().firstChild;

                    while (temp.sibling != null) {

                        temp = temp.sibling;

                    }

                    remBrac = words.replaceAll("<", "");
                    remBrac = remBrac.replaceAll(">", "");
                    ptr = new TagNode(remBrac, null, null);

                    temp.sibling = ptr;
                    DomStk.push(ptr);

                }

            }

            else {

                if (DomStk.peek().firstChild == null) {

                    DomStk.peek().firstChild = new TagNode(words, null, null);

                }

                else {

                    TagNode temp = DomStk.peek().firstChild;

                    while (temp.sibling != null) {

                        temp = temp.sibling;

                    }

                    temp.sibling = new TagNode(words, null, null);

                }
            }       
        }           
	}
	
	
	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		/** COMPLETE THIS METHOD **/
		recRepTag(root, oldTag, newTag);
	}
	private void recRepTag(TagNode t, String oldTag, String newTag)
	{
		if(t == null)
			return;
		
		if(t.tag.equals(oldTag))
			t.tag = newTag;
		
		recRepTag(t.sibling, oldTag, newTag);
		recRepTag(t.firstChild, oldTag, newTag);
	}
	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		/** COMPLETE THIS METHOD **/
		TagNode t = recFindTable(root, row);
		
		while(!t.tag.equals("tr"))//loop to get the first row in the table 
		{
			t = t.firstChild;
		}
		while(row-1 != 0)//loop to find the row you want to make bold
		{
			t = t.sibling;
			row--;
		}
		
		t = t.firstChild; //the first column in that row
		
		while(t!=null)
		{
			TagNode bold = new TagNode("b", null, null);
			bold.firstChild = t.firstChild;
			t.firstChild = bold;
			t = t.sibling;
		}
	}
	private TagNode recFindTable(TagNode t, int row)
	{
		if(t == null)
			return null;
		
		if(t.tag.equals("table"))
		{
			return t;
		}
		
		if(t.sibling != null)
			return recFindTable(t.sibling, row);
		
		return recFindTable(t.firstChild, row);
	}
	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		/** COMPLETE THIS METHOD **/
		recFindTag(root, tag);
	}
	private void recFindTag(TagNode t, String tag)
	{
		if(t == null)
			return;
		
		if(t.sibling!=null && t.sibling.tag.equals(tag))
		{
			TagNode temp = t.sibling.sibling;
			t.sibling = t.sibling.firstChild;
			
			if(tag.equals("ol"))
				recSwitchlipTags(t.sibling, "ul");
			
			else if(tag.equals("ul"))
				recSwitchlipTags(t.sibling, "ol");
			
			TagNode k = t.sibling;
			while(k.sibling!=null)
			{
				k = k.sibling;
			}
			k.sibling = temp;
		}
		
		if(t.firstChild!=null && t.firstChild.tag.equals(tag))
		{
			TagNode temp = t.firstChild.sibling;
			t.firstChild = t.firstChild.firstChild;
			
			if(tag.equals("ol"))
				recSwitchlipTags(t.firstChild, "ul");
				
			else if(tag.equals("ul"))
				recSwitchlipTags(t.firstChild, "ol");
			
			TagNode k = t.firstChild;
			while(k.sibling!=null)
			{
				k = k.sibling;
			}
			k.sibling = temp;
				
		}
		
		recFindTag(t.sibling, tag);
		
		recFindTag(t.firstChild, tag);
	}
	private void recSwitchlipTags(TagNode t,String otherTag)
	{
		if(t == null)
			return;
		
		if(t.tag.equals("li"))
			t.tag = "p";
		
		recSwitchlipTags(t.sibling, otherTag);
		if(!t.tag.equals(otherTag)) //bypass everything under the other tag
		{
			recSwitchlipTags(t.firstChild, otherTag);
		}
		
	}
	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	public void addTag(String word, String tag) {
		/** COMPLETE THIS METHOD **/
		recFindTag(root, word, tag);
	}
	private void recFindTag(TagNode t, String word, String tag) // deal with double words!!
	{
		if(t == null)
			return;
		String temp = t.tag;
		StringTokenizer st = new StringTokenizer(temp);
		int c = st.countTokens();
		String x;
		String y = word;
		TagNode taggableWord;
		TagNode tagToAdd;
		TagNode tagAfterWord;
		boolean a = false;//to check if there's something after word
		boolean pp = false;//to check if there's punctuation
		boolean t1 = false; //first method of tagging
		boolean t2 = false; //second method of tagging
		while(st.hasMoreTokens())
		{
			x = st.nextToken();
			if(x.equalsIgnoreCase(y)||x.equalsIgnoreCase(y+".")||x.equalsIgnoreCase(y+",")||x.equalsIgnoreCase(y+"?")||x.equalsIgnoreCase(y+"!")||x.equalsIgnoreCase(y+":")||x.equalsIgnoreCase(y+";"))
			{
				taggableWord = new TagNode(x, null, null);
				if(c==1) //t.tag is word
				{
					t.tag = tag;
					t.firstChild = taggableWord;
					t1 = true;
				}
				else
				{
					tagToAdd = new TagNode(tag, null, null);
					tagToAdd.firstChild = taggableWord;
					//case insensitive to get index of word
					temp = temp.toLowerCase();
					y = y.toLowerCase();
					int xl = x.length();
					int yl = y.length();
					int i = temp.indexOf(y);
					if(!temp.substring(i+yl).equals("")) //there's something after word
					{
						a = true;
					}
					if(x.charAt(xl-1)=='.'||x.charAt(xl-1)=='?'||x.charAt(xl-1)=='!'||x.charAt(xl-1)==','||x.charAt(xl-1)==':'||x.charAt(xl-1)==';')
					{
						pp = true;
					}
					if(a)
					{
						if(pp)
							tagAfterWord = new TagNode(t.tag.substring(i+yl+1),null,null);
						else
							tagAfterWord = new TagNode(t.tag.substring(i+yl), null, null);
						
						tagToAdd.sibling = tagAfterWord;
					}
					else
					{
						tagToAdd.sibling = t.sibling;
					}
					t.tag = t.tag.substring(0,i);
					t.sibling = tagToAdd;
					t2 = true;
				}
			}
		}
		/*String x = t.tag;
		x = x.toLowerCase(); //case insensitive
		String y = word;
		y = y.toLowerCase(); //case insensitive
		int xl = x.length();
		int yl = y.length();
		int i = x.indexOf(y);
		boolean taggable = false;//to check if a taggable word exists
		boolean t1 = false; //first method of tagging
		boolean t2 = false; //second method of tagging
		boolean a = false;//to check if there's something after word
		boolean pp = false;//to check if there's punctuation
		boolean app = false; //to check if there's something after punctuation
		char ch = ' ';
		TagNode taggableWord;
		TagNode tagToAdd;
		TagNode tagAfterWord;
		if(i!=-1) //the word is present in this tag
		{
			if(xl>yl) //tag longer than word
			{
				if(!x.substring(i+yl).equals("")) //there's something after word
				{
					ch = x.charAt(i+yl);
					a = true;
				}
				
				if(i!=0)//there's something before word
				{
					if(x.charAt(i-1)==' ')
					{
						if(a)
						{
							if(ch==' ')
								taggable = true;
							
							else if(ch=='.'||ch==','||ch=='?'||ch=='!'||ch==':'||ch==';')
							{
								pp = true;
								if(!x.substring(i+yl+1).equals(null)) //there's something after the punctuation
								{
									if(x.charAt(i+yl+1)==' ')//can't have double punctuation
									{
										taggable = true;
										app = true;
									}
								}
								else
									taggable = true;
							}
						}
						else //something before word but nothing after word
							taggable = true;
					}
				}
				else if(a) //nothing before the word, so check if there's something after
				{
					if(ch==' ')
						taggable = true;
					
					else if(ch=='.'||ch==','||ch=='?'||ch=='!'||ch==':'||ch==';')
					{
						pp = true;
						if(!x.substring(i+yl+1).equals(null)) //there's something after the punctuation
						{
							if(x.charAt(i+yl+1)==' ')//can't have double punctuation
							{
								taggable = true;
								app = true;
							}
						}
						else
							taggable = true;
					}
				}
			}
			else//nothing before or after..i.e tag is word
			{
				taggable = true;
			}
			
			if(taggable)
			{
				if(xl==yl || xl-yl==1)//tag is word or tag is word+punctuation
				{
					taggableWord = new TagNode(t.tag, null, null);
					t.tag = tag;
					t.firstChild = taggableWord;
					t1 = true;
					
				}
				if(xl > yl) 
				{
				tagToAdd = new TagNode(tag, null, null);
				
				if(pp)
				{
					taggableWord = new TagNode(t.tag.substring(i, yl)+t.tag.charAt(i+yl), null, null);
				}
				else
				{
					if(a)
						taggableWord = new TagNode(t.tag.substring(i, yl), null, null);
					else
						taggableWord = new TagNode(t.tag.substring(i),null,null);
				}
				
				tagToAdd.firstChild = taggableWord;
				
				if(a)
				{
					if(pp)
						tagAfterWord = new TagNode(t.tag.substring(i+yl+1),null,null);
					else
						tagAfterWord = new TagNode(t.tag.substring(i+yl), null, null);
					
					tagToAdd.sibling = tagAfterWord;
				}
				else
				{
					tagToAdd.sibling = t.sibling;
				}
				
				t.tag = t.tag.substring(0,i);
				t.sibling = tagToAdd;
				t2 = true;
				}
			}
		}
		*/
		if(!t2)//you just changed t's sibling so don't go there
			recFindTag(t.sibling, word, tag);
		
		if(!t1)//you just changed t's first child so don't go there
			recFindTag(t.firstChild, word, tag);
		
	}
	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
	
}
