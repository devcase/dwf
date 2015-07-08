package dwf.web.upload;

import java.awt.Color;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.util.exif.ExifUtils;
import net.coobird.thumbnailator.util.exif.Orientation;

import org.apache.poi.util.IOUtils;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Mode;

public abstract class AbstractUploadManager {

	public static File saveImageAsTempFile(RenderedImage image, String contentType) throws IOException {
		String formatName = "jpeg";
		boolean isJpeg = "image/jpeg".equals(contentType); 
		if(!isJpeg)
			formatName = "png";

		File tmpFile = File.createTempFile("tup-tmp", formatName.equals("jpeg") ? ".jpg" : "." + formatName);
		
		tmpFile.deleteOnExit();
		ImageOutputStream ios = ImageIO.createImageOutputStream(tmpFile);
		Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName(formatName);
		ImageWriter writer = iter.next();
		ImageWriteParam iwp = writer.getDefaultWriteParam();
		if(isJpeg) {
			iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			iwp.setCompressionQuality(0.95f);
		}
		writer.setOutput(ios);
		writer.write(null, new IIOImage(image, null, null), iwp);
		writer.dispose();
		return tmpFile;
	}


	public static File resizeImageAndSaveAsTempFile(InputStream inputStream, int targetWidth, int targetHeight, int maxWidth, int maxHeight, boolean noTransparency, String transparencyReplaceColor) throws IOException {
		
		BufferedImage tmpImg = ImageIO.read(inputStream);
		BufferedImage srcImg = null;
		// se tiver transparencia com anotação de noTransparency, pinta o fundo
		if (noTransparency &&  
				(tmpImg.getType() == BufferedImage.TYPE_INT_ARGB || tmpImg.getType() == BufferedImage.TYPE_4BYTE_ABGR)) {
			srcImg = new BufferedImage(tmpImg.getWidth(), tmpImg.getHeight(), BufferedImage.TYPE_INT_RGB);
			String transpColor = transparencyReplaceColor;
			Color bgColor = Color.decode(transpColor);
			srcImg.createGraphics().drawImage(tmpImg, 0, 0, bgColor, null);
			tmpImg.flush();
		} else if (tmpImg.getTransparency() == Transparency.OPAQUE && tmpImg.getType() != BufferedImage.TYPE_INT_RGB) {
			srcImg = new BufferedImage(tmpImg.getWidth(), tmpImg.getHeight(), BufferedImage.TYPE_INT_RGB);
			srcImg.createGraphics().drawImage(tmpImg, 0, 0, null);
			tmpImg.flush();
		} else if (tmpImg.getTransparency() != Transparency.OPAQUE && tmpImg.getType() != BufferedImage.TYPE_INT_ARGB) {
			srcImg = new BufferedImage(tmpImg.getWidth(), tmpImg.getHeight(), BufferedImage.TYPE_INT_ARGB);
			srcImg.createGraphics().drawImage(tmpImg, 0, 0, null);
			tmpImg.flush();
		} else {
			srcImg = tmpImg;
		}
		
		Thumbnails.of(srcImg).size(targetWidth, targetHeight).toFile("C:/asd");
		
		String fileSuffix;
		String outputContentType;
		if(srcImg.getType() == BufferedImage.TYPE_INT_RGB) {
			outputContentType ="image/jpeg"; fileSuffix = ".jpg";
		} else if (srcImg.getType() == BufferedImage.TYPE_INT_ARGB) {
			outputContentType ="image/png"; fileSuffix = ".png";
		} else {
			srcImg.flush();
			throw new RuntimeException("Invalid image type " + srcImg.getType());
		}

		
		BufferedImage resizedImg = null;
		BufferedImage croppedImg = null;
		try {
			Mode resizeMode = (((double) srcImg.getHeight() / (double) srcImg.getWidth()) < ((double) targetHeight / (double) targetWidth)) ? Mode.FIT_TO_HEIGHT : Mode.FIT_TO_WIDTH;
			
			if (maxHeight != 0 || maxWidth != 0) {
				if (srcImg.getWidth() < (maxWidth == 0? Integer.MAX_VALUE : maxWidth) && srcImg.getHeight() < (maxHeight == 0? Integer.MAX_VALUE : maxHeight)) {
					resizedImg = croppedImg = srcImg;
				} else {
					resizedImg = croppedImg = Scalr.resize(srcImg, Scalr.Method.ULTRA_QUALITY, Mode.AUTOMATIC, (maxWidth == 0? Integer.MAX_VALUE : maxWidth), (maxHeight == 0? Integer.MAX_VALUE : maxHeight));
				}
				return saveImageAsTempFile(resizedImg, outputContentType);
			} else {											
				resizedImg = Scalr.resize(srcImg, org.imgscalr.Scalr.Method.ULTRA_QUALITY, resizeMode, targetWidth,
						targetHeight);

				int cropStartX = Math.max((resizedImg.getWidth() - targetWidth) / 2, 0);
				int cropStartY = Math.max((resizedImg.getHeight() - targetHeight) / 2, 0);

				croppedImg = Scalr.crop(resizedImg, cropStartX, cropStartY, targetWidth, targetHeight);
				return saveImageAsTempFile(croppedImg, outputContentType);
			}	
		} finally {
			// limpa (?) dados da memória - TODO estudar mais
			if (srcImg != null)
				srcImg.flush();
			if (resizedImg != null)
				resizedImg.flush();
			if (croppedImg != null)
				croppedImg.flush();
		}		
	}

	public static InputStream exifRotation(InputStream is) throws IOException{
		//Preciso ler uma vez pra pegar width e height e outra vez pra rotacionar a imagem
		ByteArrayInputStream rereadableInputStream = new ByteArrayInputStream(IOUtils.toByteArray(is));
		//pegar width e height
		BufferedImage tempImage = ImageIO.read(rereadableInputStream);
		int width = tempImage.getWidth();
		int height = tempImage.getHeight();
		rereadableInputStream.reset();
		
		//rotaciona a imagem
		BufferedImage b = Thumbnails.of(rereadableInputStream).size(width, height).useExifOrientation(true).asBufferedImage();
		
		//Converte em inputstream para retornar
		ByteArrayOutputStream b2 = new ByteArrayOutputStream();
		ImageIO.write(b, "jpg", b2);
		ByteArrayInputStream input = new ByteArrayInputStream(b2.toByteArray());
		
		return input;
		
	}
}
