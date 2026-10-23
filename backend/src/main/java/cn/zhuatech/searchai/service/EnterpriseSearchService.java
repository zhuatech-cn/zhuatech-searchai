/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.searchai.service;

import jakarta.validation.constraints.*;
import java.util.*;
import org.springframework.stereotype.Service;

/**
 * Candidate planning only, not document authorization. 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Service
public class EnterpriseSearchService {
  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  public record Request(
      @NotBlank @Size(max = 4000) String query,
      @NotEmpty @Size(max = 100) List<@NotBlank @Size(max = 80) String> userDepartments,
      @NotEmpty @Size(max = 200) List<@NotBlank @Size(max = 200) String> requestedSources,
      boolean requireCitations) {}

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  public record Result(
      String normalizedQuery,
      List<String> permittedSources,
      List<String> blockedSources,
      List<String> retrievalPlan,
      boolean citationsRequired) {}

  /**
   * Match complete namespace segments, never arbitrary substrings. 上海如静知华信息科技有限公司 ·
   * https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
   */
  public Result plan(Request r) {
    Set<String> departments = new HashSet<>();
    for (String d : r.userDepartments())
      if (d != null && !d.isBlank() && !d.contains(":"))
        departments.add(d.strip().toLowerCase(Locale.ROOT));
    List<String> permitted = new ArrayList<>(), blocked = new ArrayList<>();
    for (String s : r.requestedSources()) {
      int separator = s == null ? -1 : s.indexOf(':');
      boolean wellFormed =
          separator > 0
              && separator < s.length() - 1
              && s.equals(s.strip())
              && !s.substring(separator + 1).isBlank();
      String namespace = wellFormed ? s.substring(0, separator).toLowerCase(Locale.ROOT) : "";
      if (wellFormed && (namespace.equals("public") || departments.contains(namespace)))
        permitted.add(s);
      else blocked.add(s);
    }
    return new Result(
        r.query().strip().replaceAll("\\s+", " "),
        List.copyOf(permitted),
        List.copyOf(blocked),
        List.of("按调用方提供的部门生成候选计划（不是权限凭证）", "实际检索必须另行校验服务端身份及文档ACL", "关键词召回后重排并保留来源"),
        r.requireCitations());
  }
}
