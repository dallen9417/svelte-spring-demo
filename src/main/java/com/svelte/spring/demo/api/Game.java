package com.svelte.spring.demo.api;

import static java.lang.Math.floor;
import static java.lang.Math.random;
import static java.lang.String.valueOf;
import static java.math.RoundingMode.HALF_EVEN;
import static org.apache.commons.lang3.StringUtils.splitPreserveAllTokens;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

@Getter
public class Game {
  private final BigDecimal index;
  private final List<String> guesses;
  private final List<String> answers;
  private final String answer;
  @JsonIgnore
  @Getter(AccessLevel.MODULE)
  private final List<String> words;
  @JsonIgnore
  @Getter(AccessLevel.MODULE)
  private final Set<String> allowed;

  /**
   * Create a game object from the player's cookie, or initialise a new game
   */
  public Game(String serialized, List<String> words, Set<String> allowed) {
    this.words = words;
    this.allowed = allowed;
    if (serialized != null && !serialized.isBlank()) {
      // index, guesses, answers
      String[] values = serialized.split("-");
      String index = values[0];
      String guesses = values[1];
      String answers = values.length == 3 ? values[2] : null;

      this.index = new BigDecimal(index).setScale(0, HALF_EVEN);
      this.guesses = StringUtils.isNotBlank(guesses)
          ? new ArrayList<>(List.of(splitPreserveAllTokens(guesses)))
          : new ArrayList<>();
      this.answers = StringUtils.isNotBlank(answers)
          ? new ArrayList<>(List.of(splitPreserveAllTokens(answers)))
          : new ArrayList<>();
    } else {
      this.index = new BigDecimal(valueOf(floor(random() * words.size())))
          .setScale(0, HALF_EVEN);
      this.guesses = new ArrayList<>(List.of("", "", "", "", "", ""));
      this.answers = new ArrayList<>();
    }

    this.answer = words.get(this.index.intValue());
  }

  /**
   * Update game state based on a guess of a five-letter word. Returns
   * true if the guess was valid, false otherwise
   */
  public boolean enter(List<String> letters) {
    String word = String.join("", letters);
    boolean valid = this.allowed.contains(word);

    if (!valid) return false;

    this.guesses.set(this.answers.size(), word);

    String[] available = this.answer.split("");
    String[] answer = new String[5];
    Arrays.fill(answer, "_");

    // first, find exact matches
    for (int i = 0; i < 5; i += 1) {
      if (Objects.equals(letters.get(i), available[i])) {
        answer[i] = "x";
        available[i] = " ";
      }
    }

    // then find close matches (this has to happen
    // in a second step, otherwise an early close
    // match can prevent a later exact match)
    for (int i = 0; i < 5; i += 1) {
      if (Objects.equals(answer[i], "_")) {
        int index = ArrayUtils.indexOf(available, letters.get(i));
        if (index != -1) {
          answer[i] = "c";
          available[index] = " ";
        }
      }
    }

    this.answers.add(String.join("", answer));

    return true;
  }

  /**
   * Serialize game state so it can be set as a cookie
   */
  @Override
  public String toString() {
    NumberFormat format = NumberFormat.getInstance();
    format.setGroupingUsed(false);
    return MessageFormat.format("{0}-{1}-{2}",
        format.format(this.index),
        String.join(" ", this.guesses),
        String.join(" ", this.answers));
  }
}
