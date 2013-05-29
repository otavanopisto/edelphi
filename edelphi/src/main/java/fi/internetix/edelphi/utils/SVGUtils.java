package fi.internetix.edelphi.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import net.hanjava.svg.SVG2EMF;

public class SVGUtils {
	
	public static byte[] convertSvgToEmf(String svgContent) throws IOException {
		InputStream svgStream = new ByteArrayInputStream(svgContent.getBytes("UTF-8"));
		try {
			ByteArrayOutputStream emfStream = new ByteArrayOutputStream();
		  SVG2EMF.convert("about:blank", svgStream, emfStream);
			emfStream.flush();
			emfStream.close();
			return emfStream.toByteArray();
		} finally {
		  svgStream.close();
		}
	}

}
