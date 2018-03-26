package dk.kb.tvsubtitle.frameprocessing;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TestRunOpenCV {

    private static FrameProcessorOpenCV frameProcessor;
//    private static final Path imageLocation = Paths.get("/home/armo/Projects/subtitles-test/testdata/output1145.png");
    private static final Path imageLocation = Paths.get("/home/armo/Projects/subtitles-test/working/frameExtraction/");
    private static final Path outputLocation = Paths.get("/home/armo/Projects/subtitles-test/out/");

    @Test
    @Disabled
    void main() {
        TestRunOpenCV testRunOpenCV = new TestRunOpenCV();
        frameProcessor = new FrameProcessorOpenCV();

        testRunOpenCV.testRun();

    }

    private void testRun() {
        System.out.println("Start");
        List<Path> paths = getFrames(imageLocation);
        paths.sort(Path::compareTo);
        System.out.println(paths.size() + " number of frames\n" + "Populating frame list");
        List<BufferedImage> frames = new LinkedList<>();

        for (Path p : paths) {
            BufferedImage img = null;
            try {
                img = ImageIO.read(p.toFile());
//                img = FrameProcessor.crop(img);
//                img = FrameProcessor.resize(img, 5);
            } catch (IOException e) {
                e.printStackTrace();
            }
            frames.add(img);
        }

        Instant t0, t1;

        System.out.println("Processing images: " + frames.size());
        t0 = Instant.now();
        AtomicInteger printIndex = new AtomicInteger();
        printIndex.set(1);
        frames.replaceAll((BufferedImage frame) -> {
            BufferedImage img = frameProcessor.processFrame(frame);
            try{
                ImageIO.write(img, "png", new File(outputLocation.toFile(), printIndex+""));
            } catch (IOException e) {
                e.printStackTrace();
            }
            printIndex.getAndIncrement();
            return img;
        });

        t1 = Instant.now();
        System.out.println("Processing done: " + Duration.between(t0, t1).toString() + " \n" +
                "Writing to disk");
        try {
            Files.createDirectories(Paths.get(imageLocation + "/output"));
        } catch (IOException e) {
            e.printStackTrace();
        }
//        try {
//            Files.delete(outputLocation);
//            Files.createDirectories(outputLocation);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        frames.forEach(x -> {
//            try {
////                File output = new File(outputLocation + "/out/" + String.valueOf(frames.indexOf(x)) + ".png");
////                ImageIO.write(x, "png", output);
////                File output = new File(outputLocation.toFile(), String.valueOf(frames.indexOf(x)) + ".png");
////                System.out.println(output);
//                ImageIO.write(x, "png", output);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
        System.out.println("Done");


    }

    private List<Path> getFrames(Path path) {
        try {
            return Files.list(path).filter(Files::isRegularFile).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}