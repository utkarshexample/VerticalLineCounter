import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class VerticalLineCounter {

    // Pixels darker than this value are treated as black.
    // 128 is the midpoint between pure black (0) and pure white (255).
    private static final int BRIGHTNESS_THRESHOLD = 128;

    // Minimum fraction of pixels in a column that must be black
    // for that column to be considered part of a vertical line.
    private static final double COVERAGE_THRESHOLD = 0.10;


    public static void main(String[] args) {

        // Exactly 1 argument is required — the path to the image
        if (args.length != 1) {
            System.out.println("Usage: java VerticalLineCounter <absolute_image_path>");
            System.out.println("Example: java VerticalLineCounter C:\\assignment\\img_1.jpg");
            System.out.println("Error: Expected 1 argument, got " + args.length + ".");
            return;
        }

        String imagePath = args[0];

        try {
            // Check the file exists before trying to open it
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                System.out.println("Error: File not found: " + imagePath);
                return;
            }
            if (!imageFile.isFile()) {
                System.out.println("Error: Path is not a file: " + imagePath);
                return;
            }

            // Load the image into memory
            BufferedImage image = ImageIO.read(imageFile);
            if (image == null) {
                // ImageIO returns null for unsupported formats instead of throwing
                System.out.println("Error: Unsupported image format: " + imagePath);
                return;
            }

            // Count the lines and print the result
            int lineCount = countVerticalLines(image);
            System.out.println(lineCount);

        } catch (IOException e) {
            // Something went wrong reading the file
            System.out.println("Error reading image: " + e.getMessage());
        } catch (Exception e) {
            // Catch anything unexpected so the app never crashes silently
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }

    // Counts vertical lines by scanning the image column by column, left to right.
    // Consecutive black columns are grouped together and counted as one line.
    // Each time we transition from a white gap into a black group, that's a new line.
    private static int countVerticalLines(BufferedImage image) {
        int width  = image.getWidth();
        int height = image.getHeight();

        int     lineCount  = 0;
        boolean insideLine = false; // are we currently inside a black column group?

        for (int x = 0; x < width; x++) {
            boolean blackColumn = isColumnBlack(image, x, height);

            if (blackColumn && !insideLine) {
                // We just entered a new line
                lineCount++;
                insideLine = true;
            } else if (!blackColumn && insideLine) {
                // The line just ended
                insideLine = false;
            }
        }

        return lineCount;
    }

    // Returns true if enough pixels in this column are black to be part of a line.
    private static boolean isColumnBlack(BufferedImage image, int x, int height) {
        int blackPixelCount = 0;

        for (int y = 0; y < height; y++) {
            if (isPixelBlack(image.getRGB(x, y))) {
                blackPixelCount++;
            }
        }

        double fraction = (double) blackPixelCount / height;
        return fraction >= COVERAGE_THRESHOLD;
    }

    // Helper funcition to checks if a pixel is black.
    // If the brightness score is below our threshold, we consider it black.
    private static boolean isPixelBlack(int rgb) {
        Color  color      = new Color(rgb, true);
        double brightness = 0.299 * color.getRed()
                          + 0.587 * color.getGreen()
                          + 0.114 * color.getBlue();
        return brightness < BRIGHTNESS_THRESHOLD;
    }
}