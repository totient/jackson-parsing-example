package com.eyeota.codingfun.cache.databinding;

import com.eyeota.codingfun.cache.SegmentConfig;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

enum DataBindingCache {

  INSTANCE;
  
  private final Map<String, Map<String, Map<String, List<SegmentConfig>>>> orgMap = new HashMap<>();
  private String dataFile = "test_data.json";
  private final String lineBreak = "\n";

  private DataBindingCache() {
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    InputStream is = classloader.getResourceAsStream("config.properties");
    try {
      ResourceBundle bundle = new PropertyResourceBundle(is);
      dataFile = bundle.getString("dataFile");
    } catch (IOException ex) {
      Logger.getLogger(DataBindingCache.class.getName()).log(Level.SEVERE, null, ex);
    }
    load();
  }

  Map<String, Map<String, Map<String, List<SegmentConfig>>>> getOrgMap() {
    return Collections.unmodifiableMap(orgMap);
  }

  private void load() {
    List<Map<String, List<Map<String, List<Map<String, SegmentConfig>>>>>> orgs = null;
    ObjectMapper mapper = new ObjectMapper();
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();

    try (InputStream is = classloader.getResourceAsStream(dataFile)) {
      orgs = mapper.readValue(is,
              new TypeReference<List<Map<String, List<Map<String, List<Map<String, SegmentConfig>>>>>>>() {
              });
    } catch (IOException ex) {
      Logger.getLogger(LookupDataBindingCache.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
    }

    parse(orgs);
  }

  private void parse(List<Map<String, List<Map<String, List<Map<String, SegmentConfig>>>>>> orgs) {
    for (Map<String, List<Map<String, List<Map<String, SegmentConfig>>>>> oMap : orgs) {
      for (String orgKey : oMap.keySet()) {
        Map<String, Map<String, List<SegmentConfig>>> paramMap = new HashMap<>();
        orgMap.put(orgKey, paramMap);
        for (Map<String, List<Map<String, SegmentConfig>>> pMap : oMap.get(orgKey)) {
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
    }
  }
}
