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

package de.bfs.radon.omsimulation.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;

/**
 * Public abstract class OMHelper providing helper methods for writing
 * simulation logs and doing calculations. Can not be instantiated.
 * 
 * @author A. Schoedon
 */
public abstract class OMHelper {

  /**
   * Stores the generic log writer, used for the whole program's log output.
   */
  private static BufferedWriter logOutput;

  /**
   * Indicates whether a log-output buffer is initialised or not.
   */
  private static boolean        isLogOutputEnabled = false;

  /**
   * Sets the log-writer, creates a filename based on the path and the project
   * type and finally initialises the log writer.
   * 
   * @param path
   *          The complete path and file name of the OMB or OMS object.
   * @param logType
   *          The type of the current log to identify the log file.
   * @throws IOException
   *           If creating log file or writing logs fails.
   */
  public static void setLogOutput(String path, String logType)
      throws IOException {
    Format format = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
    double current = System.currentTimeMillis();
    String timestamp = format.format(current);
    if (logType == null) {
      logType = timestamp;
    } else {
      if (logType.length() == 0) {
        logType = timestamp;
      }
    }
    String logName = path + "_" + logType + ".log";
    File logFile = new File(logName);
    int i = 2;
    while (logFile.exists()) {
      logName = path + "_" + logType + "(" + i + ").log";
      logFile = new File(logName);
      i++;
    }
    FileWriter logWriter = new FileWriter(logFile);
    BufferedWriter tmpOutput = new BufferedWriter(logWriter);
    OMHelper.logOutput = tmpOutput;
    setLogOutputEnabled(true);
  }

  /**
   * Indicates whether a log-output buffer is initialised or not. Only writes
   * log messages if true.
   * 
   * @return True if a log-output buffer is initialised.
   */
  public static boolean isLogOutputEnabled() {
    return OMHelper.isLogOutputEnabled;
  }

  /**
   * Indicates whether a log-output buffer is initialised or not. Set true after
   * successfully initialising log-output.
   * 
   * @param isLogOutput
   *          Indicator whether a log-output buffer is initialised or not.
   */
  private static void setLogOutputEnabled(boolean isLogOutput) {
    OMHelper.isLogOutputEnabled = isLogOutput;
  }

  /**
   * Calculates the arithmetic mean using a set of values.
   * 
   * @param values
   *          Set of values used to calculate the arithmetic mean.
   * @return The arithmetic mean.
   */
  public static double calculateAM(double[] values) {
    double am = 0.0;
    for (int i = 0; i < values.length; i++) {
      am = am + values[i];
    }
    am = am / values.length;
    return am;
  }

  /**
   * Calculates the standard deviation using a set of values and the arithmetic
   * mean.
   * 
   * @param values
   *          Set of values used to calculate the standard deviation.
   * @param am
   *          The arithmetic mean.
   * @return The standard deviation.
   */
  public static double calculateSD(double[] values, double am) {
    double sd = 0.0;
    for (int i = 0; i < values.length; i++) {
      sd = sd + (values[i] - am) * (values[i] - am);
    }
    sd = sd / (values.length - 1.0);
    sd = Math.sqrt(sd);
    return sd;
  }

  /**
   * Calculates the variation coefficient using the arithmetic mean and standard
   * deviation.
   * 
   * @param arithMean
   *          The arithmetic mean.
   * @param stdDeviation
   *          The standard deviation.
   * @return The variations coefficient.
   */
  public static double calculateCV(double arithMean, double stdDeviation) {
    double cv = stdDeviation / arithMean;
    return cv;
  }

  /**
   * Calculates the geometric mean using a set of values.
   * 
   * @param values
   *          Set of values used to calculate the geometric mean.
   * @return The geometric mean.
   */
  public static double calculateGM(double[] values) {
    double gm = 0.0;
    for (int i = 0; i < values.length; i++) {
      if (values[i] > 0) {
        gm = gm + Math.log(values[i]);
      }
    }
    gm = gm / values.length;
    gm = Math.exp(gm);
    return gm;
  }

  /**
   * Calculates the geometric standard deviation.
   * 
   * @param values
   *          An array of values.
   * @param geoMean
   *          The geometric mean of the values.
   * @return The geometric standard deviation.
   */
  public static double calculateGSD(double[] values, double geoMean) {
    double gsd = 0.0;
    for (int i = 0; i < values.length; i++) {
      if (values[i] > 0 && geoMean > 0) {
        gsd = gsd + (Math.log(values[i]) - Math.log(geoMean))
            * (Math.log(values[i]) - Math.log(geoMean));
      }
    }
    gsd = gsd / (values.length - 1);
    gsd = Math.sqrt(gsd);
    gsd = Math.exp(gsd);
    return gsd;
  }

  /**
   * Calculates the quantile deviation using the quantiles 5 and 95.
   * 
   * @param q5
   *          The quantile 5.
   * @param q95
   *          The quantile 95.
   * @return The quantile deviation.
   */
  public static double calculateQD(double q5, double q95) {
    double qd = (q95 - q5) / 2.0;
    return qd;
  }

  /**
   * Calculates the relative quantile deviation using the quantiles 5, 50 and
   * 95.
   * 
   * @param q5
   *          The quantile 5.
   * @param q50
   *          The quantile 50.
   * @param q95
   *          The quantile 95.
   * @return The quantile deviation.
   */
  public static double calculateRQD(double q5, double q50, double q95) {
    double rqd = (q95 - q5) / (2.0 * q50);
    return rqd;
  }

  /**
   * Calculates the factorial of n!.
   * 
   * @param n
   *          Integer value which is used to calculate factorial of n!.
   * @return The factorial n!.
   */
  public static int calculateFactorial(int n) {
    int f = 1;
    if (n > 1) {
      for (int i = 1; i <= n; i++) {
        f = f * i;
      }
    }
    return f;
  }

  /**
   * Gets the current timestamp using the format "dd.MM.yyyy HH:mm:ss,SSS ".
   * 
   * @return The current timestamp.
   */
  public static String getTimestamp() {
    Format format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss,SSS ");
    double current = System.currentTimeMillis();
    String timestamp = format.format(current);
    return timestamp;
  }

  /**
   * Writes a new line with a timestamp to the log file using the
   * BufferedReader. Call this method once per line to write.
   * 
   * @param newLine
   *          A string to write into the log file.
   * @throws IOException
   *           If creating log file or writing logs fails.
   */
  public static void writeLog(String newLine) throws IOException {
    OMHelper.logOutput.write(getTimestamp() + newLine);
    OMHelper.logOutput.newLine();
  }

  /**
   * Helper method to close the log file and the BufferedReader.
   * 
   * @throws IOException
   *           If closing log file or writing logs fails.
   */
  public static void closeLog() throws IOException {
    OMHelper.logOutput.close();
    setLogOutputEnabled(false);
  }
}
