package graph;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.regex.Pattern;

public class IDUtils
{
	static NumberFormat numberFormat = new DecimalFormat("#0.0000");
	static Pattern number = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?");

	public static String md5(Map<String, String> input, List<String> validKeys, int idSize)
	{
		String md5 = null;
	     
	    if(null == input) return null;
	     
	    try {
		    //Create MessageDigest object for MD5
		    MessageDigest digest = MessageDigest.getInstance("MD5");
		    //make sure it's empty
		    digest.reset();
		     
		    StringBuffer buff = new StringBuffer();
		    //need to iterate over keys in a reliable way
		    for (String key : validKeys){
		    	buff.append(key);
		    	if (input.containsKey(key)){
		    		String val = input.get(key);
			    	//check if the value is a number...if it is then convert to a double
			    	//	get formatted/rounded number
			    	if (isNumber(val))
			    	{
			    		Double dVal = Double.valueOf(val);
			    		buff.append(numberFormat.format(dVal));
			    	}else{
			    		buff.append(val);
			    	}
		    	}
		    }
		    
		    //Update input string in message digest
		    digest.update(buff.toString().getBytes(), 0, buff.length());
		
		    //Converts message digest value in base 36 (alphanumeric)
		    md5 = new BigInteger(1, digest.digest()).toString(36);
		    
	    } catch (NoSuchAlgorithmException e) {
	
	        e.printStackTrace();
	    }
	    if (idSize < md5.length())
	    {
	    	return md5.substring(0, idSize);
	    }
	    return md5;
	}
	
	public static String md5(SortedSet<String> keys)
    {
    	Map<String,String> properties = new HashMap<String,String>();
    	properties.put("key", keys.toString());
    	return IDUtils.md5(properties,16);
    }
	
	public static String md5(Map<String, String> input, int idSize)
	{
		String md5 = null;
	     
	    if(null == input) return null;
	     
	    try {
		    //Create MessageDigest object for MD5
		    MessageDigest digest = MessageDigest.getInstance("MD5");
		    //make sure it's empty
		    digest.reset();
		     
		    StringBuffer buff = new StringBuffer();
		    //Do we need to sort map keys? 
		    //if the maps are identical iteration should be identical
		    for (Map.Entry<String, String> entry : input.entrySet())
		    {
		    	buff.append(entry.getKey());
		    	//check if the value is a number...if it is then convert to a double
		    	//	get formatted/rounded number
		    	if (isNumber(entry.getValue()))
		    	{
		    		Double val = Double.valueOf(entry.getValue());
		    		buff.append(numberFormat.format(val));
		    	}else{
		    		buff.append(entry.getValue());
		    	}
		    }
		    
		    //Update input string in message digest
		    digest.update(buff.toString().getBytes(), 0, buff.length());
		
		    //Converts message digest value in base 36 (alphanumeric)
		    md5 = new BigInteger(1, digest.digest()).toString(36);
		    
	    } catch (NoSuchAlgorithmException e) {
	
	        e.printStackTrace();
	    }
	    if (idSize < md5.length())
	    {
	    	return md5.substring(0, idSize);
	    }
	    return md5;
	}
	
	static boolean isNumber(String input)
	{
		return number.matcher(input).matches();
	}
}
