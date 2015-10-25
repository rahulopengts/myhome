package org.openhab.ui.webappprofile.internal;


//URL that generated this code:
//http://txt2re.com/index-java.php3?s=ADDRESS1%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20PIC%20X(254)&-7&-13&-12&6&48&-3 

import java.util.regex.*;

class Test
{
public static void main(String[] args)
{
 String txt="ADDRESS1 PIC X(254 )";

 String re1="(ADDRESS1)";	// Variable Name 1
 String re2=".*";	// Non-greedy match on filler
 String re3="(PIC)";	// Word 1
 String re4="( )";	// White Space 1
 String re5="(X)";	// Variable Name 2
 String re6=".*?";	// Non-greedy match on filler
 String re7="(\\d+)";	// Integer Number 1
 String re8="(.)";	// Any Single Character 1

 Pattern p = Pattern.compile(re1+re2+re3+re4+re5+re6+re7+re8,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
 Matcher m = p.matcher(txt);
 if (m.find())
 {
     String var1=m.group(1);
     String word1=m.group(2);
     String ws1=m.group(3);
     String var2=m.group(4);
     String int1=m.group(5);
     String c1=m.group(6);
     System.out.print("("+var1.toString()+")"+"("+word1.toString()+")"+"("+ws1.toString()+")"+"("+var2.toString()+")"+"("+int1.toString()+")"+"("+c1.toString()+")"+"\n");
 }
}
}
