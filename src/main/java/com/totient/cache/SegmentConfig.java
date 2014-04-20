package com.totient.cache;

public class SegmentConfig {
  private String segmentId;

  public SegmentConfig(String segmentId) {
    this.segmentId = segmentId;
  }

  public SegmentConfig() {
  }

  public String getSegmentId() {
    return segmentId;
  }

  @Override
  public String toString() {
    return super.toString() + ", segmentId: " + segmentId;
  }
  
  
}
