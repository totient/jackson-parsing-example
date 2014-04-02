package com.eyeota.codingfun.cache.databinding;

import com.eyeota.codingfun.cache.LookupCache;
import com.eyeota.codingfun.cache.SegmentConfig;
import java.util.List;
import java.util.Map;

public enum LookupDataBindingCache implements LookupCache {

  INSTANCE;
  private final SegmentConfig[] emptySegConfArr = new SegmentConfig[0];
  private final Map<String, Map<String, Map<String, List<SegmentConfig>>>> orgMap;
  private final String EMPTY_STR = "";

  private LookupDataBindingCache() {
    orgMap = DataBindingCache.INSTANCE.getOrgMap();
  }

  @Override
  public SegmentConfig[] getSegmentFor(String orgKey, String paramKey) {
    return getSegmentFor(orgKey, paramKey, EMPTY_STR);
  }

  @Override
  public SegmentConfig[] getSegmentFor(String orgKey, String paramKey, String paramValKey) {
    SegmentConfig[] segConfArr = emptySegConfArr;
    Map<String, Map<String, List<SegmentConfig>>> paramMap = orgMap.get(orgKey);
    if (paramMap != null) {
      Map<String, List<SegmentConfig>> map = paramMap.get(paramKey);
      if (map != null) {
        List<SegmentConfig> segments = map.get(paramValKey);
        if (segments != null) {
          segConfArr = segments.toArray(new SegmentConfig[segments.size()]);
        }
      }
    }

    return segConfArr;
  }
  
}
