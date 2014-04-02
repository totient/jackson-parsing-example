package com.eyeota.codingfun.cache.stream;

import com.eyeota.codingfun.cache.SegmentConfig;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.type.TypeReference;

enum StreamMruCache {

  INSTANCE;
  private final LinkedHashMap<String, Map<String, Map<String, List<SegmentConfig>>>> orgMap = new LinkedHashMap<>();
  private int cacheSize = 1;
  private String dataFile = "data.json";
  private final String lineBreak = "\n";

  private StreamMruCache() {
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    InputStream is = classloader.getResourceAsStream("config.properties");
    try {
      ResourceBundle bundle = new PropertyResourceBundle(is);
      cacheSize = Integer.parseInt(bundle.getString("cacheSize"));
      dataFile = bundle.getString("dataFile");
    } catch (IOException ex) {
      Logger.getLogger(StreamMruCache.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  Map<String, Map<String, Map<String, List<SegmentConfig>>>> getOrgMap() {
    return Collections.unmodifiableMap(orgMap);
  }

  Map<String, Map<String, Map<String, List<SegmentConfig>>>> load(String orgKey) {
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    JsonFactory f = new MappingJsonFactory();
    JsonParser jp;
    try (InputStream is = classloader.getResourceAsStream(dataFile)) {
      jp = f.createJsonParser(is);
      if (jp.nextToken() == JsonToken.START_ARRAY) {
        while (jp.nextToken() != JsonToken.END_ARRAY) {
          Map<String, List<Map<String, List<Map<String, SegmentConfig>>>>> oMap = jp.readValueAs(new TypeReference<Map<String, List<Map<String, List<Map<String, SegmentConfig>>>>>>() {
          });
          if (oMap.containsKey(orgKey)) {
            Map<String, Map<String, Map<String, List<SegmentConfig>>>> map = parse(oMap);
            push(orgKey, map);
            break;
          }
        }
      }
      jp.close();

    } catch (Exception ex) {
      Logger.getLogger(LookupStreamMruCache.class.getName()).log(Level.SEVERE, "Malformed or invalid file, probably!", ex);
    }

    return getOrgMap();
  }

  private void push(String orgKey, Map<String, Map<String, Map<String, List<SegmentConfig>>>> map) {
    synchronized (orgMap) {
      if (orgMap.size() >= cacheSize) {
        Iterator iter = orgMap.keySet().iterator();
        iter.next();
        iter.remove();
      }
      orgMap.put(orgKey, map.get(orgKey));
    }
  }

  private Map<String, Map<String, Map<String, List<SegmentConfig>>>> parse(
          Map<String, List<Map<String, List<Map<String, SegmentConfig>>>>> oMap) {

    Map<String, Map<String, Map<String, List<SegmentConfig>>>> customMap = new HashMap<>();

    for (String oKey : oMap.keySet()) {
      Map<String, Map<String, List<SegmentConfig>>> paramMap = new HashMap<>();
      customMap.put(oKey, paramMap);
      for (Map<String, List<Map<String, SegmentConfig>>> pMap : oMap.get(oKey)) {
        for (String paramName : pMap.keySet()) {
          Map<String, List<SegmentConfig>> paraValMap = new HashMap<>();
          paramMap.put(paramName, paraValMap);
          for (Map<String, SegmentConfig> pvMap : pMap.get(paramName)) {
            for (String paramVal : pvMap.keySet()) {
              String[] paramVals = paramVal.split(lineBreak);
              for (String s : paramVals) {
                if (paraValMap.containsKey(s)) {
                  paraValMap.get(s).add(pvMap.get(paramVal));
                } else {
                  List<SegmentConfig> segList = new ArrayList<>();
                  segList.add(pvMap.get(paramVal));
                  paraValMap.put(s, segList);
                }
              }
            }
          }
        }
      }
    }

    return customMap;
  }
}
