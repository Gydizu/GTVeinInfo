package org.prank.additional;

import java.io.File;
import java.io.FileWriter;

import java.util.Arrays;
import java.util.Scanner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class JMWaypointDisabler {

    private CountDownLatch latch;
    private ExecutorService executor;
    private ProgressBarDialog dialog;

    public static void disableJM(File waypointsDir) {
        File[] waypointJSONs = waypointsDir.listFiles();
        new JMWaypointDisabler(waypointJSONs.length).disableJMWaypoints(waypointJSONs);
    }

    private JMWaypointDisabler(int count) {
        latch = new CountDownLatch(count);
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        dialog = new ProgressBarDialog((JFrame) JFrame.getFrames()[0], "Disabling waypoints", count);
    }

    private void disableJMWaypoints(File... waypointJSONs) {
        dialog.start();
        Arrays.stream(waypointJSONs)
                .forEach(waypointJSON -> executor.submit(() -> {
                    disableJMWaypoint(waypointJSON);
                    latch.countDown();
                    dialog.setProgress(dialog.getProgress() + 1);
                }));
        try {
            latch.await();
        } catch (InterruptedException e) {
            showErrorMessage(e, "Async execution error");
            return;
        } finally {
            executor.shutdownNow();
            dialog.close();
        }
    }

    private void disableJMWaypoint(File waypointJSON) {
        String jsonContent;
        try (Scanner scanner = new Scanner(waypointJSON)) {
            jsonContent = scanner.nextLine();
        } catch (Exception e) {
            showErrorMessage(e, "Reading json file error");
            return;
        }

        jsonContent = jsonContent.replace("\"enable\": true", "\"enable\": false");

        try (FileWriter writer = new FileWriter(waypointJSON, false)) {
            writer.write(jsonContent);
        } catch (Exception e) {
            showErrorMessage(e, "Writing in json file error");
            return;
        }
    }

    private void showErrorMessage(Exception e, String title) {
        JOptionPane.showMessageDialog(
                null,
                e,
                title,
                JOptionPane.ERROR_MESSAGE);
    }

}
