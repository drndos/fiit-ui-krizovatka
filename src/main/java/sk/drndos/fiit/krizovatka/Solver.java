/**
 * Solver.java Filip Bednárik <drndos@drndos.sk>
 *
 * @ 1.4.2013 21:07
 */
package sk.drndos.fiit.krizovatka;

import static org.bytedeco.opencv.global.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.opencv.global.opencv_core.cvCreateImage;
import static org.bytedeco.opencv.global.opencv_core.cvGet2D;
import static org.bytedeco.opencv.global.opencv_core.cvPoint;
import static org.bytedeco.opencv.global.opencv_core.cvReleaseImage;
import static org.bytedeco.opencv.global.opencv_core.cvScalar;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_THRESH_BINARY;
import static org.bytedeco.opencv.global.opencv_imgproc.cvCvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.cvDilate;
import static org.bytedeco.opencv.global.opencv_imgproc.cvErode;
import static org.bytedeco.opencv.global.opencv_imgproc.cvRectangle;
import static org.bytedeco.opencv.global.opencv_imgproc.cvThreshold;
import static org.bytedeco.opencv.helper.opencv_imgcodecs.cvLoadImage;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.bytedeco.javacv.Blobs;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameConverter.ToIplImage;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.opencv_core.CvMat;
import org.bytedeco.opencv.opencv_core.CvPoint;
import org.bytedeco.opencv.opencv_core.CvScalar;
import org.bytedeco.opencv.opencv_core.IplImage;

/**
 * Solves the game of parking lot
 *
 * @author DeathX
 */
public class Solver extends SwingWorker<Void, Vertex> {

  /**
   * @param args the command line arguments
   */
  private HashMap<String, Car> allCars = new HashMap<>();
  private HashMap<String, Vertex> verteces = new HashMap<>();
  private Deque<Vertex> queue = new LinkedList<>();
  private String whenToFinish = "";
  private boolean fifo = true;
  private Display d;
  private final boolean debug = true;

  /**
   * Loads file of textual data into solver Creating initial Vertex and remembering the configuration
   */
  Vertex loadFile(String filename) {
    allCars = new HashMap<>();
    Scanner scanner = null;
    try {
      scanner = new Scanner(new File(filename));
    } catch (FileNotFoundException ex) {
      Logger.getLogger(Solver.class.getName()).log(Level.SEVERE, null, ex);
    }

    scanner.useDelimiter("[\\( \\)]");
    String aut;
    scanner.next();
    do {
      String col = scanner.next();
      int size = scanner.nextInt();
      int y = scanner.nextInt();
      int x = scanner.nextInt();
      String vertical = scanner.next();
      Car a = new Car(col, vertical.contains("v"), x, y, size);
      allCars.put(a.toString(), a);
      scanner.next();
    } while (scanner.hasNext());
    return new Vertex(allCars);
    //d.printCars(s);
  }

  /**
   * Loads data from Array of labels into solver Creating initial Vertex and remembering the configuration
   */
  Vertex loadFromArray(JLabel[][] labels) {
    allCars = new HashMap<>();
    findAndSaveCar(labels);
    return new Vertex(allCars);
    //d.printCars(s);
  }

  private void findAndSaveCar(JLabel[][] labels) {
    int x = -1;
    int y = -1;
    int size = 1;
    boolean vertical = true;
    String color = null;

    for (int i = 0; i < 6; i++) {
      for (int ds = 0; ds < 6; ds++) {
        if (x == -1) {
          if (!labels[i][ds].getToolTipText().equals("biela")) {
            color = labels[i][ds].getToolTipText();
            x = ds;
            y = i;
            labels[i][ds].setToolTipText("biela");
          }
        } else {
          if (x == ds) {
            if (labels[i][ds].getToolTipText().equals(color)) {
              vertical = false;
              size++;
              labels[i][ds].setToolTipText("biela");
            }
          } else if (y == i) {
            if (labels[i][ds].getToolTipText().equals(color)) {
              vertical = true;
              size++;
              labels[i][ds].setToolTipText("biela");
            }
          }
        }
      }
    }
    if (x != -1) {
      Car a = new Car(color, !vertical, x + 1, y + 1, size);
      allCars.put(a.toString(), a);
      System.out.println(a);
      findAndSaveCar(labels);
    }
  }

  /**
   * Solves the game with fifo or lifo queue
   */
  boolean solve(boolean fifo) {
    this.fifo = fifo;
    boolean bc;
    for (Car a : allCars.values()) {
      if (a.getColor().name().equals("cervene")) {
        Car b = new Car(a);
        b.setX(7 - b.getSize());
        whenToFinish = b.toString();
      }
    }
    Vertex s = new Vertex(allCars);
    verteces = new HashMap<>();
    verteces.put(makeHash(allCars), s);
    queue = new LinkedList<>();
    queue.add(s);
    long startTime = System.currentTimeMillis();
    bc = runAlgorithm();
    System.out.println("Cas: " + (System.currentTimeMillis() - startTime) + " ms");
    return bc;
  }

  private Vertex finish = null;

  /**
   * Sets callback object and prepares variables
   */
  void start(Display d) {
    this.d = d;
    if (!queue.isEmpty()) {

      if (fifo) {
        finish = queue.getLast();
      } else {
        finish = queue.getFirst();
      }

      //recursiveShow(finish);
    }
  }

  private boolean runAlgorithm() {
    if (queue.isEmpty()) {
      System.out.println("Failure!");
      return false;
    }
    Vertex predchodca = queue.poll();

    HashMap<String, Car> stav1 = predchodca.getValue();
    if (stav1.containsKey(whenToFinish)) {
      System.out.println("Success!");
      return true;
    }
    for (String auticko : stav1.keySet()) {
      //System.out.println("Checking car" + auticko);
      Car a = stav1.get(auticko);
      if (a.isVertical()) {
        //System.out.println("Checking possible move UP " + auticko);
        //Going UP
        for (int i = (a.getY() - 1); i >= 1; i--) {
          //System.out.println("    to x:" + a.getX() + " y:" + i);
          HashMap<String, Car> stav = (HashMap<String, Car>) stav1.clone();
          Car b = new Car(a);
          stav.remove(a.toString());
          if (!isBlocking(stav, a.getX(), i)) {
            b.setY(i);
            stav.put(b.toString(), b);
            //System.out.println("Car " + b.getColor() + " can move to x:" + b.getX() + " y:" + b.getY());
            String hash = makeHash(stav);
            if (!verteces.containsKey(hash)) {
              Vertex s = new Vertex(stav);
              s.setLastVertex(predchodca);
              s.setMove("HORE(" + b.getColor() + ", " + Math.abs(b.getY() - a.getY()) + ")");
              verteces.put(hash, s);
              if (fifo) {
                queue.add(s);
              } else {
                queue.addFirst(s);
              }
            }
          } else {
            break;
          }
        }
        // System.out.println("Checking possible move DOWN " + auticko);
        //Going DOWN
        for (int i = (a.getY() + a.getSize()); i <= 6; i++) {
          // System.out.println("    to x:" + a.getX() + " y:" + i);
          HashMap<String, Car> stav = (HashMap<String, Car>) stav1.clone();
          Car b = new Car(a);
          stav.remove(a.toString());
          if (!isBlocking(stav, a.getX(), i)) {
            b.setY(i - a.getSize() + 1);
            stav.put(b.toString(), b);
            //System.out.println("Car " + b.getColor() + " can move to x:" + b.getX() + " y:" + b.getY());
            String hash = makeHash(stav);
            if (!verteces.containsKey(hash)) {
              Vertex s = new Vertex(stav);
              s.setLastVertex(predchodca);
              s.setMove("DOLE(" + b.getColor() + ", " + Math.abs(b.getY() - a.getY()) + ")");
              verteces.put(hash, s);
              if (fifo) {
                queue.add(s);
              } else {
                queue.addFirst(s);
              }

            }
          } else {
            break;
          }
        }
      } else {
        //System.out.println("Checking possible move LEFT " + auticko);
        //Going LEFT
        for (int i = (a.getX() - 1); i >= 1; i--) {
          //System.out.println("    to x:" + i + " y:" + a.getY());
          HashMap<String, Car> stav = (HashMap<String, Car>) stav1.clone();
          Car b = new Car(a);
          stav.remove(a.toString());
          if (!isBlocking(stav, i, a.getY())) {
            b.setX(i);
            stav.put(b.toString(), b);
            //System.out.println("Car " + b.getColor() + " can move to x:" + b.getX() + " y:" + b.getY());
            String hash = makeHash(stav);
            if (!verteces.containsKey(hash)) {
              Vertex s = new Vertex(stav);
              s.setLastVertex(predchodca);
              s.setMove("VLAVO(" + b.getColor() + ", " + Math.abs(b.getX() - a.getX()) + ")");
              verteces.put(hash, s);
              if (fifo) {
                queue.add(s);
              } else {
                queue.addFirst(s);
              }
            }
          } else {
            break;
          }
        }
        //System.out.println("Checking possible move RIGHT " + auticko);
        //Going RIGHT
        for (int i = (a.getX() + a.getSize()); i <= 6; i++) {
          // System.out.println("    to x:" + i + " y:" + a.getY());
          HashMap<String, Car> stav = (HashMap<String, Car>) stav1.clone();
          Car b = new Car(a);
          stav.remove(a.toString());
          if (!isBlocking(stav, i, a.getY())) {
            b.setX(i - a.getSize() + 1);
            stav.put(b.toString(), b);
            //System.out.println("Car " + b.getColor() + " can move to x:" + b.getX() + " y:" + b.getY());
            String hash = makeHash(stav);
            if (!verteces.containsKey(hash)) {
              Vertex s = new Vertex(stav);
              s.setLastVertex(predchodca);
              s.setMove("VPRAVO(" + b.getColor() + ", " + Math.abs(b.getX() - a.getX()) + ")");
              verteces.put(hash, s);
              if (fifo) {
                queue.add(s);
              } else {
                queue.addFirst(s);
              }
              if (stav.containsKey(whenToFinish)) {
                System.out.println("Success!");
                return true;
              }
            }
          } else {
            break;
          }
        }
      }
    }

    return runAlgorithm();
  }

  @Override
  protected Void doInBackground() {
    recursiveShow(finish);
    return null;
  }

  @Override
  protected void process(List<Vertex> pairs) {
    d.printCars(pairs.get(pairs.size() - 1));
  }
  //private boolean cancel =false;

  private void recursiveShow(Vertex finish) {
    if (!isCancelled()) {
      if (finish.getLastVertex() != null) {
        recursiveShow(finish.getLastVertex());
      }
      if (finish.getMove() != null) {
        System.out.println(finish.getMove());
      }
      if (!isCancelled()) {
        publish(finish);
        System.out.println(makeHash(finish.getValue()));

        try {
          Thread.sleep(2000);
        } catch (InterruptedException ignored) {
        }
      }
    }
  }

  private boolean isBlocking(HashMap<String, Car> auta, int x, int y) {
    for (Car c : auta.values()) {
      if (c.isBlocking(x, y)) {
        return true;
      }
    }
    return false;
  }

  private String makeHash(HashMap<String, Car> auticka) {
    String hash = "";
    Set<String> set = auticka.keySet();
    TreeSet<String> ts = new TreeSet(set);
    for (String a : ts) {
      hash += a;
    }
    //System.out.println("Made hash: " + hash);
    return hash;
  }

  private Queue<Region> processImage(IplImage rawImage) {
    int MinArea;
    int ErodeCount;
    int DilateCount;

    MinArea = 1500;
    ErodeCount = 1;
    DilateCount = 1;

    IplImage grayImage = cvCreateImage(rawImage.cvSize(), IPL_DEPTH_8U, 1);
    cvCvtColor(rawImage, grayImage, CV_BGR2GRAY);

    IplImage bWImage = cvCreateImage(grayImage.cvSize(), IPL_DEPTH_8U, 1);
    cvThreshold(grayImage, bWImage, 30, 255, CV_THRESH_BINARY);
    if (debug) {
      showImage(grayImage, "GrayImage");
    }
    IplImage WorkingImage = cvCreateImage(bWImage.cvSize(), IPL_DEPTH_8U, 1);
    cvErode(bWImage, WorkingImage, null, ErodeCount);
    if (debug) {
      showImage(bWImage, "BWImage");
    }
    cvDilate(WorkingImage, WorkingImage, null, DilateCount);
    if (debug) {
      showImage(WorkingImage, "WorkingImage");
    }
    //cvSaveImage("Images/Working.jpg", WorkingImage);
    //PrintGrayImage(WorkingImage, "WorkingImage");
    //BinaryHistogram(WorkingImage);

    Blobs Regions = new Blobs();
    Regions.BlobAnalysis(
        WorkingImage, // image
        -1, -1, // ROI start col, row
        -1, -1, // ROI cols, rows
        1, // border (0 = black; 1 = white)
        MinArea);
    if (debug) {// minarea
      Regions.PrintRegionData();
    }
    Queue<Region> regions = new LinkedList<>();
    for (int i = 1; i <= Blobs.MaxLabel; i++) {
      double[] Region = Blobs.RegionData[i];
      int Parent = (int) Region[Blobs.BLOBPARENT];
      int Color = (int) Region[Blobs.BLOBCOLOR];
      int MinX = (int) Region[Blobs.BLOBMINX];
      int MaxX = (int) Region[Blobs.BLOBMAXX];
      int MinY = (int) Region[Blobs.BLOBMINY];
      int MaxY = (int) Region[Blobs.BLOBMAXY];
      highlight(rawImage, MinX, MinY, MaxX, MaxY, 1);
      regions.add(new Region(MinX, MinY, MaxX, MaxY));
    }
    if (debug) {
      showImage(rawImage, "RawImage");
    }
    cvReleaseImage(grayImage);
    grayImage = null;
    cvReleaseImage(bWImage);
    bWImage = null;
    cvReleaseImage(WorkingImage);
    WorkingImage = null;
    return regions;
  }

  /**
   * Analyzes the Image to objects and then creates the Vertex from Image source
   */
  public Vertex analyzeImage(IplImage RawImage) {
    Queue<Region> regions = processImage(RawImage);

    Region mainRegion = regions.poll();
    int sizeX = mainRegion.getX2() - mainRegion.getX1();
    int sizeY = mainRegion.getY2() - mainRegion.getY1();

    int sizeOfBlockX = sizeX / 6;
    int sizeOfBlockY = sizeY / 6;

    int i = 0;

    for (Region one : regions) {
      //System.out.println(one);
      int realX1 = (one.getX1() / sizeOfBlockX) + 1;
      int realY1 = (one.getY1() / sizeOfBlockY) + 1;
      int realX2 = (one.getX2() / sizeOfBlockX) + 1;
      int realY2 = (one.getY2() / sizeOfBlockY);

      boolean vertical = (realX1 == realX2);
      CvScalar ds = cvGet2D(RawImage, (one.getY1() + one.getY2()) / 2, (one.getX1() + one.getX2()) / 2);
      String red = Integer.toHexString((int) ds.red());
      red = (red.length() == 1) ? ("0" + red) : red;
      String green = Integer.toHexString((int) ds.green());
      green = (green.length() == 1) ? ("0" + green) : green;
      String blue = Integer.toHexString((int) ds.blue());
      blue = (blue.length() == 1) ? ("0" + blue) : blue;
      Color col = Color.decode("#" + red + green + blue);
      //System.out.println("REAL x1:"+realX1+" y1:"+realY1+" x2:"+realX2+" y2:"+realY2);
      if ((realX1 <= 6) && (realY1 <= 6) && (realY2 <= 6) && (realX2 <= 6)) {

        Car a = new Car(col, vertical, realX1, realY1, ((vertical) ? (realY2 - realY1 + 1) : (realX2 - realX1 + 1)));

        allCars.put(a.toString(), a);
      }
    }
    double lowestDistance = Double.MAX_VALUE;
    Car lowestCar = null;
    for (Car a : allCars.values()) {
      double distance = colourDistance(a.getColor().getNum(), Color.RED);
      if (distance < lowestDistance) {
        lowestDistance = distance;
        lowestCar = a;
      }
    }
    JOptionPane.showMessageDialog(null, "Assuming car " + lowestCar.getColor().getNum().toString()
            + " is red because it has lowest color distance to red among other colors (" + lowestDistance + ").",
        "Information", JOptionPane.INFORMATION_MESSAGE);
    Car b = new Car(lowestCar);
    b.setX(7 - b.getSize());
    whenToFinish = b.toString();

    cvReleaseImage(RawImage);
    RawImage = null;
    Vertex s = new Vertex(allCars);
    return s;
  }

  private double colourDistance(Color c1, Color c2) {
    double rmean = (c1.getRed() + c2.getRed()) / 2;
    int r = c1.getRed() - c2.getRed();
    int g = c1.getGreen() - c2.getGreen();
    int b = c1.getBlue() - c2.getBlue();
    double weightR = 2 + rmean / 256;
    double weightG = 4.0;
    double weightB = 2 + (255 - rmean) / 256;
    return Math.sqrt(weightR * r * r + weightG * g * g + weightB * b * b);
  }

  /**
   * Loads image data into solver analyzing it and Creating initial Vertex and remembering the configuration
   */
  public Vertex loadFromImage(String filename) {
    IplImage RawImage;
    RawImage = cvLoadImage(filename);
    return analyzeImage(RawImage);
  }

  /**
   * Shows Image in frame
   */
  public static void showImage(IplImage image, String caption) {
    CvMat mat = image.asCvMat();
    int width = mat.cols();
    if (width < 1) {
      width = 1;
    }
    int height = mat.rows();
    if (height < 1) {
      height = 1;
    }
    double aspect = 1.0 * width / height;
    if (height < 128) {
      height = 128;
      width = (int) (height * aspect);
    }
    if (width < 128) {
      width = 128;
    }
    height = (int) (width / aspect);
    showImage(image, caption, width, height);
  }

  /**
   * Captures current video input, analyzing it and Creating initial Vertex and remembering the configuration
   */
  public Vertex capture() throws Exception {
    OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
    grabber.start();
    ToIplImage converter = new ToIplImage();
    IplImage frame = converter.convert(grabber.grab());
    IplImage gg = frame.clone();
    grabber.stop();
    return analyzeImage(gg);
    //canvasFrame.dispose();
  }

  /**
   * Shows Image in frame
   */
  public static void showImage(IplImage image, String caption, int size) {
    if (size < 128) {
      size = 128;
    }
    CvMat mat = image.asCvMat();
    int width = mat.cols();
    if (width < 1) {
      width = 1;
    }
    int height = mat.rows();
    if (height < 1) {
      height = 1;
    }
    double aspect = 1.0 * width / height;
    if (height != size) {
      height = size;
      width = (int) (height * aspect);
    }
    if (width != size) {
      width = size;
    }
    height = (int) (width / aspect);
    showImage(image, caption, width, height);
  }

  /**
   * Shows Image in frame
   */
  public static void showImage(IplImage image, String caption, int width, int height) {
    CanvasFrame canvas = new CanvasFrame(caption, 1);   // gamma=1
    canvas.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
    canvas.setCanvasSize(width, height);
    ToIplImage converter = new ToIplImage();
    canvas.showImage(converter.convert(image));
  }

  /**
   * Highlights area of image where object should be
   */
  public static void highlight(IplImage image, int xMin, int yMin, int xMax, int yMax, int Thick) {

    CvPoint pt1 = cvPoint(xMin, yMin);
    CvPoint pt2 = cvPoint(xMax, yMax);
    CvScalar color = cvScalar(255, 0, 0, 0);       // blue [green] [red]
    cvRectangle(image, pt1, pt2, color, Thick, 4, 0);
  }
}
