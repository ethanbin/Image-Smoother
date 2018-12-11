package Main;

import Main.OrderStatistic.OrderStatisticStrategy;
import Main.OrderStatistic.OrderStatisticTester;
import Main.OrderStatistic.Strategies.QuickSelectStrategy;
import Main.OrderStatistic.Strategies.QuickSortStrategy;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import org.apache.commons.cli.*;

public class ImageSmoother {
    private boolean imageExists;
    int     windowSize = 0;
    private String imageLocation;
    private String saveLocation;
    private BufferedImage image;
    private OrderStatisticStrategy orderStat;

    // use QuickSelectStrategy as default strategy
    public ImageSmoother(String imageLocation, String saveLocation) {
        this(imageLocation, saveLocation, new QuickSelectStrategy());
    }

    public ImageSmoother(String imageLocation, String saveLocation, OrderStatisticStrategy orderStatisticStrategy) {
        this.imageLocation = imageLocation;
        this.saveLocation = saveLocation;
        imageExists = openImage();
        this.orderStat = orderStatisticStrategy;
    }

    public boolean imageExists() {
        return imageExists;
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public String getSaveLocation() {
        return saveLocation;
    }

    /*
    public boolean isOverwritable() {
        return overwritable;
    }

    public void setOverwritable(boolean overwritable) {
        this.overwritable = overwritable;
    }
    */

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
        return saveImage(saveLocation);
    }

    // If given save location differs from current save location, keep track of
    // this location as the default to use when using saveImage()
    public boolean saveImage(String outPath){
        if (saveLocation == null || saveLocation.isEmpty()){
            System.err.println("ERROR: No image destination path given.");
            return false;
        }

        try {
            File output = new File(outPath);
//            if (output.exists() && !overwrite){
//                System.err.println("ERROR: Attempting to overwritable existing file when the overwriting option is off.");
//                return false;
//            }
//            else {
//                ImageIO.write(image, "png", output);
//                return true;
//            }
            ImageIO.write(image, "png", output);
            return true;
        }
        catch (Exception e){
            System.err.println("ERROR: Could not save file.");
            return false;
        }
    }

//    // make an image greyscale
//    public BufferedImage greyscaleImage(BufferedImage image){
//        for (int j = 0; j < image.getHeight(); j++){
//            for (int i = 0; i < image.getWidth(); i++){
//                Color color = new Color(image.getRGB(i,j));
//                int average = (color.getRed() + color.getGreen() + color.getBlue())/3;
//                Color grayed = new Color(average, average, average);
//                image.setRGB(i, j, grayed.getRGB());
//            }
//        }
//        return image;
//    }

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
        // if color has sentinel value or we passed the edges and are in the actual image, return color
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
                pixels[i*image.getWidth() + j] = color;
            }
        }
        return pixels;
    }

    private Color getMedianPixel(BufferedImage image){
        // put every Color in subimage into an array
        Color[] pixels = imageToArray(image);
        return orderStat.findColor(pixels, pixels.length/2);
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
        // create new buffered image to hold resulting smoothed image
        BufferedImage smoothedImage= new BufferedImage(image.getWidth(), image.getHeight(), image.getType());

        // for each pixel of the image, excluding the edges, add to smoothedImage the
        // median of the window where that pixel is the center
        for (int i = windowSize/2; i < image.getHeight() - windowSize/2; i++) {
            for (int j = windowSize/2; j < image.getWidth() - windowSize/2; j++) {
                BufferedImage subImage;
                // get window
                subImage = image.getSubimage(j - windowSize / 2, i - windowSize / 2, windowSize, windowSize);
                // get median pixel of window and use it to fill in matching position in smoothed image
                smoothedImage.setRGB(j,i, getMedianPixel(subImage).getRGB());
            }
        }
        // set image to smoothed image
        image = smoothedImage.getSubimage(windowSize/2,windowSize/2,
                image.getWidth() - windowSize + 1, image.getHeight() - windowSize + 1);
    }

    private static void printHelp(Options options){
        printHelp(null, options);
    }

    private static void printHelp(String message, Options options){
        if (message != null && !message.isEmpty())
            System.err.println(message);
        HelpFormatter hf = new HelpFormatter();
        hf.printHelp("Image Smoother using the Median Filter", options);
        return;
    }

    public static void main(String[] args) {
        Options options = new Options();

        Option optionInput = new Option("i", "input", true, "required input file path");
//        optionInput.setRequired(true);
        options.addOption(optionInput);

        Option optionOutput = new Option("o", "output", true, "required output file path");
//        optionOutput.setRequired(true);
        options.addOption(optionOutput);

        Option optionWindowSize = new Option("w", "windowSize", true,
                "required window size, must be an odd number, must be an integer");
//        optionWindowSize.setRequired(true);
        options.addOption(optionWindowSize);

        Option testOrderStatistic = new Option("t", "test", false,
                "Test and compare different order statistic implementations and print results to results.csv");
        options.addOption(testOrderStatistic);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            printHelp("Missing or Invalid Argument(s).", options);
            return;
        }

        // if test option was used
        if (cmd.hasOption(testOrderStatistic.getOpt())){
            System.out.println("Testing...");
            OrderStatisticTester.testAndPrintAll();
            System.out.println("Done.");
            return;
        }

        String inputPath = cmd.getOptionValue(optionInput.getOpt());
        if (inputPath == null || inputPath.isEmpty()){
            printHelp("No input path given", options);
            return;
        }

        String outputPath = cmd.getOptionValue((optionOutput.getOpt()));
        if (outputPath == null || outputPath.isEmpty()){
            printHelp("No output path given", options);
            return;
        }

        // get window size. if it's not a number, integer, odd, or above 1, print error message, show help menu, and exit
        int windowSize = 0;
        try {
            String windowOptionValue = cmd.getOptionValue(optionWindowSize.getOpt());
            if (windowOptionValue.indexOf('.') >= 0){
                printHelp("Window Size given is not an integer.", options);
                return;
            }
            windowSize = Integer.parseInt(windowOptionValue);
            if (windowSize % 2 == 0){
                printHelp("Window Size must be odd.", options);
                return;
            }
            else if (windowSize <= 0){
                printHelp("Window Size must be greater than 1", options);
                return;
            }
        }
        catch (Exception e)
        {
            printHelp("Window Size must be an integer number.", options);
            return;
        }

        System.out.println("Smoothing image...");
        ImageSmoother smoother = new ImageSmoother(inputPath, outputPath);
        smoother.smoothImage(windowSize);
        smoother.saveImage();
        System.out.println("Done.");

//        OrderStatisticTester.testAndPrint(new QuickSelectStrategy());
//        OrderStatisticTester.testAndPrint(new QuickSortStrategy());
//        ImageSmoother smoother = new ImageSmoother(
//                "samples/11.png",
//                "samples/11-5.png");
//        if (!smoother.imageExists()) return;
//        smoother.smoothImage(5);
//        smoother.saveImage();
    }
}
