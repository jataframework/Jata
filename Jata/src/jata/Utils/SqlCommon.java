package jata.Utils;

public class SqlCommon {

	
	
	
	public static String join(Object[] strs, String del) {
		StringBuilder sb = new StringBuilder();
		for (int i=0;i<strs.length;i++) {
			if (strs[i].getClass() == String.class || strs[i].getClass() == Character.class || strs[i].getClass() == char.class) {
				sb.append("'").append(strs[i]).append("'");
			} else {
				sb.append(strs[i]);
			}
			if (i<strs.length-1) {
				sb.append(del);
			}
		}
		return sb.toString();
	}
}
