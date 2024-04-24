package org.prank.additional;

import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ProgressBarDialog extends JDialog {

    private JProgressBar progressBar;

    public ProgressBarDialog(String title, int progressBarMaxValue) {
        this(null, title, progressBarMaxValue);
    }

    public ProgressBarDialog(JFrame parent, String title, int progressBarMaxValue) {
        super(parent, title);
        setSize(new Dimension(300, 100));
        setLocationRelativeTo(null);

        progressBar = new JProgressBar(0, progressBarMaxValue);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        JPanel panel = new JPanel();
        panel.add(progressBar);
        getContentPane().add(panel);
    }

    public int getProgress() {
        return progressBar.getValue();
    }

    public void setProgress(int progress) {
        progressBar.setValue(progress);
        progressBar.revalidate();
    }

    public void close() {
        setVisible(false);
        dispose();
    }

    public void start() {
        setVisible(true);
    }

}
