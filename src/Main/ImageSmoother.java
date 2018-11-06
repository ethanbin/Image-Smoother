package Main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageSmoother {
    public static BufferedImage grayscaleImage(BufferedImage image){
        int cols = image.getHeight();
        int rows = image.getWidth();

        for (int j = 0; j < cols; j++){
            for (int i = 0; i < rows; i++){
                Color color = new Color(image.getRGB(j,i));
                int average = (color.getRed() + color.getGreen() + color.getBlue())/3;
                Color grayed = new Color(average, average, average);
                image.setRGB(j,i, grayed.getRGB());
            }
        }
        return image;
    }

    public static BufferedImage openImage(String path) throws IOException {
        // open image
        File input = new File(path);
        return ImageIO.read(input);
    }

    public static boolean saveImage(BufferedImage image, String outPath){
        // save file
        try {
            File ouptut = new File(outPath);
            ImageIO.write(image, "png", ouptut);
        }
        catch (Exception e){
            System.err.println("ERROR: Could not save file.");
            return false;
        }
        return true;
    }

    // not yet implemented
    public static BufferedImage smoothImage(BufferedImage image, int subsize){
        return null;
    }

    public static void main(String[] args) {
        //OrderStatistic.testAndPrint();
        BufferedImage image;
        try{
            image = openImage("butterfly.jpg");
        }
        catch (IOException e){
            System.err.println("ERROR: NO FILE FOUND.");
            return;
        }
        grayscaleImage(image);
        saveImage(image, "out.png");
    }
}
