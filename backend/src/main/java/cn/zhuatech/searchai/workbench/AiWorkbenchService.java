/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.searchai.workbench;

import static cn.zhuatech.searchai.workbench.WorkbenchInput.*;

import java.math.*;
import java.util.*;
import org.springframework.stereotype.Service;

/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
@Service
public class AiWorkbenchService {
  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  public Map<String, Object> execute(Map<String, Object> r) {
    validate(r);

    keys(r, "query", "k1", "b", "topK", "documents");
    Set<String> query = new LinkedHashSet<>(strings(r, "query", 1, 50));
    double k1 = decimal(r, "k1", 0.1, 3).doubleValue(), b = decimal(r, "b", 0, 1).doubleValue();
    int top = integer(r, "topK", 1, 100);
    var docs = rows(r, "documents", 1, 100);
    Set<String> ids = new HashSet<>();
    Map<String, List<String>> tokens = new LinkedHashMap<>();
    Map<String, Integer> frequency = new HashMap<>();
    int length = 0;
    for (var d : docs) {
      keys(d, "id", "tokens");
      String id = id(d, "id");
      unique(ids, id);
      var ts = strings(d, "tokens", 0, 200);
      tokens.put(id, ts);
      length += ts.size();
      for (String term : new HashSet<>(ts))
        if (query.contains(term)) frequency.merge(term, 1, Integer::sum);
    }
    double average = length / (double) docs.size();
    List<Map<String, Object>> out = new ArrayList<>();
    for (var d : tokens.entrySet()) {
      double score = 0;
      List<String> matched = new ArrayList<>();
      for (String term : query) {
        long tf = d.getValue().stream().filter(term::equals).count();
        if (tf == 0) continue;
        matched.add(term);
        int df = frequency.getOrDefault(term, 0);
        double idf = Math.log(1 + (docs.size() - df + 0.5) / (df + 0.5));
        score += idf * tf * (k1 + 1) / (tf + k1 * (1 - b + b * d.getValue().size() / average));
      }
      if (score > 0)
        out.add(Map.of("id", d.getKey(), "score", round(score), "matchedTerms", matched));
    }
    out.sort(
        Comparator.<Map<String, Object>, BigDecimal>comparing(x -> (BigDecimal) x.get("score"))
            .reversed()
            .thenComparing(x -> x.get("id").toString()));
    return Map.of(
        "results",
        out.subList(0, Math.min(top, out.size())),
        "candidateCount",
        docs.size(),
        "averageDocumentLength",
        round(average),
        "method",
        "BM25");
  }
}
