import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Random;

import javax.swing.*;

/**
 * * FractalMaker is a JPanel upon which you can render images. See the
 * implementation of the method {@code render(Graphics2D g)} (which is called by
 * the FractalMaker's overridden {@code paintComponent(Graphics g)} method) to
 * change the behavior of the FractalMaker.
 * <p>
 * Note: I am not an aficionado of graphics processing in Swing or in general;
 * there may be more effective ways to do this, and I am always open to
 * suggestions. The following describes my methods that seem to work teh best to
 * alter the FractalMaker's behavior and to draw things to the panel.
 * <p>
 * In the @code render(Graphics2D g)} method, you can use the BufferedImage
 * {@code image} and {@code int[] pixels} fields to easily manipulate what is
 * displayed on the FractalMaker by manipulating the pixel data of the image and
 * then drawing the resulting image on the {@code Graphics2D g} parameter.
 * Alternatively, you can also call methods directly on the the graphics context
 * such as {@code setBackground(Color)} or any other host of useful methods.
 * 
 * @author Aaron Carson
 * @since Jun 23, 2014
 * @version Jan 24, 2015
 */
public class FractalMaker extends JPanel
{
	
	// ********************************************************************
	// Class constants
	// ********************************************************************
	
	private static final long	serialVersionUID	= -1475062022536824052L;
	public static final int		TILE_SIZE			= 16;
	public static final int		SCALE				= 1;
	public static final int		ARGB				= BufferedImage.TYPE_INT_ARGB;
	public static final int		RGB					= BufferedImage.TYPE_INT_RGB;
	
	// ********************************************************************
	// Fields
	// ********************************************************************
	
	protected int				alpha;
	protected BufferedImage		image;
	protected int[]				pixels;
	private Random				r;
	
	// ********************************************************************
	// Constructor
	// ********************************************************************
	
	/**
	 * Create a new Render Area of the specified size.
	 * 
	 * @param width The width, in pixels.
	 * @param height The height, in pixels.
	 */
	public FractalMaker(int width, int height) {
		super();
		alpha = 255;
		setPreferredSize(new Dimension(width, height));
		setSize(getPreferredSize());
		setBackground(Color.BLACK);
		r = new Random();
		image = new BufferedImage(getWidth(), getHeight(), RGB);
		pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	}
	
	// ********************************************************************
	// Methods
	// ********************************************************************
	
	/**
	 * PaintComponent updates the component by drawing the data of this
	 * RenderArea's BufferedImage directly to the component.
	 * <p>
	 * Note 1: overwrite paint(Graphics g) to increase performance, but no added
	 * components are drawn.
	 * <p>
	 * Note 2: overwrite paintComponent(Graphics g) to draw all extra layers.
	 * 
	 * @see javax.swing.JComponent#paintComponent(Graphics g)
	 */
	// public void paint(Graphics g) {
	// super.paint(g);
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		render((Graphics2D) g);
	}
	
	/**
	 * Assign alpha a new value. (Alpha only matters for the methods that use
	 * alpha or if you change the image type to ARGB).
	 * 
	 * @param value
	 */
	public void setAlpha(int value) {
		this.alpha = value;
	}
	
	/**
	 * Called by paintComponent(Graphics g) to update this FractalMaker. This
	 * method needs to draw something to the Graphics2D parameter, or nothing
	 * will show.
	 * <p>
	 * For experimentation purposes, change which methods are called in the
	 * first section of the method to view different fractals in the GUI.
	 * 
	 * @param g The Graphics2D object to update.
	 */
	public void render(Graphics2D g) {
		// ***********************************
		// First, edit the image's pixel data. (try any methods below)
		// ***********************************
		
		// drawRandomNoise();
		// drawHorizontalNoiseBands();
		// drawStripes();
		// drawSquareFractal(2);
		// drawBitShiftedSquareFractal(722);
		drawBlueSquareFractal();
		// drawGreenSquareFractal();
		// drawRedSquareFractal();
		// drawQuadraticFractal();
		// drawTriangleFractal();
		// drawFadingTriangleFractal();
		
		// ***************************************************
		// Second, draw the edited image on the FractalMaker.
		// ***************************************************
		g.drawImage(image, null, null);
		
		// alternatively, use other methods like this to draw content
		// g.setColor(Color.BLACK);
		// g.fillRect(0, 0, getWidth(), getHeight());
	}
	
	// ********************************************************************
	// Drawing Methods
	//
	// Here is where the fun starts. Each method below iterates through each of
	// pixel and modifies the value with some expression or algorithm. Some are
	// as simple as assigning a random value, while others use some combination
	// of arithmetic operators (+,-,*,/) along with bitwise logic operators
	// (&,|,^,~) and bit shifting operators (<<,>>,<<<,>>>).
	//
	// I use those operators in conjunction with each pixel's x and y
	// coordinates to alter the color of the images. Everything is a process of
	// discovery for me; I am not extremely familiar with exactly what will
	// happen, but the results can be surprising!
	//
	// I find the current x and y position of each pixel in the 1-dimensional
	// integer array by doing x + y * image-width. This will successfully
	// touch every pixel in the image when iterating with nested for-loops with
	// a O(n) running time, where n = the number of pixels.
	// ********************************************************************
	
	/**
	 * Draws random noise on the image.
	 */
	public void drawRandomNoise() {
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				pixels[x + y * getWidth()] = r.nextInt();
			}
		}
	}
	
	/**
	 * Draws random noise to an integer array, having a horizontal band effect.
	 * 
	 * @param pixels The pixels to draw to.
	 */
	public void drawHorizontalNoiseBands() {
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				int i = x + y * getWidth();
				pixels[i] = i & r.nextInt(256) << 8;
			}
		}
	}
	
	/**
	 * Draws stripes on the image.
	 */
	public void drawStripes() {
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				pixels[x + y * getWidth()] = (x * 2 + y / 2) ^ 255 << 23;
			}
		}
	}
	
	/**
	 * draws a square fractal to the image.
	 */
	public void drawSquareFractal(int divisor) {
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				int i = x + y * getWidth();
				pixels[i] = ((x ^ y) / divisor) ^ 255 << 24;
			}
		}
	}
	
	/**
	 * draws a square fractal to the image.
	 * 
	 * @param bitShift the value to bit shift the color data by.
	 */
	public void drawBitShiftedSquareFractal(int bitShift) {
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				pixels[x + y * getWidth()] = ((x ^ y) / 2) << bitShift;
			}
		}
	}
	
	/**
	 * draws a blue square fractal to the image.
	 */
	public void drawBlueSquareFractal() {
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				pixels[x + y * getWidth()] = (x ^ y) / 2;
			}
		}
	}
	
	/**
	 * draws a green square fractal to the image.
	 */
	public void drawGreenSquareFractal() {
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				pixels[x + y * getWidth()] = ((x ^ y) / 2) << 8;
			}
		}
	}
	
	/**
	 * draws a red square fractal to the image.
	 */
	public void drawRedSquareFractal() {
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				pixels[x + y * getWidth()] = ((x ^ y) / 2) << 16;
			}
		}
	}
	
	/**
	 * Draws a quadratic fractal to the image of various colors.
	 */
	public void drawQuadraticFractal() {
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				pixels[x + y * getWidth()] = (~x * ~y) ^ (x ^ y) / 2;
			}
		}
	}
	
	/**
	 * Draw a triangle fractal to the Image.
	 */
	public void drawTriangleFractal() {
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				pixels[x + y * getWidth()] = (x ^ y) / ((x & y) + 1)
						^ 255 << 24;
			}
		}
	}
	
	/**
	 * Draw a fading triangle fractal to the image.
	 */
	public void drawFadingTriangleFractal() {
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				pixels[x + y * getWidth()] = (x ^ y) / ((~x & y) + 1);
			}
		}
	}
	
	/**
	 * Simple description output method for when looping alpha. Used to update a
	 * JLabel that displays teh current Alpha value.
	 * 
	 * @return A String.
	 */
	private String getAlphaDescription() {
		return " Alpha: " + alpha;
	}
	
	// *******************************************************************
	// MAIN
	// *******************************************************************
	
	/**
	 * Test the render area in a JFrame. Run this method to view the results
	 * using this code only.
	 * 
	 * @param args Does nothing.
	 */
	public static void main(String[] args) {
		final FractalMaker p = new FractalMaker(512, 512);
		final JLabel label = new JLabel(p.getAlphaDescription());
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame("RenderArea Test");
				frame.getContentPane().add(p, BorderLayout.CENTER);
				frame.getContentPane().add(label, BorderLayout.SOUTH);
				frame.pack();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

    /**
     * This is a test method that was written with vim.
     */
    public static void testMethod(){
        System.out.println("I wrote this method with vim.");
    }
}
