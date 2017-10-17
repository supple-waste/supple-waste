package com.supple.waste;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.svg.SVGDocument;

import com.google.common.base.Charsets;

public class Main {

    private static final Color CUT_COLOR = Color.BLUE;
    private static final Color BURN_COLOR = Color.BLACK;
    private static final int HEIGHT_PADDING = 5;
    private static final int WIDTH_PADDING = 6;

    private static final int ELEMENT_PADDING = 1;
    private static final int BORDER_PADDING = 3;
    private static final int CANVAS_WIDTH = 600;
    private static final int CANVAS_HEIGHT = 600;

    public static void main(String[] args) {
        List<String> words = getWords("nmh-words-processed.txt");

        DOMImplementation domImpl = SVGDOMImplementation.getDOMImplementation();
        String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
        SVGDocument document = (SVGDocument) domImpl.createDocument(svgNS, "svg", null);
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

        writeWords(words, svgGenerator);

        boolean useCSS = true; // we want to use CSS style attributes
        try {
            Writer out = new OutputStreamWriter(new FileOutputStream(new File("out.svg")), Charsets.UTF_8);
            svgGenerator.stream(out, useCSS);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private static void writeWords(List<String> words, Graphics2D graphics) {
        int elementHeight = getElementHeight(graphics);

        int x = BORDER_PADDING;
        int y = BORDER_PADDING;

        for (String word : words) {
            int width = getWordWidth(word);
            if (x + width + ELEMENT_PADDING + BORDER_PADDING> CANVAS_WIDTH) {
                x = BORDER_PADDING;
                y += elementHeight + ELEMENT_PADDING;
                if (y + BORDER_PADDING > CANVAS_HEIGHT) {
                    return;
                }
            }
            writeWord(graphics, word, x, y);
            x += width + ELEMENT_PADDING;
        }
    }

    private static int getElementHeight(Graphics graphics) {
        FontMetrics fm = graphics.getFontMetrics();
        return fm.getHeight() + HEIGHT_PADDING;
    }

    private static int getWordWidth(String word) {
    	BufferedImage image = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
    	Graphics2D graphics = (Graphics2D) image.getGraphics();
    	try {
	        Font font = graphics.getFont();
	        GlyphVector gv = font.createGlyphVector(graphics.getFontRenderContext(), word);
	        Rectangle2D stringBoundsForPosition = gv.getOutline().getBounds2D();
	        double xForShapeCreation = -500;
	        double yForShapeCreation = -500;
	        Shape textShape = gv.getOutline((float) xForShapeCreation, (float) yForShapeCreation + graphics.getFontMetrics(font).getAscent());
	        graphics.fill(textShape);
	        Rectangle2D stringBoundsForEverything = textShape.getBounds2D();
	        return (int) (stringBoundsForEverything.getWidth()*.9 + WIDTH_PADDING *2);
    	} finally {
    		graphics.dispose();
    	}
    }

    private static void writeWord(Graphics2D graphics, String word, int x, int y) {
        int width = getWordWidth(word);
        int height = getElementHeight(graphics);
        
        graphics.setColor(CUT_COLOR);
        graphics.setStroke(new BasicStroke(0.01f));
        graphics.drawRoundRect(x, y, width, height, 7, 7);
        

        graphics.setColor(BURN_COLOR);
        Font font = graphics.getFont();
        GlyphVector gv = font.createGlyphVector(graphics.getFontRenderContext(), word);
        
//        BufferedImage textImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//        Graphics2D textGraphics = (Graphics2D) textImage.getGraphics();
        try {
        	
        	graphics.fill(gv.getOutline(x + (WIDTH_PADDING / 2), y + (HEIGHT_PADDING / 2) + graphics.getFontMetrics().getAscent()));
        
//        graphics.drawString(word, x + (WIDTH_PADDING / 2), y + (HEIGHT_PADDING / 2) + graphics.getFontMetrics().getAscent());
        } finally {
        	
        }
    }

    private static List<String> getWords(String path) {
        try {
            return Files.readAllLines(Paths.get(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
