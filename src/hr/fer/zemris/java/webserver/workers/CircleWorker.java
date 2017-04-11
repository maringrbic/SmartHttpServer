package hr.fer.zemris.java.webserver.workers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Represents a type of worker.
 * 
 * This worker has a task to render a 200x200 png image, create a circle in it 
 * and fill it with red color.
 * 
 * @author Marin Grbić
 * @version 1.0
 */
public class CircleWorker implements IWebWorker {

    @Override
    public void processRequest(RequestContext context) {
        BufferedImage bim = new BufferedImage(200, 200, BufferedImage.TYPE_3BYTE_BGR);
        
        Graphics2D g2d = bim.createGraphics();
        g2d.setColor(Color.RED);
        g2d.fillOval(0, 0, 200, 200);
        g2d.dispose();
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
	  ImageIO.write(bim, "png", bos);
	  context.write(bos.toByteArray());
        } catch (IOException e) {
	  e.printStackTrace();
        }

    }

}
