package dwf.utils;

import java.text.Normalizer;

public class SearchstringUtils {
	static int UPPER_TO_LOW = 'A' - 'a';
	public static String prepareForSearch(String str) {
		return prepareForSearch(str, Integer.MAX_VALUE);
	}
	
	public static String prepareForSearch(String str, int maxlength) {
		if(str == null) { return ""; }
		
		str = Normalizer.normalize(str, Normalizer.Form.NFD); //substitui acentos por letras
		
		StringBuilder sb = new StringBuilder(Math.min(str.length(), maxlength));
		
		boolean appendSpace = false;
		
		for (int i = 0; i < str.length(); i++) {
			if(sb.length() >= maxlength) {
				break;
			}
			
			char c = str.charAt(i);
			if(c >= 'A' && c <= 'Z') {
				sb.append((char) (c - UPPER_TO_LOW));
				appendSpace = true;
			} else if((c >= 'a' && c <='z') || (c >= '0' && c <='9')) {
				sb.append(c);
				appendSpace = true;
			} else if( c == ' ' || c == 0x00a0 /* no break space */ || c == '\n' /* quebra de linha */) {
				if(appendSpace) {
					sb.append(' ');
					appendSpace = false; //evita espaços duplos
				}
			} else {
				//ignora outros caracteres
			}
		}
		while(sb.length() > 0 && sb.charAt(sb.length() -1) == ' ') {
			sb.deleteCharAt(sb.length() -1); //limpa espaços ao final
		}
		
		return sb.toString();
	}

}
