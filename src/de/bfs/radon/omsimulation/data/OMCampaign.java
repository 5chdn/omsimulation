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

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

/**
 * Public class OMCampaign, defining the simulated campaigns and its attributes.
 * 
 * @author A. Schoedon
 */
public class OMCampaign {

  /**
   * Stores the timestamp of the first measurement for the simulated campaign,
   * starting with 0 for the first hour of the real value-sets. The unit is [h].
   */
  private int            start;

  /**
   * Stores a simple string to identify the used rooms and their order in this
   * campaign.
   */
  private String         variation;

  /**
   * Stores the campaign type. The campaign type identifies how many different
   * rooms were used to create this campaign. For example 'Five' means 5
   * different rooms were used.
   */
  private OMCampaignType type;

  /**
   * Stores an integer which defines the random noise that is added to the
   * values. A random noise of 0 means the original values wont be modified. The
   * unit is [%].
   */
  private int            randomNoise;

  /**
   * Stores all rooms and the cellars keeping their order to simplify
   * reconstruction of the campaign later.
   */
  private OMRoom[]       roomPattern;

  /**
   * Stores an array of different rooms of the simulated survey campaign.
   */
  private OMRoom[]       rooms;

  /**
   * Stores the cellar for the campaign.
   */
  private OMRoom         cellar;

  /**
   * Stores an array of selected values of all 6 normal rooms. The first value
   * is determined by the start timestamp. The unit is [Bq/m^3].
   */
  private double[]       roomValues;

  /**
   * Stores an array of selected values for the cellar room. The first value is
   * determined by the start timestamp. The unit is [Bq/m^3].
   */
  private double[]       cellarValues;

  /**
   * Stores an array of selected values for the whole campaign. The first value
   * is determined by the start timestamp. The unit is [Bq/m^3].
   */
  private double[]       valueChain;

  /**
   * Stores an array of logarithmic values of the normal values. Used for
   * geometric mean later on.
   */
  private double[]       roomLogValues;

  /**
   * Stores an array of logarithmic values for the cellar values. Used for
   * geometric mean later on.
   */
  private double[]       cellarLogValues;

  /**
   * Stores the arithmetic average of the selected values for normal rooms. The
   * unit is [Bq/m^3].
   */
  private double         roomAverage;

  /**
   * Stores the arithmetic average of the selected cellar. The unit is [Bq/m^3].
   */
  private double         cellarAverage;

  /**
   * Stores the highest value out of the selected normal rooms. The unit is
   * [Bq/m^3].
   */
  private double         roomMaximum;

  /**
   * Stores the highest value out of the selected cellar. The unit is [Bq/m^3].
   */
  private double         cellarMaximum;

  /**
   * Stores the lowest value out of the selected normal rooms. The unit is
   * [Bq/m^3].
   */
  private double         roomMinimum;

  /**
   * Stores the lowest value out of the selected cellar. The unit is [Bq/m^3].
   */
  private double         cellarMinimum;

  /**
   * Stores the standard deviation of the selected values for normal rooms. The
   * unit is [Bq/m^3].
   */
  private double         roomDeviation;

  /**
   * Stores the standard deviation of the selected cellar. The unit is [Bq/m^3].
   */
  private double         cellarDeviation;

  /**
   * Stores the variation coefficient which determines the ratio between
   * arithmetic mean and standard deviation for normal rooms.
   */
  private double         roomVarCoefficient;

  /**
   * Stores the variation coefficient which determines the ratio between
   * arithmetic mean and standard deviation for cellars.
   */
  private double         cellarVarCoefficient;

  /**
   * Stores the range of the values which is calculated by subtracting the
   * minimum by the maximum value for normal rooms. The unit is [Bq/m^3].
   */
  private double         roomRange;

  /**
   * Stores the range of the values which is calculated by subtracting the
   * minimum by the maximum value for cellar rooms. The unit is [Bq/m^3].
   */
  private double         cellarRange;

  /**
   * Stores the quantile 5 for normal rooms, where only 5% of the values are
   * lower. The unit is [Bq/m^3].
   */
  private double         roomQuantile05;

  /**
   * Stores the quantile 5 for cellar rooms, where only 5% of values are lower.
   * The unit is [Bq/m^3].
   */
  private double         cellarQuantile05;

  /**
   * Stores the quantile 95 for normal rooms, where 95% of the values are lower.
   * The unit is [Bq/m^3].
   */
  private double         roomQuantile95;

  /**
   * Stores the quantile 95 for cellar rooms, where 95% of the values are lower.
   * The unit is [Bq/m^3].
   */
  private double         cellarQuantile95;

  /**
   * Stores the median (quantile 50) for normal rooms, where 50% of the values
   * are lower. The unit is [Bq/m^3].
   */
  private double         roomMedian;

  /**
   * Stores the median (quantile 50) for cellar rooms, where 50% of the values
   * are lower. The unit is [Bq/m^3].
   */
  private double         cellarMedian;

  /**
   * Stores the quantile deviation for normal rooms which determines the
   * deviation of the quantiles.
   */
  private double         roomQuantileDeviation;

  /**
   * Stores the quantile deviation for cellar rooms which determines the
   * deviation of the quantiles.
   */
  private double         cellarQuantileDeviation;

  /**
   * Stores the relative quantile deviation for normal rooms which determines
   * the ratio of the quantiles.
   */
  private double         roomRelativeQuantileDeviation;

  /**
   * Stores the relative quantile deviation for cellar rooms which determines
   * the ratio of the quantiles.
   */
  private double         cellarRelativeQuantileDeviation;

  /**
   * Stores the geometric mean for normal rooms. The unit is [Bq/m^3].
   */
  private double         roomLogAverage;

  /**
   * Stores the geometric mean for cellar rooms. The unit is [Bq/m^3].
   */
  private double         cellarLogAverage;

  /**
   * Stores the geometric standard deviation for normal rooms.
   */
  private double         roomLogDeviation;

  /**
   * Stores the geometric standard deviation for cellar rooms.
   */
  private double         cellarLogDeviation;

  /**
   * Gets the timestamp of the first measurement for the simulated campaign. The
   * unit is [h].
   * 
   * @return The timestamp of the first measurement for the simulated campaign.
   */
  public int getStart() {
    return this.start;
  }

  /**
   * Sets the timestamp of the first measurement for the simulated campaign. The
   * unit is [h].
   * 
   * @param start
   *          The timestamp of the first measurement for the simulated campaign,
   *          0 equals the first hour of the real value-sets.
   * @throws IOException
   *           If creating log file or writing logs fails.
   */
  public void setStart(int start) throws IOException {
    this.start = start;
    calculateAttributes();
  }

  /**
   * Gets a simple string to identify the used rooms and their order in this
   * campaign. For example R1R2C1R3R3R4R6.
   * 
   * @return A simple string to identify the used rooms and their order in this
   *         campaign.
   */
  public String getVariation() {
    return this.variation;
  }

  /**
   * Sets a simple string to identify the used rooms and their order in this
   * campaign. For example R1R2C1R3R3R4R6.
   * 
   * @param variation
   */
  private void setVariation(String variation) {
    this.variation = variation;
  }

  /**
   * Gets the campaign type. The campaign type identifies how many different
   * rooms were used to create this campaign. For example 'Five' means 5
   * different rooms were used.
   * 
   * @return The campaign type.
   */
  public OMCampaignType getType() {
    return this.type;
  }

  /**
   * Sets the campaign type. The campaign type identifies how many different
   * rooms were used to create this campaign. For example 'Five' means 5
   * different rooms were used.
   * 
   * @throws IOException
   *           If creating log file or writing logs fails.
   */
  private void setType() throws IOException {
    OMRoom[] rooms = this.rooms;
    OMCampaignType type = OMCampaignType.Six;
    if (rooms[0].getId() != rooms[1].getId()) {
      if (rooms[1].getId() != rooms[2].getId()) {
        if (rooms[2].getId() != rooms[3].getId()) {
          if (rooms[3].getId() != rooms[4].getId()) {
            if (rooms[4].getId() != rooms[5].getId()) {
              type = OMCampaignType.Six;
            } else {
              type = OMCampaignType.Five;
            }
          } else {
            if (rooms[4].getId() != rooms[5].getId()) {
              type = OMCampaignType.Five;
            } else {
              type = OMCampaignType.Four;
            }
          }
        } else {
          if (rooms[3].getId() != rooms[4].getId()) {
            if (rooms[4].getId() != rooms[5].getId()) {
              type = OMCampaignType.Five;
            } else {
              type = OMCampaignType.Four;
            }
          } else {
            if (rooms[4].getId() != rooms[5].getId()) {
              type = OMCampaignType.Four;
            } else {
              type = OMCampaignType.Three;
            }
          }
        }
      } else {
        if (rooms[2].getId() != rooms[3].getId()) {
          if (rooms[3].getId() != rooms[4].getId()) {
            if (rooms[4].getId() != rooms[5].getId()) {
              type = OMCampaignType.Five;
            } else {
              type = OMCampaignType.Four;
            }
          } else {
            if (rooms[4].getId() != rooms[5].getId()) {
              type = OMCampaignType.Four;
            } else {
              type = OMCampaignType.Three;
            }
          }
        } else {
          if (rooms[3].getId() != rooms[4].getId()) {
            if (rooms[4].getId() != rooms[5].getId()) {
              type = OMCampaignType.Four;
            } else {
              type = OMCampaignType.Three;
            }
          } else {
            if (rooms[4].getId() != rooms[5].getId()) {
              type = OMCampaignType.Three;
            } else {
              type = OMCampaignType.Two;
              if (OMHelper.isLogOutputEnabled()) {
                OMHelper
                    .writeLog("Error: At least 3 different rooms are needed to create a campaign.");
              }
            }
          }
        }
      }
    } else {
      if (rooms[1].getId() != rooms[2].getId()) {
        if (rooms[2].getId() != rooms[3].getId()) {
          if (rooms[3].getId() != rooms[4].getId()) {
            if (rooms[4].getId() != rooms[5].getId()) {
              type = OMCampaignType.Five;
            } else {
              type = OMCampaignType.Four;
            }
          } else {
            if (rooms[4].getId() != rooms[5].getId()) {
              type = OMCampaignType.Four;
            } else {
              type = OMCampaignType.Three;
            }
          }
        } else {
          if (rooms[3].getId() != rooms[4].getId()) {
            if (rooms[4].getId() != rooms[5].getId()) {
              type = OMCampaignType.Four;
            } else {
              type = OMCampaignType.Three;
            }
          } else {
            if (rooms[4].getId() != rooms[5].getId()) {
              type = OMCampaignType.Three;
            } else {
              type = OMCampaignType.Two;
              if (OMHelper.isLogOutputEnabled()) {
                OMHelper
                    .writeLog("Error: At least 3 different rooms are needed to create a campaign.");
              }
            }
          }
        }
      } else {
        if (rooms[2].getId() != rooms[3].getId()) {
          if (rooms[3].getId() != rooms[4].getId()) {
            if (rooms[4].getId() != rooms[5].getId()) {
              type = OMCampaignType.Four;
            } else {
              type = OMCampaignType.Three;
            }
          } else {
            if (rooms[4].getId() != rooms[5].getId()) {
              type = OMCampaignType.Three;
            } else {
              type = OMCampaignType.Two;
              if (OMHelper.isLogOutputEnabled()) {
                OMHelper
                    .writeLog("Error: At least 3 different rooms are needed to create a campaign.");
              }
            }
          }
        } else {
          if (rooms[3].getId() != rooms[4].getId()) {
            if (rooms[4].getId() != rooms[5].getId()) {
              type = OMCampaignType.Three;
            } else {
              type = OMCampaignType.Two;
              if (OMHelper.isLogOutputEnabled()) {
                OMHelper
                    .writeLog("Error: At least 3 different rooms are needed to create a campaign.");
              }
            }
          } else {
            if (rooms[4].getId() != rooms[5].getId()) {
              type = OMCampaignType.Two;
              if (OMHelper.isLogOutputEnabled()) {
                OMHelper
                    .writeLog("Error: At least 3 different rooms are needed to create a campaign.");
              }
            } else {
              type = OMCampaignType.One;
              if (OMHelper.isLogOutputEnabled()) {
                OMHelper
                    .writeLog("Error: At least 3 different rooms are needed to create a campaign.");
              }
            }
          }
        }
      }
    }
    this.type = type;
  }

  /**
   * Gets an integer which defines the random noise that is added to the values.
   * A random noise of 0 means the original values wont be modified. The unit is
   * [%].
   * 
   * @return An integer which defines the random noise that is added to the
   *         values.
   */
  public int getRandomNoise() {
    return this.randomNoise;
  }

  /**
   * Sets an integer which defines the random noise that is added to the values.
   * A random noise of 0 means the original values wont be modified. The unit is
   * [%].
   * 
   * @param randomNoise
   *          An integer which defines the random noise that is added to the
   *          values.
   */
  private void setRandomNoise(int randomNoise) {
    this.randomNoise = randomNoise;
  }

  /**
   * Gets all rooms and the cellars keeping their order to simplify
   * reconstruction of the campaign later on.
   */
  public OMRoom[] getRoomPattern() {
    return this.roomPattern;
  }

  /**
   * Sets all rooms and the cellars keeping their order to simplify
   * reconstruction of the campaign later on.
   * 
   * @param rooms
   *          An array of 7 rooms including 1 cellar.
   */
  private void setRoomPattern(OMRoom[] rooms) {
    this.roomPattern = rooms;
  }

  /**
   * Gets an array of rooms of the simulated survey campaign.
   * 
   * @return An array of rooms of the simulated survey campaign.
   */
  public OMRoom[] getRooms() {
    return this.rooms;
  }

  /**
   * Sets an array of rooms of the simulated survey campaign and triggers a
   * re-calculation of all attributes.
   * 
   * @param rooms
   *          An array of rooms of the simulated survey campaign.
   * @throws IOException
   *           If creating log file or writing logs fails.
   */
  public void setRooms(OMRoom[] rooms) throws IOException {
    if (rooms.length == 6) {
      this.rooms = rooms;
      calculateAttributes();
    } else {
      if (OMHelper.isLogOutputEnabled()) {
        OMHelper.writeLog("Error: 6 rooms are needed to create a campaign.");
      }
    }
  }

  /**
   * Gets the cellar for the campaign.
   * 
   * @return The cellar for the campaign.
   */
  public OMRoom getCellar() {
    return this.cellar;
  }

  /**
   * Sets the cellar for the campaign and triggers a re-calculation of all
   * attributes.
   * 
   * @param cellar
   *          The cellar for the campaign.
   * @throws IOException
   *           If creating log file or writing logs fails.
   */
  public void setCellar(OMRoom cellar) throws IOException {
    this.cellar = cellar;
    calculateAttributes();
  }

  /**
   * Gets an array of selected values of all normal rooms. The first values is
   * determined by the start timestamp. The unit is [Bq/m^3].
   * 
   * @return An array of selected values of all normal rooms.
   */
  public double[] getRoomValues() {
    return this.roomValues;
  }

  /**
   * Sets an array of selected values of all normal rooms. The first values is
   * determined by the start timestamp. The values will be sorted from lowest to
   * highest. The unit is [Bq/m^3].
   */
  private void setRoomValues() {
    OMRoom[] rooms = this.rooms;
    int start = this.start;
    String variation = this.variation;
    char[] variationChar = variation.toCharArray();
    int cellarStart = this.start;
    for (int i = 0; i < variationChar.length; i++) {
      if (variationChar[i] == 'C' || variationChar[i] == 'c') {
        cellarStart = cellarStart + i * 12;
      }
    }
    int total = 144;
    int day = 24;
    int randomNoise = this.randomNoise * 10;
    Random generator = new Random();
    double tmpNoise = 0.0;
    double[] values = new double[total];
    int x = 0;
    double[] tmpValues = rooms[0].getValues();
    if (start == cellarStart) {
      start = start + day;
    }
    for (int i = start; i < start + day; i++) {
      if (randomNoise > 0) {
        tmpNoise = ((double) generator.nextInt(randomNoise * 2) - (double) randomNoise) / 1000;
        values[x] = (double) tmpValues[i] + ((double) tmpValues[i] * tmpNoise);
      } else {
        values[x] = (double) tmpValues[i];
      }
      x++;
    }
    start = start + day;
    if (start == cellarStart) {
      start = start + day;
    }
    tmpValues = rooms[1].getValues();
    for (int i = start; i < start + day; i++) {
      if (randomNoise > 0) {
        tmpNoise = ((double) generator.nextInt(randomNoise * 2) - (double) randomNoise) / 1000;
        values[x] = (double) tmpValues[i] + ((double) tmpValues[i] * tmpNoise);
      } else {
        values[x] = (double) tmpValues[i];
      }
      x++;
    }
    start = start + day;
    if (start == cellarStart) {
      start = start + day;
    }
    tmpValues = rooms[2].getValues();
    for (int i = start; i < start + day; i++) {
      if (randomNoise > 0) {
        tmpNoise = ((double) generator.nextInt(randomNoise * 2) - (double) randomNoise) / 1000;
        values[x] = (double) tmpValues[i] + ((double) tmpValues[i] * tmpNoise);
      } else {
        values[x] = (double) tmpValues[i];
      }
      x++;
    }
    start = start + day;
    if (start == cellarStart) {
      start = start + day;
    }
    tmpValues = rooms[3].getValues();
    for (int i = start; i < start + day; i++) {
      if (randomNoise > 0) {
        tmpNoise = ((double) generator.nextInt(randomNoise * 2) - (double) randomNoise) / 1000;
        values[x] = (double) tmpValues[i] + ((double) tmpValues[i] * tmpNoise);
      } else {
        values[x] = (double) tmpValues[i];
      }
      x++;
    }
    start = start + day;
    if (start == cellarStart) {
      start = start + day;
    }
    tmpValues = rooms[4].getValues();
    for (int i = start; i < start + day; i++) {
      if (randomNoise > 0) {
        tmpNoise = ((double) generator.nextInt(randomNoise * 2) - (double) randomNoise) / 1000;
        values[x] = (double) tmpValues[i] + ((double) tmpValues[i] * tmpNoise);
      } else {
        values[x] = (double) tmpValues[i];
      }
      x++;
    }
    start = start + day;
    if (start == cellarStart) {
      start = start + day;
    }
    tmpValues = rooms[5].getValues();
    for (int i = start; i < start + day; i++) {
      if (randomNoise > 0) {
        tmpNoise = ((double) generator.nextInt(randomNoise * 2) - (double) randomNoise) / 1000;
        values[x] = (double) tmpValues[i] + ((double) tmpValues[i] * tmpNoise);
      } else {
        values[x] = (double) tmpValues[i];
      }
      x++;
    }
    this.roomValues = values;
  }

  /**
   * Gets an array of selected values for the cellar room. The first value is
   * determined by the start timestamp. The unit is [Bq/m^3].
   * 
   * @return An array of selected values for the cellar room.
   */
  public double[] getCellarValues() {
    return this.cellarValues;
  }

  /**
   * Sets an array of selected values for the cellar room. The first value is
   * determined by the start timestamp. The values will be sorted from lowest to
   * highest. The unit is [Bq/m^3].
   */
  private void setCellarValues() {
    OMRoom cellar = this.cellar;
    String variation = this.variation;
    char[] variationChar = variation.toCharArray();
    int start = this.start;
    for (int i = 0; i < variationChar.length; i++) {
      if (variationChar[i] == 'C' || variationChar[i] == 'c') {
        start = start + i * 12;
      }
    }
    int total = 24;
    int day = 24;
    int randomNoise = this.randomNoise * 10;
    Random generator = new Random();
    double tmpNoise = 0.0;
    double[] values = new double[total];
    int x = 0;
    double[] tmpValues = cellar.getValues();
    for (int i = start; i < start + day; i++) {
      if (randomNoise > 0) {
        tmpNoise = ((double) generator.nextInt(randomNoise * 2) - (double) randomNoise) / 1000;
        values[x] = (double) tmpValues[i] + ((double) tmpValues[i] * tmpNoise);
      } else {
        values[x] = (double) tmpValues[i];
      }
      x++;
    }
    this.cellarValues = values;
  }

  /**
   * Gets an array of selected values for the whole campaign. The first value is
   * determined by the start timestamp. The unit is [Bq/m^3].
   * 
   * @return An array of selected values for the whole campaign.
   */
  public double[] getValueChain() {
    return this.valueChain;
  }

  /**
   * Sets an array of selected values for the whole campaign. The first value is
   * determined by the start timestamp. The unit is [Bq/m^3].
   * 
   * @param valueChain
   *          An array of selected values for the whole campaign.
   */
  private void setValueChain() {
    OMRoom[] roomPattern = this.roomPattern;
    double[] roomValues = this.roomValues;
    double[] cellarValues = this.cellarValues;
    double[] valueChain = new double[168];
    int r = 0;
    int c = 0;
    int a = 0;
    for (int i = a; i < a + 24; i++) {
      if (roomPattern[0].getType() == OMRoomType.Room) {
        valueChain[i] = roomValues[r];
        r++;
      } else {
        valueChain[i] = cellarValues[c];
        c++;
      }
    }
    a = a + 24;
    for (int i = a; i < a + 24; i++) {
      if (roomPattern[1].getType() == OMRoomType.Room) {
        valueChain[i] = roomValues[r];
        r++;
      } else {
        valueChain[i] = cellarValues[c];
        c++;
      }
    }
    a = a + 24;
    for (int i = a; i < a + 24; i++) {
      if (roomPattern[2].getType() == OMRoomType.Room) {
        valueChain[i] = roomValues[r];
        r++;
      } else {
        valueChain[i] = cellarValues[c];
        c++;
      }
    }
    a = a + 24;
    for (int i = a; i < a + 24; i++) {
      if (roomPattern[3].getType() == OMRoomType.Room) {
        valueChain[i] = roomValues[r];
        r++;
      } else {
        valueChain[i] = cellarValues[c];
        c++;
      }
    }
    a = a + 24;
    for (int i = a; i < a + 24; i++) {
      if (roomPattern[4].getType() == OMRoomType.Room) {
        valueChain[i] = roomValues[r];
        r++;
      } else {
        valueChain[i] = cellarValues[c];
        c++;
      }
    }
    a = a + 24;
    for (int i = a; i < a + 24; i++) {
      if (roomPattern[5].getType() == OMRoomType.Room) {
        valueChain[i] = roomValues[r];
        r++;
      } else {
        valueChain[i] = cellarValues[c];
        c++;
      }
    }
    a = a + 24;
    for (int i = a; i < a + 24; i++) {
      if (roomPattern[6].getType() == OMRoomType.Room) {
        valueChain[i] = roomValues[r];
        r++;
      } else {
        valueChain[i] = cellarValues[c];
        c++;
      }
    }
    this.valueChain = valueChain;
    Arrays.sort(this.roomValues);
    Arrays.sort(this.cellarValues);
  }

  /**
   * Gets an array of logarithmic values of the normal rooms. Used for geometric
   * mean later on.
   * 
   * @return An array of logarithmic values.
   */
  public double[] getRoomLogValues() {
    return this.roomLogValues;
  }

  /**
   * Sets an array of logarithmic values using the sorted normal values. Used
   * for geometric mean later on.
   */
  private void setRoomLogValues() {
    double[] values = this.roomValues;
    double[] logValues = new double[values.length];
    for (int i = 0; i < values.length; i++) {
      if (values[i] > 0) {
        logValues[i] = Math.log(values[i]);
      } else {
        logValues[i] = 0;
      }
    }
    this.roomLogValues = logValues;
  }

  /**
   * Gets an array of logarithmic values for the cellar rooms. Used for
   * geometric mean later on.
   * 
   * @return An array of logarithmic values for the cellar rooms.
   */
  public double[] getCellarLogValues() {
    return this.cellarLogValues;
  }

  /**
   * Sets an array of logarithmic values for the cellar rooms using the sorted
   * cellar values. Used for geometric mean later on.
   */
  private void setCellarLogValues() {
    double[] values = this.cellarValues;
    double[] logValues = new double[values.length];
    for (int i = 0; i < values.length; i++) {
      if (values[i] > 0) {
        logValues[i] = Math.log(values[i]);
      } else {
        logValues[i] = 0;
      }
    }
    this.cellarLogValues = logValues;
  }

  /**
   * Gets the arithmetic mean of the selected values for normal rooms. The unit
   * is [Bq/m^3].
   * 
   * @return The arithmetic mean of the selected values.
   */
  public double getRoomAverage() {
    return this.roomAverage;
  }

  /**
   * Sets the arithmetic mean of the selected values for normal rooms. The unit
   * is [Bq/m^3].
   */
  private void setRoomAverage() {
    double[] values = this.roomValues;
    double avg = OMHelper.calculateAM(values);
    this.roomAverage = avg;
  }

  /**
   * Gets the arithmetic average of the selected cellar. The unit is [Bq/m^3].
   * 
   * @return The arithmetic average of the selected cellar.
   */
  public double getCellarAverage() {
    return this.cellarAverage;
  }

  /**
   * Sets the arithmetic average of the selected cellar. The unit is [Bq/m^3].
   */
  private void setCellarAverage() {
    double[] values = this.cellarValues;
    double avg = OMHelper.calculateAM(values);
    this.cellarAverage = avg;
  }

  /**
   * Gets the highest value out of the selected normal rooms. The unit is
   * [Bq/m^3].
   * 
   * @return The highest value out of the selected normal rooms.
   */
  public double getRoomMaximum() {
    return this.roomMaximum;
  }

  /**
   * Sets the highest value out of the selected normal rooms. The unit is
   * [Bq/m^3].
   */
  private void setRoomMaximum() {
    double[] values = this.roomValues;
    double maximum = values[values.length - 1];
    this.roomMaximum = maximum;
  }

  /**
   * Gets the highest value out of the selected cellar. The unit is [Bq/m^3].
   * 
   * @return The highest value out of the selected cellar.
   */
  public double getCellarMaximum() {
    return this.cellarMaximum;
  }

  /**
   * Sets the highest value out of the selected cellar. The unit is [Bq/m^3].
   */
  private void setCellarMaximum() {
    double[] values = this.cellarValues;
    double maximum = values[values.length - 1];
    this.cellarMaximum = maximum;
  }

  /**
   * Gets the lowest value out of the selected normal rooms. The unit is
   * [Bq/m^3].
   * 
   * @return The lowest value out of the selected normal rooms.
   */
  public double getRoomMinimum() {
    return this.roomMinimum;
  }

  /**
   * Sets the lowest value out of the selected normal rooms. The unit is
   * [Bq/m^3].
   */
  private void setRoomMinimum() {
    double[] values = this.roomValues;
    double minimum = values[0];
    this.roomMinimum = minimum;
  }

  /**
   * Gets the lowest value out of the selected cellar. The unit is [Bq/m^3].
   * 
   * @return The lowest value out of the selected cellar.
   */
  public double getCellarMinimum() {
    return this.cellarMinimum;
  }

  /**
   * Sets the lowest value out of the selected cellar. The unit is [Bq/m^3].
   */
  private void setCellarMinimum() {
    double[] values = this.cellarValues;
    double minimum = values[0];
    this.cellarMinimum = minimum;
  }

  /**
   * Gets the standard deviation of the selected values for normal rooms. The
   * unit is [Bq/m^3].
   * 
   * @return The standard deviation of the selected values for normal rooms.
   */
  public double getRoomDeviation() {
    return this.roomDeviation;
  }

  /**
   * Sets the standard deviation of the selected values for normal rooms. The
   * unit is [Bq/m^3].
   */
  private void setRoomDeviation() {
    double[] values = this.roomValues;
    double avg = this.roomAverage;
    double dev = OMHelper.calculateSD(values, avg);
    this.roomDeviation = dev;
  }

  /**
   * Gets the standard deviation of the selected cellar. The unit is [Bq/m^3].
   * 
   * @return The standard deviation of the selected cellar.
   */
  public double getCellarDeviation() {
    return this.cellarDeviation;
  }

  /**
   * Sets the standard deviation of the selected cellar. The unit is [Bq/m^3].
   */
  private void setCellarDeviation() {
    double[] values = this.cellarValues;
    double avg = this.cellarAverage;
    double dev = OMHelper.calculateSD(values, avg);
    this.cellarDeviation = dev;
  }

  /**
   * Gets the variation coefficient which determines the ratio between
   * arithmetic mean and standard deviation for normal rooms.
   * 
   * @return The variation coefficient.
   */
  public double getRoomVarCoefficient() {
    return this.roomVarCoefficient;
  }

  /**
   * Sets the variation coefficient which determines the ratio between
   * arithmetic mean and standard deviation for normal rooms.
   */
  private void setRoomVarCoefficient() {
    double avg = this.roomAverage;
    double dev = this.roomDeviation;
    double vc = OMHelper.calculateCV(avg, dev);
    this.roomVarCoefficient = vc;
  }

  /**
   * Gets the variation coefficient which determines the ratio between
   * arithmetic mean and standard deviation for cellars.
   * 
   * @return The variation coefficient.
   */
  public double getCellarVarCoefficient() {
    return this.cellarVarCoefficient;
  }

  /**
   * Sets the variation coefficient which determines the ratio between
   * arithmetic mean and standard deviation for cellars.
   */
  private void setCellarVarCoefficient() {
    double avg = this.cellarAverage;
    double dev = this.cellarDeviation;
    double vc = OMHelper.calculateCV(avg, dev);
    this.cellarVarCoefficient = vc;
  }

  /**
   * Gets the range of the values which is calculated by subtracting the minimum
   * by the maximum value for normal rooms. The unit is [Bq/m^3].
   * 
   * @return The range of the values.
   */
  public double getRoomRange() {
    return this.roomRange;
  }

  /**
   * Sets the range of the values which is calculated by subtracting the minimum
   * by the maximum value for normal rooms. The unit is [Bq/m^3].
   */
  private void setRoomRange() {
    double min = this.roomMinimum;
    double max = this.roomMaximum;
    double range = max - min;
    this.roomRange = range;
  }

  /**
   * Gets the range of the values which is calculated by subtracting the minimum
   * by the maximum value for cellar rooms. The unit is [Bq/m^3].
   * 
   * @return The range of the values.
   */
  public double getCellarRange() {
    return this.cellarRange;
  }

  /**
   * Sets the range of the values which is calculated by subtracting the minimum
   * by the maximum value for cellar rooms. The unit is [Bq/m^3].
   */
  private void setCellarRange() {
    double min = this.cellarMinimum;
    double max = this.cellarMaximum;
    double range = max - min;
    this.cellarRange = range;
  }

  /**
   * Gets the quantile 5 for normal rooms, where only 5% of the values are
   * lower. The unit is [Bq/m^3].
   * 
   * @return The quantile 5.
   */
  public double getRoomQuantile05() {
    return this.roomQuantile05;
  }

  /**
   * Sets the quantile 5 for normal rooms, where only 5% of the values are
   * lower. The unit is [Bq/m^3].
   */
  private void setRoomQuantile05() {
    double[] values = this.roomValues;
    double x = ((double) values.length / 100.0) * 5.0;
    int i = (int) x - 1;
    double q05 = values[i];
    this.roomQuantile05 = q05;
  }

  /**
   * Gets the quantile 5 for cellar rooms, where only 5% of values are lower.
   * The unit is [Bq/m^3].
   * 
   * @return The quantile 5 for cellar rooms.
   */
  public double getCellarQuantile05() {
    return this.cellarQuantile05;
  }

  /**
   * Sets the quantile 5 for cellar rooms, where only 5% of values are lower.
   * The unit is [Bq/m^3].
   */
  private void setCellarQuantile05() {
    double[] values = this.cellarValues;
    double x = ((double) values.length / 100.0) * 5.0;
    int i = (int) x - 1;
    double q05 = values[i];
    this.cellarQuantile05 = q05;
  }

  /**
   * Gets the quantile 95 for normal rooms, where 95% of the values are lower.
   * The unit is [Bq/m^3].
   * 
   * @return The quantile 95.
   */
  public double getRoomQuantile95() {
    return this.roomQuantile95;
  }

  /**
   * Sets the quantile 95 for normal rooms, where 95% of the values are lower.
   * The unit is [Bq/m^3].
   */
  private void setRoomQuantile95() {
    double[] values = this.roomValues;
    double x = (double) values.length
        - (((double) values.length / 100.0) * 5.0);
    int i = (int) x - 1;
    double q95 = values[i];
    this.roomQuantile95 = q95;
  }

  /**
   * Gets the quantile 95 for cellar rooms, where 95% of the values are lower.
   * The unit is [Bq/m^3].
   * 
   * @return The quantile 95 for cellar rooms.
   */
  public double getCellarQuantile95() {
    return this.cellarQuantile95;
  }

  /**
   * Sets the quantile 95 for cellar rooms, where 95% of the values are lower.
   * The unit is [Bq/m^3].
   */
  private void setCellarQuantile95() {
    double[] values = this.cellarValues;
    double x = (double) values.length
        - (((double) values.length / 100.0) * 5.0);
    int i = (int) x - 1;
    double q95 = values[i];
    this.cellarQuantile95 = q95;
  }

  /**
   * Gets the median (quantile 50) for normal rooms, where 50% of the values are
   * lower. The unit is [Bq/m^3].
   * 
   * @return The median (quantile 50) for normal rooms.
   */
  public double getRoomMedian() {
    return this.roomMedian;
  }

  /**
   * Sets the median (quantile 50) for normal rooms, where 50% of the values are
   * lower. The unit is [Bq/m^3].
   */
  private void setRoomMedian() {
    double[] values = this.roomValues;
    double x = (double) values.length
        - (((double) values.length / 100.0) * 50.0);
    int i = (int) x - 1;
    double q50 = values[i];
    this.roomMedian = q50;
  }

  /**
   * Gets the median (quantile 50) for cellar rooms, where 50% of the values are
   * lower. The unit is [Bq/m^3].
   * 
   * @return The median (quantile 50) for cellar rooms.
   */
  public double getCellarMedian() {
    return this.cellarMedian;
  }

  /**
   * Sets the median (quantile 50) for cellar rooms, where 50% of the values are
   * lower. The unit is [Bq/m^3].
   */
  private void setCellarMedian() {
    double[] values = this.cellarValues;
    double x = (double) values.length
        - (((double) values.length / 100.0) * 50.0);
    int i = (int) x - 1;
    double q50 = values[i];
    this.cellarMedian = q50;
  }

  /**
   * Gets the quantile deviation for normal rooms which determines the ratio of
   * the quantiles.
   * 
   * @return The quantile deviation for normal rooms.
   */
  public double getRoomQuantileDeviation() {
    return this.roomQuantileDeviation;
  }

  /**
   * Sets the quantile deviation for normal rooms which determines the ratio of
   * the quantiles.
   */
  private void setRoomQuantileDeviation() {
    double q05 = this.roomQuantile05;
    double q95 = this.roomQuantile95;
    double qDev = OMHelper.calculateQD(q05, q95);
    this.roomQuantileDeviation = qDev;
  }

  /**
   * Gets the quantile deviation for cellar rooms which determines the ratio of
   * the quantiles.
   * 
   * @return The quantile deviation for cellar rooms.
   */
  public double getCellarQuantileDeviation() {
    return this.cellarQuantileDeviation;
  }

  /**
   * Sets the quantile deviation for cellar rooms which determines the ratio of
   * the quantiles.
   */
  private void setCellarQuantileDeviation() {
    double q05 = this.cellarQuantile05;
    double q95 = this.cellarQuantile95;
    double qDev = OMHelper.calculateQD(q05, q95);
    this.cellarQuantileDeviation = qDev;
  }

  /**
   * Gets the relative quantile deviation for normal rooms which determines the
   * ratio of the quantiles.
   * 
   * @return The relative quantile deviation for normal rooms.
   */
  public double getRoomRelativeQuantileDeviation() {
    return this.roomRelativeQuantileDeviation;
  }

  /**
   * Sets the relative quantile deviation for normal rooms which determines the
   * ratio of the quantiles.
   */
  private void setRoomRelativeQuantileDeviation() {
    double q05 = this.roomQuantile05;
    double q50 = this.roomMedian;
    double q95 = this.roomQuantile95;
    double qrDev = OMHelper.calculateRQD(q05, q50, q95);
    this.roomRelativeQuantileDeviation = qrDev;
  }

  /**
   * Gets the relative quantile deviation for cellar rooms which determines the
   * ratio of the quantiles.
   * 
   * @return The relative quantile deviation for cellar rooms.
   */
  public double getCellarRelativeQuantileDeviation() {
    return this.cellarRelativeQuantileDeviation;
  }

  /**
   * Sets the relative quantile deviation for cellar rooms which determines the
   * ratio of the quantiles.
   */
  private void setCellarRelativeQuantileDeviation() {
    double q05 = this.cellarQuantile05;
    double q50 = this.cellarMedian;
    double q95 = this.cellarQuantile95;
    double qrDev = OMHelper.calculateRQD(q05, q50, q95);
    this.cellarRelativeQuantileDeviation = qrDev;
  }

  /**
   * Gets the geometric mean for normal rooms. The unit is [Bq/m^3].
   * 
   * @return The geometric mean for normal rooms.
   */
  public double getRoomLogAverage() {
    return this.roomLogAverage;
  }

  /**
   * Sets the geometric mean for normal rooms. The unit is [Bq/m^3].
   */
  private void setRoomLogAverage() {
    double[] values = this.roomValues;
    double tmpAverage = OMHelper.calculateGM(values);
    this.roomLogAverage = tmpAverage;
  }

  /**
   * Gets the geometric mean for cellar rooms. The unit is [Bq/m^3].
   * 
   * @return The geometric mean for cellar rooms.
   */
  public double getCellarLogAverage() {
    return this.cellarLogAverage;
  }

  /**
   * Sets the geometric mean for cellar rooms. The unit is [Bq/m^3].
   */
  private void setCellarLogAverage() {
    double[] values = this.cellarValues;
    double tmpAverage = OMHelper.calculateGM(values);
    this.cellarLogAverage = tmpAverage;
  }

  /**
   * Gets the geometric standard deviation for normal rooms.
   * 
   * @return The geometric standard deviation for normal rooms.
   */
  public double getRoomLogDeviation() {
    return this.roomLogDeviation;
  }

  /**
   * Sets the geometric standard deviation for normal rooms.
   */
  private void setRoomLogDeviation() {
    double geoMean = this.roomLogAverage;
    double[] values = this.roomValues;
    double gsd = OMHelper.calculateGSD(values, geoMean);
    this.roomLogDeviation = gsd;
  }

  /**
   * Gets the geometric standard deviation for cellar rooms.
   * 
   * @return The geometric standard deviation for cellar rooms.
   */
  public double getCellarLogDeviation() {
    return this.cellarLogDeviation;
  }

  /**
   * Sets the geometric standard deviation for cellar rooms.
   */
  private void setCellarLogDeviation() {
    double geoMean = this.cellarLogAverage;
    double[] values = this.cellarValues;
    double gsd = OMHelper.calculateGSD(values, geoMean);
    this.cellarLogDeviation = gsd;
  }

  /**
   * Constructor for objects of the class OMCampaign. Creates survey campaigns
   * for 7 days in 7 different Rooms using 6 normal rooms and 1 cellar room.
   * 
   * @param start
   *          The timestamp of the first measurement for the simulated campaign,
   *          starting with 0 for the first hour of the real value-sets. The
   *          unit is [h].
   * @param rooms
   *          An array of seven rooms for the seven days (steps) of the
   *          simulated campaign. Should be 6 normal rooms and 1 cellar.
   * @param randomNoise
   *          An integer which defines the random noise that is added to the
   *          values. A random noise of 0 means the original values wont be
   *          modified. The unit is [%].
   * @throws IOException
   *           If creating log file or writing logs fails.
   */
  public OMCampaign(int start, OMRoom[] rooms, int randomNoise)
      throws IOException {
    super();
    this.start = start;
    try {
      this.rooms = new OMRoom[6];
      int x = 0;
      String tmpVariation = "";
      for (int i = 0; i < rooms.length; i++) {
        if (rooms[i].getType() != OMRoomType.Cellar) {
          this.rooms[x] = rooms[i];
          x++;
        } else {
          if (rooms[i].getType() == OMRoomType.Cellar) {
            this.cellar = rooms[i];
          }
        }
        tmpVariation = tmpVariation + rooms[i].getId();
      }
      setVariation(tmpVariation);
      setRoomPattern(rooms);
      setRandomNoise(randomNoise);
      calculateAttributes();
    } catch (Exception e) {
      if (OMHelper.isLogOutputEnabled()) {
        OMHelper.writeLog("Error: " + e.getMessage());
        OMHelper.writeLog("Error: Failed to create campaign.");
        e.printStackTrace();
      }
    }
  }

  /**
   * Calls the setters for all the attributes of the campaign to re-calculate
   * and update them. Call this method always after changes to the radon values.
   * Note: It's not needed to call this after using the OMCampaign.setRooms(),
   * setCellar() or setStart()-methods as modifying values using the setter
   * always triggers the re-calculation of attributes on its own.
   * 
   * @throws IOException
   *           If creating log file or writing logs fails.
   */
  public void calculateAttributes() throws IOException {
    setType();
    setRoomValues();
    setCellarValues();
    setValueChain();
    setRoomLogValues();
    setRoomAverage();
    setRoomMaximum();
    setRoomMinimum();
    setRoomDeviation();
    setRoomVarCoefficient();
    setRoomRange();
    setRoomQuantile05();
    setRoomQuantile95();
    setRoomMedian();
    setRoomQuantileDeviation();
    setRoomRelativeQuantileDeviation();
    setRoomLogAverage();
    setRoomLogDeviation();
    setCellarLogValues();
    setCellarAverage();
    setCellarMaximum();
    setCellarMinimum();
    setCellarDeviation();
    setCellarVarCoefficient();
    setCellarRange();
    setCellarQuantile05();
    setCellarQuantile95();
    setCellarMedian();
    setCellarQuantileDeviation();
    setCellarRelativeQuantileDeviation();
    setCellarLogAverage();
    setCellarLogDeviation();
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((this.cellar == null) ? 0 : this.cellar.hashCode());
    result = prime * result + this.randomNoise;
    result = prime * result + Arrays.hashCode(this.rooms);
    result = prime * result + this.start;
    result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
    result = prime * result
        + ((this.variation == null) ? 0 : this.variation.hashCode());
    return result;
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    OMCampaign other = (OMCampaign) obj;
    if (this.cellar == null) {
      if (other.cellar != null) {
        return false;
      }
    } else if (!this.cellar.equals(other.cellar)) {
      return false;
    }
    if (this.randomNoise != other.randomNoise) {
      return false;
    }
    if (!Arrays.equals(this.rooms, other.rooms)) {
      return false;
    }
    if (this.start != other.start) {
      return false;
    }
    if (this.type != other.type) {
      return false;
    }
    if (this.variation == null) {
      if (other.variation != null) {
        return false;
      }
    } else if (!this.variation.equals(other.variation)) {
      return false;
    }
    return true;
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Campaign: T=" + this.start + ",\tR=" + this.variation + ",\tR_AM="
        + (int) this.roomAverage + ",\tR_GM=" + (int) this.roomLogAverage
        + ",\tR_Q50=" + (int) this.roomMedian + ",\tR_MAX="
        + (int) this.roomMaximum + ",\tC_AM=" + (int) this.cellarAverage
        + ",\tC_GM=" + (int) this.cellarLogAverage + ",\tC_Q50="
        + (int) this.cellarMedian + ",\tC_MAX=" + (int) this.cellarMaximum;
  }
}
