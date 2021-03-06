/*
 * OM Simulation Tool: This tool intends to test and evaluate the scientific
 * robustness of the protocol `6+1`. Therefore, it generates a huge amount of
 * virtual measurement campaigns based on real radon concentration data
 * following the mentioned protocol. <http://github.com/donschoe/omsimulation>
 * 
 * Copyright (C) 2012 Alexander Schoedon <a.schoedon@student.htw-berlin.de>
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.bfs.radon.omsimulation.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.itextpdf.awt.DefaultFontMapper;
import com.itextpdf.text.PageSize;

import de.bfs.radon.omsimulation.data.OMBuilding;
import de.bfs.radon.omsimulation.data.OMRoom;
import de.bfs.radon.omsimulation.gui.data.OMCharts;
import de.bfs.radon.omsimulation.gui.data.OMExports;

/**
 * Creates and shows the data inspection panel for this software tool. Allows
 * the user to analyse the imported value and to display radon concentration
 * charts for selected rooms.
 * 
 * @author A. Schoedon
 */
public class OMPanelData extends JPanel {

  /**
   * Unique serial version ID.
   */
  private static final long     serialVersionUID = -4415458628159499190L;

  /**
   * Stores the absolute path to the OMB object which will be used to analyse
   * the imported data.
   */
  private String                ombFile;

  /**
   * UI: Label "Select Project"
   */
  private JLabel                lblSelectProject;

  /**
   * UI: Label "Select Room"
   */
  private JLabel                lblSelectRoom;

  /**
   * UI: Label for first orientation, content: "Select an OMB-Object file to
   * analyse its data. You can inspect radon concentration for each room."
   */
  private JLabel                lblHelp;

  /**
   * UI: Label "Select OMB-File"
   */
  private JLabel                lblSelectOmbfile;

  /**
   * UI: Label "Export charts to ..."
   */
  private JLabel                lblExportChartTo;

  /**
   * UI: Text field to enter the absolute path to the OMB object file.
   */
  private JTextField            txtOmbFile;

  /**
   * UI: Button to load the selected OMB file to the panel.
   */
  private JButton               btnRefresh;

  /**
   * UI: Button to display the chart in fullscreen mode.
   */
  private JButton               btnMaximize;

  /**
   * UI: Button to open a file browser to save an OMB file.
   */
  private JButton               btnBrowse;

  /**
   * UI: Button to export the chart to CSV.
   */
  private JButton               btnCsv;

  /**
   * UI: Button to export the chart to PDF.
   */
  private JButton               btnPdf;

  /**
   * UI: Combobox to select a project to analyse.
   */
  private JComboBox<OMBuilding> comboBoxProjects;

  /**
   * UI: Combobox to select a room.
   */
  private JComboBox<OMRoom>     comboBoxRooms;

  /**
   * UI: Progress bar to display the status of certain actions performed on this
   * panel.
   */
  private JProgressBar          progressBar;

  /**
   * UI: Panel which is used as a place holder for the chart.
   */
  private JPanel                panelData;

  /**
   * UI: Panel where the room radon concentration chart is drawn to.
   */
  private JPanel                panelRoom;

  /**
   * Stores the task to load OMB files to the panel which will be executed in a
   * separate thread to ensure the UI wont freeze.
   */
  private RefreshProjects       refreshProjectsTask;

  /**
   * Stores the task to update the charts which will be executed in a separate
   * thread to ensure the UI wont freeze.
   */
  private RefreshCharts         refreshChartsTask;

  /**
   * Gets the absolute path to the OMB object which will be used to analyse the
   * imported data.
   * 
   * @return The absolute path to the OMB object.
   */
  public String getOmbFile() {
    return this.ombFile;
  }

  /**
   * Sets the absolute path to the OMB object which will be used to analyse the
   * imported data.
   * 
   * @param ombFile
   *          The absolute path to the OMB object.
   */
  public void setOmbFile(String ombFile) {
    this.ombFile = ombFile;
  }

  /**
   * The inner class RefreshCharts used to update the charts which will be
   * executed in a separate thread to ensure the UI wont freeze.
   * 
   * @author A. Schoedon
   */
  class RefreshCharts extends SwingWorker<Void, Void> {

    /**
     * Updates the chart panel with the radon data of the selected room.
     * 
     * @see javax.swing.SwingWorker#doInBackground()
     */
    @Override
    protected Void doInBackground() throws Exception {
      OMBuilding building = (OMBuilding) comboBoxProjects.getSelectedItem();
      String title = building.getName();
      OMRoom room = (OMRoom) comboBoxRooms.getSelectedItem();
      panelRoom = createRoomPanel(title, room, false, false);
      panelData = new JPanel();
      panelData.setBounds(10, 118, 730, 347);
      panelData.add(panelRoom);
      return null;
    }

    /**
     * Executed in event dispatching thread after finishing the refresh task.
     * Updates the interface and adds the new chart panel.
     * 
     * @see javax.swing.SwingWorker#done()
     */
    @Override
    public void done() {
      add(panelData);
      btnPdf.setVisible(true);
      btnCsv.setVisible(true);
      lblExportChartTo.setVisible(true);
      comboBoxRooms.setEnabled(true);
      updateUI();
      setCursor(null);
    }
  }

  /**
   * The inner class RefreshProjects used load OMB files to the panel which will
   * be executed in a separate thread to ensure the UI wont freeze.
   * 
   * @author A. Schoedon
   */
  class RefreshProjects extends SwingWorker<Void, Void> {

    /**
     * Updates the progress bar status and message.
     * 
     * @param s
     *          The log message.
     * @param i
     *          The status in percent.
     */
    private void tmpUpdate(String s, int i) {
      progressBar.setString(s);
      progressBar.setValue(i);
      try {
        Thread.sleep(100);
      } catch (InterruptedException ie) {
        ie.printStackTrace();
      }
    }

    /**
     * Loads the object from the OMB file to the panel.
     * 
     * @see javax.swing.SwingWorker#doInBackground()
     */
    @Override
    public Void doInBackground() {
      tmpUpdate("Getting objects from Database... ", 1);
      ObjectContainer db4o = Db4oEmbedded.openFile(
          Db4oEmbedded.newConfiguration(), getOmbFile());
      ObjectSet<OMBuilding> result = db4o.queryByExample(OMBuilding.class);
      OMBuilding found;
      tmpUpdate("Refreshing list... ", 2);
      tmpUpdate("Adding items... ", 3);
      for (int i = 0; i < result.size(); i++) {
        double perc = (double) i / (double) result.size() * 100.0 + 3.0;
        while (perc > 99) {
          perc--;
        }
        found = (OMBuilding) result.next();
        comboBoxProjects.addItem(found);
        tmpUpdate("Added: " + found.getName(), (int) perc);
      }
      tmpUpdate("Finished. ", 100);
      db4o.close();
      return null;
    }

    /**
     * Executed in event dispatching thread after finishing the refresh task,
     * updates the interface.
     * 
     * @see javax.swing.SwingWorker#done()
     */
    @Override
    public void done() {
      btnRefresh.setEnabled(true);
      comboBoxProjects.setEnabled(true);
      tmpUpdate(" ", 0);
      progressBar.setStringPainted(false);
      progressBar.setValue(0);
      progressBar.setVisible(false);
      setCursor(null);
    }
  }

  /**
   * Initialises the interface of the data panel without any preloaded objects.
   */
  public OMPanelData() {
    initialize();
  }

  /**
   * Initialises the interface of the data panel with a preloaded object from
   * import panel. Launching a refresh task in background.
   * 
   * @param ombFile
   *          Absolute path to an OMB object file to load on init.
   * @param building
   *          The imported building object.
   */
  public OMPanelData(String ombFile, OMBuilding building) {
    initialize();
    txtOmbFile.setText(ombFile);
    setOmbFile(ombFile);
    comboBoxProjects.addItem(building);
    comboBoxProjects.setEnabled(true);
  }

  /**
   * Initialises the interface of the data panel.
   */
  protected void initialize() {
    setLayout(null);

    lblExportChartTo = new JLabel("Export chart to ...");
    lblExportChartTo.setBounds(436, 479, 144, 14);
    lblExportChartTo.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    lblExportChartTo.setVisible(false);
    add(lblExportChartTo);

    btnCsv = new JButton("CSV");
    btnCsv.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JFileChooser fileDialog = new JFileChooser();
        fileDialog.setFileFilter(new FileNameExtensionFilter("*.csv", "csv"));
        fileDialog.showSaveDialog(getParent());
        final File file = fileDialog.getSelectedFile();
        if (file != null) {
          String csv;
          String[] tmpFileName = file.getAbsolutePath().split("\\.");
          if (tmpFileName[tmpFileName.length - 1].equals("csv")) {
            csv = "";
          } else {
            csv = ".csv";
          }
          String csvPath = file.getAbsolutePath() + csv;
          OMRoom selectedRoom = (OMRoom) comboBoxRooms.getSelectedItem();
          double[] selectedValues = selectedRoom.getValues();
          File csvFile = new File(csvPath);
          try {
            FileWriter logWriter = new FileWriter(csvFile);
            BufferedWriter csvOutput = new BufferedWriter(logWriter);
            csvOutput.write("\"ID\";\"" + selectedRoom.getId() + "\"");
            csvOutput.newLine();
            for (int i = 0; i < selectedValues.length; i++) {
              csvOutput.write("\"" + i + "\";\"" + (int) selectedValues[i]
                  + "\"");
              csvOutput.newLine();
            }
            JOptionPane.showMessageDialog(null, "CSV saved successfully!\n"
                + csvPath, "Success", JOptionPane.INFORMATION_MESSAGE);
            csvOutput.close();
          } catch (IOException ioe) {
            JOptionPane.showMessageDialog(
                null,
                "Failed to write CSV. Please check permissions!\n"
                    + ioe.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            ioe.printStackTrace();
          }
        } else {
          JOptionPane.showMessageDialog(null,
              "Failed to write CSV. Please check the file path!", "Failed",
              JOptionPane.ERROR_MESSAGE);
        }
      }
    });
    btnCsv.setBounds(590, 475, 70, 23);
    btnCsv.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    btnCsv.setVisible(false);
    add(btnCsv);

    btnPdf = new JButton("PDF");
    btnPdf.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JFileChooser fileDialog = new JFileChooser();
        fileDialog.setFileFilter(new FileNameExtensionFilter("*.pdf", "pdf"));
        fileDialog.showSaveDialog(getParent());
        final File file = fileDialog.getSelectedFile();
        if (file != null) {
          String pdf;
          String[] tmpFileName = file.getAbsolutePath().split("\\.");
          if (tmpFileName[tmpFileName.length - 1].equals("pdf")) {
            pdf = "";
          } else {
            pdf = ".pdf";
          }
          String pdfPath = file.getAbsolutePath() + pdf;
          OMBuilding building = (OMBuilding) comboBoxProjects.getSelectedItem();
          String title = building.getName();
          OMRoom selectedRoom = (OMRoom) comboBoxRooms.getSelectedItem();
          JFreeChart chart = OMCharts.createRoomChart(title, selectedRoom,
              false);
          int height = (int) PageSize.A4.getWidth();
          int width = (int) PageSize.A4.getHeight();
          try {
            OMExports.exportPdf(pdfPath, chart, width, height,
                new DefaultFontMapper(), title);
            JOptionPane.showMessageDialog(null, "PDF saved successfully!\n"
                + pdfPath, "Success", JOptionPane.INFORMATION_MESSAGE);
          } catch (IOException ioe) {
            JOptionPane.showMessageDialog(
                null,
                "Failed to write PDF. Please check permissions!\n"
                    + ioe.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            ioe.printStackTrace();
          }
        } else {
          JOptionPane.showMessageDialog(null,
              "Failed to write PDF. Please check the file path!", "Failed",
              JOptionPane.ERROR_MESSAGE);
        }
      }
    });
    btnPdf.setBounds(670, 475, 70, 23);
    btnPdf.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    btnPdf.setVisible(false);
    add(btnPdf);

    lblSelectProject = new JLabel("Select Project");
    lblSelectProject.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    lblSelectProject.setBounds(10, 65, 132, 14);
    add(lblSelectProject);

    lblSelectRoom = new JLabel("Select Room");
    lblSelectRoom.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    lblSelectRoom.setBounds(10, 94, 132, 14);
    add(lblSelectRoom);

    panelData = new JPanel();
    panelData.setBounds(10, 118, 730, 347);
    add(panelData);

    btnRefresh = new JButton("Load");
    btnRefresh.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    btnRefresh.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        if (txtOmbFile.getText() != null && !txtOmbFile.getText().equals("")
            && !txtOmbFile.getText().equals(" ")) {
          txtOmbFile.setBackground(Color.WHITE);
          String ombPath = txtOmbFile.getText();
          String omb;
          String[] tmpFileName = ombPath.split("\\.");
          if (tmpFileName[tmpFileName.length - 1].equals("omb")) {
            omb = "";
          } else {
            omb = ".omb";
          }
          txtOmbFile.setText(ombPath + omb);
          setOmbFile(ombPath + omb);
          File ombFile = new File(ombPath + omb);
          if (ombFile.exists()) {
            txtOmbFile.setBackground(Color.WHITE);
            btnRefresh.setEnabled(false);
            comboBoxProjects.setEnabled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            btnPdf.setVisible(false);
            btnCsv.setVisible(false);
            lblExportChartTo.setVisible(false);
            progressBar.setVisible(true);
            progressBar.setStringPainted(true);
            progressBar.setIndeterminate(true);
            refreshProjectsTask = new RefreshProjects();
            refreshProjectsTask.execute();
          } else {
            txtOmbFile.setBackground(new Color(255, 222, 222, 128));
            JOptionPane.showMessageDialog(null,
                "OMB-file not found, please check the file path!", "Error",
                JOptionPane.ERROR_MESSAGE);
          }
        } else {
          txtOmbFile.setBackground(new Color(255, 222, 222, 128));
          JOptionPane.showMessageDialog(null, "Please select an OMB-file!",
              "Warning", JOptionPane.WARNING_MESSAGE);
        }
      }
    });
    btnRefresh.setBounds(616, 61, 124, 23);
    add(btnRefresh);

    btnMaximize = new JButton("Fullscreen");
    btnMaximize.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    btnMaximize.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        if (comboBoxRooms.isEnabled()) {
          if (comboBoxRooms.getSelectedItem() != null) {
            OMBuilding building = (OMBuilding) comboBoxProjects
                .getSelectedItem();
            String title = building.getName();
            OMRoom room = (OMRoom) comboBoxRooms.getSelectedItem();
            panelRoom = createRoomPanel(title, room, false, false);
            JFrame chartFrame = new JFrame();
            JPanel chartPanel = createRoomPanel(title, room, false, true);
            chartFrame.getContentPane().add(chartPanel);
            chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            chartFrame.setBounds(0, 0, (int) dim.getWidth(),
                (int) dim.getHeight());
            chartFrame.setTitle("OM Simulation Tool: " + title + ", Room "
                + room.getId());
            chartFrame.setResizable(true);
            chartFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            chartFrame.setVisible(true);
          }
        }
      }
    });
    btnMaximize.setBounds(10, 475, 124, 23);
    btnMaximize.setVisible(false);
    add(btnMaximize);

    comboBoxProjects = new JComboBox<OMBuilding>();
    comboBoxProjects.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    comboBoxProjects.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        boolean b = false;
        Color c = null;
        if (comboBoxProjects.isEnabled()) {
          if (comboBoxProjects.getSelectedItem() != null) {
            b = true;
            c = Color.WHITE;
            OMBuilding building = (OMBuilding) comboBoxProjects
                .getSelectedItem();
            comboBoxRooms.removeAllItems();
            for (int i = 0; i < building.getRooms().length; i++) {
              comboBoxRooms.addItem(building.getRooms()[i]);
            }
            for (int i = 0; i < building.getCellars().length; i++) {
              comboBoxRooms.addItem(building.getCellars()[i]);
            }
            for (int i = 0; i < building.getMiscs().length; i++) {
              comboBoxRooms.addItem(building.getMiscs()[i]);
            }
          } else {
            b = false;
            c = null;
          }
        } else {
          b = false;
          c = null;
        }
        lblSelectRoom.setEnabled(b);
        panelData.setEnabled(b);
        btnMaximize.setVisible(b);
        comboBoxRooms.setEnabled(b);
        panelData.setBackground(c);
      }
    });
    comboBoxProjects.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        boolean b = false;
        Color c = null;
        if (comboBoxProjects.isEnabled()) {
          if (comboBoxProjects.getSelectedItem() != null) {
            b = true;
            c = Color.WHITE;
            OMBuilding building = (OMBuilding) comboBoxProjects
                .getSelectedItem();
            comboBoxRooms.removeAllItems();
            for (int i = 0; i < building.getRooms().length; i++) {
              comboBoxRooms.addItem(building.getRooms()[i]);
            }
            for (int i = 0; i < building.getCellars().length; i++) {
              comboBoxRooms.addItem(building.getCellars()[i]);
            }
            for (int i = 0; i < building.getMiscs().length; i++) {
              comboBoxRooms.addItem(building.getMiscs()[i]);
            }
          } else {
            b = false;
            c = null;
          }
        } else {
          b = false;
          c = null;
        }
        lblSelectRoom.setEnabled(b);
        panelData.setEnabled(b);
        btnMaximize.setVisible(b);
        comboBoxRooms.setEnabled(b);
        panelData.setBackground(c);
      }
    });
    comboBoxProjects.setBounds(152, 61, 454, 22);
    add(comboBoxProjects);

    comboBoxRooms = new JComboBox<OMRoom>();
    comboBoxRooms.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    comboBoxRooms.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        if (comboBoxRooms.isEnabled()) {
          if (comboBoxRooms.getSelectedItem() != null) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            remove(panelData);
            comboBoxRooms.setEnabled(false);
            refreshChartsTask = new RefreshCharts();
            refreshChartsTask.execute();
          }
        }
      }
    });
    comboBoxRooms.setBounds(152, 90, 454, 22);
    add(comboBoxRooms);

    progressBar = new JProgressBar();
    progressBar.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    progressBar.setBounds(10, 475, 730, 23);
    progressBar.setVisible(false);
    add(progressBar);

    lblSelectRoom.setEnabled(false);
    panelData.setEnabled(false);
    comboBoxRooms.setEnabled(false);

    lblHelp = new JLabel(
        "Select an OMB-Object file to analyse its data. You can inspect radon concentration for each room.");
    lblHelp.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    lblHelp.setForeground(Color.GRAY);
    lblHelp.setBounds(10, 10, 730, 14);
    add(lblHelp);

    txtOmbFile = new JTextField();
    txtOmbFile.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    txtOmbFile.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent arg0) {
        setOmbFile(txtOmbFile.getText());
      }
    });
    txtOmbFile.setBounds(152, 33, 454, 20);
    add(txtOmbFile);
    txtOmbFile.setColumns(10);

    btnBrowse = new JButton("Browse");
    btnBrowse.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    btnBrowse.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        JFileChooser fileDialog = new JFileChooser();
        fileDialog.setFileFilter(new FileNameExtensionFilter("*.omb", "omb"));
        fileDialog.showOpenDialog(getParent());
        final File file = fileDialog.getSelectedFile();
        if (file != null) {
          String omb;
          String[] tmpFileName = file.getAbsolutePath().split("\\.");
          if (tmpFileName[tmpFileName.length - 1].equals("omb")) {
            omb = "";
          } else {
            omb = ".omb";
          }
          txtOmbFile.setText(file.getAbsolutePath() + omb);
          setOmbFile(file.getAbsolutePath() + omb);
        }
      }
    });
    btnBrowse.setBounds(616, 32, 124, 23);
    add(btnBrowse);

    lblSelectOmbfile = new JLabel("Select OMB-File");
    lblSelectOmbfile.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    lblSelectOmbfile.setBounds(10, 36, 132, 14);
    add(lblSelectOmbfile);
  }

  /**
   * Creates the radon concentration data panel which will be added to the
   * interface.
   * 
   * @param title
   *          The headline of the chart. Will be hidden if set to null.
   * @param room
   *          The room object containing the radon data.
   * @param preview
   *          Will hide annotations, labels and headlines if true.
   * @param fullscreen
   *          Will correctly adjust the preferred size to screen resolution if
   *          true.
   * @return A panel displaying the radon concentration of a single room.
   */
  public JPanel createRoomPanel(String title, OMRoom room, boolean preview,
      boolean fullscreen) {
    JFreeChart chart = OMCharts.createRoomChart(title, room, preview);
    ChartPanel chartPanel = new ChartPanel(chart);
    Dimension dim;
    if (fullscreen) {
      dim = Toolkit.getDefaultToolkit().getScreenSize();
    } else {
      dim = new Dimension(730, 347);
    }
    chartPanel.setPreferredSize(dim);
    JPanel roomPanel = (JPanel) chartPanel;
    return roomPanel;
  }
}
