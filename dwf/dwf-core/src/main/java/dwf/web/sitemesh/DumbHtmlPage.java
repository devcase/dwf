package dwf.web.sitemesh;

import java.io.IOException;
import java.io.Writer;

import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.module.sitemesh.parser.AbstractPage;

public class DumbHtmlPage extends AbstractPage implements HTMLPage {
	private final static char[] HEAD_START_TAG = "<head>".toCharArray();
	private final static char[] HEAD_END_TAG = "</head>".toCharArray();

	private final static char[] BODY_START_TAG = "<body>".toCharArray();
	private final static char[] BODY_END_TAG = "</body>".toCharArray();

	private final static char[] SCRIPT_START_TAG = "<script".toCharArray();
	private final static char[] SCRIPT_END_TAG = "</script>".toCharArray();

	private final static char[] TITLE_START_TAG = "<title>".toCharArray();
	private final static char[] TITLE_END_TAG = "</title>".toCharArray();
	
	private final static char[] TABLE_START_TAG = "<table".toCharArray();
	private final static char[] TABLE_END_TAG = "</table>".toCharArray();

	
	private final static char[] META_START_TAG = "<meta name=\"".toCharArray();
	private final static char[] META_MIDDLE_TAG = "\" content=\"".toCharArray();
	private final static char[] META_END_TAG = "\"".toCharArray();
	

	private final int headStartIndex;
	private final int headEndIndex;
	private final int bodyStartIndex;
	private final int bodyEndIndex;
	private final int scriptStartIndex;
	private final int scriptEndIndex;
	private final int titleStartIndex;
	private final int titleEndIndex;
	
	public DumbHtmlPage(char[] page) {
		final int pageLength = page.length;
		final int headStartIndex = indexOf(page, HEAD_START_TAG, 0, pageLength) + HEAD_START_TAG.length;
		final int titleStartIndex = indexOf(page, TITLE_START_TAG, headStartIndex, pageLength) + TITLE_START_TAG.length;
		final int titleEndIndex = indexOf(page, TITLE_END_TAG, titleStartIndex, pageLength);
		final int headEndIndex = indexOf(page, HEAD_END_TAG, headStartIndex, pageLength);
		final int bodyStartIndex = indexOf(page, BODY_START_TAG, headEndIndex + HEAD_END_TAG.length, pageLength) + BODY_START_TAG.length;
		final int bodyEndIndex = indexOf(page, BODY_END_TAG, bodyStartIndex, pageLength);
		final int scriptStartIndex = indexOf(page, SCRIPT_START_TAG, bodyStartIndex + BODY_START_TAG.length, pageLength);
		final int scriptEndIndex = lastIndexOf(page, SCRIPT_END_TAG, pageLength) + SCRIPT_END_TAG.length;
		
		
		//search for meta tags
		int metaCursor = indexOf(page, META_START_TAG, headStartIndex, headEndIndex);
		while(metaCursor > 0) {
			int nameStart = metaCursor + META_START_TAG.length;
			int nameEnd = indexOf(page, META_MIDDLE_TAG, nameStart, headEndIndex);
			int valueEnd = indexOf(page, META_END_TAG, nameEnd + META_MIDDLE_TAG.length, headEndIndex);
			
			if(nameStart > 0 && nameEnd > 0 && valueEnd > 0) {
				String propertyName=String.valueOf(page, nameStart, nameEnd - nameStart);
				String value = String.valueOf(page, nameEnd + META_MIDDLE_TAG.length, valueEnd - nameEnd - META_MIDDLE_TAG.length);
				
				this.addProperty("meta." + propertyName, value);
				metaCursor = indexOf(page, META_START_TAG, valueEnd, headEndIndex);
			} else {
				metaCursor = -1;
			}
		}
		
		this.pageData = page;
		this.headStartIndex = headStartIndex;
		this.headEndIndex = headEndIndex;
		this.bodyStartIndex = bodyStartIndex;
		this.bodyEndIndex = bodyEndIndex;
		this.scriptStartIndex = scriptStartIndex;
		this.scriptEndIndex = scriptEndIndex;
		this.titleStartIndex = titleStartIndex;
		this.titleEndIndex = titleEndIndex;
	}


	@Override
	public void writeBody(Writer out) throws IOException {
		
		if(bodyStartIndex > 0 && bodyEndIndex - bodyStartIndex > 0) {
			if(scriptStartIndex > 0 && scriptEndIndex - scriptStartIndex > 0) {
				//se tiver scripts dentro do corpo, dá um jeito de não desenhar!
				out.write(pageData, bodyStartIndex, scriptStartIndex - bodyStartIndex);
				out.write(pageData, scriptEndIndex, bodyEndIndex - scriptEndIndex);
				
			} else {
				out.write(pageData, bodyStartIndex, bodyEndIndex - bodyStartIndex);
			}

		}
	}

	
	
	@Override
	public String getTitle() {
		if(titleStartIndex > 0 && titleEndIndex - titleStartIndex > 0)
			return String.valueOf(pageData, titleStartIndex, titleEndIndex - titleStartIndex);
		else return null;
	}

	@Override
	public String getHead() {
		if(headStartIndex > 0 && headEndIndex - headStartIndex > 0)
			return String.valueOf(pageData, headStartIndex, headEndIndex - headStartIndex);
		else return null;
	}

	@Override
	public boolean isFrameSet() {
		return false;
	}

	@Override
	public void setFrameSet(boolean arg0) {
	}

	@Override
	public void writeHead(Writer out) throws IOException {
		if(headStartIndex > 0 && headEndIndex - headStartIndex > 0)
			out.write(pageData, headStartIndex, headEndIndex - headStartIndex);
	}

	public void writeScript(Writer out) throws IOException {
		if(scriptStartIndex > 0 && scriptEndIndex - scriptStartIndex > 0)
			out.write(pageData, scriptStartIndex, scriptEndIndex - scriptStartIndex);
		
	}
	
	/**
	 * Searches and writes the first table from the decorated page 
	 * @param out
	 * @throws IOException
	 */
	public void writeTable(Writer out) throws IOException {
		int pageLength = pageData.length;
		final int tableStartIndex = indexOf(pageData, TABLE_START_TAG, this.bodyStartIndex, pageLength);
		final int tableEndIndex = indexOf(pageData, TABLE_END_TAG, tableStartIndex + TABLE_START_TAG.length, pageLength) + TABLE_END_TAG.length;
		if(tableStartIndex > 0 && tableEndIndex - tableStartIndex > 0)
			out.write(pageData, tableStartIndex, tableEndIndex - tableStartIndex);
	}
	

	
    static int indexOf(char[] source, 
            char[] target, 
            int fromIndex,
            int toIndex) {
    	int sourceCount = toIndex;
    	int sourceOffset = 0;
    	int targetOffset = 0;
    	int targetCount = target.length;
    	
        if (fromIndex >= sourceCount) {
            return (targetCount == 0 ? sourceCount : -1);
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        if (targetCount == 0) {
            return fromIndex;
        }

        char first = target[targetOffset];
        int max = sourceOffset + (sourceCount - targetCount);

        for (int i = sourceOffset + fromIndex; i <= max; i++) {
            /* Look for first character. */
            if (source[i] != first) {
                while (++i <= max && source[i] != first);
            }

            /* Found first character, now look at the rest of v2 */
            if (i <= max) {
                int j = i + 1;
                int end = j + targetCount - 1;
                for (int k = targetOffset + 1; j < end && source[j]
                        == target[k]; j++, k++);

                if (j == end) {
                    /* Found whole string. */
                    return i - sourceOffset;
                }
            }
        }
        return -1;
    }
    
    
    /**
     * Code shared by String and StringBuffer to do searches. The
     * source is the character array being searched, and the target
     * is the string being searched for.
     *
     * @param   source       the characters being searched.
     * @param   sourceOffset offset of the source string.
     * @param   sourceCount  count of the source string.
     * @param   target       the characters being searched for.
     * @param   targetOffset offset of the target string.
     * @param   targetCount  count of the target string.
     * @param   fromIndex    the index to begin searching from.
     */
    static int lastIndexOf(char[] source, 
            char[] target, 
            int fromIndex) {
		final int sourceOffset = 0; 
		final int sourceCount = source.length;
		final int targetOffset = 0;
		final int targetCount = target.length;

        /*
         * Check arguments; return immediately where possible. For
         * consistency, don't check for null str.
         */
        int rightIndex = sourceCount - targetCount;
        if (fromIndex < 0) {
            return -1;
        }
        if (fromIndex > rightIndex) {
            fromIndex = rightIndex;
        }
        /* Empty string always matches. */
        if (targetCount == 0) {
            return fromIndex;
        }

        int strLastIndex = targetOffset + targetCount - 1;
        char strLastChar = target[strLastIndex];
        int min = sourceOffset + targetCount - 1;
        int i = min + fromIndex;

        startSearchForLastChar:
        while (true) {
            while (i >= min && source[i] != strLastChar) {
                i--;
            }
            if (i < min) {
                return -1;
            }
            int j = i - 1;
            int start = j - (targetCount - 1);
            int k = strLastIndex - 1;

            while (j > start) {
                if (source[j--] != target[k--]) {
                    i--;
                    continue startSearchForLastChar;
                }
            }
            return start - sourceOffset + 1;
        }
    }

	
}
