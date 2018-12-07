package Main;

import Main.OrderStatistic.OrderStatistic;
import Main.OrderStatistic.Strategies.QuickSelectStrategy;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;

public class ImageSmoother {
    private boolean imageExists;
    private boolean overwritable;
    int     windowSize = 0;
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

    private void fillEdges(){
        for (int i = 0; i < image.getHeight(); i++) {
            // left edge
            fillEdge(0, i, 0);
            // right edge
            fillEdge(0, i,image.getWidth()-1);
        }
        for (int i = 0; i < image.getWidth(); i++) {
            // top edge
            fillEdge(0, 0, i);
            // bottom edge
            fillEdge(0, image.getHeight()-1, i);
        }

    }


    // recursively get and fill in RGBs
    private int fillEdge(int layer, int row, int col){
        if (image.getRGB(col,row) != -1 || layer > windowSize/2)
            return image.getRGB(col,row);

        // calculate the coordinate for color to copy from
        int nextRow = row, nextCol = col;
        if (row == layer) nextRow = row + 1;
        else if (row == image.getHeight() - 1 - layer) nextRow = row - 1;
        if (col == layer) nextCol = col + 1;
        else if (col == image.getWidth() - 1 - layer) nextCol = col - 1;

        // get the color of the given coordinates as innerColor
        int innerColor = fillEdge(layer + 1, nextRow, nextCol);
        // since the first statement didn't trigger, this current pixel
        // has no color, so set this current pixel to innerColor
        image.setRGB(col, row, innerColor);

        // return the color we got, which is now the same as this current pixel's
        return innerColor;
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

    private void createEdges() {
        // for testing
        // new picture will have original's height + windowSize-1, since half of the windowSize will be appended to top
        // and bottom of image, which is windowSize-1 in total since windowSize must always be odd.
        int edgeSize = windowSize - 1;
        BufferedImage edgedImage = new BufferedImage(image.getWidth() + edgeSize, image.getHeight() + edgeSize, BufferedImage.TYPE_INT_ARGB);
        int[] edgeData = ((DataBufferInt) edgedImage.getRaster().getDataBuffer()).getData();

        // set every pixels in edged image to -1 to use for checking unassigned pixels when filling edges
        for (int i = 0; i < edgeData.length; i++) {
            edgeData[i] = -1;
        }

        // draw original image onto center of edged image
        edgedImage.getGraphics().drawImage(image, edgeSize/2, edgeSize/2, null);
        edgedImage.getGraphics().dispose();


        //TODO - get this working after program works to improve speed
//        byte[] edgedData = ((DataBufferByte) edgedImage.getRaster().getDataBuffer()).getData();
//        byte[] originalData = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
//
        // copy image into larger, edged image. currently not working,
//        for (int i = windowSize / 2 * 3, originalI = 0; i < image.getHeight() - windowSize / 2; i++, originalI++) {
//            for (int j = windowSize / 2 * 3, originalJ = 0; j < image.getWidth() - windowSize / 2; j++, originalJ++) {
//                edgedData[i + j * edgedImage.getWidth()] = originalData[originalI + originalJ * image.getWidth()];
//            }
//        }

        image = edgedImage;
        fillEdges();
    }

    public void smoothImage(int subsize){
        windowSize = subsize;
        createEdges();
        BufferedImage smoothedImage= new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        for (int i = windowSize/2; i < image.getHeight() - windowSize/2; i++) {
            for (int j = windowSize/2; j < image.getWidth() - windowSize/2; j++) {
                BufferedImage subImage;
                subImage = image.getSubimage(j - windowSize / 2, i - windowSize / 2, windowSize, windowSize);
                smoothedImage.setRGB(j,i, getMedianPixel(subImage).getRGB());
            }
        }
        image = smoothedImage.getSubimage(windowSize/2,windowSize/2,
                image.getWidth() - windowSize + 1, image.getHeight() - windowSize + 1);
    }

    public static void main(String[] args) {
        OrderStatistic.testAndPrint(new QuickSelectStrategy());
        ImageSmoother smoother = new ImageSmoother(
                "samples/11.png",
                "samples/11-3.png", false);
        if (!smoother.imageExists()) return;
        smoother.smoothImage(3);
        smoother.saveImage();
    }
}
