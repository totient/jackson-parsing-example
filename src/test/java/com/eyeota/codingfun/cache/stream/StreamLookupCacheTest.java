package com.eyeota.codingfun.cache.stream;

import com.eyeota.codingfun.cache.LookupCache;
import com.eyeota.codingfun.cache.SegmentConfig;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class StreamLookupCacheTest {

  LookupCache lookupCache = null;

  @Before
  public void init() {
    lookupCache = LookupStreamMruCache.INSTANCE;
  }

  /**
   * The query getSegmentFor("org1", "paramName1") will return an empty SegmentConfig array.
   */
  @Test
  public void testGetSegmentFor01() {
    SegmentConfig[] segments = lookupCache.getSegmentFor("org1", "paramName1");
    assertEquals(0, segments.length);
  }

  /**
   * The query getSegmentFor("org1", "paramName1", "paramVal1") will return a 1-element SegmentConfigArray containing a
   * SegmentConfig object for seg_1234.
   */
  @Test
  public void testGetSegmentFor02() {
    SegmentConfig[] segments = lookupCache.getSegmentFor("org1", "paramName1", "paramVal1");
    assertEquals(1, segments.length);
    assertEquals("seg_1234", segments[0].getSegmentId());
  }

  /**
   * The query getSegmentFor("org1", "paramName1", <<"paramVal2" | "paramVal3" | "paramVal4" | "paramVal5">> ) will
   * return a 1-element SegmentConfigArray containing a SegmentConfig object with id: "intr.edu".
   */
  @Test
  public void testGetSegmentFor03a() {
    SegmentConfig[] segments = lookupCache.getSegmentFor("org1", "paramName1", "paramVal2");
    assertEquals(1, segments.length);
    assertEquals("intr.edu", segments[0].getSegmentId());
  }

  @Test
  public void testGetSegmentFor03b() {
    SegmentConfig[] segments = lookupCache.getSegmentFor("org1", "paramName1", "paramVal3");
    assertEquals(1, segments.length);
    assertEquals("intr.edu", segments[0].getSegmentId());
  }

  @Test
  public void testGetSegmentFor03c() {
    SegmentConfig[] segments = lookupCache.getSegmentFor("org1", "paramName1", "paramVal4");
    assertEquals(1, segments.length);
    assertEquals("intr.edu", segments[0].getSegmentId());
  }

  @Test
  public void testGetSegmentFor03d() {
    SegmentConfig[] segments = lookupCache.getSegmentFor("org1", "paramName1", "paramVal5");
    assertEquals(1, segments.length);
    assertEquals("intr.edu", segments[0].getSegmentId());
  }


  /**
   * The query getSegmentFor("org1", "paramName1", "paramVal6" ) will return a 3-element SegmentConfigArray containing
   * SegmentConfig objects with ids: dem.infg.m, intr.heal, dem.infg.f
   */
  @Test
  public void testGetSegmentFor04() {
    SegmentConfig[] segments = lookupCache.getSegmentFor("org1", "paramName1", "paramVal6");
    assertEquals(3, segments.length);
    List<String> segConfIds = new ArrayList<String>() {
      {
        add("dem.infg.m");
        add("intr.heal");
        add("dem.infg.f");
      }
    };
    assertTrue(segConfIds.contains(segments[0].getSegmentId()));
    assertTrue(segConfIds.contains(segments[1].getSegmentId()));
    assertTrue(segConfIds.contains(segments[2].getSegmentId()));
  }

  /**
   * The query getSegmentFor("org1", "testedu") will return a 1-element SegmentConfigArray containing a SegmentConfig
   * object with id "n277".
   */  
  @Test
  public void testGetSegmentFor05() {
    SegmentConfig[] segments = lookupCache.getSegmentFor("org1", "testedu");
    assertEquals(1, segments.length);
    assertEquals("n277", segments[0].getSegmentId());    
  }

  /**
   * The query getSegmentFor("org1", "testedu", "") will return a 1-element SegmentConfigArray containing a
   * SegmentConfig object with id "n277".
   */
  @Test
  public void testGetSegmentFor06() {
    SegmentConfig[] segments = lookupCache.getSegmentFor("org1", "testedu", "");
    assertEquals(1, segments.length);
    assertEquals("n277", segments[0].getSegmentId());    
  }

  /**
   * The query getSegmentFor("org1", "testedu", <<any value other than an empty string>>) will return an empty
   * SegmentConfig array.
   */
  @Test
  public void testGetSegmentFor07() {
    SegmentConfig[] segments = lookupCache.getSegmentFor("org1", "testedu", "$$");
    assertEquals(0, segments.length);
  }

  /**
   * The query getSegmentFor("org1", "gen", "Female") will return a 1-element SegmentConfigArray containing a
   * SegmentConfig object with id "dem.g.f".
   */
  @Test
  public void testGetSegmentFor08() {
    SegmentConfig[] segments = lookupCache.getSegmentFor("org1", "gen", "Female");
    assertEquals(1, segments.length);
    assertEquals("dem.g.f", segments[0].getSegmentId());    
  }

  /**
   * The query getSegmentFor("org1", "gen", "Male") will return a 1-element SegmentConfigArray containing a
   * SegmentConfig object with id "dem.g.m".
   */
  @Test
  public void testGetSegmentFor09() {
    SegmentConfig[] segments = lookupCache.getSegmentFor("org1", "gen", "Male");
    assertEquals(1, segments.length);
    assertEquals("dem.g.m", segments[0].getSegmentId());    
  }

  /**
   * The query getSegmentFor("org1", "gen", <<any value other than "Male" or "Female">>) will return an empty
   * SegmentConfig array.
   */
  @Test
  public void testGetSegmentFor10() {
    SegmentConfig[] segments = lookupCache.getSegmentFor("org1", "gen", "**");
    assertEquals(0, segments.length);
  }

  /**
   * The query getSegmentFor("6lkb2cv", "sub", "buying") will return a 1-element SegmentConfigArray containing a
   * SegmentConfig object with id "dem.life.homebuy".
   */
  @Test
  public void testGetSegmentFor11() {
    SegmentConfig[] segments = lookupCache.getSegmentFor("6lkb2cv", "sub", "buying");
    assertEquals(1, segments.length);
    assertEquals("dem.life.homebuy", segments[0].getSegmentId());    
  }

}
