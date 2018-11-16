package Main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageSmoother {
    private boolean imageExists;
    private boolean overwritable;
    private String imageLocation;
    private String saveLocation;
    private BufferedImage image;

    public ImageSmoother(String imageLocation) {
        this.imageLocation = imageLocation;
        this.overwritable = false;
        imageExists = openImage();
    }

    public ImageSmoother(String imageLocation, String saveLocation) {
        this.imageLocation = imageLocation;
        this.saveLocation = saveLocation;
        this.overwritable = false;
        imageExists = openImage();
    }

    public ImageSmoother(String imageLocation, boolean overwritable) {
        this.overwritable = overwritable;
        this.imageLocation = imageLocation;
        imageExists = openImage();
    }

    public ImageSmoother(String imageLocation, String saveLocation, boolean overwritable) {
        this.overwritable = overwritable;
        this.imageLocation = imageLocation;
        this.saveLocation = saveLocation;
        imageExists = openImage();
    }

    public boolean imageExists() {
        return imageExists;
    }

    public boolean isOverwritable() {
        return overwritable;
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public String getSaveLocation() {
        return saveLocation;
    }

    public void setOverwritable(boolean overwritable) {
        this.overwritable = overwritable;
    }

    private boolean openImage() {
        try {
            File input = new File(imageLocation);
            image = ImageIO.read(input);
            return true;
        }
        catch (Exception e) {
            image = null;
            System.err.println("ERROR: Image not found.");
            return false;
        }
    }

    public boolean saveImage(){
        return saveImage(saveLocation, overwritable);
    }

    // If given save location differs from current save location, keep track of
    // this location as the default to use when using saveImage()
    public boolean saveImage(String outPath){
        return saveImage(outPath, this.overwritable);
    }

    public boolean saveImage(boolean overwritable){
        return saveImage(this.saveLocation, overwritable);
    }

    // If given save location differs from current save location, keep track of
    // this location as the default to use when using saveImage()
    public boolean saveImage(String outPath, boolean overwrite){
        if (saveLocation == null || saveLocation.isEmpty()){
            System.err.println("ERROR: No image destination path given.");
            return false;
        }

        try {
            File output = new File(outPath);
            if (output.exists() && !overwrite){
                System.err.println("ERROR: Attempting to overwritable existing file when the overwriting option is off.");
                return false;
            }
            else {
                ImageIO.write(image, "png", output);
                return true;
            }
        }
        catch (Exception e){
            System.err.println("ERROR: Could not save file.");
            return false;
        }
    }

    // make an image greyscale
    public BufferedImage greyscaleImage(BufferedImage image){
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

    // get each Color (AKA pixel) in an image and place it into an array
    private Color[] imageToArray(BufferedImage image){
        Color[] pixels = new Color[image.getWidth() * image.getWidth()];
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                Color color = new Color(image.getRGB(j,i));
                // get greyscale value
                pixels[i*image.getWidth() + j] = color;
            }
        }
        return pixels;
    }

    private Color getMedianPixel(BufferedImage image){
        // put every Color in subimage into an array
        Color[] pixels = imageToArray(image);

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

    public void smoothImage(int subsize){
        BufferedImage smoothedImage= new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        for (int i = 20; i < image.getHeight() - 20; i++) {
            for (int j = 20; j < image.getWidth() - 20; j++) {
                BufferedImage subImage;
                //try {
                    subImage = image.getSubimage(j + subsize / 2, i + subsize / 2, subsize, subsize);
                //}
                //catch (RasterFormatException e){

                //}
                smoothedImage.setRGB(j,i, getMedianPixel(subImage).getRGB());
            }
        }
        image = smoothedImage;
    }

    public static void main(String[] args) {
        ImageSmoother smoother = new ImageSmoother(
                "samples/11.png",
                "samples/11-out-5.png");
        if (!smoother.imageExists()) return;
        smoother.smoothImage(3);
        smoother.saveImage();
    }
}
