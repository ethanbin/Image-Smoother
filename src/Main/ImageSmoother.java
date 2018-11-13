package Main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageSmoother {
    public static BufferedImage grayscaleImage(BufferedImage image){
        for (int j = 0; j < image.getHeight(); j++){
            for (int i = 0; i < image.getWidth(); i++){
                Color color = new Color(image.getRGB(i,j));
                int average = (color.getRed() + color.getGreen() + color.getBlue())/3;
                Color grayed = new Color(average, average, average);
                image.setRGB(i, j, grayed.getRGB());
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

    private static Color getMedianPixel(BufferedImage image){
        // put every Color in subimage into an array
        Color[] pixels = new Color[image.getWidth() * image.getWidth()];
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                Color color = new Color(image.getRGB(j,i));
                // get greyscale value
                pixels[i*image.getWidth() + j] = color;
            }
        }

        // calculate median index
        int targetIndex = pixels.length/2;

        // selection sort iteratively
        for (int i = 0; i <= targetIndex; i++)
        {
            // Find the minimum element in unsorted array
            int minIndex = i;
            for (int j = i+1; j < pixels.length; j++) {
                int jGray = (pixels[j].getRed() + pixels[j].getBlue() + pixels[j].getGreen()) / 3;
                int minGray = (pixels[minIndex].getRed() + pixels[minIndex].getBlue() + pixels[minIndex].getGreen()) / 3;
                if (jGray < minGray)
                    minIndex = j;
            }

            // Swap the found minimum element with the first
            // element
            Color temp = new Color(pixels[minIndex].getRGB());
            pixels[minIndex] = new Color(pixels[i].getRGB());
            pixels[i] = new Color(temp.getRGB());
        }

        return pixels[targetIndex];
    }

    /* // deprecated
    private static Color getGreyscaleMedianPixel(BufferedImage image){
        int[] values = new int[image.getWidth() * image.getWidth()];
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                Color color = new Color(image.getRGB(j,i));
                // get greyscale value
                int x = j + i*j;
                values[i*image.getWidth() + j] = (color.getRed() + color.getGreen() + color.getBlue())/3;
            }
        }

        int medianGreyscale = OrderStatistic.orderStatistic(values, values.length/2);
        return new Color(medianGreyscale, medianGreyscale, medianGreyscale);
    }
    */

    public static BufferedImage smoothImage(BufferedImage image, int subsize){
        BufferedImage smoothedImage= new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        for (int i = 20; i < image.getHeight() - 20; i++) {
            for (int j = 20; j < image.getWidth() - 20; j++) {
                BufferedImage subImage = image.getSubimage(j+subsize/2,i+subsize/2, subsize, subsize);
                smoothedImage.setRGB(j,i, getMedianPixel(subImage).getRGB());
            }
        }
        return smoothedImage;
    }

    public static void main(String[] args) {
        //OrderStatistic.testAndPrint();
        BufferedImage image;
        try{
            image = openImage("8.png");
        }
        catch (IOException e){
            System.err.println("ERROR: NO FILE FOUND.");
            return;
        }
        //grayscaleImage(image);
        image = smoothImage(image, 3);
        saveImage(image, "out2.png");
    }
}
