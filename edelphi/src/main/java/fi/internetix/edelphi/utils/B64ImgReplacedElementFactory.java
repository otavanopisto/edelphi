package fi.internetix.edelphi.utils;

import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Element;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.ReplacedElementFactory;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextFSImage;
import org.xhtmlrenderer.pdf.ITextImageElement;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;

/**
 * Modified version of B64ImgReplacedElementFactory.java from https://gist.github.com/915348#comments
 */
public class B64ImgReplacedElementFactory implements ReplacedElementFactory {

  public ReplacedElement createReplacedElement(LayoutContext c, BlockBox box, UserAgentCallback uac, int cssWidth, int cssHeight) {
    Element e = box.getElement();
    if (e == null) {
      return null;
    }
    
    String nodeName = e.getNodeName();
    if (nodeName.equals("img")) {
      String src = e.getAttribute("src");
      FSImage fsImage = null;
      
      if (src.startsWith("data:")) {
        fsImage = buildBase64Image(c.getSharedContext(), src, uac);
      } else {
        fsImage = uac.getImageResource(e.getAttribute("src")).getImage();
      }

      if (fsImage != null) {
        if (cssWidth != -1 || cssHeight != -1) {
          fsImage.scale(cssWidth, cssHeight);
        }
        return new ITextImageElement(fsImage);
      } else {
        return null;
      }
    }
    
    return null;
  }

  protected FSImage buildBase64Image(SharedContext sharedContext, String srcAttr, UserAgentCallback uac) {
    String b64encoded = srcAttr.substring(srcAttr.indexOf("base64,") + "base64,".length(), srcAttr.length());

    byte[] decodedBytes = Base64.decodeBase64(b64encoded);

    try {
      Image image = Image.getInstance(decodedBytes);
      float factor = sharedContext.getDotsPerPixel();
      image.scaleAbsolute(image.getPlainWidth() * factor, image.getPlainHeight() * factor);
      return new ITextFSImage(image);

    } catch (BadElementException e) {
      return null;
    } catch (IOException e) {
      return null;
    }
  }
  
  public void remove(Element e) {
  }

  public void reset() {
  }

  @Override
  public void setFormSubmissionListener(FormSubmissionListener listener) {

  }
}