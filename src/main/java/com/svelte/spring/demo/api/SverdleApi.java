package com.svelte.spring.demo.api;

import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.Duration.ofDays;

import com.svelte.spring.demo.configuration.yml.Words;
import com.svelte.spring.demo.svelte.form.ActionResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RestController
@RequestMapping(
    path = "/api",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
)
public class SverdleApi {

  private final List<String> words;
  private final Set<String> allowed;

  public SverdleApi(Words wordData) {
    this.words = Collections.unmodifiableList(wordData.getWords());

    List<String> allowed = new ArrayList<>(List.copyOf(this.words));
    allowed.addAll(wordData.getAllowed());
    this.allowed = Set.copyOf(allowed);
  }

  @GetMapping(path = "/start", consumes = MediaType.ALL_VALUE)
  public ResponseEntity<?> start(
      @CookieValue(required = false) String sverdle) {
    Game game = new Game(sverdle, this.words, this.allowed);
    return ResponseEntity.ok(game);
  }

  @PostMapping(path = "/restart", consumes = MediaType.ALL_VALUE)
  public ResponseEntity<?> restart() {
    return ResponseEntity
        .ok()
        .header(HttpHeaders.SET_COOKIE, createCookie(""))
        .body(ActionResult.emptySuccess());
  }

  /**
   * Modify game state in reaction to a keypress. If client-side JavaScript
   * is available, this will happen in the browser instead of here
   */
  @PostMapping("/update")
  public ResponseEntity<?> update(
      MultipartHttpServletRequest request,
      @CookieValue(required = false) String sverdle) throws IOException {
    Game game = new Game(sverdle, this.words, this.allowed);

    String key = String.join("", request.getParameterValues("key"));

    int i = game.getAnswers().size();

    if (Objects.equals(key, "backspace")) {
      String guess = game.getGuesses().get(i);
      game.getGuesses().set(i, guess.substring(0, guess.length() - 1));
    } else {
      String update = game.getGuesses().get(i) + key;
      game.getGuesses().set(i, update);
    }

    return ResponseEntity
        .ok()
        .header(HttpHeaders.SET_COOKIE, createCookie(game.toString()))
        .body(ActionResult.emptySuccess());
  }

  /**
   * Modify game state in reaction to a guessed word. This logic always runs on
   * the server, so that people can't cheat by peeking at the JavaScript
   */
  @PostMapping("/enter")
  public ResponseEntity<?> enter(
      MultipartHttpServletRequest request,
      @CookieValue(required = false) String sverdle) throws IOException {
    Game game = new Game(sverdle, this.words, this.allowed);

    List<String> guess = new ArrayList<>(
        List.of(request.getParameterValues("guess")));

    if (!game.enter(guess)) {
      return ResponseEntity
          .badRequest()
          .body(ActionResult.success()
              .status(400)
              .field("badGuess", true)
              .build());
    }

    return ResponseEntity
        .ok()
        .header(HttpHeaders.SET_COOKIE, createCookie(game.toString()))
        .body(ActionResult.emptySuccess());
  }

  private static String createCookie(String value) {
    return ResponseCookie.from(
            "sverdle",
            encode(value, UTF_8)
                .replace("+", "%20"))
        .maxAge(ofDays(1))
        .secure(false)
        .httpOnly(false)
        .path("/")
        .build()
        .toString();
  }
}
