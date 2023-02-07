package com.svelte.spring.demo.svelte.form;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Map;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;

@Getter
@Builder
@EqualsAndHashCode
@ToString
@JsonSerialize(using = ActionResultSerializer.class)
public class ActionResult {
  private final String type;

  @Builder.Default
  private final int status = 204;

  @Singular("field")
  private final Map<String, Object> data;

  public static ActionResultBuilder success() {
    return ActionResult.builder()
        .type("success")
        .status(200);
  }

  public static ActionResultBuilder failure() {
    return ActionResult.builder()
        .type("failure")
        .status(400);
  }

  public static ActionResult emptySuccess() {
    return ActionResult.builder()
        .type("success")
        .status(204)
        .build();
  }

  public static ActionResult emptyFailure() {
    return ActionResult.builder()
        .type("failure")
        .status(400)
        .build();
  }

}
