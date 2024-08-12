import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class Traffic_Signal extends JFrame implements ActionListener {

    JTextField[] vehicleCountFields = new JTextField[3];
    JTextField[] signalDisplays = new JTextField[3];
    JButton[] updateButtons = new JButton[3];
    JTextArea reportArea;

    int[] vehicleCounts = new int[3];
    String[] signals = new String[3];
    Timer timer;

    public Traffic_Signal(String title) {
        super(title);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(1000, 500));
        add(mainPanel, BorderLayout.CENTER);

        JPanel trafficLightPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawTrafficSignals(g, 20, 20);
            }
        };
        trafficLightPanel.setPreferredSize(new Dimension(400, 400));
        mainPanel.add(trafficLightPanel, BorderLayout.WEST);

        JPanel intersectionPanel = new JPanel();
        intersectionPanel.setLayout(new BoxLayout(intersectionPanel, BoxLayout.Y_AXIS));
        intersectionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Optional padding
        mainPanel.add(intersectionPanel, BorderLayout.CENTER);

        for (int i = 0; i < 3; i++) {
            signals[i] = "";
            intersectionPanel.add(createIntersectionPanel("Intersection " + (i + 1), i));
        }

        reportArea = new JTextArea(10, 30);
        reportArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(reportArea);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);

        // Simulate real-time data updates
        timer = new Timer(5000, e -> updateRealTimeData());
        timer.start();
    }

    private JPanel createIntersectionPanel(String title, int index) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel label = new JLabel(title);
        panel.add(label);

        JLabel countLabel = new JLabel("Vehicle Count:");
        panel.add(countLabel);

        vehicleCountFields[index] = new JTextField(10);
        vehicleCountFields[index].setToolTipText("Enter vehicle count for " + title);
        panel.add(vehicleCountFields[index]);

        updateButtons[index] = new JButton("Update Signal");
        updateButtons[index].addActionListener(this);
        updateButtons[index].setActionCommand("Update" + index);
        panel.add(updateButtons[index]);

        signalDisplays[index] = new JTextField(20);
        signalDisplays[index].setEditable(false);
        panel.add(signalDisplays[index]);

        return panel;
    }

    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < 3; i++) {
            if (e.getActionCommand().equals("Update" + i)) {
                updateSignal(i);
            }
        }
    }

    private void updateSignal(int index) {
        try {
            vehicleCounts[index] = Integer.parseInt(vehicleCountFields[index].getText());
        } catch (NumberFormatException e) {
            signalDisplays[index].setText("Invalid vehicle count.");
            return;
        }

        // Determine the signal based on vehicle count
        for (int i = 0; i < 3; i++) {
            if (vehicleCounts[i] == 0) {
                signals[i] = "Stop!";
            } else if (vehicleCounts[i] > 50) {
                signals[i] = "Stop!";
            } else if (vehicleCounts[i] > 25) {
                signals[i] = "Get Ready to go!";
            } else {
                signals[i] = "Go!!";
            }
        }

        // Find the intersections with the maximum and second maximum number of vehicles
        int maxIndex = 0;
        int secondMaxIndex = 0;
        for (int i = 1; i < 3; i++) {
            if (vehicleCounts[i] > vehicleCounts[maxIndex]) {
                secondMaxIndex = maxIndex;
                maxIndex = i;
            } else if (vehicleCounts[i] > vehicleCounts[secondMaxIndex] && i != maxIndex) {
                secondMaxIndex = i;
            }
        }

        // Set the intersection with the maximum vehicle count to "Go!!"
        // and the second maximum to "Get Ready to go!"
        for (int i = 0; i < 3; i++) {
            if (i == maxIndex) {
                signals[i] = "Go!!";
            } else if (i == secondMaxIndex) {
                signals[i] = "Get Ready to go!";
            } else if (vehicleCounts[i] == 0) {
                signals[i] = "Stop!";
            } else {
                signals[i] = "Stop!";
            }
        }

        signalDisplays[index].setText(signals[index]);
        repaint();
        generateReport();
    }

    private void updateRealTimeData() {
        Random rand = new Random();
        for (int i = 0; i < 3; i++) {
            vehicleCounts[i] = rand.nextInt(100); // Simulate random vehicle counts
            vehicleCountFields[i].setText(String.valueOf(vehicleCounts[i]));
            updateSignal(i);
        }
    }

    private void generateReport() {
        StringBuilder report = new StringBuilder("Traffic Signal Report:\n");
        for (int i = 0; i < 3; i++) {
            report.append(String.format("Intersection %d: %s (%d vehicles)\n",
                    i + 1, signals[i], vehicleCounts[i]));
        }
        reportArea.setText(report.toString());
    }

    private void drawTrafficSignals(Graphics g, int xOffset, int yOffset) {
        int lightWidth = 110;
        int lightHeight = 270;
        int lightSpacing = 30; // Space between traffic lights

        for (int i = 0; i < 3; i++) {
            int x = xOffset + i * (lightWidth + lightSpacing);
            int y = yOffset;

            // Draw the traffic light structure
            g.setColor(Color.DARK_GRAY);
            g.fillRect(x, y, lightWidth, lightHeight);

            g.setColor(Color.BLACK);
            g.drawRect(x, y, lightWidth, lightHeight);

            // Draw the signal lights
            int signalRadius = 60;
            g.setColor(Color.BLACK);
            g.drawOval(x + 20, y + 45, signalRadius, signalRadius);
            g.drawOval(x + 20, y + 125, signalRadius, signalRadius);
            g.drawOval(x + 20, y + 205, signalRadius, signalRadius);

            switch (signals[i]) {
                case "Stop!":
                    g.setColor(Color.RED);
                    g.fillOval(x + 20, y + 45, signalRadius, signalRadius);
                    g.setColor(Color.WHITE);
                    g.fillOval(x + 20, y + 125, signalRadius, signalRadius);
                    g.fillOval(x + 20, y + 205, signalRadius, signalRadius);
                    break;
                case "Get Ready to go!":
                    g.setColor(Color.WHITE);
                    g.fillOval(x + 20, y + 45, signalRadius, signalRadius);
                    g.setColor(Color.YELLOW);
                    g.fillOval(x + 20, y + 125, signalRadius, signalRadius);
                    g.setColor(Color.WHITE);
                    g.fillOval(x + 20, y + 205, signalRadius, signalRadius);
                    break;
                case "Go!!":
                    g.setColor(Color.WHITE);
                    g.fillOval(x + 20, y + 45, signalRadius, signalRadius);
                    g.fillOval(x + 20, y + 125, signalRadius, signalRadius);
                    g.setColor(Color.GREEN);
                    g.fillOval(x + 20, y + 205, signalRadius, signalRadius);
                    drawArrow(g, x + 55, y + 235); // Draw arrow on green light
                    break;
                default:
                    g.setColor(Color.WHITE);
                    g.fillOval(x + 20, y + 45, signalRadius, signalRadius);
                    g.fillOval(x + 20, y + 125, signalRadius, signalRadius);
                    g.fillOval(x + 20, y + 205, signalRadius, signalRadius);
                    break;
            }
        }
    }

    private void drawArrow(Graphics g, int x, int y) {
        int[] xPoints = {x, x - 10, x + 10};
        int[] yPoints = {y, y + 20, y + 20};
        g.setColor(Color.BLACK);
        g.fillPolygon(xPoints, yPoints, 3);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Traffic_Signal("Traffic Lights"));
    }
}