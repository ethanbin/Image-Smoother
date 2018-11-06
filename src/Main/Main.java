package Main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Main {

    public static void main(String[] args) {
        BufferedImage image;
        try {
            OrderStatistic.testAndPrint();
            // open image
            File input = new File("butterfly.jpg");
            image = ImageIO.read(input);

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
            // save file
            File ouptut = new File("grayscale.jpg");
            ImageIO.write(image, "jpg", ouptut);
        }
        catch (Exception e){
            System.err.println("ERROR: NO FILE FOUND");
            return;
        }
    }
}
