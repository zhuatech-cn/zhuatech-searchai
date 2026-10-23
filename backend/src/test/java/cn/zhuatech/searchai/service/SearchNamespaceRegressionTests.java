/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.searchai.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.*;
import java.util.*;
import org.junit.jupiter.api.Test;

/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
class SearchNamespaceRegressionTests {
  private final EnterpriseSearchService service = new EnterpriseSearchService();

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  private EnterpriseSearchService.Result run(List<String> departments, List<String> sources) {
    return service.plan(
        new EnterpriseSearchService.Request(" 交付\t验收\n 流程 ", departments, sources, true));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void substringDoesNotGrantCandidate() {
    var r = run(List.of("fin"), List.of("finance:budget", "shipping:finish"));
    assertEquals(List.of(), r.permittedSources());
    assertEquals(2, r.blockedSources().size());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void completeNamespacesAreCaseInsensitive() {
    var r = run(List.of("FINANCE"), List.of("finance:budget", "PUBLIC:policy"));
    assertEquals(2, r.permittedSources().size());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void blankDepartmentDoesNotMatchEverything() {
    var r = run(List.of(" "), List.of("finance:budget", "public:policy"));
    assertEquals(List.of("public:policy"), r.permittedSources());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void normalizesWhitespaceAndRejectsMalformedSource() {
    var r = run(List.of("finance"), List.of("finance:", "finance", " finance:budget"));
    assertEquals("交付 验收 流程", r.normalizedQuery());
    assertEquals(List.of(), r.permittedSources());
  }
}
