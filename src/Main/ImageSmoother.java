package Main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
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

    // recursively get and fill in colors
    private Color fillBlank(BufferedImage image, int row, int col){
        // find way to satisfy the below commented if statement
        // if image.getRGB(row,col) != empty return image.getRGB(row,col);

        // calculate the coordinate for color to copy from
        int nextRow = row, nextCol = col;
        if (row == 0) nextRow = row + 1;
        else if (row == image.getHeight()) nextRow = row - 1;
        if (col == 0) nextCol = col + 1;
        else if (col == image.getWidth()) nextCol = col - 1;

        // get the color of the given coordinates as innerColor
        Color innerColor = fillBlank(image.getSubimage(1,1,image.getWidth() - 2, image.getHeight() - 2),
                nextRow, nextCol);
        // since the first statement didn't trigger, this current pixel
        // has no color, so set this current pixel to innerColor
        image.setRGB(row, col, innerColor.getRGB());

        // return the color we got, which is now the same as this current pixel's
        return innerColor;
    }

    // this method will get a subimage in the case where the required subimage is out
    // of bounds of the original image. It will do this by copying the pixels at the
    // available edge(s) to the places that would be out of bounds.
    private BufferedImage getSubImageWithCopiedEdges(BufferedImage image, int x, int y, int subsize) {
        Color [][] pixelMatrix = new Color[subsize][subsize];
        // calculate range of coordinates that subimage would have within image
        int rowStart = x - subsize/2;
        int rowEnd   = x + subsize/2;
        int colStart = y - subsize/2;
        int colEnd   = y + subsize/2;
        for (int i = rowStart; i <= rowEnd; i++){
            for (int j = colStart; j <= colEnd; j++) {
                pixelMatrix[i][i] = new Color(image.getRGB(i,j));
            }
        }
        for (int i = 0; i < 101; i++)
            fillBlank(pixelMatrix, 0, i, 0,0, image.getHeight(), image.getWidth());
        BufferedImage subimage = new BufferedImage(subsize, subsize, image.getType());
        for (int i = 0; i < subsize; i++)
            for (int j = 0; j < subsize; j++)
                subimage.setRGB(i,j, pixelMatrix[i][j].getRGB());

        return subimage;
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
        for (int i = 0; i <= targetIndex; i++) {
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
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                BufferedImage subImage;
                try {
                    subImage = image.getSubimage(j + subsize / 2, i + subsize / 2, subsize, subsize);
                }
                catch (RasterFormatException e){

                    subImage = getSubImageWithCopiedEdges(image, j, i, subsize);
                }
                smoothedImage.setRGB(j,i, getMedianPixel(subImage).getRGB());
            }
        }
        image = smoothedImage;
    }

    public static void main(String[] args) {
        ImageSmoother smoother = new ImageSmoother(
                "samples/butterfly.jpg",
                "samples/image-out-3.png");
        if (!smoother.imageExists()) return;
        BufferedImage image = new BufferedImage(101, 101, BufferedImage.TYPE_INT_RGB);
        image.setRGB(50, 50, 100000100);
        smoother.image=image;
        //smoother.smoothImage(3);
        smoother.saveImage();
    }
}
